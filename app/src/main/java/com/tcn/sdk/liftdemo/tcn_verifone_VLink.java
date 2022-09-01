package com.tcn.sdk.liftdemo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.net.Socket;

/**
 * main class for handling payments with verifone POS
 */
public class tcn_verifone_VLink {

    public interface OnOperationCompleted {
        void operationCompleted(Boolean result, tcn_verifone_EFTPOStransaction trans, Boolean outOfOrder);

    }

    // printing mode
    public static final int VLINK_PRINTING_MODE_NO_PRINT = 0;
    public static final int VLINK_PRINTING_MODE_PRINT = 1;

    // operation
    private static final int VLINK_NO_OPERATION = 0;
    private static final int VLINK_PURCHASE = 1;

    //internal states
    public static final int VLINK_STATE_READY = 0;
    public static final int VLINK_STATE_IN_PROGRESS = 1;
    public static final int VLINK_STATE_COMPLETED = 2;
    public static final int VLINK_STATE_ERROR = 3;
    public static final int VLINK_STATE_TIMEOUT = 4;

    // operation timeout
    private static final Long VLINK_OPERATION_TIMEOUT = 240L; // 60 seconds

    private static final Long VLINK_CONNECTION_TIMEOUT = 30L;
    private tcn_verifone_VXLinkWrapper VX = new tcn_verifone_VXLinkWrapper();

    // verifone asynchronous client
    tcn_verifone_VLinkAsyncClient VC=null;
    // verifone connector
    tcn_verifone_VLinkConnector VCon=null;

    // terminal IP
    private String IPaddess;
    // terminal port
    private String Port;
    // transaction id
    private Integer txid;
    // merchant id
    private Integer mid;

    // internal state
    private Integer internalState = VLINK_STATE_READY;

    // parameters
    private String[] params = new String[4];

    // not used
    private int currentOperation = VLINK_NO_OPERATION;

    // EFTPOS transaction object
    private tcn_verifone_EFTPOStransaction trans;

    // transaction start time
    private Long transactionStartTime = 0L;

    // not used
    public Integer getState() {
        return internalState;
    }

    // EFTPOS transaction getter
    public tcn_verifone_EFTPOStransaction getTransaction() {
        return trans;
    }

    // operation completion listener
    OnOperationCompleted listener;

    // verifone outbound connector
    tcn_verifone_VLinkThreadConnectorOut VOut = null;
    // verifone inbound connector
    tcn_verifone_VLinkThreadConnectorIn VIn=null;

    // connection state flag
    private Boolean isConnected = false;

    // printer mode
    private int printerMode = VLINK_PRINTING_MODE_NO_PRINT;

    // receipt string
    private String receipt ="";

    // not used
    private int resultRequestRetryCounter = 0;
    // command retry counter
    private int commandRetryCounter = 0;
    // not used
    private int noResponseCounter = 0;
    // reconnect counter
    private int reconnectCounter = 0;
    // in process flag
    private Boolean inProcess = false;
    // handler
    private Handler handler;
    // last command
    private String lastCommand="";

    // operation completed flag
    private Boolean operationCompletedFlag=false;

    // reset flag
    public void ResetOperationCompletedFlag(){
        operationCompletedFlag=false;
    }

    // log tag
    private static final String TAG = "DBG";

    // not used
    private Boolean fullStopFlag = false;

    // receipt getter
    public String getReceipt(){
        return receipt;
    }

    // not used
    public void setPrinterMode(int printerMode) {
        this.printerMode = printerMode;
    }

    // connection state getter
    public boolean isConnected(){
        return isConnected;
    }

    /**
     * reset variables
     */
    public void reset() {
        internalState = VLINK_STATE_READY;
        currentOperation = VLINK_NO_OPERATION;
        trans = null;
        transactionStartTime = 0L;
        commandRetryCounter=0;
        reconnectCounter=0;
        lastCommand="";
        resultRequestRetryCounter=0;
    }

