package com.example.calendarmanagerbeta;

import android.content.Context;
import android.util.Log;


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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class NUSmodsHelper {
    private static NUSmodsHelper INSTANCE = null;
    private final RequestQueue mRequestQueue;
    private final Context mContext;
    private final String mBaseUrl;
    private final String mUrl1;
    private String jsonString;
    private List<NUSModuleLite> nusModulesLite;
    private NUSModuleMain nusModuleFull;
    boolean debug = true;

    private NUSmodsHelper(Context context){
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mBaseUrl = "https://api.nusmods.com/v2/2019-2020/";
        mUrl1 = "https://api.nusmods.com/v2/2019-2020/moduleList.json";
    }

    public static synchronized NUSmodsHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new NUSmodsHelper(context);
        }
        return INSTANCE;
    }

    public void refreshModulesDatabase(){
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, mBaseUrl + "moduleList.json", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("NUSmodsHelper refreshModulesDatabase success", response.toString());
                jsonString = response.toString();

                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try{
                    nusModulesLite = Arrays.asList(mapper.readValue(jsonString, NUSModuleLite[].class));

                    // Debugging purposes:
                    if(debug == true){
                        for(NUSModuleLite numModules : nusModulesLite){
                            System.out.println(numModules.getModuleCode() + " " + numModules.getTitle());
                        }
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("NUSmodsHelper refreshModulesDatabase failure", error.toString());
            }
        });

        mRequestQueue.add(arrayRequest);
    }

    public void refreshSpecificModule(String moduleCode){
        String url = completeModuleUrl(moduleCode);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                Log.e("NUSmodsHelper refreshSpecificModule success", response.toString());
                jsonString = response.toString();

                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
                mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
                mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try{
                    nusModuleFull = mapper.readValue(jsonString, NUSModuleMain.class);

                    // Debugging purposes:
                    if(debug == true){
                        System.out.println(nusModuleFull.getTitle());
                        System.out.println(nusModuleFull.getModuleCode());
                        System.out.println(nusModuleFull.getDepartment());
                        System.out.println(nusModuleFull.getFaculty());
                        System.out.println(nusModuleFull.getSemesterData().get(1).getTimetable().get(0).getVenue());
                        System.out.println(nusModuleFull.getSemesterData().get(1).getTimetable().get(0).getClassNo());
                        System.out.println(nusModuleFull.getSemesterData().get(1).getTimetable().get(0).getDay());
                        System.out.println(nusModuleFull.getSemesterData().get(1).getTimetable().get(0).getStartTime());
                        System.out.println(nusModuleFull.getSemesterData().get(1).getTimetable().get(0).getEndTime());
                        System.out.println(nusModuleFull.getSemesterData().get(1).getTimetable().get(0).getLessonType());
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
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
}
