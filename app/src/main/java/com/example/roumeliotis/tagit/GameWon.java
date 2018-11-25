package com.example.roumeliotis.tagit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class GameWon extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_won);
    }
    //disable back button
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Its Over, You Won!", Toast.LENGTH_SHORT).show();
    }

}
