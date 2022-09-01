package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * main (in fact no) activtiy
 * auxiliary activity to start shadow data unload and dispatching navigation between splash and other activities
 * @author v.vasilchikov
 */

public class tcn_verifone_MainActivity extends AppCompatActivity {

    private Button splashButton;
    private Button settingsButton;
    private Button saleButton;
    private Button adminButton;
    private Button netButton;

    private static final int ACTIVITY_SPLASH = 0;
    private static final int ACTIVITY_SETTINGS = 1;
    private static final int ACTIVITY_SALE = 2;
    private static final int ACTIVITY_ADMIN = 3;


    //  private StorageReference mStorageRef;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__main);

        View mView = findViewById(android.R.id.content).getRootView();

        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            context = this; // save context
        // start shadowed data unload
        startShadowUnload();
        // if cart is not paid - display main sale screen
        if(!myCart.getPaid()) {
            Intent intent = new Intent(context, tcn_verifone_SaleMainScreen.class);
            Log.d("DBG", "start SaleMainScreen from MainActivity");
            startActivityForResult(intent, ACTIVITY_SALE);
        }else{
            // cart paid - back to splash to start items shipping process
            Log.d("DBG", "MainActivity onCreate but getPaid=true, finish it");
            finish();
        }

    }

    /** shadowed and asynchronous data unload */
    private void startShadowUnload(){

        Handler shadowHandler = new Handler();
        shadowHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tcn_verifone_NetworkOperations NOP = new tcn_verifone_NetworkOperations(new tcn_verifone_NetworkOperations.onResultListener() {
                    @Override
                    public void OnResult(Pair<Integer, String> inData) {

                    }
                });
                NOP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"1");
            }
        },1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        Log.d("DBG","MainActivity onActivityResult");

        switch (requestCode) {
            case ACTIVITY_SPLASH: {
                /** returned from splash activity (after system start or finished shipping) */
//                Intent intent = new Intent(context,tcn_verifone_SaleMainScreen.class);
                Intent intent = new Intent(context,tcn_verifone_MainMenu.class);
                Log.d("DBG","start SaleMainScreen from MainActivity on ACTIVITY_SPLASH");
                startActivityForResult(intent,ACTIVITY_SALE);
            }
            break;
            case ACTIVITY_SALE:{
                /** returned from other activities - so, return to splash */
                    Log.d("DBG","finish MainActivity on ActivitySale result");
                    finish();
            }
            break;
        }
    }


}