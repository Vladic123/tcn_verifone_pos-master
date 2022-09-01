package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;

/**
 * class for displaying barcode scanning result
 * @author v.vasilchikov
 */

public class tcn_verifone_BarcodeResult extends AppCompatActivity {

    /** some UI elements */
    private TextView scanText;
    private ImageView scanImage;
    private ImageView backButton;
    private Context context;
    private JSONObject barcodeToCheck;
    private TextView barcodeLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__barcode_result);

        context = this;
        /** bind UI elements */
        barcodeLog = findViewById(R.id.BarcodelogView);
        scanImage = findViewById(R.id.success_image);
        scanImage.setVisibility(View.INVISIBLE);
        scanText = findViewById(R.id.discount_text);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /** get scanned barcode */
        Intent intent = getIntent();
        String barcode = intent.getStringExtra("barcode");

        Log.d("DBG",barcode);
        if(barcode!=null){
            // test
          //  rightBarcode(null,null);
            /** process barcode */
           processBarcode(barcode);
        }else {
            /** shows "wrong barcode" message*/
            wrongBarcode();
        }
    }

    /** calculation HMAC MD5 barcode signature */
    public String sStringToHMACMD5(String s, String keyString) {
        String sEncodedString = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes(StandardCharsets.UTF_8), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);

            byte[] bytes = mac.doFinal(s.getBytes(StandardCharsets.US_ASCII));

            StringBuffer hash = new StringBuffer();

            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            sEncodedString = hash.toString();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sEncodedString;
    }

    /** process scanned barcode */
    protected void processBarcode(String barcode){

        StringBuilder builder = new StringBuilder();


        String preparedBarcode="";
        Boolean flag=false;

        /** prepeare barcode */
        for(int a=0;a<barcode.length();a++){
            if(barcode.charAt(a)==0x7b){
                flag=true;
            }
            if(barcode.charAt(a)==0x7d){
                flag=false;
                preparedBarcode+="}";
            }
            if(flag){
                if(barcode.charAt(a)!=0) {
                    preparedBarcode += barcode.charAt(a);
                }
            }
        }

        barcode = preparedBarcode;

        Log.d("DBG","|" +barcode+"|");

        /** check signature */
        if(checkHMAC(barcode)){

            try {
                /** check barcode online */
                JSONObject discount = new JSONObject(barcode);
                checkBarcodeOnline(discount);

            } catch (Exception ex) {
		ex.printStackTrace();
           }


        }else {
            Log.d("DBG","Possible wrong HMAC\n");
            wrongBarcode();

        }

    }

    /** split scanned barcode */
    private String [] splitBarcode(String barcode){


        try {
            String[] parts = barcode.split("\\}");
            parts[0]=parts[0]+"}";
            parts[1]=parts[1]+"}";

            return parts;
        }catch(Exception ex){
            return null;
        }

    }

    /** check HMAC signature */
    private  Boolean checkHMAC(String barcode){

        String key = myDB.SettingsGetSingleValue("hmackey");
        JSONObject json = null;
        String signFromBarcode="";

        String sign="";

        try{
            String [] parts = splitBarcode(barcode);

            if (parts.length >= 2) {
                sign = sStringToHMACMD5(parts[0], key);
                try {
                    json = new JSONObject(parts[1]);
                    signFromBarcode = json.getString("sig");
                } catch (Exception ex) {
                    return false;
                }
            }

            if (sign.length() > 0 && signFromBarcode.length() > 0 && sign.equals(signFromBarcode)) {
                return true;
            }else{
                Log.d("DBG","Signatures are not equal");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    /** check barcode via webservice */
    private void checkBarcodeOnline(JSONObject barcode){

        barcodeToCheck = barcode;
        Log.d("DBG","Checking QR online");
        tcn_verifone_NetworkOperations NOP = null;
        try {
            NOP = new tcn_verifone_NetworkOperations(new tcn_verifone_NetworkOperations.onResultListener() {
                @Override
                public void OnResult(Pair<Integer, String> inData) {
                    //try {
                    if (inData != null && inData.first == 202) {
                        Log.d("DBG", "QR correct");
                        rightBarcode(barcodeToCheck, inData.second);
                    } else {
                        Log.d("DBG", "Online validation error - ");
                        if (inData != null) {
                            Log.d("DBG", "1. " + inData.first.toString() + " 2. " + inData.second);
                        }
                        wrongBarcode();
                    }

                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

        String uid = "";
        try {
            uid = barcode.getString("id");
            Log.d("DBG","Extracted uid - "+uid);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        if(checkBarcodeUIDExist(barcode)){
            wrongBarcode();
        }

        if(NOP != null && uid.length()>0){
            Log.d("DBG","Execute NOP");
            //NOP.execute("0",uid);
            NOP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"0",uid);
        }

    }

    /** check if barcode from UID already exist in the local database */
    private Boolean checkBarcodeUIDExist(JSONObject barcode){

        try {
            return myDB.TransactionCheckByDiscountUID(barcode.getString("id"));
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    /** shows "wrong barcode" messge */
    protected void wrongBarcode(){

        scanImage.setImageResource(R.drawable.unsuccessfulscan);
        scanImage.setVisibility(View.VISIBLE);

        scanText.setText(R.string.tcn_verifone_barcode_scan_unsuccessful);
        Log.d("DBG","Incorrect qrcode");

        autoFinish();

    }

    /** process correct barcode */
    protected void rightBarcode(JSONObject barcode,String answer){

        scanImage.setImageResource(R.drawable.successfulscan);
        scanImage.setVisibility(View.VISIBLE);
        Log.d("DBG","Correct qrcode");
        String name = "";

        try{
            name = barcode.getString("n");

        String welcomeMessage = getString(R.string.tcn_verifone_barcode_scan_welcome_back) + " " + name + "!\n";

        Double discount = 0D;
        try {
            JSONObject json = new JSONObject(answer);
            discount = json.getDouble("discount_value");
        } catch (Exception ex) {
        }

        Double multipliedDiscount = discount * 100;
        Integer discountInt = multipliedDiscount.intValue();
        String discountText = discountInt.toString();
        welcomeMessage = welcomeMessage + getString(R.string.tcn_verifone_barcode_scan_your_discount) + " " + discountText+getString(R.string.tcn_verifone_barcode_scan_off_everything);
        scanText.setText(welcomeMessage);

        /** add barcode in the current cart and set discount value */
        myCart.setBarcode(barcode);
        myCart.setDiscountValue(discount);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        autoFinish();


    }

    /** report barcode processing finished */
    private void autoFinish() {
        Log.d("DBG","autoFinish");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("DBG","Finish");
                finish();
            }
        }, 2000L);

    }

}