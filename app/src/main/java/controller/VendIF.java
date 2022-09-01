package controller;

import android.os.Message;
import android.util.Log;

import com.tcn.liftboard.control.MsgTrade;
import com.tcn.liftboard.control.TcnComDef;
import com.tcn.liftboard.control.TcnComResultDef;
import com.tcn.liftboard.control.TcnVendIF;


/**
 * Created by Administrator on 2016/6/30.
 */
public class VendIF {
    private static final String TAG = "VendIF";
    private static VendIF m_Instance = null;

    /**************************  故障代码表 ****************************
     public static final int ERROR_CODE_BUSY = -5;
     public static final int ERROR_CODE_0 = 0;    //正常
     public static final int ERROR_CODE_1 = 1;    //锁门时锁开关没检测到位
     public static final int ERROR_CODE_2 = 2;    //锁门时门开关没检测到位
     public static final int ERROR_CODE_3 = 3;    //升降电机电流过大
     public static final int ERROR_CODE_4 = 4;    //超过极限步数还没到底
     public static final int ERROR_CODE_5 = 5;    //检测到的最大层数比现在要出货的层数还少
     public static final int ERROR_CODE_6 = 6;    //回原点运行超时
     public static final int ERROR_CODE_7 = 7;    //正常运行时超时
     public static final int ERROR_CODE_8 = 8;    //下降正常运行时超时
     public static final int ERROR_CODE_9 = 9;    //开门时锁开关没检测到位
     public static final int ERROR_CODE_10 = 10;    //等待离开层检测光检超时
     public static final int ERROR_CODE_10i = 11;    //升降机光检被挡住
     public static final int ERROR_CODE_20i = 21;    //升降机光检不发送也有接收
     public static final int ERROR_CODE_30 = 30;    //往上移动了一段距离，但原点开关仍然没放开
     public static final int ERROR_CODE_31 = 31;    //推板运行超时
     public static final int ERROR_CODE_32 = 32;    //推板电流过大
     public static final int ERROR_CODE_33 = 33;    //推板从来没有电流
     public static final int ERROR_CODE_34 = 34;    //取货口没有货物
     public static final int ERROR_CODE_35 = 35;    //售货前货斗里面有货
     public static final int ERROR_CODE_36 = 36;    //货在货道口被卡住
     public static final int ERROR_CODE_37 = 37;    //升降电机开路
     public static final int ERROR_CODE_40 = 40;    //货道驱动板故障
     public static final int ERROR_CODE_41 = 41;    //FLASH檫除错误
     public static final int ERROR_CODE_42 = 42;    //FLASH写错误
     public static final int ERROR_CODE_43 = 43;    //错误命令
     public static final int ERROR_CODE_44 = 44;    //校验错误
     public static final int ERROR_CODE_45 = 45;    //柜门没关
     public static final int ERROR_CODE_46 = 46;    //第二次购买到履带货道

     public static final int ERROR_CODE_47 = 47;    //1层超时
     public static final int ERROR_CODE_48 = 48;    //1层过流
     public static final int ERROR_CODE_49 = 49;    //1层断线（正反都没有电流）
     public static final int ERROR_CODE_50 = 50;    //2层超时
     public static final int ERROR_CODE_51 = 51;    //2层过流
     public static final int ERROR_CODE_52 = 52;    //2层断线（正反都没有电流）
     public static final int ERROR_CODE_53 = 53;    //3层超时
     public static final int ERROR_CODE_54 = 54;    //3层过流
     public static final int ERROR_CODE_55 = 55;    //3层断线（正反都没有电流）
     public static final int ERROR_CODE_56 = 56;    //4层超时
     public static final int ERROR_CODE_57 = 57;    //4层过流
     public static final int ERROR_CODE_58 = 58;    //4层断线（正反都没有电流）
     public static final int ERROR_CODE_59 = 59;    //5层超时
     public static final int ERROR_CODE_60 = 60;    //5层过流
     public static final int ERROR_CODE_61 = 61;    //5层断线（正反都没有电流）

     public static final int ERROR_CODE_64 = 64;    //无效电机
     public static final int ERROR_CODE_80 = 80;    //转动超时
     public static final int ERROR_CODE_127 = 127;    //驱动板不回复命令
    public static final int ERR_CODE_255            = 255;   //货道不存在

    ********************************************************************************/


    public static synchronized VendIF getInstance() {
        if (null == m_Instance) {
            m_Instance = new VendIF();
        }
        return m_Instance;
    }

    public void initialize() {
        registerListener ();
    }


    public void deInitialize() {
        unregisterListener();
    }

    public void registerListener () {
        TcnVendIF.getInstance().setOnCommunicationListener(m_CommunicationListener);
    }

    public void unregisterListener() {
        TcnVendIF.getInstance().setOnCommunicationListener(null);
    }

    private void OnSelectedSlotNo(int slotNo) {

    }

    //驱动板上报过来的数据 slotNo:货道号     status:0 货道状态正常     4：没有检测到掉货      255：货道号不存在（检测不到该货道）
    public void OnUploadSlotNoInfo(boolean finish, int slotNo, int status) {

    }

    //驱动板上报过来的数据 slotNo:货道号     status:0 货道状态正常     4：没有检测到掉货      255：货道号不存在（检测不到该货道）
    public void OnUploadSlotNoInfoSingle(boolean finish, int slotNo, int status) {
        Log.i(TAG, "OnUploadSlotNoInfoSingle finish: " + finish + " slotNo: " + slotNo + " status: " + status);
    }

