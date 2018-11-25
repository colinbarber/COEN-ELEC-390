package com.example.roumeliotis.tagit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JoinGame extends AppCompatActivity {

    private final String TAG = "joinGameActivity";
    EditText getGameEditText;
    Button getGameButton;
    TextView gameDisplayTextView;
    Game mGame;
    ServerHelper serverHelper;
    GameManager gameManager;
    String gameText;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        getGameEditText = findViewById(R.id.enter_game_name_edittext);
        getGameButton = findViewById(R.id.get_game_button);
        gameDisplayTextView = findViewById(R.id.game_display_textview);
        serverHelper = new ServerHelper();
        gameManager = new GameManager(this);

        gameText="";

        getGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getGameEditText.getText().toString().trim();
                if(name.equals("")){
                    LayoutInflater inflater = getLayoutInflater();
                    View layoutToast = inflater.inflate(R.layout.toast,
                            (ViewGroup) findViewById(R.id.toast_layout));
                    TextView textToast = layoutToast.findViewById(R.id.toast_text);
                    textToast.setText("Enter a Game Name");
                    Toast toastEmptyName = new Toast(getApplicationContext());
                    toastEmptyName.setGravity(Gravity.CENTER, 0, 0);
                    toastEmptyName.setDuration(Toast.LENGTH_SHORT);
                    toastEmptyName.setView(layoutToast);
                    getGameEditText.startAnimation(shakeError());
                    toastEmptyName.show();
                }
                else{
                    serverHelper.fetchGame(name, getApplicationContext(), new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                mGame = gameManager.getGameByRemoteID(response.getLong("game_id"));
                                gameText+= mGame.getName() +"\n" +
                                        mGame.getUsername() +"\n"+
                                        new Date(mGame.getTime_end()).toString() + "\n\n";

                                List<Team> teams= gameManager.getTeamsByGameID(mGame.getId());
                                for(int i = 0; i< teams.size(); i++){
                                    gameText += teams.get(i).getName() + "\t" + teams.get(i).getColour() + "\n";
                                }

                                gameText+="\n\n";

                                List<NFCTag> tags = gameManager.getTagsByGameID(mGame.getId());
                                for (int i = 0; i<tags.size(); i++){
                                    gameText+= tags.get(i).getHint()+"\n";
                                }

                                goToSignInActivity(mGame, teams, tags);

                            } catch(JSONException e){
                                e.printStackTrace();
                                Toast toast=Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            LayoutInflater inflater = getLayoutInflater();
                            View layoutToast = inflater.inflate(R.layout.toast,
                                    (ViewGroup) findViewById(R.id.toast_layout));
                            TextView textToast = layoutToast.findViewById(R.id.toast_text);
                            textToast.setText("Invalid Game ID");
                            Toast toastWrongName = new Toast(getApplicationContext());
                            toastWrongName.setGravity(Gravity.CENTER, 0, 0);
                            toastWrongName.setDuration(Toast.LENGTH_SHORT);
                            toastWrongName.setView(layoutToast);
                            getGameEditText.startAnimation(shakeError());
                            toastWrongName.show();
                        }
                    });
                }
            }
        });
        //start NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
    }

    //method to switch activity
    void goToSignInActivity(Game game, List<Team> team, List<NFCTag> tags) {
        Intent intent = new Intent();
        intent.setClass(JoinGame.this, SignInActivity.class);
        intent.putExtra("Game", mGame);
        intent.putExtra("Team", (Serializable) team);
        intent.putExtra("Hint", (Serializable) tags);
        startActivity(intent);
    }
    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    /* *** Handle NFC Tagging *** */

    protected void onResume(){
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
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
}
