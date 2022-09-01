package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;

/**
 * payment processing activity class
 * handles interface and verifone payment process
 * @author v.vasilchikov
 */

public class tcn_verifone_ProcessPayment extends tcn_verifone_CustomActivity {

    private ImageView backButton;

    private static final int STAGE_CONNECT = 0;
    private static final int STAGE_PURCHASE = 1;
    private static final int STAGE_CONFIG_PRINTER = 2;

    private tcn_verifone_VLink VL;
    private String receipt=null;

    private int stage=STAGE_CONNECT;
    private Boolean isConnected = false;
    private String disconnectReason="";
    private Boolean finished=false;

    private static final String TAG="DBG";

    Context context;

    final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.arg1==0) {
                onDisconnect(msg.obj.toString());
            }else{
//                progressText.setText(msg.obj.toString());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment);

        context = this;

        // next three methods derived from CustomActivity class
        // disable disconnect timer
        stopDisconnectTimer();
        //  set no timeout mode
        setNoTimeout();
        // disable barcode scanning
        setNoScan();

        backButton=findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VL!=null){

                }
            }
        });


        processPayment();
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("DBG","ProcessPayment onStart");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DBG","ProcessPayment onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        VL.fullStop();
        VL=null;
    }

    /** payment processing via verifone pos terminal*/
    private void processPayment(){

        // for 0 price we do not need to contact verifone
        stopDisconnectTimer();
        try {
            // check for 100% discount (price ==0)
            if (myCart.getCartTotal() == 0) {

                // make new transaction
                tcn_verifone_AuxTransaction AT = new tcn_verifone_AuxTransaction();

                // set next transaction id
                Integer txid = Integer.parseInt(myDB.SettingsGetSingleValue("transaction_id")) + 1;
                myDB.SettingsUpdateSingleValue("transaction_id", txid.toString());

                // set some appropriate flags
                AT.setUnloaded(0);
                AT.setSuccessful(1);
                AT.setTxid(txid);
                AT.setMid(0);
                AT.setAmount(myCart.getCartTotal());

                // set date time
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                AT.setDatetime(formatter.format(date));
                for (Pair<Integer, Integer> pair : myCart.getItems()) {
                    AT.addItem(pair.first, pair.second);
                }

                // set discount uid and value
                AT.setDiscountUid(myCart.getDiscountUid());
                AT.setDiscountValue(myCart.getDiscountValue().toString());

                // store transaction in the databse
                myDB.TransactionsStore(AT);
                // add transaction to cart
                myCart.setTransaction(AT);

                // close activity
                finishIt(true, null, "");
                return;
            }

            // make new Vlink object
            VL = new tcn_verifone_VLink(myDB.SettingsGetSingleValue("verifone_ip_address"), myDB.SettingsGetSingleValue("verifone_port"), null);

            // get Transaction Id from db
            Integer transactionId = Integer.parseInt(myDB.SettingsGetSingleValue("transaction_id"));
            // pass it to tcn_verifone_VLink
            VL.setTxid(transactionId);

            // set operation complete listener
            Log.i(TAG,"Set listener");
            VL.setListener(new tcn_verifone_VLink.OnOperationCompleted() {
                @Override
                public void operationCompleted(Boolean result, tcn_verifone_EFTPOStransaction trans, Boolean outOfOrder) {
                    onResult(result, trans);

                }
            });

            // connect to verifone terminal
            Log.i(TAG,"Connect ot terminal");
            VL.Connect();

            // wait for terminal
            Long StartTime = System.currentTimeMillis() / 1000L;
            while (!VL.isConnected() && (System.currentTimeMillis() / 1000L - StartTime) < 3) {

            }

            // oops. terminal is not connected
            if (!VL.isConnected()) {

                Log.i(TAG,"Could not connect");
                // close activity with error
                finishIt(false, null, "notconnected");

            } else {
                Log.i(TAG,"Connected");
                // configure verifone terminal printing mode
                VL.configurePrinting(true);
            }
        }catch(Exception ex){

            ex.printStackTrace();
            finishIt(false, null, "notconnected");
        }

    }

    /** on terminal disconnect event */
    void onDisconnect(String reason){
        Log.i(TAG,"Disconnected");
        if(reason!=null) {
            Log.i(TAG,"Reason "+reason);
            Toast.makeText(context, reason, Toast.LENGTH_LONG).show();
        }
        // close activity
        finishIt(false,null,reason);
    }

    /** on result event */
    void onResult(Boolean result, tcn_verifone_EFTPOStransaction trans){

        Log.i(TAG,"On result");

        // positive result
        if(result){
            // process answer for printing config
            if(trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_CONFIGURE_PRINTING_RESPONSE)){
                stage = STAGE_PURCHASE;
                Message msg = new Message();
                msg.arg1=1;
                msg.obj=getText(R.string.tcn_verifone_process_sale_process_prchase).toString();
                handler.sendMessage(msg);

                Integer txid = Integer.parseInt(myDB.SettingsGetSingleValue("transaction_id"))+1;
                myDB.SettingsUpdateSingleValue("transaction_id",txid.toString());
                if(myCart.getDiscountValue()>0) {
                    Double amountDiscount = myCart.getCartTotal() * 1D;//myCart.getCartTotal()-myCart.getCartTotal()*myCart.getDiscountValue();
                    VL.purchase(amountDiscount.intValue());
                }else{
                    VL.purchase(myCart.getCartTotal());
                }
            }

            // process terminal print request
            if(trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_PRINT_REQUEST)){
                receipt = trans.getReceipt_text();
            }

            // process result
            if(trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_RESULT_RESPONSE)){
                finishIt(result,trans,"");
            }
        }else{
            // process result
            if(trans!=null && trans.getOper().toUpperCase().equals(tcn_verifone_VXLinkWrapper.COMMAND_RESULT_RESPONSE)){
                finishIt(result,trans,"");
            }else{
                finishIt(result,null,"terminal didn't answer");
            }

        }

    }

    void finishIt(Boolean result, tcn_verifone_EFTPOStransaction trans, String reason){

        if(!finished) {
            finished = true;
        }else{return;}

        tcn_verifone_AuxTransaction AT = new tcn_verifone_AuxTransaction();

        Log.i("DBG", "finishit: result="+result);

        // check if transaction is not null
        if(trans!=null) {
            // ok, we've got transaction
            // set information from it to our transaction object
            Log.i(TAG, "finishit: EFTPOS transactio ="+trans.toString());
            AT.setUnloaded(0);
            if (result) {
                AT.setSuccessful(1);
            } else {
                AT.setSuccessful(0);
            }
            AT.setOnlineflag(trans.getOnline_flag());
            AT.setRespcode(trans.getRespcode());
            AT.setReceipt(VL.getReceipt());
            AT.setMid(Integer.parseInt(trans.getMid()));
            AT.setTxid(Integer.parseInt(trans.getTxid()));
            AT.setAmount(myCart.getCartTotal());
            AT.setCardType(trans.getCard_type());

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();
            AT.setDatetime(formatter.format(date));
            for (Pair<Integer, Integer> pair : myCart.getItems()) {
                AT.addItem(pair.first, pair.second);
            }

            AT.setDiscountUid(myCart.getDiscountUid());
            AT.setDiscountValue(myCart.getDiscountValue().toString());

            // store transaction in the databse and add it to cart
            myDB.TransactionsStore(AT);
            myCart.setTransaction(AT);
        }else{
            Log.i(TAG, "finishit: reason="+reason);
        }

        Intent intent = new Intent();
        intent.putExtra("amount",myCart.getCartTotal().toString());

        if(trans!=null){
            myCart.setTransaction(AT);
        }


        if(!result) {
            // oops. error in payment process
            // mark it as unpaid
            myCart.setPaid(false);
            if(reason.length()>0){
                intent.putExtra("reason",reason);
            }else{
                intent.putExtra("reason",trans.getRespcode());
            }
        }else{
            // okay, transaction successfully completed
            // mark it as paid
            Log.i(TAG, "fishIt: transaction="+AT.toString());
            myCart.setPaid(true);
        }

        // close activty
        finish();
    }

}