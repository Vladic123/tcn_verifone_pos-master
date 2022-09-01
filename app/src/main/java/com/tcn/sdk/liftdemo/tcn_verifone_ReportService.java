package com.tcn.sdk.liftdemo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

/**
 * asynchronous report class
 * used to run report creation and sending in the backgrounf
 * @author v.vasilchikov
 */

public class tcn_verifone_ReportService extends Service {

    tcn_verifone_MakeReport MR;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MR = new tcn_verifone_MakeReport(new tcn_verifone_MakeReport.OnTaskCompleted() {
            @Override
            public void taskCompleted() {
                stopSelf();
            }
        });
        MR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }


}
