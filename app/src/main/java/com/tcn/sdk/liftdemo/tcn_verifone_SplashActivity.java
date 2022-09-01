package com.tcn.sdk.liftdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tcn.liftboard.control.PayMethod;
import com.tcn.liftboard.control.TcnShareUseData;
import com.tcn.liftboard.control.TcnVendEventID;
import com.tcn.liftboard.control.TcnVendEventResultID;
import com.tcn.liftboard.control.TcnVendIF;
import com.tcn.liftboard.control.VendEventInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import controller.VendService;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * splash screen activity class
  * handles startup and shipping processes
 *
 * class divided in two parts - splash and shipping
 *
 * after startup process completed (splash part), activity starts MainActivity
 * after MainActivity returned result, it starts shipping process
 * after shipping process completion it starts MainActivity again and so on
 */

public class tcn_verifone_SplashActivity extends AppCompatActivity {
    private Intent tcnVendService;

    Context context;

    // tcn shipping states for out state machine
    private static final int STAGE_WAITING = 0;
    private static final int STAGE_BOARD_INFO = 1;
    private static final int STAGE_SLOT_INFO = 2;
    private static final int STAGE_SHIPPING = 3;
    private static final int STAGE_TAKE_YOUR_GOODS = 4;
    private static final int STAGE_SHIPPING_FAILURE = 5;

    // shipping timeout
    private static final int SHIPPING_TIMEOUT = 180000;
    private Boolean shippingTimeout = false;
    private Integer shippingTimeoutId=0;

    // items
    private ArrayList<Pair<Integer, Integer>> items;

    // items shipping numbmer
    private Integer shippingIndex;

    // shipping lane
    private Integer shippingLane;

    // current shipping stage
    private int shippingStage = STAGE_WAITING;

    // not used
    private Boolean reqSlotInProgress = false;
    private Boolean shippingInProgress = false;
    private int errorCode = 0;
    private boolean result = false;
    private int slotSelectCount = 0;

    //
    private static final int ACTIVITY_MAIN = 1;
    private static final String TAG = "tcn_verifone_Splash";
    private static final Long SERVICE_TIMEOUT = 5L;

    // progress bar element
    private ProgressBar pb;

    // textview for logging
    TextView logView;
    // overal slots number
    private static final int SLOTS = 59;

    // mode
    private static final int TEST_MODE_SLOT = 1;
    private int testMode = TEST_MODE_SLOT;

    // Test slot
    private int slotNo = 0;
    private int testedSlotNo = 0;

    // board testing flag
    private Boolean boarTestingInProgress=false;

    // list for prepared intems
    private ArrayList<Integer> preparedItems;

    // another log textview
    private TextView logText;

    // UI elements
    private ImageView loadingBalls;
    private ImageView takeitemImage;
    private ImageView logoImage;

    // handler for balls rotating
    private Handler handler;
    // balls rotating angle
    private float rotation=0;

    // UI elements
    private TextView dateReceipt;
    private TextView amountReceipt;
    private TextView tidReceipt;
    private TextView cardTypeReceipt;
    private TextView gstReceipt;

    // Activity mode flag
    private Boolean activityMode = false;

    // shipping ID
    private Integer shippingId = 0;

    // not used
    private Long shippingFailureStartTime=0L;
    private Integer takeYourItemsMode = 0;

    // slot reselect flag
    private Boolean reSelect = false;

    // DPM related variables
    private DevicePolicyManager dpm;
    private ComponentName adminName;

    // UI element
    private ScrollView scroller;

    // transaction id
    private Integer txid =0;

    // main activity flag
    private Boolean mainActivityStarted=false;

    /**
     * Append string to log
     * @param logString String for logging
     */
    private void appendLog(String logString){

        Log.d("DBG",logString);

        if(activityMode){
            logText.append(logString+"\n");
        }else{
            logView.append(logString+"\n");
        }
    }

