package com.example.calendarmanagerbeta;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class NUSmodsHelper {
    private static NUSmodsHelper INSTANCE = null;
    private final RequestQueue mRequestQueue;
    private final Context mContext;
    private final String mBaseUrl;

    private NUSmodsHelper(Context context){
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mBaseUrl = "https://api.nusmods.com/v2";
    }

    public static synchronized NUSmodsHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new NUSmodsHelper(context);
        }
        return INSTANCE;
    }

    private String constructUrl(String method){
        return mBaseUrl + "/" + method;
    }

    // As NUSmods only has GET requests, implementation of other HTTP requests are not needed.
    public void get(String method, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, constructUrl(method), jsonRequest, listener, errorListener);
        mRequestQueue.add(objectRequest);
    }
}
