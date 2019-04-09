package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedbackActivity extends AppCompatActivity {

    ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

      // final EditText to = (EditText) findViewById(R.id.sendTo);
        final TextView to = (TextView) findViewById(R.id.tv_defaultb);
       final EditText subject = (EditText) findViewById(R.id.subject);
       final EditText message = (EditText) findViewById(R.id.EmailText);

        Button sendE = (Button) findViewById(R.id.sendEmail);
        sendE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toS = to.getText().toString();
                String subS = subject.getText().toString();
                String messageS = message.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, toS);
               // email.putExtra(Intent.EXTRA_EMAIL, new String[] { toS});
                email.putExtra(Intent.EXTRA_SUBJECT, subS);
                email.putExtra(Intent.EXTRA_TEXT, messageS);

                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose app to send mail"));
            }
        });

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent d = new Intent(FeedbackActivity.this, MapsActivity.class);
                startActivity(d);
            }
        });
    }
}
