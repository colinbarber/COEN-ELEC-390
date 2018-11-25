package com.example.roumeliotis.tagit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                    LayoutInflater inflater = getLayoutInflater();
                    View layoutToast = inflater.inflate(R.layout.toast,
                            (ViewGroup) findViewById(R.id.toast_layout));
                    TextView textToast = (TextView) layoutToast.findViewById(R.id.toast_text);
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

                                gameDisplayTextView.setText(gameText);
                                gameDisplayTextView.setVisibility(View.VISIBLE);

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
                            TextView textToast = (TextView) layoutToast.findViewById(R.id.toast_text);
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
    }

    //method to switch activity
    void goToSignInActivity(Game game, List<Team> team, List<NFCTag> tags)
    {
        /*Intent intent = new Intent(this, SignInActivity.class);

        intent.putExtra("Game_id", Long.toString(game.getId()));
        intent.putExtra("Game_remote_id", Long.toString(game.getRemote_id()));
        intent.putExtra("Game_username", game.getUsername());
        intent.putExtra("Game_name", game.getName());
        intent.putExtra("Game_time_end", Long.toString(game.getTime_end()));*/

        Intent intent = new Intent();
        intent.setClass(JoinGame.this, SignInActivity.class);
        intent.putExtra("Game", (Parcelable) mGame);
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
}
