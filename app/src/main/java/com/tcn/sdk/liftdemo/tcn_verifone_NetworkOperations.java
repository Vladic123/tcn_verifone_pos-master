package com.tcn.sdk.liftdemo;

import android.os.AsyncTask;
import android.util.Pair;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;

import static controller.VendApplication.myDB;

/**
 * class for asynchronous network operations
 * @author v.vasilchikov
 */

class tcn_verifone_NetworkOperations extends AsyncTask<String, Integer, Void> {

    interface onResultListener{
        void OnResult(Pair<Integer,String> inData);
    }

    private onResultListener listener;
    private Pair<Integer,String> answer;

    tcn_verifone_NetworkOperations(onResultListener listener){
        this.listener=listener;
    }


    @Override
    protected Void doInBackground(String... params) {

        Integer mode = Integer.parseInt(params[0]);


        switch(mode){

            case 0:
                // validate QR code
                String uid = params[1];
                validateCode(uid);
                break;
            case 1:
                //upload transactions

                uploadTransactions();

                break;


        }

        return null;
    }

    /** upload transactions to server */
    private void uploadTransactions() {


        tcn_verifone_Network Net = new tcn_verifone_Network(null);

        // get all transactions for unload
        ArrayList<tcn_verifone_AuxTransaction> TRS = myDB.TransactionsGetAllForUnload();
        if (TRS == null) {
            return;
        }
        // make json data from transactions
        for (tcn_verifone_AuxTransaction trans : TRS) {

            if (trans.getUnloaded() != 1) {

                Double finalAmount = trans.getAmount().doubleValue() / 100;

                String request = "{\"receipt\":{\"total\":" + finalAmount.toString() + ",";
                request += "\"datetime\":\"" + trans.getDatetime() + "\",";
                request += "\"txid\":" + trans.getTxid() + ",";
                request += "\"receipt_text\":\"" + StringEscapeUtils.escapeJson(trans.getReceipt()) + "\",";
                request += "\"card_type\":\"" + trans.getCardType() + "\",";

                request += "\"items\":[";
                // add times to transaction
                ArrayList<Integer> ITS = myDB.TransactionsItemsGetItemsByTransaction(trans.getId());
                if (ITS != null) {
                    boolean firstFlag = true;
                    for (Integer inta : ITS) {

                        if (firstFlag) {
                            request += "{";
                            firstFlag = false;
                        } else {
                            request += ",\n{";
                        }
                        tcn_verifone_AuxItem AI = myDB.GoodsGetItemById(inta);

                        Integer quantity = myDB.TransactionsItemsGetItemsQuantityByItemId(AI.getId());

                        request += "\"item_name\":\"" + AI.getDescription() + "\",";
                        request += "\"item_sku\":\"" + AI.getDescription() + "\",";
                        request += "\"quantity\":" + quantity;

                        request += "}";

                    }
                    request += "]";
                    Boolean udiFlag = false;
                    // get discount
                    if (trans.getDiscountUid().length() > 0) {

                        request += "},\"discount_uuid\": \"" + trans.getDiscountUid() + "\",\"discount_value\":" + trans.getDiscountValue() + "}";
                        udiFlag = true;
                    } else {
                        request += "}\n}";
                    }

                    Pair<Integer, String> Answer = null;
                    // upload transactions to server
                    if (udiFlag) {
                        Answer = Net.UploadTransactions(request);
                    } else {
                        Answer = Net.UploadTransactionsNonUid(request);
                    }

                    // if server status code == 200, remove transactions from database
                    if (Answer.first == 200) {
                        myDB.TransactionsRemove(trans);
                    }
                }
            }

        }


    }

    // validate barcode on the server
    private void validateCode(String uid){

        // validate discount
        String request = "{ \"discount_uuid\":\""+uid+"\" }";
        tcn_verifone_Network myNet = new tcn_verifone_Network(null);
        answer = myNet.Validate(request);

    }


    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        listener.OnResult(answer);

    }
}


