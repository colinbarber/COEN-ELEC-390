package com.example.roumeliotis.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameHints extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private TextView CountDown;
    private ListView HintView;
    private CountDownTimer countDownTimer;
    private ArrayList<NFCTag> tags;
    private Team team;
    private Game game;
    private ServerHelper server = new ServerHelper();
    ArrayList<Long> hintsTagged = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_hints);

        Intent intent = getIntent();
        tags = (ArrayList<NFCTag>) intent.getSerializableExtra("Hint");
        team = (Team) intent.getSerializableExtra("Team");
        game = (Game) intent.getParcelableExtra("Game");

        HintView = (ListView) findViewById(R.id.hint_list);
        CountDown = (TextView) findViewById(R.id.count_down);
        HintView.setOnItemClickListener(this);

        //inicialise count down
        startCountDown();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setHintsInListView();
    }

    public void fetchHints(){
        server.fetchTeamScore(team.getId(),GameHints.this, new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hints_id");
                            if (jsonArray != null) {
                                int len = jsonArray.length();
                                for (int i=0;i<len;i++){
                                    hintsTagged.add(Long.parseLong(jsonArray.get(i).toString()));
                                }
                            }
                            if(hintsTagged.containsAll(tags)){
                                countDownTimer.cancel();
                                Intent intent = new Intent(GameHints.this, GameWon.class);
                                startActivity(intent);
                            }
                            // TODO display all hints that have been found in a textview
                            

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

    public void setHintsInListView(){
        List<NFCTag> hints = tags;
        ArrayAdapter<NFCTag> adapter = new ArrayAdapter<NFCTag>(this,
                android.R.layout.simple_list_item_1, hints);
        HintView.setAdapter(adapter);
    }

    //starts countdown
    private void startCountDown(){
        long currenttime = Calendar.getInstance().getTimeInMillis();
        long timeremainingatstart = game.getTime_end() - currenttime;

        if(timeremainingatstart > 0) {
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
        }else{
            goToGameOver();
        }
    }

    private void goToGameOver() {
        Intent intent = new Intent();
        intent.setClass(this, GameOver.class);
        startActivity(intent);
    }
}
