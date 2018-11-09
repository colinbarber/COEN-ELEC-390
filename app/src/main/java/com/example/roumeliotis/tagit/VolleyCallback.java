package com.example.roumeliotis.tagit;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}

//An interface that must be implemented when expecting a response from the server
//Functions onSuccess and onError will be called after the server responds and must be overridden
/* Typical instantiation:

new VolleyCallback(){
    @Override
    public void onSuccess(JSONObject response){
        //DO STUFF HERE e.g.
        mVariable = response.getString("field");
        intent.putExtra("DATA", data);
    }
    @Overrode
    public void onError(VolleyError response){
        //Handle the error
    }
}

 */