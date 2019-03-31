package com.example.myapplication;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class TaxiFareActivity extends AppCompatActivity {

    EditText startAddr;
    EditText endAddr;
    TextView resultTextView;
    ImageView btnBack;

    public void calculateFare(View view) {

        Log.i("Start address", startAddr.getText().toString());
        Log.i("end address", endAddr.getText().toString());
        Log.i("website address", "https://api.taxifarefinder.com/fare?key=r2EWGCweL29j&entity_handle=Singapore&origin=" + getLocationFromAddress(startAddr.getText().toString()) + "&destination=" + getLocationFromAddress(endAddr.getText().toString()));

        DownloadTask task = new DownloadTask();
        task.execute("https://api.taxifarefinder.com/fare?key=r2EWGCweL29j&entity_handle=Singapore&origin=" + getLocationFromAddress(startAddr.getText().toString()) + "&destination=" + getLocationFromAddress(endAddr.getText().toString()));

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_fare);

        startAddr = (EditText) findViewById(R.id.startid);
        endAddr = (EditText) findViewById(R.id.endid);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(TaxiFareActivity.this, MapsActivity.class);
                startActivity(d);
            }
        });

    }


    public String getLocationFromAddress (String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        String p1total = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            //idk what to do with this below statement
            //p1 = new LatLng(location.getLatitude(), location.getLongitude());
            double p1lat = location.getLatitude();
            double p1long = location.getLongitude();
            p1total = p1lat + "," + p1long;
            Log.i("p1total", p1total);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return p1total;
    }



    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data!= -1){
                    char current  = (char) data;

                    result += current;

                    data = reader.read();

                }
                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String message="";
                String totalfare = "";
                String initialfare = "";
                String meterfare = "";
                String durationmin="";
                String durationsec="";
                String distance="";
                String distanceround="";

                totalfare= String.valueOf(jsonObject.getDouble("total_fare"));
                initialfare = String.valueOf(jsonObject.getDouble("initial_fare"));
                meterfare = String.valueOf(jsonObject.getDouble("metered_fare"));
                durationmin = String.valueOf(Math.round(Math.floor(jsonObject.getDouble("duration")/60)));
                durationsec = String.valueOf(Math.round(jsonObject.getDouble("duration")%60));
                distance = String.format("%.2f",jsonObject.getDouble("distance")/1000);




                Log.i("total_fare", totalfare);
                Log.i("initial_fare", initialfare);
                Log.i("metered_fare", meterfare);
                Log.i("duration minutes", durationmin);
                Log.i("duration seconds", durationsec);
                Log.i("distance", distance);





                if (true){

                    message += "Total Fare: $" + totalfare + "\r\n" + "Initial Fare: $" + initialfare + "\r\n" + "Metered Fare: $" + meterfare + "\r\n" + "Duration Of Journey: " + durationmin + " mins " + durationsec + " secs" + "\r\n" + "Distance Covered: " + distance + " km" + "\r\n";
                    Log.i("Printout", message);
                    resultTextView.setText(message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


}