package com.tcn.sdk.liftdemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tcn.sdk.liftdemo.slider.SliderAdapter;
import com.tcn.sdk.liftdemo.slider.SliderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import static controller.VendApplication.myCart;

/**
 * main sale screen activity class
 * used for displaying video and some information
 * @author v.vasilchikov
 */

public class tcn_verifone_SaleMainScreen extends AppCompatActivity /*implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback */{

    /*
     *
     *
     * USB CAMERA VARIABLES & LISTENER
     *
     *
     * */

   /* private View mTextureView;
    private CameraViewInterface mUVCCameraView;
    private UVCCameraHelper mCameraHelper;*/

    private SliderView sliderView;
    private SliderAdapter sliderAdapter;

    private boolean isRequest;
    private boolean isPreview;
    /*
     *
     * DATE AND TIME
     *
     * */

    private TextView textViewDate;



    private static final int ACTIVITY_MAIN = 1;
    private int counter = 0;
    private static Activity activity;
    private String barcode = "";
    private DevicePolicyManager dpm;
    private ComponentName adminName;

    // video panel
    private VideoView videoview;


    private Context context = null;

    private void setLock() {

        final View decorView = getWindow().getDecorView();
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

        // Keep the screen on and bright while this kiosk activity is running.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Create an intent filter to specify the Home category.
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            adminName = new ComponentName(context, tcn_verifone_AdminManager.class);

            // Set the activity as the preferred option for the device.
            ComponentName activity = new ComponentName(context, tcn_verifone_ManagerActivity.class);
            dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            dpm.addPersistentPreferredActivity(adminName, filter, activity);

            // If the system is running in lock task mode, set the user restrictions
            // for a kiosk after launching the activity.
            String[] restrictions = {
                    UserManager.DISALLOW_FACTORY_RESET,
                    UserManager.DISALLOW_SAFE_BOOT,
                    UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
                    UserManager.DISALLOW_ADJUST_VOLUME,
                    UserManager.DISALLOW_ADD_USER};

            for (String restriction : restrictions) dpm.addUserRestriction(adminName, restriction);

            dpm.addUserRestriction(adminName, UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS);
            dpm.addUserRestriction(adminName, UserManager.DISALLOW_CREATE_WINDOWS);


            int pluggedInto = BatteryManager.BATTERY_PLUGGED_AC |
                    BatteryManager.BATTERY_PLUGGED_USB |
                    BatteryManager.BATTERY_PLUGGED_WIRELESS;
            dpm.setGlobalSetting(adminName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, String.valueOf(pluggedInto));

            PackageManager packageManager = getPackageManager();

            List<ApplicationInfo> packageList = packageManager.getInstalledApplications(0);
            ListIterator<ApplicationInfo> iterator = packageList.listIterator();


            while (iterator.hasNext()) {

                String packageName = iterator.next().packageName;

                //listView.append(packageName + "\n");

                if (packageName.equals("com.android.launcher3")) {
                    dpm.setApplicationHidden(adminName, packageName, true);
                }
                if (packageName.equals("com.android.systemui")) {
                    dpm.setApplicationHidden(adminName, packageName, true);
                }

            }

            if (dpm.isLockTaskPermitted(context.getPackageName())) {
                this.startLockTask();
            } else {
                Toast.makeText(context, "Could not lock task", Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(context, "App is not a Device Admin", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start);

        activity = this;
        context = this;

        // setLock();

        CardView mainMenuButton = findViewById(R.id.continue_button);
        ConstraintLayout hdoCheckInButton = findViewById(R.id.check_in_button);

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_MainMenu.class);
                startActivityForResult(intent, ACTIVITY_MAIN);
            }
        });

     /*   hdoCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, tcn_verifone_tutorial_step_one.class);
                startActivity(intent);
            }
        });
*/
        // start video
        videoview = (VideoView) findViewById(R.id.videoPanel);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_mp4);
        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoview.start(); //need to make transition seamless.
            }
        });


        /*
         *
         * Date
         *
         * */
      /*  textViewDate = findViewById(R.id.textViewDate);
        setDateTime();*/

        /*
         *
         * CALLING INIT METHOD USB CAMERA
         *
         * */
        /*mTextureView = findViewById(R.id.camera_view);
        mUVCCameraView = (CameraViewInterface) mTextureView;
        initUsbCamera();*/

        /*
         *
         *
         * INIT SLIDER
         *
         * */
        initSlider();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(videoview!=null) {
            videoview.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==20 && myCart.getDiscountValue()>0){
            Intent intent = new Intent(context, tcn_verifone_MainMenu.class);
            startActivityForResult(intent, ACTIVITY_MAIN);
        }else {
            if (myCart.getPaid()) {
                setResult(resultCode);
                finish();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if (e.getAction() == KeyEvent.ACTION_DOWN) {

            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;

            // new version
            if (pressedKey == 0x09) {
                Intent barcodeIntent = new Intent(activity, tcn_verifone_BarcodeResult.class);
                barcodeIntent.putExtra("barcode", barcode);
                startActivityForResult(barcodeIntent, 20);

                barcode = "";

            }
        }
    /*   if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            //Toast.makeText(getApplicationContext(),"barcode--->>>" + barcode, Toast.LENGTH_LONG).show();

            Intent barcodeIntent = new Intent(activity,tcn_verifone_BarcodeResult.class);
            barcodeIntent.putExtra("barcode",barcode);
            startActivity(barcodeIntent);

            barcode="";
        }
*/

        return super.dispatchKeyEvent(e);
    }




    /*
     *
     * ACTIVITY LIFECYCLE METHODS CONTROLLING USB CAMERA INSTANCE
     *
     * */

   /* @Override
    protected void onStart() {
        super.onStart();
        showShortMsg("ON START");
        //initUsbCamera();
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //initUsbCamera();
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }*/


    /*
     *
     * USB CAMERA METHODS & Class
     *
     * */

    /*private void initUsbCamera() {
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper.initUSBMonitor(tcn_verifone_SaleMainScreen.this, mUVCCameraView, new UVCCameraHelper.OnMyDevConnectListener() {
            @Override
            public void onAttachDev(UsbDevice device) {
                // request open permission
                if (!isRequest) {
                    isRequest = true;
                    if (mCameraHelper != null) {
                        int i = 0;
                        for (UsbDevice usbDevice : mCameraHelper.getUsbDeviceList()) {
                            if (usbDevice.getProductId() == 12416 && usbDevice.getVendorId() == 3804) {
                                mCameraHelper.requestPermission(i);
                            }
                            i++;
                        }
                    }
                }
            }

            @Override
            public void onDettachDev(UsbDevice device) {
                // close camera
                if (isRequest) {
                    isRequest = false;
                    mCameraHelper.closeCamera();
                    showShortMsg(device.getDeviceName() + " is out");
                }
            }

            @Override
            public void onConnectDev(UsbDevice device, boolean isConnected) {
                if (!isConnected) {
                    showShortMsg("fail to connect,please check resolution params");
                    isPreview = false;
                } else {
                    isPreview = true;
                    // need to wait UVCCamera initialize over
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Looper.prepare();
                            Looper.loop();
                        }
                    }).start();
                }
            }

            @Override
            public void onDisConnectDev(UsbDevice device) {
                //showShortMsg("disconnecting");
            }
        });
    }*/

   /* @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();

    }

    @Override
    public void onDialogResult(boolean canceled) {
        showShortMsg("CANCEL OPERATION");
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
        *//*
         *
         * VIEW NOT MUTABLE, NO MODIFICATIONS REQUIRED
         *
         * *//*
    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }*/


    /*
     *
     * For Toast Only
     *
     * */
    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /*
     *
     * For date and time
     *
     * */

   /* private void setDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd'th' MMM yyyy", Locale.getDefault());
        Date date = new Date();
        textViewDate.setText(dateFormat.format(date));
    }*/


    private void initSlider() {
        sliderView = findViewById(R.id.imageSlider);
        sliderAdapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        renewItems();

        sliderView.setOnIndicatorClickListener(position -> {
            /*
             *
             * SLIDER CLICK LISTENER FOR LATER IMPLEMENTATIONS
             *
             * */
        });

    }


    public void renewItems() {
        counter ++;

        ArrayList<SliderItem> sliderItemList = new ArrayList<>();
        sliderItemList.add(new SliderItem(R.mipmap.one));
        sliderItemList.add(new SliderItem(R.mipmap.three));
        sliderItemList.add(new SliderItem(R.mipmap.four));
        sliderItemList.add(new SliderItem(R.mipmap.five));
        sliderItemList.add(new SliderItem(R.mipmap.six));
        sliderItemList.add(new SliderItem(R.mipmap.two));

        if(counter == 1){
            sliderAdapter.renewItems(sliderItemList);
        }else{
            new Handler().postDelayed(() -> sliderAdapter.renewItems(sliderItemList), 4000);
            counter = 0;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sliderAdapter = null;
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

}