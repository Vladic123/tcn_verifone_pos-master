package com.tcn.sdk.liftdemo;

import android.util.Pair;

import java.util.ArrayList;

/**
 * class for handling transaction
 * @author v.vasilchikov
 */

public class tcn_verifone_AuxTransaction {

    /** transaction internal id */
    private Integer id=-1;
    /** transaction date and time */
    private String datetime="";
    /** items amount */
    private Integer amount=-1;
    /** transaction id from verifone transaction */
    private Integer txid=-1;
    /** merchant id from verifone transaction */
    private Integer mid=-1;
    /** receipt text */
    private String receipt="";
    /** verifoen response code */
    private String respcode="";
    /** verifone online flag */
    private String onlineflag="";
    /** transaction status */
    private Integer successful=-1;
    /** unload flag */
    private Integer unloaded=-1;
    /** discoount uid */
    private String discountUid="";
    /** discount value */
    private String discountValue="";
    /** card type */
    private String cardType="";

    /** card type getter */
    public String getCardType() {
        return cardType;
    }

    /** card type setter */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /** list for items storage */
    private ArrayList<Pair<Integer,Integer>> items=null;

    /** items list initializations */
    tcn_verifone_AuxTransaction(){
        items=new ArrayList<Pair<Integer,Integer>>();
    }

    /** add item to transaction */
    public void addItem(Integer itemId,Integer quantity){

        Pair<Integer,Integer> pair = new Pair<Integer, Integer>(itemId,quantity);

        items.add(pair);
    }

    /** discount uid getter */
    public String getDiscountUid() {
        return discountUid;
    }

    /** discount value getter */
    public String getDiscountValue() {
        return discountValue;
    }

    /** discount uid setter */
    public void setDiscountUid(String discountUid) {
        this.discountUid = discountUid;
    }

    /** discount value setter */
    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    /** items getter */
    public ArrayList<Pair<Integer,Integer>> getItems() {
        return items;
    }

    /** id getter */
    public Integer getId() {
        return id;
    }

    /** amount getter */
    public Integer getAmount() {
        return amount;
    }

    /** merchant id getter */
    public Integer getMid() {
        return mid;
    }

    /** successful flag getter */
    public Integer getSuccessful() {
        return successful;
    }

    /** verifone transaction id getter */
    public Integer getTxid() {
        return txid;
    }

    /** unloaded flag getter */
    public Integer getUnloaded() {
        return unloaded;
    }

    /** datetime getter */
    public String getDatetime() {
        return datetime;
    }

    /** verifone online flag getter */
    public String getOnlineflag() {
        return onlineflag;
    }

    /** receipt text getter */
    public String getReceipt() {
        return receipt;
    }

    /** verifone response code getter */
    public String getRespcode() {
        return respcode;
    }

    /** id setter */
    public void setId(Integer id) {
        this.id = id;
    }

    /** amount setter */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /** date & time setter */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /** merchant id setter */
    public void setMid(Integer mid) {
        this.mid = mid;
    }

    /** verifone online flag setter */
    public void setOnlineflag(String onlineflag) {
        this.onlineflag = onlineflag;
    }

    /** receipt text setter */
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    /** verifone response code setter */
    public void setRespcode(String respcode) {
        this.respcode = respcode;
    }

    /** successful flag setter */
    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    /** verifone transaction id setter */
    public void setTxid(Integer txid) {
        this.txid = txid;
    }

    /** unload flag setter */
    public void setUnloaded(Integer unloaded) {
        this.unloaded = unloaded;
    }

    /** formatted amount getter */
    public String getAmountFormatted(){
        String priceS = amount.toString();

        // some code from India, sorry :)
        if(priceS.length()==1){
            priceS="00"+priceS;
        }else{
            if(priceS.length()==2){
                priceS="0"+priceS;
            }
        }

        if(amount!=0) {

            return "$" + priceS.substring(0, priceS.length() - 2) + "." + priceS.substring(priceS.length() - 2);
        }
        return "$0.0";

    }

    @Override
    public String toString() {
        try {
            return "tcn_verifone_AuxTransaction{" +
                    "id=" + id +
                    ", datetime='" + datetime + '\'' +
                    ", amount=" + amount +
                    ", txid=" + txid +
                    ", mid=" + mid +
                    ", receipt='" + receipt + '\'' +
                    ", respcode='" + respcode + '\'' +
                    ", onlineflag='" + onlineflag + '\'' +
                    ", successful=" + successful +
                    ", unloaded=" + unloaded +
                    ", discountUid='" + discountUid + '\'' +
                    ", discountValue='" + discountValue + '\'' +
                    ", cardType='" + cardType + '\'' +
                    ", items=" + items +
                    '}';
        }catch (Exception ex){
             ex.printStackTrace();
        }
        return "";
    }
}
