package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

//include functions: on/off taxi stand locations, choose radius, change password
public class SettingsActivity extends AppCompatActivity {


    ImageView btnBack;

//optional
    Button btnChangepw;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnChangepw = findViewById(R.id.btnChangepw);
        btnBack = findViewById(R.id.btnBack);

        btnChangepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(SettingsActivity.this, ChangePassword.class);
                startActivity(d);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(SettingsActivity.this, MapsActivity.class);
                startActivity(e);
            }
        });


    }
}
