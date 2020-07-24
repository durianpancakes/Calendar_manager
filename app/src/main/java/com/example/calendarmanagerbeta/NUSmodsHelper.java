package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class NUSmodsHelper{
    private static NUSmodsHelper INSTANCE = null;
    private final RequestQueue mRequestQueue;
    private final Context mContext;
    private final String mBaseUrl;
    private String jsonString;
    private List<NUSModuleLite> nusModulesLite;
    private NUSModuleMain nusModuleFull;
    private onRefreshFullListener mOnRefreshFullListener;
    private onRefreshSpecificListener mOnRefreshSpecificListener;

    public void setOnRefreshFullListener(onRefreshFullListener refreshListener){
        mOnRefreshFullListener = refreshListener;
    }

    public void setOnRefreshSpecificListener(onRefreshSpecificListener refreshListener){
        mOnRefreshSpecificListener = refreshListener;
    }

    public List<NUSModuleLite> getNusModulesLite() {
        return nusModulesLite;
    }

    public NUSModuleMain getNusModuleFull() {
        return nusModuleFull;
    }

    private NUSmodsHelper(Context context){
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mBaseUrl = "https://api.nusmods.com/v2/2020-2021/";
    }

    public static synchronized NUSmodsHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new NUSmodsHelper(context);
        }
        return INSTANCE;
    }

    public void mapModuleDatabase(String jsonString){
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<NUSModuleLite> nusModules;
        try{
            nusModules = Arrays.asList(mapper.readValue(jsonString, NUSModuleLite[].class));
            nusModulesLite = nusModules;
            if(mOnRefreshFullListener != null){
                mOnRefreshFullListener.onRefresh(nusModulesLite);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void mapFullModule(String jsonString){
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NUSModuleMain nusModule;
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            nusModule = mapper.readValue(jsonString, NUSModuleMain.class);
            nusModuleFull = nusModule;

            if(mOnRefreshSpecificListener != null){
                mOnRefreshSpecificListener.onRefresh(nusModuleFull);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void refreshModulesDatabase(){
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, mBaseUrl + "moduleList.json", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("NUSmodsHelper refreshModulesDatabase success", response.toString());
                jsonString = response.toString();
                mapModuleDatabase(jsonString);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("NUSmodsHelper refreshModulesDatabase failure", error.toString());
            }
        });

        mRequestQueue.add(arrayRequest);
    }

    public void refreshSpecificModule(final String moduleCode){
        String url = completeModuleUrl(moduleCode);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                Log.d("NUSmodsHelper refreshSpecificModule success", response.toString());
                jsonString = response.toString();

                mapFullModule(jsonString);
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("NUSmodsHelper refreshSpecificModule failure", error.toString());
            }
        });

        mRequestQueue.add(objectRequest);
    }

    private String completeModuleUrl(String moduleCode){
        return mBaseUrl + "modules/" + moduleCode + ".json";
    }

    public void refreshSpecificModuleSpecial(String moduleCode, final String lessonType, final String classNo) {
        String url = completeModuleUrl(moduleCode);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                Log.d("NUSmodsHelper refreshSpecificModule success", response.toString());
                jsonString = response.toString();

                mapFullModuleSpecial(jsonString, lessonType, classNo);
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("NUSmodsHelper refreshSpecificModule failure", error.toString());
            }
        });

        mRequestQueue.add(objectRequest);
    }

    public void mapFullModuleSpecial(String jsonString, String lessonType, String classNo){
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NUSModuleMain nusModule;
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            nusModule = mapper.readValue(jsonString, NUSModuleMain.class);
            nusModuleFull = nusModule;

            if(mOnRefreshSpecificListener != null){
                mOnRefreshSpecificListener.onRefreshSpecial(nusModuleFull, lessonType, classNo);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
