package com.example.roumeliotis.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameHints extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TextView CountDown;
    private ListView HintView;
    private FloatingActionButton update;

    private CountDownTimer countDownTimer;
    private ArrayList<NFCTag> tags;
    private Team team;
    private Game game;
    private ServerHelper server = new ServerHelper();
    private ArrayList<Long> hintsTagged = new ArrayList();

    private Handler mHandler;
    private Runnable refresh;
    private int REFRESH_TIME = 15000; //in milliseconds


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_hints);

        Intent intent = getIntent();
        tags = (ArrayList<NFCTag>) intent.getSerializableExtra("Hint");
        team = (Team) intent.getSerializableExtra("Team");
        game = intent.getParcelableExtra("Game");

        update = findViewById(R.id.button);
        HintView = findViewById(R.id.hint_list);
        CountDown = findViewById(R.id.count_down);
        HintView.setOnItemClickListener(this);

        //initialise count down
        startCountDown();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchHintsSetList();
            }
        });


        //Set up auto refresh
        this.mHandler = new Handler();
        refresh = new Runnable() {
            @Override
            public void run() {
                fetchHintsSetList();
                mHandler.postDelayed(refresh, REFRESH_TIME);
            }
        };

    }

    @Override
    protected void onResume(){
        super.onResume();
        fetchHintsSetList();
        this.mHandler.postDelayed(refresh, REFRESH_TIME);
    }

    @Override
    protected void onPause(){
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
                        countDownTimer.cancel();
                        Intent intent = new Intent(GameHints.this, GameWon.class);
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
        Intent intent = new Intent();
        intent.setClass(this, ScanHint.class);
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

                public void onFinish() {
                    goToGameOver();
                }
            };
            countDownTimer.start();
        } else {
            goToGameOver();
        }
    }

    private void goToGameOver() {
        Intent intent = new Intent();
        intent.setClass(this, GameOver.class);
        startActivity(intent);
    }
}
