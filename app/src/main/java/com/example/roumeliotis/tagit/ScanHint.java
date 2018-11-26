package com.example.roumeliotis.tagit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.SoundPool;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ScanHint extends AppCompatActivity {

    public static final String TAG = "ScanHint";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private ServerHelper server = new ServerHelper();
    private SoundEffects tagHitSound;
    private SoundEffects tagFoundSound;
    private NFCTag hint;
    private Team team;
    private Game game;
    private NfcAdapter mNfcAdapter;
    private TextView hintView;
    private GameManager gm;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_hint);
//        Toolbar mytoolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mytoolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gm = new GameManager(ScanHint.this);

        //Sound Effects
        tagHitSound = new SoundEffects(this);
        tagFoundSound = new SoundEffects(this);

        Intent intent = getIntent();
        hint = (NFCTag) intent.getSerializableExtra("Hint");
        team = (Team) intent.getSerializableExtra("Team");
        game = (Game) intent.getParcelableExtra("Game");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        hintView = findViewById(R.id.hintView);
        if (hint != null) {
            hintView.setText(hint.toString());
        }

    }
    protected void onResume(){
        super.onResume();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        setupForegroundDispatch(this, mNfcAdapter);
    }

    protected void onPause(){
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    protected void handleNFC(String result){
        server.pushTeamScore(team.getRemote_id(), Long.parseLong(result), ScanHint.this, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d(TAG,response.toString());
                    String message = response.getString("message");
                    if("tag added".equals(message)){
                        Intent intent = new Intent(ScanHint.this, GameHints.class);
                        intent.putExtra("Game", (Parcelable) game);
                        intent.putExtra("Team", (Serializable) team);
                        intent.putExtra("Hint", (Serializable) gm.getTagsByGameID(game.getId()));
                        tagHitSound.playTagHitSound();
                        startActivity(intent);
                    }
                    else if(message.equals("tag already found")){

                        LayoutInflater inflater = getLayoutInflater();
                        View layoutToast = inflater.inflate(R.layout.toast,
                                (ViewGroup) findViewById(R.id.toast_layout));
                        TextView textToast = (TextView) layoutToast.findViewById(R.id.toast_text);
                        textToast.setText("Tag Already Found");
                        Toast tagFoundToast = new Toast(getApplicationContext());
                        tagFoundToast.setGravity(Gravity.CENTER, 0, 0);
                        tagFoundToast.setDuration(Toast.LENGTH_SHORT);
                        tagFoundToast.setView(layoutToast);
                        tagFoundToast.show();
                        tagFoundSound.playAlreadyTaggedSound();
                    }
                    else{

                        LayoutInflater inflater = getLayoutInflater();
                        View layoutToast = inflater.inflate(R.layout.toast,
                                (ViewGroup) findViewById(R.id.toast_layout));
                        TextView textToast = (TextView) layoutToast.findViewById(R.id.toast_text);
                        textToast.setText("Wrong Tag!");
                        Toast wrongTagToast = new Toast(getApplicationContext());
                        wrongTagToast.setGravity(Gravity.CENTER, 0, 0);
                        wrongTagToast.setDuration(Toast.LENGTH_SHORT);
                        wrongTagToast.setView(layoutToast);
                        wrongTagToast.show();
                    }

                } catch(JSONException e){
                    e.printStackTrace();
                    Toast toast=Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            @Override
            public void onError(VolleyError error) {
                Toast toast=Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
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
        Log.d(TAG,"setupForegroundDispatch()");
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
        Log.d(TAG,"setupForegroundDispatch()");
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
                new ScanHint.NdefReaderTask().execute(tag);
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
}
