package com.example.roumeliotis.tagit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GameHints extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView HintView;
    private ArrayList<NFCTag> tags;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_hints);

        Intent intent = getIntent();
        tags = (ArrayList<NFCTag>) intent.getSerializableExtra("Hint");

        HintView = (ListView) findViewById(R.id.hint_list);
        HintView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setHintsInListView();
    }

    //set on click so that an item can be clicked
    public void onItemClick(AdapterView<?> hints, View v, int position, long id) {
        NFCTag hint = (NFCTag) hints.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.setClass(this, ScanHint.class);
        intent.putExtra("Hint", hint);
        //TODO intent.putExtra("Team", team);
        startActivity(intent);
    }

    public void setHintsInListView(){
        List<NFCTag> hints = tags;
        ArrayAdapter<NFCTag> adapter = new ArrayAdapter<NFCTag>(this,
                android.R.layout.simple_list_item_1, hints);
        HintView.setAdapter(adapter);
    }
}
