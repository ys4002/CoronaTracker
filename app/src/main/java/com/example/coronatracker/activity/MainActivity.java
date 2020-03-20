package com.example.coronatracker.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.coronatracker.support.FileUtil;
import com.example.coronatracker.R;
import com.example.coronatracker.modelInfected.JsonData;
import com.example.coronatracker.modelInfected.Location;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerCountry;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> countries;
    private JsonData jsonData;
    private List<Location> locations;
    BarChart barChart;
    TreeMap<Date, String> sortedMap = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonData = new Gson().fromJson(FileUtil.readFromFile(getApplicationContext(), "jsonData.txt"), JsonData.class);
        locations = jsonData.getConfirmed().getLocations();
        spinnerCountry = (Spinner) findViewById(R.id.spinnerCountry);
        barChart = (BarChart) findViewById(R.id.barGraph);


        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        countries = (ArrayList<String>) getIntent().getSerializableExtra("countries");
        Collections.sort(countries);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);
        if(countries.contains("India")) {
            int spinnerPosition = adapter.getPosition("India");
            spinnerCountry.setSelection(spinnerPosition);
        }

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country = (String) adapterView.getItemAtPosition(i);
                String[] countryArray = country.split("-");
                if (countryArray.length == 2) {
                    Location current = locations.stream().filter(p -> p.getCountry().equalsIgnoreCase(countryArray[0]) && p.getProvince().equalsIgnoreCase(countryArray[1])).findFirst().get();
                    try {
                        sortedMap = FileUtil.formatKey(current.getHistory());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Location current = locations.stream().filter(p -> p.getCountry().equalsIgnoreCase(countryArray[0])).findFirst().get();
                    try {
                        sortedMap = FileUtil.formatKey(current.getHistory());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                ArrayList<String> dates = new ArrayList<>();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                int j = 1;
                int max =0;
                for (Map.Entry<Date, String> map : sortedMap.entrySet()) {
                    max = Integer.valueOf(map.getValue())>max ? Integer.valueOf(map.getValue()) : max;
                    dates.add(formatter.format(map.getKey()));
                    barEntries.add(new BarEntry(j, Float.valueOf(map.getValue())));
                    Log.i("value",map.getValue());
                    Log.i("key",formatter.format(map.getKey()));
                    j++;
                }

                barChart.setMaxVisibleValueCount(max);
                BarDataSet barDataSet = new BarDataSet(barEntries, "No. of Cases");
                barDataSet.setColors(ColorTemplate.getHoloBlue());

                BarData data = new BarData(barDataSet);
                data.setBarWidth(0.6f);

                XAxis xAxis = barChart.getXAxis();

                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

                barChart.animateX(1000);
                barChart.setData(data);
                barChart.setFitBars(true); // make the x-axis fit exactly all bars
                barChart.invalidate();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }
}