package com.example.leodw.androidrestapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import RestApi.IRest;
import RestApi.Rest;

public class MainActivity extends AppCompatActivity {

    private IRest mIRest = (IRest) new Rest();
    private Button pushPointCloudBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushPointCloudBtn = (Button) findViewById(R.id.pushPointCloudBtn);
        pushPointCloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIRest.PushUser("Leo");
            }
        });
    }
}