    /**
     * constructor
     * @param IPaddess IP address of the verifone POS
     * @param Port port of the verifone POS
     * @param listener listener for operation complete
     */
    tcn_verifone_VLink(String IPaddess, String Port, OnOperationCompleted listener) {
        this.IPaddess = IPaddess;
        this.Port = Port;
        this.listener = listener;
        txid = 0;
        mid = 0;

        try {
            // run message handler
            Log.d(TAG,"set handler");
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        Log.i("DBG", "handle message " + msg.obj.toString());
                        processAnswer(msg.obj.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            Log.d(TAG,"handler ="+handler);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * stop all
     */
    public void fullStop(){
        fullStopFlag=true;
        disconnect();

    }

    /**
     * disconnect inbound and outbound connections
     */
    public void disconnect(){
        VIn.close();
        VOut.close();
    }

    /**
     * merchant id setter
     * @param mid
     */
    public void setMid(Integer mid) {
        this.mid = mid;
    }

    /**
     * transaction id setter
     * @param txid
     */
    public void setTxid(Integer txid) {
        this.txid = txid;
    }

    /**
     * port setter
     * @param port
     */
    public void setPort(String port) { this.Port = port; }

    /**
     * ip setter
     * @param IPaddess
     */
    public void setIPaddess(String IPaddess) { this.IPaddess = IPaddess; }

    /**
     * listener setter
     * @param listener
     */
    public void setListener(OnOperationCompleted listener) {
        this.listener = listener;
    }

    /**
     * out connection connected
     * @param s
     */
    void outConnected(Socket s){

        Log.i(TAG,"Out connected");

        VIn = new tcn_verifone_VLinkThreadConnectorIn(IPaddess, Port, VOut.getSocket(), handler, new tcn_verifone_VLinkThreadConnectorIn.OnDisconnect() {
            @Override
            public void onDisconnect() {
                Log.i(TAG,"Out disconnected");
                isConnected=false;
            }
        });
        VIn.start();
        isConnected=true;
    }

    /**
     * connect to verifone POS
     */
    public void Connect() {
        // make outbound connection with out connector
        VOut = new tcn_verifone_VLinkThreadConnectorOut(IPaddess, Port, handler, new tcn_verifone_VLinkThreadConnectorOut.OnConnect() {
            // define event handlers
            @Override
            public void onConnect(Socket s) {
                Log.i(TAG,"onConnect");
                outConnected(s);
            }

            @Override
            public void onDisconnect() {
                Log.i(TAG,"onDisconnect");
                isConnected=false;
            }
        });

        // start connector
        VOut.start();

    }

    /**
     * send command to POS
     * @param message
     */
    private void executeCommand(String message) {
        Log.i(TAG,"Execute command "+message);

        ResetOperationCompletedFlag();
        inProcess=true;
        lastCommand=message;
        VOut.send(message);

    }

    /**
     * purchase operation
     * @param amount
     */
    public void purchase(Integer amount) {

        Log.i(TAG,"Purchase "+amount);

        // reset variables
        reset();

        // set states and operation
        internalState = VLINK_STATE_IN_PROGRESS;
        currentOperation = VLINK_PURCHASE;

        // prepare transaction
        tcn_verifone_EFTPOStransaction purchaseMessage = new tcn_verifone_EFTPOStransaction();

        // set start time
        transactionStartTime=System.currentTimeMillis()/1000L;

        // set next txid
        txid++;

        // fill transaction with mandatory data
        purchaseMessage.setMid(mid.toString());
        purchaseMessage.setTxid(txid.toString());
        purchaseMessage.setAmount(amount.toString());

        trans=purchaseMessage;

        // prepare string representation with command wrapper
        String purchaseString = VX.preparePurchase(purchaseMessage);

        // execute request
        executeCommand(purchaseString);
        Handler handler = new Handler();

        // 1.5 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resultRequest();
            }
        }, 1500);

        // ask for command result

    }

    /**
     * not used
     * @param amount
     * @param cashAmount
     */
    public void purchasePlusCash(Integer amount, Integer cashAmount) {

        tcn_verifone_EFTPOStransaction purchasePlusCashMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        purchasePlusCashMessage.setMid(mid.toString());
        purchasePlusCashMessage.setTxid(txid.toString());
        purchasePlusCashMessage.setAmount(amount.toString());
        purchasePlusCashMessage.setCash(cashAmount.toString());

        trans=purchasePlusCashMessage;

        String purchasePlusCashString = VX.preparePurchasePlusCash(purchasePlusCashMessage);

        executeCommand(purchasePlusCashString);

    }

    /**
     * not used
     * @param amount
     */
    public void cashOut(Integer amount) {


        tcn_verifone_EFTPOStransaction cashOutMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        cashOutMessage.setMid(mid.toString());
        cashOutMessage.setTxid(txid.toString());
        cashOutMessage.setAmount(amount.toString());

        String cashOutString = VX.prepareCashOut(cashOutMessage);

        executeCommand(cashOutString);

    }

