package controller;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tcn.liftboard.control.TcnShareUseData;
import com.tcn.liftboard.control.TcnVendApplication;
import com.tcn.sdk.liftdemo.tcn_verifone_AuxCart;
import com.tcn.sdk.liftdemo.tcn_verifone_DatabaseHelper;
import com.tcn.sdk.liftdemo.tcn_verifone_ImageCache;

import java.io.File;

/**
 * 描述：
 * 作者：Jiancheng,Song on 2016/5/31 15:53
 * 邮箱：m68013@qq.com
 *
 * @modified Vasily Vasilchikov
 */
public class VendApplication extends TcnVendApplication {

    public static tcn_verifone_DatabaseHelper myDB;
    public static tcn_verifone_ImageCache myImageCache;
    public static tcn_verifone_AuxCart myCart;
    private Intent tcnVendService;


    @Override
    public void onCreate() {
        super.onCreate();

        /** database init */
        myDB = new tcn_verifone_DatabaseHelper(getApplicationContext());
        myDB.OpenDB();

        /** image cache init */
        myImageCache = new tcn_verifone_ImageCache();

        /** cart init */
        myCart = new tcn_verifone_AuxCart();
        myCart.Init();

        /** process log file */
        String exStoragePath = System.getenv("EXTERNAL_STORAGE")+"/Download/logcat.txt";
        String exStoragePathOld = System.getenv("EXTERNAL_STORAGE")+"/Download/logcat.old";
        try {
            Runtime.getRuntime().exec("mv -f "+exStoragePath + " " +exStoragePathOld);
            Runtime.getRuntime().exec("logcat -f" +exStoragePath);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        /** init some TCN related settings */
        TcnShareUseData.getInstance().setBoardSerPortFirst(myDB.SettingsGetSingleValue("tcn_first_serial"));    //此处主板串口接安卓哪个串口，就填哪个串口
//        TcnShareUseData.getInstance().setBoardSerPortFirst("/dev/ttymxc1");
        //VendIF 这个文件里面 TcnComDef.COMMAND_SLOTNO_INFO这个消息，是上报货道信息的消息,每次重启程序都会查询一次所有的货道信息

        /****************  如果接有副柜  则需要如下设置  **********************/
        TcnShareUseData.getInstance().setSerPortGroupMapFirst(myDB.SettingsGetSingleValue("tcn_serial_port_group_map_first"));    //设置主柜组号，也可不设置，默认就是0
        TcnShareUseData.getInstance().setSerPortGroupMapSecond(myDB.SettingsGetSingleValue("tcn_serial_port_group_map_second"));   //设置副柜组号为0,副柜需要接安卓另外一个串口
//        TcnShareUseData.getInstance().setBoardTypeSecond(TcnConstant.DEVICE_CONTROL_TYPE[1]);   //设置副柜类型为升降机
        TcnShareUseData.getInstance().setBoardSerPortSecond(myDB.SettingsGetSingleValue("tcn_second_serial"));    //设置副柜串口，副柜接安卓哪个串口，就填哪个串口

        //先运行程序之后，请将TcnKey目录的tcn_sdk_device_id.txt文件发给我们，授权才能使用，每台机器都必须先授权。


        /*******************************      故障代码表见 VendIF  这个文件 **************************************/

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                e.printStackTrace();
            }
        });

     }

}
