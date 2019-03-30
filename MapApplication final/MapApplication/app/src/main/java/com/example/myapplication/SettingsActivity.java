package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//include functions: on/off taxi stand locations, choose radius, change password
public class SettingsActivity extends AppCompatActivity {

    Button btnChangepw;
    ImageView btnBack;

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
