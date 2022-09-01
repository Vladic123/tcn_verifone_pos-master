package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcn.liftboard.control.PayMethod;
import com.tcn.liftboard.control.TcnVendEventID;
import com.tcn.liftboard.control.TcnVendEventResultID;
import com.tcn.liftboard.control.TcnVendIF;
import com.tcn.liftboard.control.VendEventInfo;

import java.util.ArrayList;

import controller.VendService;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;

/**
 * deprecated class for vending activity
 * not used
 */

public class tcn_verifone_ProcessVend extends AppCompatActivity {

    private static final int STAGE_WAITING = 0;
    private static final int STAGE_BOARD_INFO = 1;
    private static final int STAGE_SLOT_INFO = 2;
    private static final int STAGE_SHIPPING = 3;
    private static final int STAGE_TAKE_YOUR_GOODS = 4;

    private Intent tcnVendService;
    private int shippingStage = STAGE_WAITING;

    private static final String TAG = "tcn_verifone_ProcessVend";

    private static final int PROCESS_SALE = 1;

    private static final int reqSlotStatus = 0;
    private static final int reqShip = 0;

    //  private Intent tcnVendService;
    private Context context;

    private int slotSelectCount=0;

    private int errorCode=0;
    private boolean result=false;
    private ArrayList<Pair<Integer,Integer>> items;
    private Integer shippingIndex;
    private Integer shippingLane;
    private Boolean reqSlotInProgress=false;
    private Boolean shippingInProgress = false;

    private ArrayList<Integer> preparedItems;

    private TextView logText;

    private ImageView loadingBalls;
    private ImageView logoImage;
    private Handler handler;
    private float rotation=0;

    private TextView dateReceipt;
    private TextView amountReceipt;
    private TextView tidReceipt;
    private TextView cardTypeReceipt;
    private TextView gstReceipt,txv_loadingball;

