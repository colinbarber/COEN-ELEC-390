package com.example.roumeliotis.tagit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    protected Button joinGameButton;
    protected Button createGameButton;
    private final String baseURL = "https://coen390-a-team.herokuapp.com/";
    private TextView hint;
    private String url;
    private NfcAdapter mNfcAdapter;
    private Boolean gameStart = false;
    private GameManager gameManager;
    private ServerHelper serverHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hint = findViewById(R.id.hint);
        gameManager = new GameManager(this);

        serverHelper = new ServerHelper();

        joinGameButton = findViewById(R.id.joinGameButton);
        createGameButton = findViewById(R.id.createGameButton);
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "joinGameButtonOnClick");
                //TODO
                serverHelper.fetchGame("bitch", getApplicationContext(), new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d(TAG,"response: " + response.toString());
                    }

                    @Override
                    public void onError(VolleyError response) {
                        Log.d(TAG, "response: " + response.toString());
                    }
                });
                //goToJoinGameActivity();
            }
        });
        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "createGameButtonOnClick");
                goToCreateGameActivity();
            }
        });


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //Ensure that device is nfc compatible
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();

            ///////////////// Closes app if not NFC compatible ///////////////////
            //finish();
            //return;
        }

        handleIntent(getIntent());
    }
    void goToJoinGameActivity(){
        Log.d(TAG, "Go to Join Game Activity");
        Intent intent = new Intent(MainActivity.this, JoinGame.class);
        startActivity(intent);
    }
    void goToCreateGameActivity(){
        Log.d(TAG, "Go to Create Game Activity");
        Intent intent = new Intent(MainActivity.this, CreateGame.class);
        startActivity(intent);
    }

    protected void onResume(){
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    protected void onPause(){
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    //TODO REMOVE
//    public void hintGET(String url){
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                url,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    gameStart = true;
//                    Toast toast=Toast.makeText(getApplicationContext(),"New Game started!",Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                    Log.d(TAG, "hintGET response: "+response);
//                    hint.setText(response.get("hint").toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener(){
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG,error.toString());
//            }
//        });
//        queue.add(jsonObjectRequest);
//    }
//
//    public void nextHintGET(String url){
//        JSONObject jsonsend = new JSONObject();
//        if (gameStart) {
//            try {
//                jsonsend.put("gameId", gameId);
//                jsonsend.put("tags", new JSONArray(tagList));
//                Log.e(TAG, jsonsend.toString());
//            } catch (JSONException e) {
//                Log.d(TAG, e.toString());
//            }
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                    url, jsonsend, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        Log.d(TAG, "hintGET response: " + response);
//                        String res = response.get("hint").toString();
//                        if ("BAD_TAG".equals(res)) {
//                            Toast toast = Toast.makeText(getApplicationContext(), "Wrong tag, try again :(", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            tagList.remove(tagList.size() - 1);
//                        }
//                        else if ("CONGRATULATIONS! YOU WIN".equals(res)){
//                            Toast toast = Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            hint.setText(res);
//                            gameStart = false;
//                        }
//                        else {
//                            Toast toast = Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            hint.setText(res);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d(TAG, error.toString());
//                }
//            });
//            queue.add(jsonObjectRequest);
//        }
//    }

    @Override
    protected void onNewIntent(Intent intent){
        //Prevent the intent found by scanning the nfc tag forcing open a new activity
        handleIntent(intent);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    protected void handleIntent(Intent intent){
        String action = intent.getAction();
        Log.d(TAG,"handleIntent action:" + action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            Log.d(TAG,"handleIntent type:" + type);

            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
    }


    //Inner class that allows async handling of nfc tag reading asynchronously
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        static final String TAG = "NdefReaderTask";

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                Log.d(TAG,"Ndef not supported by this tag");
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG,"Result: "+result);
                //TODO
            }
        }
    }
}
