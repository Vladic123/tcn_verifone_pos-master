package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import static controller.VendApplication.myDB;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * class for working with network connections
 * @author v.vasilchikov
 */

public class tcn_verifone_Network {

    private String MainURL;
    private Context context;
    private boolean SecInitiated = false;
    private SSLContext sslContext;
    private PrintWriter wr;

    public tcn_verifone_Network(Context Incontext) {
        context = Incontext;
    }

    /** input stream reader */
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    /** SSL initialization stub. not used here */
    private boolean InitSec() {


        try {

            SecInitiated = true;

        } catch (Exception ex) {
            SecInitiated = false;
        }

        return SecInitiated;
    }

    /** execute http POST request */
    private Pair<Integer,String> ExecutePostRequest(String inUrl, String inAuth, String inAction) {

        URL url=null;
        String Answer = "";

        HttpURLConnection urlSConnection=null;
        InputStream in;

        Pair<Integer,String> result = null;

        try {
            url = new URL(inUrl);

            if (!SecInitiated) {
                if (!InitSec()) {
                    return null;
                }
            }

            try {

                urlSConnection = (HttpURLConnection) url.openConnection();
                urlSConnection.setRequestMethod("POST");
                urlSConnection.setDoOutput(true);

                urlSConnection.setRequestProperty("User-Agent", "TCN POS");
                urlSConnection.setRequestProperty("Content-Type", "application/json");
                urlSConnection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBUElfS0VZIjoiMTIzNDU2Nzg5MCJ9.ola4dnE3yTMMZxmu4SkQ2J08XgLV43BxJ8jrcIc-09M");

                wr = new PrintWriter(urlSConnection.getOutputStream());
                wr.print(inAction);
                wr.close();

                int status = urlSConnection.getResponseCode();
                in = new BufferedInputStream(urlSConnection.getInputStream());

                Answer = readStream(in);

                result = new Pair<Integer, String>(status,Answer);

                Log.d("ExecutePostRequest", Answer);
                urlSConnection.disconnect();

            } catch (Exception ex) {

                result = new Pair<Integer, String>(0,ex.getMessage());
            }


        } catch (Exception ex) {
            result = new Pair<Integer, String>(0,ex.getMessage());
        }finally {
            if(urlSConnection!=null){
                urlSConnection.disconnect();
            }
        }

        return result;

    }

    /** execute http GET request */
    private String ExecuteGetRequest(String inUrl, String inAuth, String inAction) {

        URL url;
        String Answer = "";

        HttpsURLConnection urlSConnection=null;
        InputStream in;

        try {
            url = new URL(inUrl + inAction);

            if (!SecInitiated) {
                if (!InitSec()) {
                    return "";
                }
            }

            try {


                urlSConnection = (HttpsURLConnection) url.openConnection();
                urlSConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                urlSConnection.setRequestMethod("GET");


                urlSConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                urlSConnection.setRequestProperty("Accept", "*/*");

                int status = urlSConnection.getResponseCode();
                in = new BufferedInputStream(urlSConnection.getInputStream());

                Answer = readStream(in);

                Log.d("ExecuteGetRequest", Answer);
                urlSConnection.disconnect();
                urlSConnection=null;

            } catch (IOException ex) {


            }


        } catch (Exception ex) {

        }finally {
            if(urlSConnection!=null){
                try {
                    urlSConnection.disconnect();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        return Answer;
    }

    /** wrapper for barcode validation request */
    public Pair<Integer,String> Validate(String request){

        Pair<Integer,String> answer = ExecutePostRequest(myDB.SettingsGetSingleValue("base_url")+"api/v1/discounts/verify_discount","",request);
        Log.d("DBG","validation request "+request);
        Log.d("DBG","validation answer "+answer.toString());

        return answer;
    }

    /** wrapper for transaction upload request */
    public Pair<Integer,String> UploadTransactions(String request){

        Pair<Integer,String> answer = ExecutePostRequest(myDB.SettingsGetSingleValue("base_url")+"api/v1/receipts/send","",request);

        Log.d("DBG","upload trans request "+request);
        Log.d("DBG","upload trans answer "+answer.toString());

        return answer;
    }

    /** wrapper for non uid transaction upload request */
    public Pair<Integer,String> UploadTransactionsNonUid(String request){

        Pair<Integer,String> answer = ExecutePostRequest(myDB.SettingsGetSingleValue("base_url")+"api/v1/receipts/sendNoUserReceipt","",request);
        Log.d("DBG","upload trans non uid request "+request);
        Log.d("DBG","upload trans non uid answer "+answer.toString());

        return answer;
    }

}
