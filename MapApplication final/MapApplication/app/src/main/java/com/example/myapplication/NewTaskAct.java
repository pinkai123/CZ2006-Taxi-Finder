package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class NewTaskAct extends AppCompatActivity {

    TextView addtitle, addaddress;
    EditText titlefavourites, addressfavourites;
    Button btnSaveTask, btnCancel;
    DatabaseReference reference;
    Integer favouritesNum = new Random().nextInt();
    String keyfavourites = Integer.toString(favouritesNum);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        addtitle = findViewById(R.id.addtitle);
        addaddress = findViewById(R.id.addaddress);

        titlefavourites = findViewById(R.id.titlefavourites);
        addressfavourites = findViewById(R.id.addressfavourites);

        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancel);

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert data to database
                reference = FirebaseDatabase.getInstance().getReference().child("MapApplication").child("Addr" + favouritesNum);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("addresstitle").setValue(titlefavourites.getText().toString());
                        dataSnapshot.getRef().child("address").setValue(addressfavourites.getText().toString());
                        dataSnapshot.getRef().child("keyfavourites").setValue(keyfavourites);

                        Intent a = new Intent(NewTaskAct.this, FavouritesActivity.class);
                        startActivity(a);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
