package com.tcn.sdk.liftdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static android.content.ContentValues.TAG;

/**
 * class for handling tcp/ip socket to verifone pos
 */

public class tcn_verifone_VLinkConnector {

    /**
     * connection timeout
     */
    private static final int SOCKET_CONNECTION_TIMEOUT = 5000; // in milliseconds
    /**
     * read from socket timeout
     */
    private static final int READ_TIMEOUT = 2; // in seconds

    /**
     * verifone interaction mode
     */
    public static final int VLINK_MODE_REQUEST_ONLY = 0;
    public static final int VLINK_MODE_REQUEST_ANSWER = 1;

    // socket
    private Socket socket;
    // ip address
    private String address;
    // port
    private Integer port;

    // run flag
    private boolean runFlag = false;

    // listener for message handling
    private OnMessageReceived answerListener = null;

    // in out buffers
    private PrintWriter bufferOut;
    private BufferedReader bufferIn;

    // answer from terminal
    private String terminalAnswer="";

    /**
     * connector constructor
     * @param address
     * @param port
     * @param listener
     */
    tcn_verifone_VLinkConnector(String address, Integer port, OnMessageReceived listener){

        this.address=address;
        this.port=port;
        this.answerListener = listener;
    }

    /**
     * send prepared command to socket
     * @param command
     */
    public void sendCommand(String command) {
        if (bufferOut != null && !bufferOut.checkError()) {
            bufferOut.println(command);
            bufferOut.flush();
        }
    }

    /**
     * stop connector
     */
    public void stopClient() {

        runFlag = false;

        if (bufferOut != null) {
            bufferOut.flush();
            bufferOut.close();
        }

        answerListener = null;
        bufferIn = null;
        bufferOut = null;
        terminalAnswer = null;
    }

    /**
     * connect socket
     * @return
     */
    public Boolean Connect() {

        Boolean result = true;
        try {
            // prepare socket
            InetAddress terminalAddress = InetAddress.getByName(address);
            SocketAddress sockAddr = new InetSocketAddress(terminalAddress, port);
            try {
                socket = new Socket();

                // connect it
                socket.connect(sockAddr, SOCKET_CONNECTION_TIMEOUT);

                runFlag = true;

                // bind buffers
                bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            } catch (Exception ex) {
                ex.printStackTrace();
                result = false;
            }
        }catch (Exception ex){
            result = false;
        }

        return result;
    }

    /**
     * disconnect
     */
    public void Disconnect(){
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        }catch (Exception ex){

        }

    }

    /**
     * command processing
     * @param command
     * @param mode
     * @return
     */

    public Boolean processCommand(String command, int mode){

        Boolean result = true;
        try{
                if(command.length()>0) {
                    sendCommand(command);
                }

                if(mode == VLINK_MODE_REQUEST_ANSWER) {
                    // wait for answer
                    while (runFlag) {
                        if (bufferOut.checkError()) {
                            runFlag = false;
                        }
                        terminalAnswer="";
                        Integer chr = 0;
                        Long startTime = System.currentTimeMillis()/1000L;
                        boolean readFlag=false;
                        String tempString="";
                        while((System.currentTimeMillis()/1000L-startTime)<READ_TIMEOUT){
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
                            }
                        }

                        if (terminalAnswer != null && answerListener != null) {
                            answerListener.messageReceived(terminalAnswer);
                            runFlag=false;
                        }else{
                            result = false;
                        }
                    }
                }

        }catch(Exception ex){
            result = false;
        }

        return result;
    }

    /**
     * cconnection state getter
     * @return
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }


    public interface OnMessageReceived {
        void messageReceived(String message);

        void onConnected();
    }

}
