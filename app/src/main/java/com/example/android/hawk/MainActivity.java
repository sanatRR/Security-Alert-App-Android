package com.example.android.hawk;

/**
 * @author Sanat
 * MainActivity class, launcher activity
 */

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mailjet.client.Main;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private CheckBox adminEnabled;
    private DevicePolicyManager devicePolicyManager;
    private TextView statusTV,countTV;
    private EditText emailET;
    private Button tickIV;
    private ComponentName compName;
    private boolean isAdminActive;

    @Override
    protected void onResume() {
        super.onResume();
        isAdminActive = devicePolicyManager.isAdminActive(compName);
        if(isAdminActive){
            statusTV.setText("ON");
            statusTV.setTextColor(Color.GREEN);
        }
        else{
            statusTV.setText("OFF");
            statusTV.setTextColor(Color.RED);
        }
        countTV.setText(String.valueOf(SecurityService.failedPasswordCount));
        //First Set the CheckBox
        if(!isAdminActive && adminEnabled.isChecked()){
                adminEnabled.toggle();
        }
        else if(isAdminActive && !adminEnabled.isChecked()){
            adminEnabled.toggle();
        }
        else if(!isAdminActive)
            Toast.makeText(MainActivity.this,"Admin Privilege Needed",Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O) //Requires minimum Oreo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adminEnabled=findViewById(R.id.adminAccessCheckBox);
        emailET=findViewById(R.id.et_email);
        tickIV=findViewById(R.id.tv_tick);
        statusTV=findViewById(R.id.tv_status);
        countTV=findViewById(R.id.tv_count);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this,loginWatch.class);

        if(!SecurityService.serviceRunning){
            startForegroundService(new Intent(MainActivity.this,SecurityService.class));
        }

        tickIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurityService.senderEmail=emailET.getText().toString();
            }
        });

        adminEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!isAdminActive){
                   Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                   intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                   intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin Access Needed To Check If Incorrect PIN Entered");
                   startActivityForResult(intent,3 );  //request code 3
                   isAdminActive = devicePolicyManager.isAdminActive(compName);
               }
               else
               {
                   devicePolicyManager.removeActiveAdmin(compName);
                   isAdminActive = false;
                   statusTV.setText("OFF");
                   statusTV.setTextColor(Color.RED);
                   Toast.makeText(MainActivity.this,"Admin Privilege Revoked",Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 3:
                if (resultCode == RESULT_OK)
                    Toast.makeText(MainActivity.this, "Admin Privilege Granted", Toast.LENGTH_SHORT);
                else
                    Toast.makeText(MainActivity.this, "Failed to Enable Admin Privilege", Toast.LENGTH_SHORT).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}