package com.tcn.sdk.liftdemo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * outbound verifone connector
 */
public class tcn_verifone_VLinkThreadConnectorOut extends Thread{

    private Handler handler;

    tcn_verifone_VLinkThreadConnectorOut(String IPAddress, String Port, Handler handler, OnConnect onConnectListener){
        this.IPAddress=IPAddress;
        this.Port=Integer.parseInt(Port);
        this.onConnectListener=onConnectListener;
        this.handler=handler;
    }

    interface OnConnect{
        void onConnect(Socket s);
        void onDisconnect();
    }

    OnConnect onConnectListener;

    private Integer Port;
    private String IPAddress;

    private ArrayList<String> outMessages = new ArrayList<>();

    private boolean mRun = true;
    Socket s = null;
    Boolean isConnected = false;

    public Boolean isConnected(){
        return isConnected;
    }

    @Override
    public void run() {

        while (mRun) {

            try {
                InetAddress terminalAddress = InetAddress.getByName(IPAddress);
                SocketAddress sockAddr = new InetSocketAddress(terminalAddress, Port);
                s = new Socket();
                s.connect(sockAddr, 5000);
                onConnectListener.onConnect(s);
                isConnected=true;
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                while (mRun) {
                    String message;
                    // Wait for message
                    synchronized (outMessages) {
                        while (outMessages.isEmpty()) {
                            try {
                                outMessages.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // Get message and remove from the list
                        message = outMessages.get(0);
                        outMessages.remove(0);
                    }

                    //send output msg
                    Log.i("DBG","outmessage "+message);
                    String outMsg = message;
                    out.write(outMsg);
                    out.flush();
                }

            } catch (IOException e) {

                e.printStackTrace();
            } finally {
                //close connection
                if (s != null) {
                    try {
                        Log.i("DBG","out: disconnect");
                        s.close();
                        close();
                        Message msg = new Message();
                        msg.obj="disconnect";
                        handler.sendMessage(msg);
                        onConnectListener.onDisconnect();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void send(String message) {
        synchronized (outMessages) {
            outMessages.add(message);
            outMessages.notify();
        }
    }

    public Socket getSocket(){
        return s;
    }

    public void close() {
        mRun = false;

    }
}