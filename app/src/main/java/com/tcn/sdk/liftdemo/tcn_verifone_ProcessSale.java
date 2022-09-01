package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import static controller.VendApplication.myDB;

/**
 * deprecated class for payment process
 * not used
 */

public class tcn_verifone_ProcessSale extends tcn_verifone_CustomActivity {

    private static final int STAGE_CONNECT = 0;
    private static final int STAGE_PURCHASE = 1;
    private static final int STAGE_CONFIG_PRINTER = 2;
    private static final int SALE_OPERATION_TIMEOUT = 180000; //in milliseconds

    private TextView progressText;
    private tcn_verifone_AuxLane AL;
    private tcn_verifone_VLink VL;
    private String receipt = null;

    private int stage = STAGE_CONNECT;
    private Boolean isConnected = false;
    private String disconnectReason = "";


    Context context;

    final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.arg1==0) {
                onDisconnect(msg.obj.toString());
            }else{
                progressText.setText(msg.obj.toString());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__process_sale);

        context = this;

        progressText = findViewById(R.id.tcn_verifone_process_sale_progress_textView);

        Handler operationTimeoutHandler = new Handler();
        operationTimeoutHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishIt(false, null, getText(R.string.vlink_error_operation_timeout).toString());
            }
        }, SALE_OPERATION_TIMEOUT);

        Intent intent = getIntent();

        Long laneId = (Long) intent.getSerializableExtra("id");

        AL = myDB.LanesGetById(laneId);

        if (AL != null) {

            // make new Vlink
            VL = new tcn_verifone_VLink(myDB.SettingsGetSingleValue("verifone_ip_address"),myDB.SettingsGetSingleValue("verifone_port"),null);

            // get Transaction Id from db
            Integer transactionId = Integer.parseInt(myDB.SettingsGetSingleValue("transaction_id"));
            // pass it to tcn_verifone_VLink
            VL.setTxid(transactionId);

            // set operation complete listener
            VL.setListener(new tcn_verifone_VLink.OnOperationCompleted() {
                @Override
                public void operationCompleted(Boolean result, tcn_verifone_EFTPOStransaction trans, Boolean outOfOrder) {
                    onResult(result, trans);
                }
            });

            // update UI progress text
            progressText.setText(R.string.tcn_verifone_process_sale_progress_connecting);

            // connect to terminal

            VL.Connect();

            Long StartTime = System.currentTimeMillis() / 1000L;
            while (!VL.isConnected() && (System.currentTimeMillis() / 1000L - StartTime) < 2) {
            }

            if (!VL.isConnected()) {
                finishIt(false, null, getText(R.string.vlink_error_couldnot_connect).toString());
            } else {
                VL.purchase(0);
            }

        }

    }

    void onDisconnect(String reason){
        Toast.makeText(context,reason,Toast.LENGTH_LONG).show();
        progressText.setText(R.string.tcn_verifone_process_sale_error);
        finishIt(false,null,reason);
    }

    void onResult(Boolean result, tcn_verifone_EFTPOStransaction trans){

        if(result){

            if(trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_CONFIGURE_PRINTING_RESPONSE)){
                stage = STAGE_PURCHASE;
                Message msg = new Message();
                msg.arg1=1;
                msg.obj=getText(R.string.tcn_verifone_process_sale_process_prchase).toString();
                handler.sendMessage(msg);

                Integer txid = Integer.parseInt(myDB.SettingsGetSingleValue("transaction_id"))+1;
                myDB.SettingsUpdateSingleValue("transaction_id",txid.toString());
                VL.purchase(AL.getItem().getPrice());

            }

            if(trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_PRINT_REQUEST)){
                receipt = trans.getReceipt_text();
            }

            if(trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_RESULT_RESPONSE)){
                finishIt(result,trans,"");
            }
        }

    }

    void finishIt(Boolean result, tcn_verifone_EFTPOStransaction trans, String reason) {

        Intent intent = new Intent();
        intent.putExtra("id", AL.getId());
        intent.putExtra("amount", AL.getItem().getPrice().toString());

        if (trans != null) {
            intent.putExtra("txid", trans.getTxid());
        }

        if (!result && !trans.getRespcode().equals("aa")) {
            setResult(RESULT_CANCELED);
            if (reason.length() > 0) {
                intent.putExtra("reason", reason);
            } else {
                intent.putExtra("reason", trans.getRespcode());
            }
        } else {
            setResult(RESULT_OK, intent);
        }

        finish();

    }

}