    /**
    * not used
     */
    public void refund(Integer amount) {


        tcn_verifone_EFTPOStransaction refundMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        refundMessage.setMid(mid.toString());
        refundMessage.setTxid(txid.toString());
        refundMessage.setAmount(amount.toString());

        String refundString = VX.prepareRefund(refundMessage);

        executeCommand(refundString);

    }

    /**
     * not used
     */

    public void logon() {


        tcn_verifone_EFTPOStransaction logonMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        logonMessage.setMid(mid.toString());
        logonMessage.setTxid(txid.toString());

        String logonString = VX.prepareLogon(logonMessage);

        executeCommand(logonString);

    }

    /**
     * not used
     */
    public void settlementCutover() {

        tcn_verifone_EFTPOStransaction settlementCutoverMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        settlementCutoverMessage.setMid(mid.toString());
        settlementCutoverMessage.setTxid(txid.toString());

        String settlementCutoverString = VX.prepareSettlmentCutover(settlementCutoverMessage);

        executeCommand(settlementCutoverString);

    }

    /**
     * not used
     */
    public void reprintReceipt() {

        tcn_verifone_EFTPOStransaction reprintReceiptMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        reprintReceiptMessage.setMid(mid.toString());
        reprintReceiptMessage.setTxid(txid.toString());

        String reprintReceiptString = VX.prepareSettlmentCutover(reprintReceiptMessage);

        executeCommand(reprintReceiptString);

    }

    /**
     * not used
     */
    public void displayAdministrationMenu() {

        tcn_verifone_EFTPOStransaction displayAdministrationMenuMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        displayAdministrationMenuMessage.setMid(mid.toString());
        displayAdministrationMenuMessage.setTxid(txid.toString());

        String displayAdministrationMenuString = VX.prepareDisplayAdministrationMen(displayAdministrationMenuMessage);

        executeCommand(displayAdministrationMenuString);

    }

    /**
     * not used
     */
    public void getReceiptRequest() {

        tcn_verifone_EFTPOStransaction getReceiptRequestMessage = new tcn_verifone_EFTPOStransaction();

        txid++;
        getReceiptRequestMessage.setMid(mid.toString());
        getReceiptRequestMessage.setTxid(txid.toString());

        String getReceiptRequestString = VX.prepareGetReceiptRequest(getReceiptRequestMessage);

        executeCommand(getReceiptRequestString);

    }

    /**
     * request for operation result
     */
    public void resultRequest() {

        Log.i(TAG,"Result request");

        tcn_verifone_EFTPOStransaction resultRequestMessage = new tcn_verifone_EFTPOStransaction();

        resultRequestMessage.setMid(mid.toString());
        resultRequestMessage.setTxid(txid.toString());

        String resultRequestString = VX.prepareResultRequest(resultRequestMessage);

        executeCommand(resultRequestString);

    }

    /**
     * printing configuration command
     * @param onOff
     */
    public void configurePrinting(Boolean onOff) {

        Log.i(TAG,"Printer cfg");

        transactionStartTime=System.currentTimeMillis()/1000L;
        String configurePrintingString = VX.prepareConfigurePrinting(onOff);

        executeCommand(configurePrintingString);

    }

    /**
     * ready to print response
     */
    public void readyToPrintResponse() {

        Log.i(TAG,"RTP response");

        transactionStartTime=System.currentTimeMillis()/1000L;
        String readyToPrintResponseString = VX.prepareReadyToPrintResponse();

        executeCommand(readyToPrintResponseString);

    }

    /**
     * print response
     */
    public void printResponse(){


        Log.i(TAG,"print response");
        transactionStartTime=System.currentTimeMillis()/1000L;
        String printResponseString = VX.preparePrintResponse();

        executeCommand(printResponseString);

    }

    /**
     * repeat last command
     */
    public void repeatLastCommand(){

        Log.i(TAG,"repeat last command");

        transactionStartTime=System.currentTimeMillis()/1000L;

        executeCommand(lastCommand);

    }

