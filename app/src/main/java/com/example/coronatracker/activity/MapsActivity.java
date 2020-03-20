package com.example.coronatracker.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.coronatracker.modelDeath.JsonDeathData;
import com.example.coronatracker.modelInfected.Coordinates;
import com.example.coronatracker.support.FileUtil;
import com.example.coronatracker.R;
import com.example.coronatracker.modelInfected.JsonData;
import com.example.coronatracker.modelInfected.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog pd;
    JsonData jsonData;
    public TextView count;
    ArrayList<String> countries;
    Spinner spinner;
    private List<Location> locations;
    JsonDeathData jsonDeathData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        MapsInitializer.initialize(getApplicationContext());
        mapFragment.getMapAsync(this);
        spinner = (Spinner) findViewById(R.id.spinner);
        countries = new ArrayList<>();
        count = (TextView) findViewById(R.id.count);

        new JsonTask().execute("https://coronavirus-tracker-api.herokuapp.com/all");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MapsActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            Log.i("result: ", "> " + result);
            jsonData = new Gson().fromJson(result, JsonData.class);
            count.setText("Total Cases: " + jsonData.getConfirmed().getLatest().toString());
            setDropDown(jsonData);
        }
    }

    private void setDropDown(JsonData jsonData) {

        for (Location location : jsonData.getConfirmed().getLocations()) {
            if (location.getLatest() != 0) {
//                LatLng latLng = new LatLng(Double.valueOf(location.getCoordinates().getLat()), Double.valueOf(location.getCoordinates().getLong()));
//                mMap.addMarker(new MarkerOptions().position(latLng).title(!location.getProvince().equals("") ? location.getCountry()+"-"+location.getProvince() + " : " + location.getLatest(): location.getCountry() + " : " + location.getLatest()));
                countries.add(!location.getProvince().equals("") ? location.getCountry() + "-" + location.getProvince() : location.getCountry());
            }
        }
        Collections.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        locations = jsonData.getConfirmed().getLocations();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country = (String) adapterView.getItemAtPosition(i);
                String[] countryArray = country.split("-");
                if(countryArray.length ==2) {
                    Location current = locations.stream().filter(p -> p.getCountry().equalsIgnoreCase(countryArray[0]) && p.getProvince().equalsIgnoreCase(countryArray[1])).findFirst().get();
                    setMarker(current, country);
                }
                else {
                    Location current = locations.stream().filter(p -> p.getCountry().equalsIgnoreCase(countryArray[0])).findFirst().get();
                    setMarker(current, country);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private void setMarker(Location current, String country) {

        mMap.clear();
        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        LatLng latLng = new LatLng(Double.valueOf(current.getCoordinates().getLat()), Double.valueOf(current.getCoordinates().getLong()));
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(country+" -> "+current.getLatest()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10f));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.details) {
            Intent intent = new Intent(this, MainActivity.class);

            FileUtil.writeToFile(new Gson().toJson(jsonData), "jsonData.txt", this);
            intent.putExtra("countries", countries);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.update) {
            new JsonTask().execute("https://coronavirus-tracker-api.herokuapp.com/all");
        }
        else if (item.getItemId() == R.id.death) {
            Intent intentDeath = new Intent(this, MapsActivity2.class);
            startActivity(intentDeath);
        }
        return (super.onOptionsItemSelected(item));
    }

}

