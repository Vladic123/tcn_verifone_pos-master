package com.tcn.sdk.liftdemo;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import static controller.VendApplication.myDB;

/**
 * administration activity class
 * @author v.vasilchikov
 */

public class tcn_verifone_AdminActivity extends AppCompatActivity {

    /** buttons */
    private Button lanesConfigButton;
    private Button productButton;
    private Button serialConfig;
    private Button tcnButton;
    private Button exitButton;
    private Button colorButton;
    private Button prodMgrButton;
    private Button imageButton;
    private Button flavourButton;
    private Button lockButton;
    private Button unlockButton;
    private Button resetTo5;
    private Button backupDB;
    private Button restoreDB;

    /** context */
    Context context = null;

    /** DPM */
    private DevicePolicyManager dpm=null;
    private ComponentName adminName=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__admin);

        context = this;

        /** bind buttons and set click listeners */

        /** test shipping button */
        Button testShip = findViewById(R.id.tcn_verifone_admin_test_backupdb_button);
        testShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_ProcessVend.class);
                startActivity(intent);
            }
        });

        /** product manager button*/
        prodMgrButton = findViewById(R.id.tcn_verifone_admin_product_manager);
        prodMgrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_ProductManager.class);
                startActivity(intent);
            }
        });

        /** color manager button */
        colorButton = findViewById(R.id.tcn_verifone_admin_color_button);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_ColorManager.class);
                startActivity(intent);
            }
        });

        /** exit button */
        exitButton = findViewById(R.id.admin_exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /** lanes config button */
        lanesConfigButton = findViewById(R.id.tcn_verifone_admin_lanes_config_button);
        lanesConfigButton.setVisibility(View.INVISIBLE);
        lanesConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, tcn_verifone_LanesConfig.class);
                startActivity(intent);
            }
        });

        /** products button */
        productButton = findViewById(R.id.tcn_verifone_admin_products_button);
        productButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, tcn_verifone_Products.class);
                startActivity(intent);
            }
        });

        /** serial port configuration button */
        serialConfig = findViewById(R.id.tcn_verifone_admin_serial_config_button);
        serialConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, tcn_verifone_SettingsActivity.class);
                startActivity(intent);
            }
        });

        /** TCN interface button */
        tcnButton = findViewById(R.id.tcn_verifone_admin_tcn_button);
        tcnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainAct.class);
                startActivity(intent);
            }
        });

        /** image manager button */
        imageButton = findViewById(R.id.tcn_verifone_admin_image_manager_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_ImageManager.class);
                startActivity(intent);
            }
        });

        /** flavour manager button */
        flavourButton = findViewById(R.id.tcn_verifone_admin_flavour_manager_button);
        flavourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_FlavoursManager.class);
                startActivity(intent);
            }
        });

        /** lock button */
        lockButton = findViewById(R.id.tcn_verifone_admin_set_lock);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockUnlockScreen(true);
            }
        });

        /** unlock button */
        unlockButton = findViewById(R.id.tcn_verifone_admin_clear_lock);
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockUnlockScreen(false);
            }
        });

        /** reset lane to 5 button */
        resetTo5 = findViewById(R.id.tcn_verifone_admin_reset_lanes_to_5);
        resetTo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTo5();
            }
        });

        /** database backup button */
        backupDB = findViewById(R.id.tcn_verifone_admin_test_backupdb_button);
        backupDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    /** close database */
                    myDB.CloseDB();

                    /** get paths */
                    String exStoragePath = System.getenv("EXTERNAL_STORAGE");
                    String backupDBPath = exStoragePath + "/Download/local.db";

                    String currentDBPath ="local.db";

                    if (android.os.Build.VERSION.SDK_INT >= 17)
                        currentDBPath = context.getApplicationInfo().dataDir + "/databases/"+currentDBPath;
                    else
                        currentDBPath = "/data/data/" + context.getPackageName() + "/databases/"+currentDBPath;

                    FileChannel source = null;
                    FileChannel destination = null;

                    File currentDB = new File(currentDBPath);
                    File backupDB = new File(backupDBPath);

                    /** copy database file */

                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    destination.force(true);
                    source.close();
                    destination.close();

                    /** open current database */
                    myDB.OpenDB();

                } catch (Exception e) {
                }
            }
        });

        /** database restore button */
        restoreDB = findViewById(R.id.tcn_verifone_admin_restore_db_button);
        restoreDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    /** close database */
                    myDB.CloseDB();

                    /** process paths */
                    String exStoragePath = System.getenv("EXTERNAL_STORAGE");
                    String backupDBPath = exStoragePath + "/Download/local.db";

                    String currentDBPath ="local.db";
                    if (android.os.Build.VERSION.SDK_INT >= 17)
                        currentDBPath = context.getApplicationInfo().dataDir + "/databases/"+currentDBPath;
                    else
                        currentDBPath = "/data/data/" + context.getPackageName() + "/databases/"+currentDBPath;

                    FileChannel source = null;
                    FileChannel destination = null;

                    File currentDB = new File(currentDBPath);
                    File backupDB = new File(backupDBPath);

                    /** copy database file */
                    source = new FileInputStream(backupDB).getChannel();
                    destination = new FileOutputStream(currentDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    destination.force(true);
                    source.close();
                    destination.close();

                    /** open new database */
                    myDB.OpenDB();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /** reset product quantity on the all lanes to 5 */
    private void resetTo5(){

        Integer lanesPerRow = 0;
        Integer rows = 0;

        /** get rows and lanes per row counts */
        try {
            lanesPerRow = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_lanes_per_row_main"));
            rows = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_rows_main"));
        }catch(Exception ex){

        }

        /** reset product amount for every lane */
        Integer laneNumber = 1;
        for(int row=1;row<=rows;row++) {
            TableRow trow = new TableRow(context);
            for (int lane = 1; lane <= lanesPerRow; lane++) {
                if ((laneNumber % 10) != 0) {
                    tcn_verifone_AuxLane AL = myDB.LanesGetById(laneNumber.longValue());
                    AL.setAmount(4);
                    myDB.LanesUpdateItem(AL);
                }
                laneNumber++;

            }
        }
        Toast.makeText(context,getText(R.string.tcn_verifone_admin_reset_lanes_success),Toast.LENGTH_LONG).show();
    }

    /** lock and unlock application with DPM*/
    private void lockUnlockScreen(Boolean lock){

        try {

            /** init dpm */
            if(adminName==null && dpm == null) {
                adminName = new ComponentName(context, tcn_verifone_AdminManager.class);
                // Set the activity as the preferred option for the device.

                dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            }

            if(lock){
                /** add application to device admins group and lock */
                final Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Add app to Device admin");
                startActivityForResult(intent,0);

                Toast.makeText(context,"Locked",Toast.LENGTH_LONG).show();
            }else{
                /** remove lock, unhide system UI and default system launcher */
                dpm.setApplicationHidden(adminName, "com.android.systemui", false);
                dpm.setApplicationHidden(adminName, "com.android.launcher3", false);
                dpm.removeActiveAdmin(adminName);
                dpm.clearDeviceOwnerApp("com.tcn.sdk.liftdemo");
                Toast.makeText(context,"Unlocked",Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Toast.makeText(context,"App is not Device Admin",Toast.LENGTH_LONG).show();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0){
            try {
                /** hide default launcher and system UI */
                dpm.setApplicationHidden(adminName, "com.android.launcher3", true);
                dpm.setApplicationHidden(adminName, "com.android.systemui", true);
            }catch (Exception ex){

            }
        }

    }
}
