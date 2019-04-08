package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//include functions: on/off taxi stand locations, choose radius, change password
public class SettingsActivity extends AppCompatActivity {




//optional
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    protected ProgressDialog mProgressDialog;
    String userid;
    CheckBox checkbox;
    SeekBar radiusControl;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private void setCheckbox(){
        mProgressDialog = ProgressDialog.show(this, "Please wait","Long operation starts...", true);
        new Thread() {
            @Override
            public void run() {
                    DatabaseReference radiusRef = FirebaseDatabase.getInstance().getReference("DisplayTaxiStand/users/"+ userid + "/");
                    radiusRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String p =dataSnapshot.getValue(String.class);
                            if(p != null) {
                                Log.i("Display",p);
                                checkbox.setChecked(Boolean.parseBoolean(p));
                            }
                            else checkbox.setChecked(Boolean.FALSE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //set code to show an error
                            Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                        }
                    });

                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                        }
                    });
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        }.start();

    }

    {
        DatabaseReference radiusRef = FirebaseDatabase.getInstance().getReference("DisplayTaxiStand/users/"+ userid + "/");
        radiusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String p =dataSnapshot.getValue(String.class);
                if(p != null) {
                    Log.i("Display",p);
                    checkbox.setChecked(Boolean.parseBoolean(p));
                }
                else checkbox.setChecked(Boolean.FALSE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //set code to show an error
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setRadius(){
        DatabaseReference radiusRef = FirebaseDatabase.getInstance().getReference("Radius/users/"+ userid + "/");
        radiusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String p =dataSnapshot.getValue(String.class);
                if(p!=null) {
                    Log.i("Radius",p);
                    radiusControl.setProgress(Integer.parseInt(p));
                }
                else{
                    radiusControl.setProgress(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //set code to show an error
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View declaration
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.activity_settings);

        //Variable declaration
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //set checkbox base on database value
        setCheckbox();
        // set radius base on database value
        setRadius();


        Button btnChangePw = findViewById(R.id.btnChangepw);
        btnChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(SettingsActivity.this, ChangePassword.class);
                startActivity(d);
            }
        });
        ImageView btnBack = findViewById(R.id.btnBack);;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(SettingsActivity.this, MapsActivity.class);
                startActivity(e);
            }
        });
        checkbox =(CheckBox)findViewById(R.id.TaxiStandIndicator);
        checkbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                DatabaseReference displayRef = myRef.child("DisplayTaxiStand").child("users").child(userid);
                if (((CheckBox) v).isChecked()) {
                        displayRef.setValue(Boolean.toString(true));
                }
                else{
                    displayRef.setValue(Boolean.toString(false));
                }

            }
        });

        radiusControl  = (SeekBar) findViewById(R.id.Radius);
        final TextView RadiusDisplay = (TextView) findViewById(R.id.RadiusDisplay);
        final DatabaseReference radiusRef = myRef.child("Radius").child("users").child(userid);
        radiusControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("Progress",Integer.toString(progress));
                RadiusDisplay.setText(Integer.toString(progress)+"Km");
                radiusRef.setValue(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
}
