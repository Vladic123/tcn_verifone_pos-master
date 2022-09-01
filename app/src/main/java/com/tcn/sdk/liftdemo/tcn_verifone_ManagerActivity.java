package com.tcn.sdk.liftdemo;

import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * DPM manager activity
 * used to handle some DPM tasks
 * @author v.vasilchikov
 */

public class tcn_verifone_ManagerActivity extends AppCompatActivity {
    DevicePolicyManager deviceManger;
    ComponentName compName;
    String TAG = "aaa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        compName = new ComponentName(this, tcn_verifone_AdminManager.class);
        deviceManger = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        if (deviceManger.isDeviceOwnerApp(getPackageName())) {
            setDefaultCosuPolicies(true);
        } else {
            Log.e(TAG, "This application not whitelisted");
        }
    }

    private void setDefaultCosuPolicies(boolean active) {// Set user restrictions    setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);    setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);    setUserRestriction(UserManager.DISALLOW_ADD_USER, active);    setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);  setUserRestriction(UserManager.DISALLOW_INSTALL_APPS, active);

        // Disable keyguard and status bar    deviceManger.setKeyguardDisabled(compName, active);    deviceManger.setStatusBarDisabled(compName, active);// Set system update policy
        if (active) {
            deviceManger.setSystemUpdatePolicy(compName, SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            deviceManger.setSystemUpdatePolicy(compName, null);
        }    // set this Activity as a lock task package    deviceManger.setLockTaskPackages(compName,active ? new String[]{getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home TrackerIntent receiver so that it is started        // on reboot        deviceManger.addPersistentPreferredActivity(compName, intentFilter, new ComponentName(getPackageName(), DeviceHandlerActivity.class.getName()));    } else {
            deviceManger.clearPackagePersistentPreferredActivities(compName, getPackageName());
        }
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            deviceManger.addUserRestriction(compName, restriction);
        } else {
            deviceManger.clearUserRestriction(compName, restriction);
        }
    }
}