    //出货状态返回    slotNo： 货道号    shipStatus： 出货状态    status: 货道状态正常    支付订单号（出货接口传入，原样返回） amount：支付金额（出货接口传入，原样返回）
    private void OnShipWithMethod(int slotNo, int shipStatus,int errCode, String tradeNo, String amount) {
        Log.i(TAG, "OnShipWithMethod slotNo: " + slotNo + " shipStatus: " + shipStatus+" errCode: "+errCode
                + " tradeNo: " + tradeNo+" amount: "+amount);

        if (TcnComResultDef.SHIP_SHIPING == shipStatus) {   //出货中

        } else if (TcnComResultDef.SHIP_SUCCESS == shipStatus) {   //出货成功

        } else if (TcnComResultDef.SHIP_FAIL == shipStatus) {    //出货失败

        } else {

        }
    }

    private void OnDoorSwitch(int door) {

    }

    private void OnSelectedGoods(int slotNoOrKey, String price) {

    }

    private void OnShipForTestSlot(int slotNo, int errCode, int shipStatus) {
        Log.i(TAG, "OnShipForTestSlot slotNo: " + slotNo + " errCode: " + errCode + " shipStatus: " + shipStatus);
    }

//    private void OnUploadGoodsInfo(int slotNo, int finish, Coil_info slotInfo) {
//
//    }

    /*
     * 此处监听底层发过来的数据，此处接收数据位于线程内
     */
    private VendCommunicationListener m_CommunicationListener = new VendCommunicationListener();
    private class VendCommunicationListener implements TcnVendIF.CommunicationListener {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TcnComDef.COMMAND_SELECT_SLOTNO:
                    OnSelectedSlotNo(msg.arg1);
                    break;
                case TcnComDef.COMMAND_SLOTNO_INFO:
                    OnUploadSlotNoInfo((boolean) msg.obj, msg.arg1, msg.arg2);
                    break;
                case TcnComDef.COMMAND_SLOTNO_INFO_SINGLE:
                    OnUploadSlotNoInfoSingle((boolean) msg.obj, msg.arg1, msg.arg2);
                    break;
                case TcnComDef.COMMAND_SHIPMENT_CASHPAY:
	                MsgTrade mMsgToSendcash = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2,mMsgToSendcash.getErrCode(), mMsgToSendcash.getTradeNo(),mMsgToSendcash.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_WECHATPAY:
	                MsgTrade mMsgToSendWx = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendWx.getErrCode(),mMsgToSendWx.getTradeNo(),mMsgToSendWx.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_ALIPAY:
	                MsgTrade mMsgToSendAli = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendAli.getErrCode(),mMsgToSendAli.getTradeNo(),mMsgToSendAli.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_GIFTS:
                    MsgTrade mMsgToSendGifts = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendGifts.getErrCode(),mMsgToSendGifts.getTradeNo(),mMsgToSendGifts.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_REMOTE:
                    MsgTrade mMsgToSendRemote = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendRemote.getErrCode(),mMsgToSendRemote.getTradeNo(),mMsgToSendRemote.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_VERIFY:
                    MsgTrade mMsgToSendVerfy = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendVerfy.getErrCode(),mMsgToSendVerfy.getTradeNo(),mMsgToSendVerfy.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_BANKCARD_ONE:
                    MsgTrade mMsgToSendBankcard = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2,mMsgToSendBankcard.getErrCode(), mMsgToSendBankcard.getTradeNo(),mMsgToSendBankcard.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_BANKCARD_TWO:
                    MsgTrade mMsgToSendBankcardTwo = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardTwo.getErrCode(),mMsgToSendBankcardTwo.getTradeNo(),mMsgToSendBankcardTwo.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_TCNCARD_OFFLINE:
                    MsgTrade mMsgToSendBankcardOffLine = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardOffLine.getErrCode(),mMsgToSendBankcardOffLine.getTradeNo(),mMsgToSendBankcardOffLine.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_TCNCARD_ONLINE:
                    MsgTrade mMsgToSendBankcardOnLine = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardOnLine.getErrCode(),mMsgToSendBankcardOnLine.getTradeNo(),mMsgToSendBankcardOnLine.getAmount());
                    break;
                case TcnComDef.COMMAND_SHIPMENT_OTHER_PAY:
                    MsgTrade mMsgToSendBankcardPay = (MsgTrade) msg.obj;
                    OnShipWithMethod(msg.arg1, msg.arg2, mMsgToSendBankcardPay.getErrCode(),mMsgToSendBankcardPay.getTradeNo(),mMsgToSendBankcardPay.getAmount());
                    break;
                case TcnComDef.CMD_TEST_SLOT:
                    OnShipForTestSlot(msg.arg1, msg.arg2, (Integer) msg.obj);
                    break;
                case TcnComDef.CMD_READ_DOOR_STATUS:
                    Log.i(TAG, "CMD_READ_DOOR_STATUS msg.arg1: " + msg.arg1+" msg.arg2: "+msg.arg2);
                    if (TcnComResultDef.DOOR_CLOSE == msg.arg1) {   //关门

                    } else if (TcnComResultDef.DOOR_OPEN == msg.arg1) {   //开门

                    }
                    else {

                    }
                    break;
                case TcnComDef.CMD_READ_CURRENT_TEMP:   //单个柜子温度上报，msg.arg1：柜子编号0,1,2    msg.arg2：温度值
                    String temper = (String) msg.obj;  //温度描述
                    break;
                case TcnComDef.CMD_READ_TEMP:     //所有柜子温度描述  (String) msg.obj: 主柜和副柜温度描述
                    String temperAll = (String) msg.obj;
                    break;
                default:
                    break;
            }
        }
    }
}
