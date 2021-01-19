package com.example.android.hawk;

/**
 * @author Sanat
 * MainActivity class, launcher activity
 */

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.mailjet.client.Main;

public class MainActivity extends AppCompatActivity {
    private CheckBox adminEnabled;
    private DevicePolicyManager devicePolicyManager;
 //   private ActivityManager activityManager;
    private ComponentName compName;
    private boolean isAdminActive;

    @Override
    protected void onResume() {
        super.onResume();
        isAdminActive = devicePolicyManager.isAdminActive(compName);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adminEnabled=findViewById(R.id.adminAccessCheckBox);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
     //   activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this,loginWatch.class);

        if(!SecurityService.serviceRunning){
            startForegroundService(new Intent(MainActivity.this,SecurityService.class));
        }

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