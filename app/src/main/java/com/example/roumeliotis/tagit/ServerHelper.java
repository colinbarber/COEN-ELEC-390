package com.example.roumeliotis.tagit;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerHelper {

    public static final String TAG = "ServerHelper";
    private static final String base_url = "https://coen390-a-team.herokuapp.com/";

    public void fetchGame(final String gameName, final Context context, final VolleyCallback callback){
        /*
        Fetches game from server and adds relevant components to SQLite db
        callback can be used for further manipulation of response
         */

        Log.d(TAG, "fetchGame");

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,base_url+"game/"+gameName,null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                try {
                    Game game = new Game(-1, response.getLong("game_id"), "Username", gameName,0 ); //TODO ADD USERNAME CALL AND ENDTIME CALLS WHEN SERVER RESPONSE IS CHANGED
                    JSONArray jsonTeamIds = response.getJSONArray("team_ids");
                    JSONArray jsonTeamNames = response.getJSONArray("team_names");
                    JSONArray jsonTeamColours = response.getJSONArray("team_colours");
                    JSONArray jsonHints = response.getJSONArray("hints");
                    JSONArray jsonTagIds = response.getJSONArray("tag_ids");

                    GameManager manager = new GameManager(context);
                    long game_id = manager.insertGame(game);

                    for(int i = 0; i < jsonTeamIds.length(); i++){
                        manager.insertTeam(new Team(-1, game_id, jsonTagIds.getLong(i),jsonTeamNames.getString(i),jsonTeamColours.getString(i)));
                    }

                    for (int i = 0; i < jsonTagIds.length(); i++){
                        manager.insertTag(new NFCTag(-1, jsonTagIds.getLong(i), game_id, jsonHints.getString(i)));
                    }

                    callback.onSuccess(response);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d(TAG,error.toString());
                callback.onError(error);
            }
        } );

        Log.d(TAG, "request: " + request.toString());
        VolleySingleton.getInstance(context).addToQueue(request);


    }


    public void fetchTeamScore(final long remote_id, final Context context, final VolleyCallback callback){
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, base_url + "team_score/" + remote_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    GameManager gm = new GameManager(context);
                    JSONArray hints_arr = response.getJSONArray("hints_id");
                    for (int i = 0; i < hints_arr.length(); i++) {
                        NFCTag nfcTag = gm.getTagByRemoteID(hints_arr.getLong(i));
                        nfcTag.markPoint();
                        gm.updateTagPoints(nfcTag);
                    }
                    callback.onSuccess(response);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d(TAG,error.toString());
                callback.onError(error);
            }
        } );

        Log.d(TAG, "request: " + request.toString());
        VolleySingleton.getInstance(context).addToQueue(request);
    }

    public void pushTeamScore(final long team_remote_id, final long tag_remote_id, final Context context, final VolleyCallback callback){
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, base_url + team_remote_id + "/" + tag_remote_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });

        Log.d(TAG, "request: " + request.toString());
        VolleySingleton.getInstance(context).addToQueue(request);
    }
}
