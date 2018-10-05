package com.example.leodw.twoviews;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button secondActivityBtn = (Button) findViewById(R.id.secondActivityBtn);
        secondActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), SecondActivity.class);
                startIntent.putExtra("com.example.leodw.twoviews.STRING", "This is the text!");
                startActivity(startIntent);
            }
        });

        //Attempt to launch an activity outside our app
        Button googleBtn = (Button) findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String google = "http://www.yahoo.com";
                Uri webaddress = Uri.parse(google);
                Intent goToGoogle = new Intent(Intent.ACTION_VIEW, webaddress);
                if(goToGoogle.resolveActivity(getPackageManager()) != null){
                    startActivity(goToGoogle);
                }
            }
        });
    }
}
