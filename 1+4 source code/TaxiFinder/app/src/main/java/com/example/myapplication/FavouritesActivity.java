package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavouritesActivity extends AppCompatActivity {

    Button btnAddNew;
    ImageView btnBack;
    DatabaseReference reference;
    RecyclerView ourFavourites;
    ArrayList<FavouritesList> list;
    FavouritesAdapter favouritesAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        btnAddNew = findViewById(R.id.btnAddNew);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(FavouritesActivity.this, MapsActivity.class);
                startActivity(c);
            }
        });

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(FavouritesActivity.this, NewTaskAct.class);
                startActivity(a);
            }
        });


        //working with data
        ourFavourites = findViewById(R.id.ourFavourites);
        ourFavourites.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<FavouritesList>();

        //get data from Firebase
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("MapApplication").child("users").child(userid).child("Favourites");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //set code to retrieve data and replace layout
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {

                    FavouritesList p =dataSnapshot1.getValue(FavouritesList.class);
                    list.add(p);
                }
                    favouritesAdapter = new FavouritesAdapter(FavouritesActivity.this, list);
                    ourFavourites.setAdapter(favouritesAdapter);
                    favouritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //set code to show an error
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
