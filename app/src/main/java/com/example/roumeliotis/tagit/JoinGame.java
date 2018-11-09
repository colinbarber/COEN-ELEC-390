package com.example.roumeliotis.tagit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JoinGame extends AppCompatActivity {

    //TODO PoC, can and should be made to look better
    private final String TAG = "joinGameActivity";
    EditText getGameEditText;
    Button getGameButton;
    TextView gameDisplayTextView;
    Game mGame;
    ServerHelper serverHelper;
    GameManager gameManager;
    String gameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        Intent intent = getIntent();

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
                    Toast toast=Toast.makeText(getApplicationContext(),"Enter a game name",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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

                                gameDisplayTextView.setText(gameText);
                                gameDisplayTextView.setVisibility(View.VISIBLE);

                            } catch(JSONException e){
                                e.printStackTrace();
                                Toast toast=Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {

                        }
                    });
                }
            }
        });
    }
}
