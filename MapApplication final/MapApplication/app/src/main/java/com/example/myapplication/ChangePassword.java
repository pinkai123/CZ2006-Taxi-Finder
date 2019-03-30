package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    EditText newpw;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newpw = (EditText) findViewById(R.id.et_newpw);
        auth = FirebaseAuth.getInstance();
    }


    public void change(View v){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!= null){
            user.updatePassword(newpw.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Your password has been changed successfully", Toast.LENGTH_SHORT).show();
                                            }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Your password could not be changed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
