package com.example.android.hawk;

/**
 * @author Sanat
 * loginWatch class, handle enabling and disabling of security Policies
 * by the user
 */
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class loginWatch extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context,"Device Admin Enabled",Toast.LENGTH_SHORT).show();
        Log.i("loginWatch","Device Admin Enabled");
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context,"Device Admin Disabled",Toast.LENGTH_SHORT).show();
        Log.i("loginWatch","Device Admin Disabled");
    }

    //Handle Password Failure
    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordFailed(context, intent);
        SecurityService.failedPasswordCount++;
        Log.d("loginWatch-passFailed", String.valueOf(SecurityService.failedPasswordCount));
    }
}
