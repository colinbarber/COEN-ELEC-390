package com.example.roumeliotis.tagit;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ServerHelper {

    private static ServerHelper INSTANCE = null;
    private static Context mContext;

    public static final String TAG = "ServerHelper";
    private static final String base_url = "https://coen390-a-team.herokuapp.com";
    RequestQueue mQueue;

    private ServerHelper(Context context){
        mContext = context;
        mQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if (mQueue == null)
            mQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        return mQueue;
    }

    public static synchronized ServerHelper getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new ServerHelper(context);
        return INSTANCE;
    }

    public Game getGame(String gameName){
        Log.d(TAG, "getGame");
        Game game = null;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,base_url+gameName,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                game = new Game(-1,response.getLong("game_id"),);
            }
        },new Response.ErrorListener(){
            @Override
            public  void onErrorResponse(VolleyError error){
                Log.d(TAG,error.toString());
            }
        } );
    }
}