    /**
     * process terminal answer
     * @param message
     */
    void processAnswer(String message) {

        Log.i(TAG, "process answer message=" + message);

        try {
            if (!inProcess) {
                message = "disconnect";
            }

            this.VCon = VCon;
            // check for processing timeout
            if ((System.currentTimeMillis() / 1000L - transactionStartTime) > VLINK_OPERATION_TIMEOUT) {
                if (reconnectCounter > 0) {
                    // TODO: timeout
                    internalState = VLINK_STATE_TIMEOUT;
                    reconnectCounter = 0;
                    this.OperationCompleted(false, null, true);
                    return;
                } else {
                    message = "disconnect";
                }
            }

            // not responding
            if (inProcess && message.toLowerCase().equals("notresp")) {
                if (commandRetryCounter < 10) {
                    commandRetryCounter++;
                    repeatLastCommand();
                    return;

                } else {
                    message = "disconnect";
                    commandRetryCounter = 0;
                }

            }

            // disconnect
            if (message.toLowerCase().equals("disconnect")) {
                if (inProcess) {
                    /*if (reconnectCounter <1) {
                        reconnectCounter++;
                        Log.d("DBG","Reconnect "+reconnectCounter);
                        disconnect();
                        Connect();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                repeatLastCommand();
                            }
                        }, 1000);

                        return;
                    } else {*/

                        this.OperationCompleted(false, null, true);
                        return;
                    //}
                }else{
                    disconnect();

                    this.OperationCompleted(false, null, true);
                    return;
                }

                //message = "";
            }


            Integer nextStep = 0;

            if (message != null) {
                // check for any payload
                if (message.length() > 0) {
                    // try to parse payload
                    Log.d("DBG","decoded message = "+VX.ConvertStringHEX2Char(message));
                    tcn_verifone_EFTPOStransaction tempTrans = VX.parseResponse(message);
                    // is it correct?
                    if (tempTrans != null) {

                        // let's do something with answer
                        switch (tempTrans.getOper()) {
                            case tcn_verifone_VXLinkWrapper.COMMAND_RESULT_RESPONSE: {
                                // we've got a response for operation
                                if (trans.getMid().equals(tempTrans.getMid()) && tempTrans.getRespcode().toUpperCase().equals(tcn_verifone_EFTPOStransaction.EFT_RESP_CODE_TRANSACTION_IN_PROGRESS)) {
                                    commandRetryCounter = 0;
                                    nextStep = 1;
                                } else {
                                    if (tempTrans.getRespcode().toUpperCase().equals(tcn_verifone_EFTPOStransaction.EFT_RESP_CODE_APPROVED) || tempTrans.getRespcode().toUpperCase().equals(tcn_verifone_EFTPOStransaction.EFT_RESP_CODE_APPROVED_WITH_SIGNATURE)) {
                                        internalState = VLINK_STATE_COMPLETED;
                                    } else {
                                        internalState = VLINK_STATE_ERROR;
                                    }
                                    trans = tempTrans;
                                    inProcess = false;
                                }
                            }
                            break;
                            case tcn_verifone_VXLinkWrapper.COMMAND_CONFIGURE_PRINTING_RESPONSE: {
                                // we've got a response on our Configure Printing request
                                internalState = VLINK_STATE_COMPLETED;
                                trans = tempTrans;
                                inProcess = false;
                            }
                            break;
                            case tcn_verifone_VXLinkWrapper.COMMAND_READY_TO_PRINT_REQUEST: {
                                // we need to send ready to print response
                                nextStep = 3;
                            }
                            break;
                            case tcn_verifone_VXLinkWrapper.COMMAND_PRINT_REQUEST: {
                                // we need to "print" text and send Print response
                                receipt = tempTrans.getPrint_text();
                                nextStep = 2;
                            }
                            break;
                        }
                    }
                } else {
                    int a = 1;
                    a = a + 1;
                }
            }

            // prepare answer on result
            switch (internalState) {
                case VLINK_STATE_COMPLETED:
                    nextStep = -1;
                    this.OperationCompleted(true, trans, false);
                    break;
                case VLINK_STATE_ERROR:
                    nextStep = -1;
                    this.OperationCompleted(false, trans, false);
                    break;
                case VLINK_STATE_TIMEOUT:
                    nextStep = -1;
                    this.OperationCompleted(false, null, false);
                    break;
            }

            Handler handler = new Handler();
            // start next step
            switch (nextStep) {
                case 1:
                    resultRequestRetryCounter++;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                resultRequest();
                            }
                        }, 1000);


                    break;
                case 2:
                    printResponse();
                    resultRequest();

                    break;
                case 3:
                    readyToPrintResponse();
                    break;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * prepare final operation status and send it via listener
      */

    private void OperationCompleted(Boolean result, tcn_verifone_EFTPOStransaction trans, Boolean outOfOrder){

        if(!operationCompletedFlag){
            operationCompletedFlag=true;
            inProcess = false;
            listener.operationCompleted(result,trans,outOfOrder);
        }
    }
}
