package com.example.coronatracker.activity;

import android.os.Bundle;

import com.example.coronatracker.support.MyAdapter;
import com.example.coronatracker.R;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ListView;

import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    ListView listDate;
    Map<String, String> history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listDate = (ListView) findViewById(R.id.listDate);
        history = new Gson().fromJson((String) getIntent().getSerializableExtra("history"),Map.class);
        MyAdapter adapter = new MyAdapter(history);
        listDate.setAdapter(adapter);

    }

}
