package com.example.roumeliotis.tagit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class GameWon extends AppCompatActivity {

    public static final String TAG = "GameWon";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcAdapter mNfcAdapter;
    private ServerHelper server = new ServerHelper();
    private Team team;
    private Game game;
    GameManager gameManager;
    ListView winningTeams;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_won);

        Intent intent = getIntent();
        game = intent.getParcelableExtra("Game");

        gameManager = new GameManager(this);
        winningTeams = findViewById(R.id.winningTeamsOver);

        //start NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
    }

    /* *** Handle NFC Tagging *** */

    protected void onResume(){
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
        setWinningTeams();
    }

    protected void onPause(){
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

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
                handleNFC(result);
            }
        }
    }

    protected void handleNFC(String result){
        // does nothing on this page
    }

    // remove back button functionality
    @Override
    public void onBackPressed() {
        if (!shouldAllowBack()) {
            // do nothing
        } else {
            Intent intent = new Intent(GameWon.this, MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    private Boolean shouldAllowBack() {
        return true;
    }

    public void setWinningTeams() {
        server.getTeamRanking(game.getRemote_id(), GameWon.this, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("winner_ids");
                    String team_arr[] = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        team_arr[i] = gameManager.getTeamByRemoteID(jsonArray.getLong(i)).toString();
                    }
                    ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(GameWon.this, R.layout.spinner_item, team_arr);
                    winningTeams.setAdapter(itemsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            @Override
            public void onError(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
