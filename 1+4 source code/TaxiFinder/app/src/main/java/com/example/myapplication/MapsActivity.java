package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

//changed fragmentactivity to appcombatactivity
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    //search bar




    //for navigation menu
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    LatLng myCurrentlocation;
    private FirebaseDatabase database;
    private EditText editLocation = null;
    Geocoder gcd;
    DateFormat df;
    TimeZone timezone;
    ArrayList< TaxiStand> TaxiStands = new ArrayList<TaxiStand>();
    DatabaseReference myRef;
    String userid;
    boolean DisplayIndicator;
    double Radius;
    protected ProgressDialog mProgressDialog;;

    private void moveCameraToKml(KmlLayer layer) {
        KmlContainer container = layer.getContainers().iterator().next();
        container = container.getContainers().iterator().next();
        mMap.clear();
        int i = 0;
        Log.i("Taxi Stop", "End3");
        for (KmlPlacemark placemark : container.getPlacemarks()) {
            Log.i("Taxi Stop", "End4");
            Log.i("Trial", String.valueOf(i));
            if (placemark.getGeometry().getGeometryType().equals("Point")) {
                KmlPoint point = (KmlPoint) placemark.getGeometry();
                LatLng TaxiStandCoordinate = new LatLng(point.getGeometryObject().latitude, point.getGeometryObject().longitude);
                Log.i("Info", TaxiStandCoordinate.toString());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(TaxiStandCoordinate.latitude,
                            TaxiStandCoordinate.longitude, 1);
                    if (addresses.size() > 0) {
                        String s = addresses.get(0).getAddressLine(0) + "\n"
                                + addresses.get(0).getLocality() + "\n"
                                + addresses.get(0).getPostalCode() + "\n"
                                + addresses.get(0).getThoroughfare() + "\n";


                        mMap.addMarker(new MarkerOptions().position(TaxiStandCoordinate).title(addresses.get(0).getThoroughfare()).snippet(addresses.get(0).getPostalCode()));
                        Log.i("Coordinates", TaxiStandCoordinate.toString() + s);
                        TaxiStand mylocation = new TaxiStand(TaxiStandCoordinate, s);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference();
                        myRef = myRef.child("TaxiStands");
                        myRef.child("Taxi" + i).setValue(mylocation, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError == null){
                                    Log.i("info", "Save successful");
                                }
                                else{
                                    Log.i("info", "Save failed");
                                }
                            }
                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            i++;
        }
        Log.i("Taxi Stop", "End5");
    }
    private void displayTaxiStand(){
        Double latitudeRange = 0.02;
        Double longtitudeRange = 0.02;
        for ( int i = 0; i<TaxiStands.size();i++){
            if (Math.abs(TaxiStands.get(i).lat - myCurrentlocation.latitude) > latitudeRange) {
                continue;
            }
            if (Math.abs(TaxiStands.get(i).lng - myCurrentlocation.longitude) > longtitudeRange) {

                continue;
            }
            mMap.addMarker(new MarkerOptions().position(new LatLng(TaxiStands.get(i).lat,TaxiStands.get(i).lng)).title(TaxiStands.get(i).getdescription()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxistand)));
        }
    }
    private void Download(){
        DownloadTask task =new DownloadTask();
        timezone = TimeZone.getTimeZone("GMT+08:00");
        Calendar calendar = Calendar.getInstance(timezone);
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH'%3A'mm'%3A'ss");
        //Log.i("TIme",df.format(calendar.getTime()));
        try {
            Log.i("Download","1");
            task.execute("https://api.data.gov.sg/v1/transport/taxi-availability?date_time=" + df.format(calendar.getTime()));
            //Log.i("info",result);
        } catch (Exception e) {
            e.printStackTrace(); }
    }
    private void retrieveDisplayIndicator(){
        DatabaseReference radiusRef = FirebaseDatabase.getInstance().getReference("DisplayTaxiStand/users/"+ userid + "/");
        radiusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String p =dataSnapshot.getValue(String.class);
                if (p != null) {
                    Log.i("Display",p);
                    DisplayIndicator = Boolean.parseBoolean(p);
                }
                else{
                    DisplayIndicator = Boolean.FALSE;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //set code to show an error
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveRadius(){
        DatabaseReference radiusRef = FirebaseDatabase.getInstance().getReference("Radius/users/"+ userid + "/");
        radiusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String p =dataSnapshot.getValue(String.class);
                if (p != null) {
                    Log.i("Display",p);
                    Radius = 0.01 * Integer.parseInt(p);
                }
                else{
                    Radius = 0.01;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1200,10,locationListener);
                }
            }
        }
    }




    public class DownloadTask extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;


            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in  = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!= -1){
                    char current = (char) data;
                    result += current;
                    data= reader.read();
                }
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("Download","2");

            String message = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray a = jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
                Log.i("DisplayIndicator", Boolean.toString(DisplayIndicator));
                //To display TaxiStand
                if(DisplayIndicator) {
                    displayTaxiStand();
                }
                for (int i = 0; i < a.length(); i++) {
                    LatLng mylocation = new LatLng(a.getJSONArray(i).getDouble(1), a.getJSONArray(i).getDouble(0));
                    if (Math.abs(mylocation.latitude - myCurrentlocation.latitude) > Radius) {
                        continue;
                    }
                    if (Math.abs(mylocation.longitude - myCurrentlocation.longitude) > Radius) {
                        continue;
                    }
                    //mMap.clear();

                    Geocoder gcd = new Geocoder(getApplicationContext(),
                            Locale.getDefault());
                    List<Address> addresses;
                    addresses = gcd.getFromLocation(mylocation.latitude,
                            mylocation.longitude, 1);

                    if (addresses.size() > 0) {
                        String s = addresses.get(0).getAddressLine(0) + "\n"
                                + addresses.get(0).getLocality() + "\n"
                                + addresses.get(0).getPostalCode() + "\n"
                                + addresses.get(0).getThoroughfare() +  "\n";


                        mMap.addMarker(new MarkerOptions().position(mylocation).title(addresses.get(0).getThoroughfare()).snippet(addresses.get(0).getPostalCode()).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxicon1)));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,10));
                        Log.i("Coordinates", mylocation.toString() + s);
                    }
                }
            }catch(JSONException e){
                    e.printStackTrace();
            } catch(IOException e){
                    e.printStackTrace();
            }
            Log.i("Download","3");
            Toast.makeText(MapsActivity.this, "Map Updated", Toast.LENGTH_LONG).show();

        }
    }

    //logout function
    private void logout(){
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(t);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gcd = new Geocoder(getApplicationContext(),
                Locale.getDefault());
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Read database for TaxiStand Location
        myRef = FirebaseDatabase.getInstance().getReference();
        Query myTopPostsQuery = myRef.child("TaxiStands")
                .orderByChild("lat");
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    TaxiStand value = ds.getValue(TaxiStand.class);
                    TaxiStands.add(value);
                }
                Log.i("ArrayList","created");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //for navigation menu

        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);
        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


    }



    public void onClick(View v) {
        EditText addressField = (EditText) findViewById(R.id.location_search);
        String address = addressField.getText().toString();

        List<Address>  addressList = null;
        MarkerOptions userMarketOptions = new MarkerOptions();

        if (!TextUtils.isEmpty(address)){
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(address, 6);

                if(addressList != null){
                    //marker for loop
                    for(int i=0; i<addressList.size(); i++){
                        mMap.clear();

                        Address userAddress = addressList.get(i);
                        LatLng latlng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                        myCurrentlocation = latlng;

                        userMarketOptions.position(latlng);
                        userMarketOptions.title(address);
                        userMarketOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        mMap.addMarker(userMarketOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                        Download();
                    }
                }
                else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        else {
            Toast.makeText(this, "Please write any location address", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(myToggle.onOptionsItemSelected(item)){
            return true;}



        return super.onOptionsItemSelected(item);
    }

    //jumping to next activity of menu items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //clicking "Settings" from menu bar, insert "settings" code to SettingsActivity
            case R.id.menuSettings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                System.exit(0);
                break;

            //clicking "Taxi Fare Calculator" from menu bar, insert "Taxi Fare Calculator" code to TaxiFareActivity
            case R.id.menuTaxiFare:
                Intent taxifare = new Intent(this, TaxiFareActivity.class);
                startActivity(taxifare);
                System.exit(0);
                break;

            //clicking "Favourites" from menu bar, insert "favourites" code to FavouriteActivity
            case R.id.menuFavourites:
                Intent favourites = new Intent(MapsActivity.this, FavouritesActivity.class);
                startActivity(favourites);
                System.exit(0);
                break;

            //clicking "Feedback" from menu bar, insert "feedback" code to FeedbackActivity
            case R.id.menuFeedback:
                Intent feedback = new Intent(MapsActivity.this, FeedbackActivity.class);
                startActivity(feedback);
                System.exit(0);
                break;

            //click "Logout" at menu bar to logout
            case R.id.menuLogout:
                logout();

        }

        return false;
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
        retrieveDisplayIndicator();
        retrieveRadius();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //myCurrentlocation = new LatLng(Double.valueOf(1.3521),Double.valueOf(103.8198));
                myCurrentlocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                //mMap.clear();
                mMap.addMarker(new MarkerOptions().position(myCurrentlocation).title("My location").snippet("and snippet")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentlocation,17));
                Log.i("Location",location.toString());
                //Toast.makeText(MapsActivity.this,location.toString(),Toast.LENGTH_SHORT).show();

                Download();



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1200,10,locationListener);
//            long time= System.currentTimeMillis();
//            long duration = 5000;
//            while(System.currentTimeMillis()< time + duration){
//                continue;
//            }
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //myCurrentlocation = new LatLng(Double.valueOf(1.3521),Double.valueOf(103.8198));
            myCurrentlocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            //mMap.clear();
            mMap.addMarker(new MarkerOptions().position(myCurrentlocation).title("My location").snippet("and snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentlocation,17));
            Download();

        }


        // To store KML file as database
//        KmlLayer layer;
//        try {
//            Log.i("Taxi Stop", "Start");
//            layer = new KmlLayer(mMap, R.raw.taxistand, getApplicationContext());
//            Log.i("Taxi Stop", "End");
//            layer.addLayerToMap();
//            moveCameraToKml(layer);
//            Log.i("Taxi Stop", "End2");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        };
//            TaxiStand mylocation = new TaxiStand(new LatLng(1.344,100.34), "I am here");
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference();
//            myRef = myRef.child("Test1");
//            myRef.child("Taxi1").setValue(mylocation, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                  if(databaseError == null){
//                      Log.i("info", "Save successful");
//                  }
//                  else{
//                      Log.i("info", "Save failed");
//                  }
//                }
//            });
//

    }
}