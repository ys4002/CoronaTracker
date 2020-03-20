package com.example.coronatracker.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.coronatracker.support.FileUtil;
import com.example.coronatracker.R;
import com.example.coronatracker.modelInfected.JsonData;
import com.example.coronatracker.modelInfected.Location;
import com.google.gson.Gson;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> countries;
    private JsonData jsonData;
    private List<Location> locations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonData = new Gson().fromJson(FileUtil.readFromFile(getApplicationContext(),"jsonData.txt"), JsonData.class);
        locations = jsonData.getConfirmed().getLocations();
        list = (ListView) findViewById(R.id.list);
        countries = (ArrayList<String>) getIntent().getSerializableExtra("countries");
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, countries);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                String[] data = item.split("-");
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                if(data.length ==2) {
                    Location current = locations.stream().filter(p -> p.getCountry().equalsIgnoreCase(data[0]) && p.getProvince().equalsIgnoreCase(data[1])).findFirst().get();
                    intent.putExtra("history", new Gson().toJson(current.getHistory()));
                }
                else {
                    Location current = locations.stream().filter(p -> p.getCountry().equalsIgnoreCase(data[0])).findFirst().get();
                    intent.putExtra("history", new Gson().toJson(current.getHistory()));

                }
                startActivity(intent);
            }

        });
    }

}
