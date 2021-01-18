package com.example.android.hawk;

/**
 * @author Sanat
 * MainActivity class, launcher activity
 */

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O) //Requires minimum Oreo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!SecurityService.serviceRunning){
            startForegroundService(new Intent(MainActivity.this,SecurityService.class));
        }
    }
}