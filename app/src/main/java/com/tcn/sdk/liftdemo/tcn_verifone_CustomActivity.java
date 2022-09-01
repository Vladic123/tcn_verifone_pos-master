package com.tcn.sdk.liftdemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.ListIterator;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;

/**
 * custom activity class for handling some nonstandard tasks
 * @author v.vasilchikov
 */

public class tcn_verifone_CustomActivity extends AppCompatActivity {

    public static final long DISCONNECT_TIMEOUT = Integer.parseInt(myDB.SettingsGetSingleValue("activitytimeout"))*1000;

    private static Activity activity;
    private String barcode="";
    private DevicePolicyManager dpm;
    private ComponentName adminName;
    private Context context;
    private Boolean noScan;
    private static Boolean running=false;
    private static Boolean noTimeout=false;

    private static void setRunning(Boolean state){
        running=state;
    }

    private static Boolean getRunning(){
        return running;
    }

    private static void setNoTimeout(Boolean state){
        noTimeout=state;
    }

    private static Boolean getNoTimeout(){
        return noTimeout;
    }

    /** DPM lock */
    private void setLock(){

        final View decorView = getWindow().getDecorView();
        /** set some flags to minimize user interaction with system UI elements */
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if ((i & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    //visible
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);

                }
            }
        });

        /** Keep the screen on and bright while this kiosk activity is running.*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /** Create an intent filter to specify the Home category.*/
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            adminName = new ComponentName(context, tcn_verifone_AdminManager.class);

            /** Set the activity as the preferred option for the device.*/
            ComponentName activity = new ComponentName(context, tcn_verifone_ManagerActivity.class);
            dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            dpm.addPersistentPreferredActivity(adminName, filter, activity);

            /** If the system is running in lock task mode, set the user restrictions
            for a kiosk after launching the activity.*/
            String[] restrictions = {
                    UserManager.DISALLOW_FACTORY_RESET,
                    UserManager.DISALLOW_SAFE_BOOT,
                    UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
                    UserManager.DISALLOW_ADJUST_VOLUME,
                    UserManager.DISALLOW_ADD_USER};

            for (String restriction : restrictions) dpm.addUserRestriction(adminName, restriction);

            dpm.addUserRestriction(adminName, UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS);
            dpm.addUserRestriction(adminName, UserManager.DISALLOW_CREATE_WINDOWS);

            /** set some power management flags to prevent screen off */
            int pluggedInto = BatteryManager.BATTERY_PLUGGED_AC |
                    BatteryManager.BATTERY_PLUGGED_USB |
                    BatteryManager.BATTERY_PLUGGED_WIRELESS;
            dpm.setGlobalSetting(adminName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, String.valueOf(pluggedInto));

            /** disable some standard applications */
            PackageManager packageManager = getPackageManager();

            List<ApplicationInfo> packageList = packageManager.getInstalledApplications(0);
            ListIterator<ApplicationInfo> iterator = packageList.listIterator();


            while (iterator.hasNext()) {

                String packageName = iterator.next().packageName;

                //listView.append(packageName + "\n");

                /** hide default launcher */
                if (packageName.equals("com.android.launcher3")) {
                    dpm.setApplicationHidden(adminName, packageName, true);
                }
                /** hide system UI */
                if (packageName.equals("com.android.systemui")) {
                    dpm.setApplicationHidden(adminName, packageName, true);
                }

            }

            /** start lock task */
            if (dpm.isLockTaskPermitted(context.getPackageName())) {
                this.startLockTask();
            } else {
                Toast.makeText(context,"Could not lock task",Toast.LENGTH_LONG).show();
            }

        }catch(Exception ex){
            Toast.makeText(context, "App is not a Device Admin", Toast.LENGTH_SHORT).show();
        }
    }

    /** message handler */
    private Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // todo
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        activity = this;
        noScan=false;
        //setLock();
        /** reset inactivity timer */
        resetDisconnectTimer();
    }

    /** inactivity disconnect timer */
    private static Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {

            Log.d("DBG","noTimeout="+getNoTimeout());
            if(getRunning() && !getNoTimeout()) {
                Log.d("DBG", "CustomActivity inactivity timout");
                // Perform any required operation on disconnect
                myCart.Init();
                myCart.setPaid(false);

                activity.setResult(RESULT_CANCELED);
                activity.finish();
            }else{
                Log.d("DBG", "CustomActivity inactivity timout, but activity paused. Do nothing.");
            }
        }
    };

    /** reset inactivity timer */
    public void resetDisconnectTimer(){
        //Log.d("DBG", "CustomActivity resetDisconnectTimer");
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    /** disable inactivity timer */
    public void stopDisconnectTimer(){
        //Log.d("DBG", "CustomActivity stopDisconnectTimer");
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onStart(){
        super.onStart();
        /** reset inactivity timer */
        resetDisconnectTimer();
    }

    @Override
    public void onUserInteraction(){
        /** reset inactivity timer */
        resetDisconnectTimer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        /** set running state to running */
        setRunning(true);
        /** reset inactivity timer */
        resetDisconnectTimer();
    }

    @Override
    protected void onPause(){
        super.onPause();
        /** set runnig state to stopped */
        setRunning(false);
        /** reset and stop inactivity timer */
        resetDisconnectTimer();
        stopDisconnectTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /** stop pinactivity timer */
        stopDisconnectTimer();
    }

    /** work with barcode reader
     * barcode reader acts as keyboard, so, we catch every char sent from "keyboard" and add it to barcode string
     * barcode end symbol - 0x09
     * */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        /** key pressed */
        if (!noScan && e.getAction() == KeyEvent.ACTION_DOWN) {

            /** get char and add it to barcode string */
            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;

            /** barcode end symbol received */
            if (pressedKey == 0x09) {

                /** show barcode result activity */
                Intent barcodeIntent = new Intent(activity, tcn_verifone_BarcodeResult.class);
                barcodeIntent.putExtra("barcode", barcode);
                startActivity(barcodeIntent);

                barcode = "";

            }
        }

        return super.dispatchKeyEvent(e);
    }

    /** set no scan mode */
    public void setNoScan(){
        noScan=true;
    }

    /** set no timeout mode */
    public void setNoTimeout(){ setNoTimeout(true);}

}