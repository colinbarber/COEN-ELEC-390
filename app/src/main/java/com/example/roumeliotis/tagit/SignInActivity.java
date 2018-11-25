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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity{

    public static final String TAG = "SignInActivity";
    EditText EditUser;
    TextView TeamText;
    TextView PlayerDisplay;
    private Spinner TeamSpinner;
    Button SaveButton = null;
    Game mGame;
    String mUsername;
    String mTeam;
    GameManager gameManager;
    String PlayerText;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private List<Team> teams;
    private Team myTeam;
    private Game game;
    private List<NFCTag> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        EditUser = (EditText) findViewById(R.id.username_edit_text);
        SaveButton = findViewById(R.id.savebtn);
        TeamText = (TextView) findViewById(R.id.team_text);
        PlayerDisplay = (TextView) findViewById(R.id.player_display);
        TeamSpinner = (Spinner) findViewById(R.id.team_spinner);
        gameManager = new GameManager(this);


        //get Game from previous screen
        //Bundle
        Intent GameInfo = getIntent();
        if(GameInfo != null){
            game = GameInfo.getParcelableExtra("Game");
            teams = (List<Team>) GameInfo.getSerializableExtra("Team");
            tags = (List<NFCTag>) GameInfo.getSerializableExtra("Hint");

            long id = game.getId();
            long remote_id = game.getRemote_id();
            String username = game.getUsername();
            String name = game.getName();
            long  time_end = GameInfo.getLongExtra("end_time", 0);

            mGame = new Game(id, remote_id, username, name, time_end);
        }

        //get teams for spinner
        /*List<Team> TeamList= gameManager.getTeamsByGameID(mGame.getId());
        final String[] teams = new String[TeamList.size()];
        for(int i = 0; i< TeamList.size(); i++){
            teams[i] = TeamList.get(i).getName();
        }*/


        //set up spinner
       // ArrayAdapter<Team> adapter = new ArrayAdapter<Team>(this,
         //       android.R.layout.simple_spinner_item, teams);

    ArrayAdapter<Team> adapter = new ArrayAdapter<Team>(this, R.layout.spinner_item, teams);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        TeamSpinner.setAdapter(adapter);
        TeamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myTeam = (Team) parent.getItemAtPosition(position);
                Log.d(TAG, "Team " + myTeam.toString() + " selected");
                //log when team selected
                /*// TODO make it work for any given number of teams that the creator wants
                switch (position) {
                    case 0:
                        Log.d(TAG, "Team " + teams[0].toString() + " selected");
                        break;
                    case 1:
                        Log.d(TAG, "Team " + teams[1].toString() + " selected");
                        break;
                    case 2:
                        Log.d(TAG, "Team " + teams[2].toString() + " selected");
                        break;
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save Button Clicked");
                mUsername = EditUser.getText().toString();
                if (mUsername.equals("")){
                    LayoutInflater inflater = getLayoutInflater();
                    View layoutToast = inflater.inflate(R.layout.toast,
                            (ViewGroup) findViewById(R.id.toast_layout));
                    TextView textToast = (TextView) layoutToast.findViewById(R.id.toast_text);
                    textToast.setText("Enter a Username");
                    Toast toastEmptyUsername = new Toast(getApplicationContext());
                    toastEmptyUsername.setGravity(Gravity.CENTER, 0, 0);
                    toastEmptyUsername.setDuration(Toast.LENGTH_SHORT);
                    toastEmptyUsername.setView(layoutToast);
                    EditUser.startAnimation(shakeError());
                    toastEmptyUsername.show();
                }
                else{
                    mTeam = TeamSpinner.getSelectedItem().toString();

                    PlayerText = "Username : " + mUsername + "\nTeam : " + mTeam;
                    PlayerDisplay.setText(PlayerText);
                    PlayerDisplay.setVisibility(View.INVISIBLE);
                    //use when next activity is done
                    goToNextActivity();
                }

            }
        });
        //start NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
    }

    //to use with next activity
    void goToNextActivity()
    {
        Intent intent = new Intent(this, SignInActivity.class);

        /*
        //pass game
        intent.putExtra("Game_id", Long.toString(game.getId()));
        intent.putExtra("Game_remote_id", Long.toString(game.getRemote_id()));
        intent.putExtra("Game_username", game.getUsername());
        intent.putExtra("Game_name", game.getName());
        intent.putExtra("Game_time_end", Long.toString(game.getTime_end()));

        //pass player information
        intent.putExtra("Player_username", username);
        intent.putExtra("Player_team", team_name);
        */
        intent.setClass(this, GameHints.class);
        intent.putExtra("Game", (Parcelable) game);
        intent.putExtra("Team", (Serializable) myTeam);
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
