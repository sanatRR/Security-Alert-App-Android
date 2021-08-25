package com.example.android.hawk;

/**
 * @author Sanat
 * MainActivity class, launcher activity
 */

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    /**
     * Use will tick the adminEnabled check box, this will prompt for
     * enabling admin privilege (Monitor screen unlock attempts)
     */
    private CheckBox adminEnabled;
    /**
     * DevicePolicyManager is interface used for managing policies enforced on device
     * This app uses "watch-login" policy
     */
    private DevicePolicyManager devicePolicyManager;
    /**
     * statusTV: displays OFF is admin privilege is not given, else ON
     * countTV: displays the number of incorrect PIN attempts
     * reset: resets the number of incorrect PIN attempts to 0
     */
    private TextView statusTV,countTV,reset;
    /**
     * emailET: The user will enter the destination email-address here
     */
    private EditText emailET;
    /**
     * tickIV button sets the email-address
     */
    private Button tickIV;
    /**
     * compName: A component name object which will identify loginWatch class
     */
    private ComponentName compName;
    private boolean isAdminActive;

    @Override
    protected void onResume() {
        super.onResume();
        isAdminActive = devicePolicyManager.isAdminActive(compName);
        if(SecurityService.senderEmail!=null)
            emailET.setHint(SecurityService.senderEmail);
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
        //This will disable night mode for the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  //Inflate objects from activity_main.xml
        adminEnabled=findViewById(R.id.adminAccessCheckBox);
        emailET=findViewById(R.id.et_email);
        tickIV=findViewById(R.id.tv_tick);
        statusTV=findViewById(R.id.tv_status);
        countTV=findViewById(R.id.tv_count);
        reset=findViewById(R.id.tv_Reset);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this,loginWatch.class);

        /**
         * If security service is not running, then start it
         * Foreground service will keep on running, even if the app stops
         */
        if(!SecurityService.serviceRunning){
            startForegroundService(new Intent(MainActivity.this,SecurityService.class));
        }

        /**
         * Clicking on tickIV button will set the email-address
         * The text-view hint shall also be set same
         */
        tickIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurityService.senderEmail=emailET.getText().toString();
                emailET.setHint(SecurityService.senderEmail);
                Toast.makeText(MainActivity.this,"Email Set:"+SecurityService.senderEmail,Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Clicking on the reset view will reset the incorrect PIN cout
         */
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurityService.failedPasswordCount=0;
                countTV.setText(String.valueOf(0));
            }
        });

        /**
         *If device admin is not enabled, then checking the box will open a menu
         * the user will be asked to enable admin privilege
         */
        adminEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!isAdminActive){
                   //ACTION_ADD_DEVICE_ADMIN: Prompt user to give admin privilege to this app
                   Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                   //EXTRA_DEVICE_ADMIN: The ComponentName identifier object of the admin component
                   intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
                   intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin Access Needed To Check If Incorrect PIN Entered");
                   startActivityForResult(intent,3 );  //The request code will be used while fetching result
                   isAdminActive = devicePolicyManager.isAdminActive(compName);   //Check if the admin is enabled
               }
               else
               {
                   /***
                    * If admin privilege was already granted (checkbox ticked), then disable admin privelege
                    * set isAdminActive to false
                    * set status to OFF (also change color to red)
                    * Show a toast to alert the user
                    */
                   devicePolicyManager.removeActiveAdmin(compName);
                   isAdminActive = false;
                   statusTV.setText("OFF");
                   statusTV.setTextColor(Color.RED);
                   Toast.makeText(MainActivity.this,"Admin Privilege Revoked",Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    /***
     * Called when control returns to original activity after an Intent operation
     * @param requestCode  The request code sent while calling the Intent
     * @param resultCode   RESULT_OK signifies privilege granted
     * @param data         Intent object
     */
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