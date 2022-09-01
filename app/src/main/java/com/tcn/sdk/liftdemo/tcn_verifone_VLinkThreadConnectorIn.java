package com.tcn.sdk.liftdemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * inbound verifone connector
 */
public class tcn_verifone_VLinkThreadConnectorIn extends Thread{

    private Handler handler;

    interface OnDisconnect{
        void onDisconnect();
    }

    tcn_verifone_VLinkThreadConnectorIn.OnDisconnect onDisconnectListener;


    tcn_verifone_VLinkThreadConnectorIn(String IPAddress, String Port, Socket s, Handler handler, OnDisconnect listener){
        this.IPAddress=IPAddress;
        this.Port=Integer.parseInt(Port);
        this.s=s;
        this.handler=handler;
        this.onDisconnectListener = listener;
        if(handler==null){
            Log.d("DBG","handler is null!");
        }
    }

    public interface OnTaskCompleted {
        void taskCompleted(String message);

    }

    private Integer Port;
    private String IPAddress;

    private ArrayList<String> outMessages = new ArrayList<>();

    private boolean mRun = true;
    private Socket s;
    @Override
    public void run() {

        while (mRun) {

            try {
                BufferedReader bufferIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                while (mRun) {
                    Integer chr = 0;
                    Long startTime = System.currentTimeMillis()/1000L;
                    boolean readFlag=false;
                    String terminalAnswer="";
                    String tempString="";
                    while(((System.currentTimeMillis()/1000L)-startTime)<5){
                        if(bufferIn.ready()) {
                            readFlag=true;
                            chr = bufferIn.read();
                            // little hack :)
                            if(chr>0xff){
                                chr=0xff;
                            }

                            tempString=Integer.toHexString(chr);
                            if(tempString.length()==1){
                                tempString="0"+tempString;
                            }
                            terminalAnswer += tempString;
                        }else{
                            if(readFlag){
                                break;
                            }
                            try{
                                Thread.sleep(10);
                            }catch(Exception ex){}
                        }
                    }
                    if(terminalAnswer.length()>0){
                        Log.d("DBG","inMessage "+terminalAnswer);
                        Message msg = new Message();
                        msg.obj=terminalAnswer;
                        try {
                            Log.d("DBG", "run: send message to handler "+handler);
                            handler.sendMessage(msg);
                            Log.d("DBG", "run: message sent");

                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    if(((System.currentTimeMillis()/1000L)-startTime)>=5) {
                        Message msg = new Message();
                        msg.obj="notresp";
                        handler.sendMessage(msg);
                    }
                }


            }  catch (IOException e) {

                e.printStackTrace();
            } finally {
                //close connection
                if (s != null) {
                    try {
                        s.close();
                        close();
                        Message msg = new Message();
                        msg.obj="disconnect";
                        handler.sendMessage(msg);
                        onDisconnectListener.onDisconnect();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void close() {
        mRun = false;
    }
}
