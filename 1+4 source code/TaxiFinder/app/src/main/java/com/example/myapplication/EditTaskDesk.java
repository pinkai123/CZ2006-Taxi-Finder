package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTaskDesk extends AppCompatActivity {


    EditText titlefavourites, addressfavourites;
    Button btnUpdate, btnDelete;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_desk);


        titlefavourites = findViewById(R.id.titlefavourites);
        addressfavourites = findViewById(R.id.addressfavourites);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        //get value from previous screen
        titlefavourites.setText(getIntent().getStringExtra("addresstitle"));
        addressfavourites.setText(getIntent().getStringExtra("address"));
        final String keykeyfavourites = getIntent().getStringExtra("keyfavourites");

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("MapApplication").child("users").child(userid).child("Favourites").child("Addr" + keykeyfavourites);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent a = new Intent(EditTaskDesk.this, FavouritesActivity.class);
                            startActivity(a);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //make event for buttons
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("addresstitle").setValue(titlefavourites.getText().toString());
                        dataSnapshot.getRef().child("address").setValue(addressfavourites.getText().toString());
                        dataSnapshot.getRef().child("keyfavourites").setValue(keykeyfavourites);
                        Intent a = new Intent(EditTaskDesk.this, FavouritesActivity.class);
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
