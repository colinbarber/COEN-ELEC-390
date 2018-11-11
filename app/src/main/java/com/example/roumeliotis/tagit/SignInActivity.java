package com.example.roumeliotis.tagit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class SignInActivity extends AppCompatActivity {

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
        Bundle GameInfo = getIntent().getExtras();
        if(GameInfo != null){
            long id = Long.parseLong(GameInfo.getString("Game_id"));
            long remote_id = Long.parseLong(GameInfo.getString("Game_remote_id"));
            String username = GameInfo.getString("Game_username");
            String name = GameInfo.getString("Game_name");
            long  time_end = Long.parseLong(GameInfo.getString("Game_time_end"));

            mGame = new Game(id, remote_id, username, name, time_end);
        }

        //get teams for spinner
        List<Team> TeamList= gameManager.getTeamsByGameID(mGame.getId());
        final String[] teams = new String[TeamList.size()];
        for(int i = 0; i< TeamList.size(); i++){
            teams[i] = TeamList.get(i).getName();
        }

        //set up spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, teams);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TeamSpinner.setAdapter(adapter);
        TeamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //log when team selected
                switch (position) {
                    case 0:
                        Log.d(TAG, "Team " + teams[0] + " selected");
                        break;
                    case 1:
                        Log.d(TAG, "Team " + teams[1] + " selected");
                        break;
                    case 2:
                        Log.d(TAG, "Team " + teams[2] + " selected");
                        break;
                }
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
                mTeam = TeamSpinner.getSelectedItem().toString();

                PlayerText = "Username : " + mUsername + "\nTeam : " + mTeam;
                PlayerDisplay.setText(PlayerText);
                PlayerDisplay.setVisibility(View.VISIBLE);

                //use when next activity is done
                //goToNextActivity(mGame, mUsername, mTeam);
            }
        });
    }

    //to use with next activity
    void goToNextActivity(Game game, String username, String team_name)
    {
        Intent intent = new Intent(this, SignInActivity.class);

        //pass game
        intent.putExtra("Game_id", Long.toString(game.getId()));
        intent.putExtra("Game_remote_id", Long.toString(game.getRemote_id()));
        intent.putExtra("Game_username", game.getUsername());
        intent.putExtra("Game_name", game.getName());
        intent.putExtra("Game_time_end", Long.toString(game.getTime_end()));

        //pass player information
        intent.putExtra("Player_username", username);
        intent.putExtra("Player_team", team_name);
        startActivity(intent);
    }


}
