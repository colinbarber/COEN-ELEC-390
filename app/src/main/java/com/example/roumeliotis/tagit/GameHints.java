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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameHints extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = "GameHints";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private TextView CountDown;
    private TextView UserNameText;
    private TextView TeamNameText;
    private SoundEffects gameWonSound;
    private SoundEffects gameLostSound;
    private ListView HintView;
    private NfcAdapter mNfcAdapter;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;

    private CountDownTimer countDownTimer;
    private ArrayList<NFCTag> tags;
    private Team team;
    private Game game;
    private String username;
    private ServerHelper server = new ServerHelper();
    private ArrayList<Long> hintsTagged = new ArrayList();

    private Handler mHandler;
    private Runnable refresh;
    private int REFRESH_TIME = 15000; //in milliseconds


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_hints);
        drawerLayout  = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        gameWonSound = new SoundEffects(this);
        gameLostSound = new SoundEffects(this);
        if(getSupportActionBar() != null){ getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        if(getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Add Nav Bar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if(true){
                            switch (menuItem.getItemId()) {
                                case R.id.JoinGameDM:
                                    Log.d(TAG, "JoinGame Menu Item Clicked");
                                    goToJoinGameActivity();
                                    drawerLayout.closeDrawers();
                                    return true;
                            }
                        }
                        return true;
                    }
                }
        );

        Intent intent = getIntent();
        tags = (ArrayList<NFCTag>) intent.getSerializableExtra("Hint");
        team = (Team) intent.getSerializableExtra("Team");
        game = intent.getParcelableExtra("Game");
        username = intent.getStringExtra("username");

        //Displaying User Name & Team Name/Colour on top of hints list
        UserNameText = findViewById(R.id.username_text);
        TeamNameText = findViewById(R.id.teamname_text);
        UserNameText.setText(username);  //USERNAME
        int teamColor=0;

        switch(team.getColour()){
            case "pink":
                teamColor= getResources().getColor(R.color.ColorPink);
                break;
            case "blue":
                teamColor= getResources().getColor(R.color.ColorBlue);
                break;
            case "purple":
                teamColor= getResources().getColor(R.color.ColorPurple);
                break;
            case "green":
                teamColor= getResources().getColor(R.color.ColorGreen);
                break;
            case "orange":
                teamColor= getResources().getColor(R.color.ColorOrange);
                break;
            case "red":
                teamColor= getResources().getColor(R.color.ColorRed);
                break;
            default:
                teamColor= getResources().getColor(R.color.colorWhite);
        }
        TeamNameText.setText(team.toString());
        TeamNameText.setTextColor(teamColor);

        HintView = findViewById(R.id.hint_list);
        CountDown = findViewById(R.id.count_down);
        HintView.setOnItemClickListener(this);
        //initialise count down
        startCountDown();

        //Set up auto refresh
        this.mHandler = new Handler();
        refresh = new Runnable() {
            @Override
            public void run() {
                fetchHintsSetList();
                mHandler.postDelayed(refresh, REFRESH_TIME);
            }
        };
        //start NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void goToJoinGameActivity(){
        Log.d(TAG, "Go to Join Game Activity");
        Intent intent = new Intent(GameHints.this, JoinGame.class);
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        fetchHintsSetList();
        this.mHandler.postDelayed(refresh, REFRESH_TIME);
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause(){
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
        this.mHandler.removeCallbacks(refresh);
    }

    public void fetchHintsSetList() {
        server.fetchTeamScore(team.getRemote_id(), GameHints.this, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("hints_id");
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        hintsTagged.add(Long.parseLong(jsonArray.get(i).toString()));
                    }
                    ArrayList<Long> alltagid = new ArrayList();
                    for(int i=0; i<tags.size(); i++){
                        alltagid.add(tags.get(i).getRemote_id());
                    }
                    if (hintsTagged.containsAll(alltagid)) {
                        if (countDownTimer!=null){
                            countDownTimer.cancel();
                        }
                        Intent intent = new Intent(GameHints.this, GameWon.class);
                        intent.putExtra("Game", game);
                        gameWonSound.playGameWinSound();
                        startActivity(intent);
                    }else{
                        HintView.setAdapter(new HintAdapter(GameHints.this, tags, hintsTagged));
                    }


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

    //set on click so that an item can be clicked
    public void onItemClick(AdapterView<?> hints, View v, int position, long id) {
        NFCTag hint = (NFCTag) hints.getItemAtPosition(position);
        Long hint_id = hint.getId();
        Intent intent = new Intent();
        intent.setClass(this, ScanHint.class);
        intent.putExtra("username", username);
        intent.putExtra("hint_id", hint_id.toString());
        intent.putExtra("Hint", hint);
        intent.putExtra("Team", team);
        intent.putExtra("Game", game);
        startActivity(intent);
    }

    //Handles countdown
    private void startCountDown() {
        //java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.roumeliotis.tagit/com.example.roumeliotis.tagit.GameHints}: java.lang.NullPointerException: Attempt to invoke virtual method 'long com.example.roumeliotis.tagit.Game.getTime_end()' on a null object reference
        long timeremainingatstart = game.getTime_end() - Calendar.getInstance().getTimeInMillis();

        if (timeremainingatstart > 0) {
            countDownTimer = new CountDownTimer(timeremainingatstart, 1000) {
                long days;
                long hours;
                long minutes;
                long seconds;

                public void onTick(long millisUntilFinished) {
                    days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                    hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                    minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                    seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                    CountDown.setText(new StringBuilder().append(days).append(":")
                            .append(hours).append(":")
                            .append(minutes).append(":")
                            .append(seconds).toString());
                }

                public void onFinish() { goToGameOver(); }
            };
            countDownTimer.start();
        } else {
            goToGameOver();
        }
    }

    private void goToGameOver() {
        gameLostSound.playLoseGameSound();
        Intent intent = new Intent();
        intent.setClass(this, GameOver.class);
        intent.putExtra("Game", game);
        startActivity(intent);
    }

    // remove back button functionality
    @Override
    public void onBackPressed() {
        if (!shouldAllowBack()) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }

    private Boolean shouldAllowBack() {
        return false;
    }

    /* *** Handle NFC Tagging *** */

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