    private void appendLog(String logString){
        logText.append(logString+"\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__process_vend);
            finish();
        context = this;

        logText = findViewById(R.id.logtext);

        if(!TcnVendIF.getInstance().isServiceRunning()){
            logText.append("TcnVendService is not started. Starting");
            startTcnVendService();
        }

        while (!TcnVendIF.getInstance().isServiceRunning()) {
            try {
                Thread.sleep(500L);
            } catch (Exception ex) {
            }
        }
        logText.append("TcnVendService started");

        loadingBalls = findViewById(R.id.loadingBalls);
        logoImage = findViewById(R.id.imageView2);

        dateReceipt = findViewById(R.id.tcn_verifone_process_vend_date_body);
        amountReceipt = findViewById(R.id.tcn_verifone_process_vend_amount_body);
        tidReceipt = findViewById(R.id.tcn_verifone_process_vend_tid_body);
        cardTypeReceipt = findViewById(R.id.tcn_verifone_process_vend_card_type_body);
        gstReceipt = findViewById(R.id.tcn_verifone_process_vend_gst_body);
        txv_loadingball = findViewById(R.id.txv_loadingball);
        Intent data = getIntent();
        Integer txid = data.getIntExtra("txid", -1);

        tcn_verifone_AuxTransaction AT = myDB.TransactionsGetTransactionById(txid);

        ArrayList<tcn_verifone_AuxTransaction> ata = myDB.TransactionsGetAll();
        if (txid > 0) {
            dateReceipt.setText(AT.getDatetime());
            amountReceipt.setText(AT.getAmountFormatted());
            tidReceipt.setText(AT.getTxid().toString());
            cardTypeReceipt.setText(AT.getCardType());
            gstReceipt.setText("12345");
        }

        rotateBalls();
    }

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

    private void testBoard(){

        appendLog("Request board status");
        TcnVendIF.getInstance().reqQueryStatus(-1);
        shippingStage = STAGE_BOARD_INFO;


        Handler boardHandler = new Handler();
        boardHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // if(shippingStage==STAGE_BOARD_INFO){
                testBoard();
                //  }
            }
        },10000);

    }

    private void startShipping(){
        Toast.makeText(context," start shipping",Toast.LENGTH_SHORT).show();

        items = myCart.getItems();

        preparedItems = new ArrayList<Integer>();

        for(Pair<Integer,Integer> item:items){
            for(int a=0;a<item.second;a++){
                preparedItems.add(item.first);
            }
        }

        shippingIndex = 0;
        shippingLane = -1;

        appendLog("start shipping");
        appendLog("items to ship - "+preparedItems.size());

        Toast.makeText(context,"next shipping",Toast.LENGTH_SHORT).show();

        nextShipping();

    }

    private void nextShipping(){
        Toast.makeText(context,"next shipping start",Toast.LENGTH_SHORT).show();

        if(shippingIndex==items.size()){
            Toast.makeText(context,"next shipping finish",Toast.LENGTH_SHORT).show();
            finish();
            //TODO clear cart
        }

        ArrayList<tcn_verifone_AuxLane> AL = myDB.LanesGetByItemId(preparedItems.get(shippingIndex));
        int highestindex = -1;
        Integer highestamount = 0;
        for(int a=0;a<AL.size();a++){
            if(AL.get(a).getAmount()>highestamount){
                highestamount=AL.get(a).getAmount();
                highestindex=AL.get(a).getId();
            }
        }

        shippingLane=highestindex;

        appendLog("Shipping item no "+shippingIndex+" from lane "+shippingLane);

        reqSlotInProgress=true;
        appendLog("request slot no "+shippingLane);
        shippingStage = STAGE_SLOT_INFO;
        Toast.makeText(context,"next shipping req slot",Toast.LENGTH_SHORT).show();

        //myTcnVendIf.getInstance().reqSelectSlotNo(shippingLane);

/*        Handler reqSlotHandler = new Handler();
        reqSlotHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(reqSlotInProgress){
                    appendLog("Timeout, restart request slot");
                    nextShipping();
                }
            }
        },20000L);
*/
    }


    private void finishResult(Boolean result, int errorCode){
        Log.d("TCN","Finishit");
        this.result=result;
        this.errorCode=errorCode;

    }

    private void takeYourItem(){

        appendLog("Update UI for take your item stage");
        loadingBalls.setImageResource(R.drawable.lifthandup);
        logoImage.setVisibility(View.INVISIBLE);
        txv_loadingball.setVisibility(View.INVISIBLE);
    }

    private void shipping(){

        appendLog("Update UI for shipping stage");
        loadingBalls.setImageResource(R.drawable.loading);
        logoImage.setVisibility(View.VISIBLE);
        txv_loadingball.setVisibility(View.VISIBLE);

    }

    private void onReqSelectSlotNo(VendEventInfo event) {

        appendLog("onReqSelectSlotNo");
        Log.d("TCN","onReqSelectSlotNo");

        switch (event.m_lParam1) {
            case TcnVendEventResultID.STATUS_FREE:
                //amount = amount.substring(0,amount.length()-2)+"."+amount.substring(amount.length()-2,amount.length());
                appendLog("Status - free, start shipping from lane " + shippingLane);
                reqSlotInProgress = false;
                shippingInProgress = true;
                shippingStage=STAGE_SHIPPING;
                TcnVendIF.getInstance().reqShip(shippingLane, PayMethod.PAYMETHED_NONE, myCart.getTransaction().getAmount().toString(), myCart.getTransaction().getTxid().toString());
                break;
            case TcnVendEventResultID.STATUS_BUSY:
                // wait and stop after some timeout
                appendLog("System busy, retry " + slotSelectCount);
//                if (slotSelectCount < 10) {
                TcnVendIF.getInstance().reqSelectSlotNo(shippingLane);
/*                    slotSelectCount++;
                } else {
                    reqSlotInProgress = false;
                    appendLog("System still busy, exit");
                    finishResult(false, -1);
                }*/
                break;
            case TcnVendEventResultID.CMD_NO_DATA_RECIVE:
                // todo process error
                appendLog("CMD_NO_DATA_RECEIVE, exit");
                finishResult(false, -1);
                break;

        }
    }


    private void processBoardStatus(VendEventInfo event){
        Toast.makeText(context,"process board start",Toast.LENGTH_SHORT).show();
        Log.d("TCN","processBoardStatus");
        shippingStage=STAGE_WAITING;
        switch (event.m_lParam1) {
            case TcnVendEventResultID.STATUS_FREE:
                appendLog("BoardStatus: Free");
                startShipping();
                break;
            case TcnVendEventResultID.STATUS_BUSY:
                appendLog("BoardStatus: Busy");
                break;
            case TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS:
                appendLog("BoardStatus: Take your good!");
                // show Take your item
                appendLog("Take your goods!");
                takeYourItem();
                shippingIndex++;
                nextShipping();
                break;
            case TcnVendEventResultID.CMD_NO_DATA_RECIVE:
                appendLog("BoardStatus: System error");
                break;
        }
        Toast.makeText(context,"process board stop",Toast.LENGTH_SHORT).show();

    }

    private void processLifterStatus(VendEventInfo event){
        Toast.makeText(context,"process lifter start",Toast.LENGTH_SHORT).show();


/*        switch (shippingStage){
            case STAGE_BOARD_INFO:*/
        processBoardStatus(event);
/*                break;
        }*/
        Toast.makeText(context,"process lifter stop",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        TcnVendIF.getInstance().registerListener(m_vendListener);
        testBoard();

    }

    @Override
    protected void onPause() {
        super.onPause();
        TcnVendIF.getInstance().unregisterListener(m_vendListener);
    }

    void startTcnVendService() {

        if (!TcnVendIF.getInstance().isServiceRunning()) {
            tcnVendService = new Intent(getApplication(), VendService.class);
            startService(tcnVendService);
        }
    }

    private String ConvertcEventInfoToString(VendEventInfo cEventInfo) {
        Log.d("TCN","ConvertcEventInfoToString");
        String result="";
        try {
            result = "Code=" + cEventInfo.m_iEventID;
        } catch (Exception ex) {
            result = "Code=null";
        }

        try {
            result += " P1=" + cEventInfo.m_lParam1;
        } catch (Exception ex) {
            result += " P1=null";
        }
        try {
            result += " P2=" + cEventInfo.m_lParam2;
        } catch (Exception ex) {
            result += " P2=null";
        }
        try {
            result += " P3=" + cEventInfo.m_lParam3;
        } catch (Exception ex) {
            result += " P3=null";
        }
        if (cEventInfo.m_lParam4 != null) {
            try {
                result += " P4=" + cEventInfo.m_lParam4;
            } catch (Exception ex) {
                result += " P4=null";
            }
        }
        if (cEventInfo.m_lParam5 != null) {
            try {
                result += " P5=" + cEventInfo.m_lParam5;
            } catch (Exception ex) {
                result += " P5=null";
            }
        }


        return result;
    }


    private tcn_verifone_ProcessVend.VendListener m_vendListener = new VendListener();
    private class VendListener implements TcnVendIF.VendEventListener {
        @Override
        public void VendEvent(VendEventInfo cEventInfo) {
            Log.i("TCN","VendEvent");

            try {
                if (null == cEventInfo) {
                    appendLog("cEventInfo == null");
                    return;
                }
                appendLog("LISTENER_INFO " + ConvertcEventInfoToString(cEventInfo));
                Toast.makeText(context, "after listener start " + ConvertcEventInfoToString(cEventInfo), Toast.LENGTH_LONG).show();

                switch (cEventInfo.m_iEventID) {
                    case TcnVendEventID.PROMPT_INFO:
                        //Toast.makeText(context,"PROMPT_INFO "+ ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("PROMPT_INFO " + ConvertcEventInfoToString(cEventInfo));
                        break;
                    case TcnVendEventID.CMD_TEST_SLOT:
                        //Toast.makeText(context,"CMD_TEST_SLOT "+ ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("CMD_TEST_SLOT " + ConvertcEventInfoToString(cEventInfo));
                        break;
                    case TcnVendEventID.COMMAND_SELECT_GOODS:
                        //  appendLog("COMMAND_SELECT_GOODS " + ConvertcEventInfoToString(cEventInfo));
                        appendLog("COMMAND_SELECT_GOODS " + ConvertcEventInfoToString(cEventInfo));
                        break;
                    case TcnVendEventID.CMD_QUERY_SLOT_STATUS:
                        //Toast.makeText(context, "CMD_QUERY_SLOT_STATUS " + ConvertcEventInfoToString(cEventInfo), Toast.LENGTH_LONG).show();
                        appendLog("CMD_QUERY_SLOT_STATUS " + ConvertcEventInfoToString(cEventInfo));
                        Toast.makeText(context, "CMD_QUERY_SLOT_STATUS", Toast.LENGTH_LONG).show();
                        onReqSelectSlotNo(cEventInfo);
                        break;
                    case TcnVendEventID.CMD_QUERY_STATUS_LIFTER:
                        //Toast.makeText(context, "CMD_QUERY_STATUS_LIFTER " + ConvertcEventInfoToString(cEventInfo), Toast.LENGTH_LONG).show();
                        appendLog("CMD_QUERY_STATUS_LIFTER " + ConvertcEventInfoToString(cEventInfo));
                        Toast.makeText(context, "CMD_QUERY_STATUS_LIFTER", Toast.LENGTH_SHORT).show();

                        processLifterStatus(cEventInfo);
                        break;
                    case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                        //Toast.makeText(context,"COMMAND_SYSTEM_BUSY "+ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("COMMAND_SYSTEM_BUSY " + ConvertcEventInfoToString(cEventInfo));
                        break;
                    case TcnVendEventID.SERIAL_PORT_SECURITY_ERROR:
                        //Toast.makeText(context,"COMMAND_SECURITY_ERROR " + ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("COMMAND_SECURITY_ERROR " + ConvertcEventInfoToString(cEventInfo));
                        break;
                    case TcnVendEventID.SERIAL_PORT_CONFIG_ERROR:
                        //Toast.makeText(context,"COMMAND_CONFIG_ERROR " + ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("COMMAND_CONFIG_ERROR " + ConvertcEventInfoToString(cEventInfo));
                        break;
                    case TcnVendEventID.SERIAL_PORT_UNKNOWN_ERROR:
                        //Toast.makeText(context,"COMMAND_UNKNOWN_ERROR " + ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("COMMAND_UNKNOWN_ERROR " + ConvertcEventInfoToString(cEventInfo));
                        break;

                    case TcnVendEventID.COMMAND_SHIPPING:    //正在出货
                        // todo show shipping message
                        //Toast.makeText(context,"COMMAND_SHIPPING " + ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("COMMAND_SHIPPING " + ConvertcEventInfoToString(cEventInfo));
                        Toast.makeText(context, "COMMAND_SHIPPING", Toast.LENGTH_LONG).show();
                        shipping();
                        //progressText.setText(getText(R.string.tcn_verifone_process_vend_progress_shipping));
                        break;

                    case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:    //出货成功
                        //Toast.makeText(context,"COMMAND_SHIPMENT_SUCCESS " + ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        // todo show teke your good
                        appendLog("COMMAND_SHIPMENT_SUCCESS " + ConvertcEventInfoToString(cEventInfo));
                        Toast.makeText(context, "COMMAND_SHIPMENT_SUCCESS", Toast.LENGTH_LONG).show();
                        takeYourItem();
                        shippingInProgress = false;
                        //progressText.setText(getText(R.string.tcn_verifone_process_vend_progress_take_your_goods));
                        // finishResult(true,0);
                        break;

                    case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:    //出货失败
                        //Toast.makeText(context,"COMMAND_SHIPMENT_FAILURE " + ConvertcEventInfoToString(cEventInfo),Toast.LENGTH_LONG).show();
                        appendLog("COMMAND_SHIPMENT_FAILURE " + ConvertcEventInfoToString(cEventInfo));
                        // todo show shipment failure
                        // getText(R.string.tcn_verifone_process_vend_progress_shipping_failure);
                        break;
                    default:
                        appendLog("EVENT " + ConvertcEventInfoToString(cEventInfo));
                        break;

                }
            }catch (Exception ex){
                Toast.makeText(context,"Exception!",Toast.LENGTH_LONG).show();
            }
            Toast.makeText(context,"Listener stop",Toast.LENGTH_LONG).show();

        }
    }
}