    /**
     * set DPM lock to switch in to kiosk mode
     */
    private void setLock(){

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
                    Toast.makeText(context,"launcher3 locked",Toast.LENGTH_LONG).show();
                }
                if (packageName.equals("com.android.systemui")) {
                    dpm.setApplicationHidden(adminName, packageName, true);
                    Toast.makeText(context,"SystemUI locked",Toast.LENGTH_LONG).show();
                }

            }

            if (dpm.isLockTaskPermitted(context.getPackageName())) {
                this.startLockTask();
            } else {
                Toast.makeText(context,"Could not lock task",Toast.LENGTH_LONG).show();
            }

        }catch(Exception ex){
            Toast.makeText(context, "App is not a Device Admin", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make app fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tcn_verifone__splash);
        context = this;

        // switch it to kiosk mode
        setLock();

        // get current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        long time = calendar.getTimeInMillis();

        // prepare report service
        Intent intent = new Intent(this, tcn_verifone_ReportService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_HOUR, pintent);

        // bind UI elements
        logView = findViewById(R.id.tcn_verifone_splash_log_textView);
        pb = findViewById(R.id.tcn_verifone_splash_progressBar);

        Button exitButton = findViewById(R.id.tcn_verifone_splash_exit_button);

        // set onclicklistener for exit button
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, tcn_verifone_MainMenu.class);
                Log.d("DBG","start MainActivity from splash on exitbutton");
                shippingTimeout=false;
                if(!mainActivityStarted) {
                    mainActivityStarted=true;
                    startActivityForResult(intent, ACTIVITY_MAIN);
                }
            }
        });

        // load images from disk into image cache
        loadImages();

        // check TCN service and start it
        if(!TcnVendIF.getInstance().isServiceRunning()) {
           // Toast.makeText(context,"TcnVendService not started, starting...",Toast.LENGTH_SHORT);
            startTcnVendService();
        }

        // waiting for service up
        Long StartTime = System.currentTimeMillis()/1000L;
        while(!TcnVendIF.getInstance().isServiceRunning() && (System.currentTimeMillis()-StartTime)<SERVICE_TIMEOUT){
            try{
                Thread.sleep(500L);
            }catch (Exception ex){}
        }

        // check if service up
        if(!TcnVendIF.getInstance().isServiceRunning()){
            // error
            appendLog(getText(R.string.tcn_verifone_main_splash_service_not_running).toString());
        }else {
            // register listener
            appendLog(getText(R.string.tcn_verifone_main_splash_service_started).toString());
            TcnVendIF.getInstance().registerListener(m_vendListener);
            testBoard(null);
        }



    }

    /**
     * load images from disk into image cachce
     */
    private void loadImages(){

        // create list
        ArrayList<tcn_verifone_AuxImage> images = new ArrayList<tcn_verifone_AuxImage>();

        // get images from DB
        images = myDB.ImagesGetAll();

        // load images from the disk
        myImageCache.preload(images);

    }

    /**
     * test TCN board
     * @param event
     */
    private void testBoard(VendEventInfo event){

        // test for the first start
        if(!activityMode) {
            appendLog("testBoard");

            if (event == null) {
                boarTestingInProgress = true;

                Handler bTestHandler = new Handler();
                bTestHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (boarTestingInProgress) {
                            testBoard(null);
                        }
                    }
                }, 20000L);

                // request board status
                appendLog("Querying board status");
                TcnVendIF.getInstance().reqQueryStatus(-1);
            } else {
                if (event.m_iEventID != TcnVendEventID.COMMAND_SYSTEM_BUSY) {
                    switch (event.m_lParam1) {
                        case TcnVendEventResultID.STATUS_FREE:
                            appendLog(event.m_lParam4);
                            break;
                        case TcnVendEventResultID.STATUS_BUSY:
                            appendLog(getText(R.string.background_notify_sys_busy).toString());
                            break;
                        case TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS:
                            appendLog(getText(R.string.background_notify_receive_goods).toString());
                            break;
                        case TcnVendEventResultID.CMD_NO_DATA_RECIVE:
                            appendLog(getString(R.string.background_drive_check_seriport));
                            break;
                    }
                    boarTestingInProgress = false;
                    testSlots(null);
                }
            }
        }
    }

    /**
     * test TCN slots one by one
     * @param event
     */
    private void testSlots(VendEventInfo event) {

        if (event != null) {
            switch (event.m_iEventID){
                case TcnVendEventID.CMD_QUERY_SLOT_STATUS:
                    appendLog("testSlot: Lane " + event.m_lParam1 +" status - " + event.m_lParam4);
                        testedSlotNo++;
                        if(testedSlotNo==SLOTS){
                            myCart.Init();
                            Intent intent = new Intent(context,tcn_verifone_MainActivity.class);
                            Log.d("DBG","start MainActivity after slot tests");
                            shippingTimeout=false;
                            if(!mainActivityStarted){
                                mainActivityStarted=true;
                                startActivityForResult(intent, ACTIVITY_MAIN);
                            }

                        }
                    break;
            }
        }
        slotNo++;
        if (slotNo <= SLOTS) {
            TcnVendIF.getInstance().reqQuerySlotStatus(slotNo);

        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("DBG","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("DBG","onResume");

        // activity resumed from sale activity
        mainActivityStarted=false;

        // check for paid status
        if(myCart.getPaid()){
            // prepare shipping
            preShipping();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DBG","onPause");
        //  TcnVendIF.getInstance().unregisterListener(m_vendListener);
    }

    /**
     * start TCN service
     */
    void startTcnVendService() {

        if (!TcnVendIF.getInstance().isServiceRunning()) {
            tcnVendService = new Intent(getApplication(), VendService.class);
            appendLog("Starting Vend Service");
            startService(tcnVendService);
        }
    }

    /**
     * stop TCN service
     */
    void stopTcnVendService(){
        if (TcnVendIF.getInstance().isServiceRunning()) {
            appendLog("Stopping Vend Service");
            stopService(tcnVendService);
        }
    }

    /**
     * convert VendEventInfo into string representation for loggin purposes
     * @param cEventInfo
     * @return
     */
    private String ConvertcEventInfoToString(VendEventInfo cEventInfo){
        String result ="result - ";
        try {
            result += "Code=" + cEventInfo.m_iEventID;
            result += " P1=" + cEventInfo.m_lParam1;
            result += " P2=" + cEventInfo.m_lParam2;
            result += " P3=" + cEventInfo.m_lParam3;
            if (cEventInfo.m_lParam4 != null) {
                result += " P4=" + cEventInfo.m_lParam4;
            }
            if (cEventInfo.m_lParam5 != null) {
                result += " P5=" + cEventInfo.m_lParam5;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * TCN board events processing
     * @param cEventInfo
     */
    private void processVendEventSplash(VendEventInfo cEventInfo){

        switch (cEventInfo.m_iEventID) {

            case TcnVendEventID.TEMPERATURE_INFO:
                appendLog("TEMPERATURE_INFO " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_LIFTER_UP:
                appendLog("CMD_LIFTER_UP " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.PROMPT_INFO:
                appendLog("PROMPT_INFO " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_TEST_SLOT:
                appendLog("CMD_TEST_SLOT " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SELECT_GOODS:
                appendLog("COMMAND_SELECT_GOODS " + ConvertcEventInfoToString(cEventInfo));
                // call event handler for Goods Select
                onCmdGoodsSelect(cEventInfo);
                break;
            case TcnVendEventID.CMD_QUERY_SLOT_STATUS:
                appendLog("CMD_QUERY_SLOT_STATUS " + ConvertcEventInfoToString(cEventInfo));
                // test next slot
                testSlots(cEventInfo);
                break;
            case TcnVendEventID.CMD_MACHINE_LOCKED:
                appendLog("CMD_MACHINE_LOCKED " + ConvertcEventInfoToString(cEventInfo));
                //TcnVendIF.getInstance().reqCleanDriveFaults(-1);
                break;
            case TcnVendEventID.CMD_CLEAN_FAULTS:
                appendLog("CMD_CLEAN_FAULTS " + ConvertcEventInfoToString(cEventInfo));
                break;

            case TcnVendEventID.CMD_QUERY_STATUS_LIFTER:
                appendLog("CMD_QUERY_STATUS_LIFTER " + ConvertcEventInfoToString(cEventInfo));
                // test board
                testBoard(cEventInfo);
                break;

            case TcnVendEventID.COMMAND_SELECT_FAIL:
                appendLog("COMMAND_SELECT_FAIL " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
                appendLog("CMD_TAKE_GOODS_DOOR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                appendLog("COMMAND_SYSTEM_BUSY " + ConvertcEventInfoToString(cEventInfo));
                // test board
                testBoard(cEventInfo);
                break;
            case TcnVendEventID.SERIAL_PORT_SECURITY_ERROR:
                appendLog("COMMAND_SECURITY_ERROR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.SERIAL_PORT_CONFIG_ERROR:
                appendLog("COMMAND_CONFIG_ERROR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.SERIAL_PORT_UNKNOWN_ERROR:
                appendLog("COMMAND_UNKNOWN_ERROR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SHIPPING:    //正在出货
                appendLog("COMMAND_SHIPPING " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:    //出货成功
                appendLog("COMMAND_SHIPMENT_SUCCESS " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:    //出货失败
                appendLog("COMMAND_SHIPMENT_FAILURE " + ConvertcEventInfoToString(cEventInfo));
                break;
            default:
                appendLog("ERROR NO " + cEventInfo.m_iEventID + ConvertcEventInfoToString(cEventInfo));
                break;
        }


    }

    /**
     * TCN events listener
     * catches events from the TCN board and process it
     */
    private tcn_verifone_SplashActivity.VendListener m_vendListener = new tcn_verifone_SplashActivity.VendListener();
    private class VendListener implements TcnVendIF.VendEventListener {
        @Override
        public void VendEvent(VendEventInfo cEventInfo) {
            try {
             //   Toast.makeText(getApplicationContext(), "event", Toast.LENGTH_LONG);
                if (null == cEventInfo) {
                    TcnVendIF.getInstance().LoggerError(TAG, "VendListener cEventInfo is null");
                    return;
                }
                // switch processing depend on activity mode flag
                if(!activityMode){
                    processVendEventSplash(cEventInfo);
                }else{
                    processVendEventInfoShipping(cEventInfo);
                }
            }catch (Exception ex){
                //appendLog("VendEvent ex"+ex.getMessage());
		ex.printStackTrace();
            }
        }
    }

    /**
     * prepare shipping process
     */
    private void preShipping(){

        appendLog("shipping");


        // get transaction id for timeout timer
        shippingTimeoutId=myCart.getTxid();

        // start shipping processinng
        processShipping();

        // start main activity after shipping completed
        Handler shippingTimeoutHandler = new Handler();
        shippingTimeout = true;

        // start shipping timeout handler
        shippingTimeoutHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("DBG","process shipping timeout");
                if (shippingTimeout && shippingTimeoutId==myCart.getTxid()) {
                    shippingTimeout = false;
                    shippingTimeoutId=-1;
                    myCart.Init();
                    Intent intent = new Intent(context, tcn_verifone_MainActivity.class);
                    Log.d("DBG","start MainActivity after shipping timeout");
                    if(!mainActivityStarted) {
                        mainActivityStarted=true;
                        startActivityForResult(intent, ACTIVITY_MAIN);
                    }

                }
            }
        }, SHIPPING_TIMEOUT);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    Log.d("DBG","onActivityResult");

    }

    // process slot event information
    private void processSlot(VendEventInfo cEventInfo) {

        switch (cEventInfo.m_iEventID) {
            case TcnVendEventID.COMMAND_SELECT_GOODS:
                appendLog("processSlot COMMAND_SELECT_GOODS " + ConvertcEventInfoToString(cEventInfo));
                // process onCmdGoodsSelect event
                onCmdGoodsSelect(cEventInfo);
                break;
            case TcnVendEventID.CMD_CLEAN_FAULTS:
                appendLog("processSlot CMD_CLEAN_FAULTS " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SELECT_FAIL:
                appendLog("processSlot COMMAND_SELECT_FAIL " + ConvertcEventInfoToString(cEventInfo));
                // process onCmdGoodsSelect event
                onCmdGoodsSelect(cEventInfo);
                break;
            case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
                appendLog("processSlot CMD_TAKE_GOODS_DOOR " + ConvertcEventInfoToString(cEventInfo));
                // process onCmdGoodsSelect event
                onCmdGoodsSelect(cEventInfo);
                break;

        }
    }

    /**
     * process shipping failure event
     * @param cEventInfo
     */
    private void processShippingFailure(VendEventInfo cEventInfo) {


        appendLog("processShipping COMMAND_SHIPMENT_FAILURE " + ConvertcEventInfoToString(cEventInfo));
        // todo restart shipping
        shippingStage = STAGE_SHIPPING_FAILURE;
        shippingFailureStartTime = System.currentTimeMillis();
        Handler failureHandler = new Handler();
        failureHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (shippingStage == STAGE_SHIPPING_FAILURE) {
                    shippingTimeout = false;
                    myCart.Init();
                    Intent intent = new Intent(context, tcn_verifone_OutOfOrder.class);
                    startActivity(intent);
                }
            }
        }, 5000L);

    }

    /**
     * restart shipping
     */
    private void restartShipping(){

        Handler nextHandler = new Handler();
        nextHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextShipping();
            }
        }, 10000L);

    }

    /**
     * process shipping
     * @param cEventInfo
     */
    private void processShipping(VendEventInfo cEventInfo){

        switch (cEventInfo.m_iEventID) {
            case TcnVendEventID.COMMAND_SHIPPING:
                appendLog("processShipping COMMAND_SHIPPING " + ConvertcEventInfoToString(cEventInfo));
                // update UI (display rotating balls)
                shipping();
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                appendLog("processShipping COMMAND_SHIPMENT_SUCCESS " + ConvertcEventInfoToString(cEventInfo));
                // display "take your item" message
                takeYourItem();
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                appendLog("processShipping COMMAND_SHIPMENT_FAILURE " + ConvertcEventInfoToString(cEventInfo));

                // shipping process failed
                processShippingFailure(cEventInfo);
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                // critical error, display "out of order" screen
                appendLog("processShipping COMMAND_SHIPMENT_FAULT " + ConvertcEventInfoToString(cEventInfo));
                // TODO lane failure. disable lane! show error
                myCart.Init();
                Intent intent = new Intent(context, tcn_verifone_OutOfOrder.class);
                startActivity(intent);
                break;
            case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
                    // oops, item still int he tray, show "take your item" message and restart shipping
                    appendLog("items in the tray on shipping stage");
                    takeYourItem();
                    restartShipping();
                break;


        }
    }

    /**
     * process Take your goods event
     * @param cEvent
     */
    private void processTakeYourGoods(VendEventInfo cEvent){


        switch (cEvent.m_iEventID){

            // item still in the tray, show "take your item" message
            case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
                appendLog("Goods still in the tray");
                takeYourItem();
                //reSelect=true;
                //reselectSlotDelayed();
                break;

                // process lift status
            case TcnVendEventID.CMD_QUERY_STATUS_LIFTER: {
                switch(cEvent.m_lParam1){
                    // lift free and we can start next item shipping
                    case TcnVendEventResultID.STATUS_FREE:
                        appendLog("STATUS_FREE");

                        tcn_verifone_AuxLane AL = myDB.LanesGetById(shippingLane.longValue());
                        AL.setAmount(AL.getAmount()-1);
                        myDB.LanesUpdateItem(AL);
                        shippingIndex++;
                        nextShipping();
                        break;

                        // lift is busy or item in the tray, do nothing
                    case TcnVendEventResultID.STATUS_BUSY:
                        appendLog("STATUS_BUSY");
                        break;
                    case TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS:
                        appendLog("STATUS_WAIT_TAKE_GOODS");
                        break;
                }

            }

        }


    }

    /**
     * reselect delayed slot
     */
    private void reselectSlotDelayed(){
        Handler selectHandler = new Handler();
        selectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(reSelect){
                    shippingStage=STAGE_SLOT_INFO;
                    TcnVendIF.getInstance().reqSelectSlotNo(shippingLane);
                    reselectSlotDelayed();
                }
            }
        },5000L);

    }

    /**
     * process onCmdGoodsSelect event
     * @param event
     */
    private void onCmdGoodsSelect(VendEventInfo event){

        appendLog("onCmdGoodsSelect");

        switch (event.m_iEventID){

            // slot select failed, try to select it again
            case TcnVendEventID.COMMAND_SELECT_FAIL:
                appendLog("Rerequest slot select");
                if(!reSelect) {
                    reselectSlotDelayed();
                }
                break;
            // slot selected successfully, request shipping
            case TcnVendEventID.COMMAND_SELECT_GOODS:
                appendLog("Start shipping from lane " + shippingLane);
                reSelect=false;
                reqSlotInProgress = false;
                shippingInProgress = true;
                shippingId++;
                shippingStage = STAGE_SHIPPING;
                TcnVendIF.getInstance().reqShip(shippingLane, PayMethod.PAYMETHED_NONE, myCart.getTransaction().getAmount().toString(), shippingId.toString());
                break;
            // item still in the tray, show "take your item" message
            case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
                appendLog("Goods in the tray on select stage");
                reSelect=false;
                takeYourItem();
                break;
        }

    }

    /**
     * dispatch catched TCN events depending on the current stage
     * @param cEventInfo
     */
    private void processVendEventInfoShipping(VendEventInfo cEventInfo){


        switch (shippingStage){
            case STAGE_SLOT_INFO:
                processSlot(cEventInfo);
                break;
            case STAGE_SHIPPING:
                processShipping(cEventInfo);
                break;
            case STAGE_TAKE_YOUR_GOODS:
                processTakeYourGoods(cEventInfo);
                break;
            case STAGE_SHIPPING_FAILURE:
                processShippingFailure(cEventInfo);
                break;

        }

        appendLog("stage = "+shippingStage);
        switch (cEventInfo.m_iEventID) {

            case TcnVendEventID.TEMPERATURE_INFO:
                appendLog("TEMPERATURE_INFO " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_LIFTER_UP:
                appendLog("CMD_LIFTER_UP " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.PROMPT_INFO:
                appendLog("PROMPT_INFO " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_TEST_SLOT:
                appendLog("CMD_TEST_SLOT " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SELECT_GOODS:
                appendLog("COMMAND_SELECT_GOODS " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_QUERY_SLOT_STATUS:
                appendLog("CMD_QUERY_SLOT_STATUS " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_MACHINE_LOCKED:
                appendLog("CMD_MACHINE_LOCKED " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_CLEAN_FAULTS:
                appendLog("CMD_CLEAN_FAULTS " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_QUERY_STATUS_LIFTER:
                appendLog("CMD_QUERY_STATUS_LIFTER " + ConvertcEventInfoToString(cEventInfo));
                break;

            case TcnVendEventID.COMMAND_SELECT_FAIL:
                appendLog("COMMAND_SELECT_FAIL " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
                appendLog("CMD_TAKE_GOODS_DOOR " + ConvertcEventInfoToString(cEventInfo));
                break;

            case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                appendLog("COMMAND_SYSTEM_BUSY " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.SERIAL_PORT_SECURITY_ERROR:
                appendLog("COMMAND_SECURITY_ERROR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.SERIAL_PORT_CONFIG_ERROR:
                appendLog("COMMAND_CONFIG_ERROR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.SERIAL_PORT_UNKNOWN_ERROR:
                appendLog("COMMAND_UNKNOWN_ERROR " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SHIPPING:    //正在出货
                appendLog("COMMAND_SHIPPING " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:    //出货成功
                appendLog("COMMAND_SHIPMENT_SUCCESS " + ConvertcEventInfoToString(cEventInfo));
                break;
            case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:    //出货失败
                appendLog("COMMAND_SHIPMENT_FAILURE " + ConvertcEventInfoToString(cEventInfo));
                break;
            default:
                appendLog("ERROR NO " + cEventInfo.m_iEventID + ConvertcEventInfoToString(cEventInfo));
                break;
        }
    }

    /**
     * scrolling handler
     */
   private void scrollDown(){

        scroller.fullScroll(View.FOCUS_DOWN);
        Handler scrollHandler = new Handler();
        scrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollDown();
            }
        },100);


    }

    /**
     * prepare UI part of the shipping
     */
    private void processShipping(){


        Log.d("DBG","processShipping");
        activityMode=true;
        shippingStage = STAGE_WAITING;

        setContentView(R.layout.activity_tcn_verifone__process_vend);
        scroller = findViewById(R.id.splash_scroll_view);
        logText = findViewById(R.id.logtext);

        scrollDown();

        Button reqslot = findViewById(R.id.reqslot);
        Button reqboard = findViewById(R.id.reqboard);
        Button reqshipp = findViewById(R.id.reqshipping);
        Button reqclean = findViewById(R.id.reqclean);

        reqslot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMode=true;
                TcnVendIF.getInstance().reqSelectSlotNo(2);
            }
        });

        reqboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        reqshipp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txid++;
                TcnVendIF.getInstance().reqShip(2, PayMethod.PAYMETHED_NONE, "10", txid.toString());

            }
        });

        reqclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanFaults();
            }
        });

        handler = new Handler();

        appendLog("processShipping");

        if(TcnVendIF.getInstance().isMachineLocked()){
            appendLog("machine is locked");

            TcnShareUseData.getInstance().setShipFailCountLock(100000);

        }

        loadingBalls = findViewById(R.id.loadingBalls);
        loadingBalls.setVisibility(View.INVISIBLE);
        takeitemImage = findViewById(R.id.loadingTakeItem);

        logoImage = findViewById(R.id.imageView2);

        dateReceipt = findViewById(R.id.tcn_verifone_process_vend_date_body);
        amountReceipt = findViewById(R.id.tcn_verifone_process_vend_amount_body);
        tidReceipt = findViewById(R.id.tcn_verifone_process_vend_tid_body);
        cardTypeReceipt = findViewById(R.id.tcn_verifone_process_vend_card_type_body);
        gstReceipt = findViewById(R.id.tcn_verifone_process_vend_gst_body);

        tcn_verifone_AuxTransaction ATR = myCart.getTransaction();

        if (ATR!=null) {
            dateReceipt.setText(ATR.getDatetime());
            amountReceipt.setText(ATR.getAmountFormatted());
            tidReceipt.setText(ATR.getTxid().toString());
            cardTypeReceipt.setText(ATR.getCardType());
            gstReceipt.setText(myDB.SettingsGetSingleValue("gst"));
        }
        //todo card type and gst

        rotateBalls();
        try {
            startShipping();
        }catch (Exception ex){
            //appendLog("Exception - "+ex.getMessage());
		ex.printStackTrace();
        }

    }

    /**
     * balls rotator
     */
    private void rotateBalls(){
        loadingBalls.setPivotX(loadingBalls.getWidth()/2);
        loadingBalls.setPivotY(loadingBalls.getHeight()/2+15);
        loadingBalls.setRotation(rotation);
        rotation=rotation+2;

        if(rotation>=358){
            rotation=0;
        }

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateBalls();
            }
        },50);

    }

    /**
     * prepare flat list of items for shipping
     */
    private void startShipping(){

        appendLog("startShipping");

        items = myCart.getItems();

        preparedItems = new ArrayList<Integer>();

        for(Pair<Integer,Integer> item:items){
            for(int a=0;a<item.second;a++){
                preparedItems.add(item.first);
            }
        }

        shippingIndex = 0;
        shippingLane = -1;

        appendLog("items to ship - "+preparedItems.size());
        nextShipping();

    }

    /**
     * start next shipping
     */
    private void nextShipping(){
        appendLog("nextShipping");

        try {

            appendLog("shippingIndex = "+shippingIndex + " items.size = "+items.size());

            // all items shipped, clear cart, start main activity
            if (shippingIndex == preparedItems.size()) {
              //  Toast.makeText(context, "next shipping finish", Toast.LENGTH_SHORT).show();
                // finish();
                //TODO clear cart
                myCart.Init();

                appendLog("finish");

        //        Toast.makeText(context, "FINISH", Toast.LENGTH_LONG).show();
                Handler finishDelay = new Handler();
                finishDelay.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shippingTimeout = false;
                        myCart.Init();
                        Intent intent = new Intent(context, tcn_verifone_MainActivity.class);
                        Log.d("DBG","start MainActivity after successful shipping");
                        if(!mainActivityStarted) {
                            mainActivityStarted=true;
                            startActivityForResult(intent, ACTIVITY_MAIN);
                        }
                    }
                }, 10000L);
            }else {

                // get lane for next item (if some lanes have similar items, will be selected lane with maximum amount of items
                ArrayList<tcn_verifone_AuxLane> AL = myDB.LanesGetByItemId(preparedItems.get(shippingIndex));
                int highestindex = -1;
                Integer highestamount = 0;
                for (int a = 0; a < AL.size(); a++) {
                    if (AL.get(a).getAmount() > highestamount) {
                        highestamount = AL.get(a).getAmount();
                        highestindex = AL.get(a).getId();
                    }
                }

                shippingLane = highestindex;

                // start shippig from selected lane
                appendLog("Shipping item no " + shippingIndex + " from lane " + shippingLane);

                reqSlotInProgress = true;
                shippingStage = STAGE_SLOT_INFO;
                TcnVendIF.getInstance().reqSelectSlotNo(shippingLane);
            }
        }catch(Exception ex){
//            appendLog("nextShipping ex "+ex.getMessage());
		ex.printStackTrace();
        }
    }

    /**
     * not used
     * @param result
     * @param errorCode
     */
    private void finishResult(Boolean result, int errorCode){
        Log.d("TCN","Finishit");
        this.result=result;
        this.errorCode=errorCode;

    }

    /**
     * display "Take your item" message
     */
    private void takeYourItem(){

        appendLog("Update UI for take your item stage");
        shippingStage = STAGE_TAKE_YOUR_GOODS;
        try {
            if (loadingBalls != null && logoImage != null) {
                loadingBalls.setVisibility(View.INVISIBLE);
                takeitemImage.setVisibility(View.VISIBLE);
                logoImage.setVisibility(View.INVISIBLE);
            }
        }catch (Exception ex){
//            appendLog("takeYourItem ex "+ex.getMessage());
		ex.printStackTrace();
        }

    }

    /**
     * prepare UI for shipping. Displays rotating balls
     */
    private void shipping(){

        appendLog("Update UI for shipping stage");
        if(loadingBalls!=null && logoImage!=null) {
            loadingBalls.setVisibility(View.VISIBLE);
            takeitemImage.setVisibility(View.INVISIBLE);
            logoImage.setVisibility(View.VISIBLE);
        }

    }

    /**
     * clear current lane fault
     */
    private void cleanFaults() {
        TcnVendIF.getInstance().reqCleanDriveFaults(shippingLane);
    }

    /**
     * back button processing
     */
    public void onBackPressed() {

        myCart.Init();
        Intent intent = new Intent(context, tcn_verifone_MainActivity.class);
        Log.d("DBG","start MainActivity from splash on back");
        shippingTimeout=false;
        if(!mainActivityStarted) {
            mainActivityStarted=true;
            startActivityForResult(intent, ACTIVITY_MAIN);
        }

    }

}
