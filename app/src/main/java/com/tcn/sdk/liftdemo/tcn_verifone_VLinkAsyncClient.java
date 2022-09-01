package com.tcn.sdk.liftdemo;

import android.os.AsyncTask;

/**
 * class for asynchronous interaction with verifone Pos
 */

public class tcn_verifone_VLinkAsyncClient extends AsyncTask<String,Integer,Boolean>
{
    private tcn_verifone_VLinkConnector VC;
    private String answerMessage="";
    private OnTaskCompleted listener;

    /**
     * on task complete interface
     */
    public interface OnTaskCompleted {
        void taskCompleted(Boolean result, String message, tcn_verifone_VLinkConnector VCon);

    }

    /**
     * construcot
     * @param listener
     * @param VC
     */
    tcn_verifone_VLinkAsyncClient(OnTaskCompleted listener, tcn_verifone_VLinkConnector VC){
        this.listener=listener;
        this.VC=VC;
    }

    /**
     * listener setter
     * @param listener
     */
    public void setListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * background task
     * @param params
     * @return
     */
    @Override
    protected Boolean doInBackground(String...params){

        Boolean result = false;
        String address = params[0];
        Integer port = Integer.parseInt(params[1]);
        String command = params[2];
        Integer mode = Integer.parseInt(params[3]);

        // make connector and connect to verifone
        if(VC==null || !VC.isConnected()) {
            VC = new tcn_verifone_VLinkConnector(address, port, new tcn_verifone_VLinkConnector.OnMessageReceived() {

                @Override
                public void onConnected() {
                }

                @Override
                public void messageReceived(final String message) {
                    answerMessage = message;
                }
            });
            VC.Connect();
            return false;
        }
        // start command processing
        result = VC.processCommand(command,mode);

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        // disconnect after command execution completed
        if(VC.isConnected()){
            VC.stopClient();
        }

        // report via listener
        if(listener!=null){
            listener.taskCompleted(result,answerMessage,VC);
        }
    }
}
