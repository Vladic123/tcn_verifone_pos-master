package com.tcn.sdk.liftdemo;

/**
 * clsas for handling verifone transaction
 * @author v.vasilchikov
 */

public class tcn_verifone_EFTPOStransaction {

    /** EFT response codes */
    public static final String EFT_RESP_CODE_APPROVED = "00";
    public static final String EFT_RESP_CODE_APPROVED_WITH_SIGNATURE = "09";
    public static final String EFT_RESP_CODE_TRANSACTION_CANCELLED = "CC";
    public static final String EFT_RESP_CODE_DECLINED = "55";
    public static final String EFT_RESP_CODE_SETTLED_OK = "90";
    public static final String EFT_RESP_CODE_HOST_UNAVAILABLE = "91";
    public static final String EFT_RESP_CODE_SYSTEM_ERROR = "99";
    public static final String EFT_RESP_CODE_TRANSACTION_IN_PROGRESS = "??";
    public static final String EFT_RESP_CODE_TERMINAL_IS_BUSY = "BB";

    /** operation */
    private String oper = "";
    /** transaction id */
    private String txid = "";
    /** merchant id */
    private String mid = "";
    /** transaction amount */
    private String amount = "";
    /** cash */
    private String cash = "";
    /** response code */
    private String respcode = "";
    /** response text */
    private String resp_text = "";
    /** printable result */
    private String print_result = "";
    /** printable text */
    private String print_text = "";
    /** receipt text */
    private String receipt_text = "";
    /** on off text */
    private String on_off = "";
    /** card type */
    private String card_type = "";
    /** online flag */
    private String online_flag = "";

    /** setters and getters */
    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getAmount() {
        return amount;
    }

    public String getCard_type() {
        return card_type;
    }

    public String getCash() {
        return cash;
    }

    public String getMid() {
        return mid;
    }

    public String getOn_off() {
        return on_off;
    }

    public String getPrint_result() {
        return print_result;
    }

    public String getOnline_flag() {
        return online_flag;
    }

    public String getPrint_text() {
        return print_text;
    }

    public String getReceipt_text() {
        return receipt_text;
    }

    public String getResp_text() {
        return resp_text;
    }

    public String getRespcode() {
        return respcode;
    }

    public String getTxid() {
        return txid;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setOn_off(String on_off) {
        this.on_off = on_off;
    }

    public void setPrint_result(String print_result) {
        this.print_result = print_result;
    }

    public void setOnline_flag(String online_flag) {
        this.online_flag = online_flag;
    }

    public void setPrint_text(String print_text) {
        this.print_text = print_text;
    }

    public void setReceipt_text(String receipt_text) {
        this.receipt_text = receipt_text;
    }

    public void setResp_text(String resp_text) {
        this.resp_text = resp_text;
    }

    public void setRespcode(String respcode) {
        this.respcode = respcode;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    @Override
    public String toString() {
        try {
            return "tcn_verifone_EFTPOStransaction{" +
                    "oper='" + oper + '\'' +
                    ", txid='" + txid + '\'' +
                    ", mid='" + mid + '\'' +
                    ", amount='" + amount + '\'' +
                    ", cash='" + cash + '\'' +
                    ", respcode='" + respcode + '\'' +
                    ", resp_text='" + resp_text + '\'' +
                    ", print_result='" + print_result + '\'' +
                    ", print_text='" + print_text + '\'' +
                    ", receipt_text='" + receipt_text + '\'' +
                    ", on_off='" + on_off + '\'' +
                    ", card_type='" + card_type + '\'' +
                    ", online_flag='" + online_flag + '\'' +
                    '}';
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
