package com.tcn.sdk.liftdemo;

import android.util.Pair;

import org.json.JSONObject;

import java.util.ArrayList;

import static controller.VendApplication.myDB;

/**
 * class for handling Cart object
 * @author v.vasilchikov
 */

public class tcn_verifone_AuxCart {

    /** List for storing items*/
    private ArrayList<Pair<Integer,Integer>> itemsCount;

    /** Cart using start time */
    private Long startTime;

    /** transaction linked to cart after payment */
    private tcn_verifone_AuxTransaction transaction;

    /** barcode */
    private JSONObject barcode;

    /** flag indicates payment complete */
    private Boolean Paid = false;

    /** tinteger ransaction Id */
    private Integer txid = -1;

    /** discount value */
    public double discountValue;

    /** transaction Id setter */
    public void setTxid(Integer txid) {
        this.txid = txid;
    }

    /** transaction Id getter */
    public Integer getTxid() {
        return txid;
    }

    /** paid flag setter */
    public void setPaid(Boolean paid) {
        Paid = paid;
    }

    /** paid flag getter */
    public Boolean getPaid() {
        return Paid;
    }

    /** discount UID getter */
    public String getDiscountUid(){
        String result = "";
        try{
            result = barcode.getString("id");
        }catch(Exception ex){

        }

        return result;
    }

    /** discount value setter */
    public void setDiscountValue(Double discount){
        discountValue = discount;
    }

    /** discount value getter */
    public Double getDiscountValue(){
        return discountValue;
    }

    /** barcode getter */
    public JSONObject getBarcode() {
        return barcode;
    }

    /** barcode setter */
    public void setBarcode(JSONObject barcode) {
        this.barcode = barcode;
    }

    /** cart object initialization */
    public void Init(){
        itemsCount = new ArrayList<Pair<Integer,Integer>>();
        transaction=new tcn_verifone_AuxTransaction();
        barcode=null;
        discountValue = 0;
        Paid = false;
        resetTime();
    }

    /** transaction getter */
    public tcn_verifone_AuxTransaction getTransaction() {
        return transaction;
    }

    /** transcation setter */
    public void setTransaction(tcn_verifone_AuxTransaction transaction) {
        this.transaction = transaction;
    }

    /** start time reset */
    private void resetTime(){
        startTime = System.currentTimeMillis()/1000L;
    }

    /** add one item in to cart */
    public void AddOneItem(Integer itemId){

        Boolean flag = false;
        if(discountValue<0.5) {
            flag=true;
        }else{
            if(getItemsCount()<1){
                flag=true;
            }
        }

        if(flag){
            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(itemId, 1);
            AddItem(pair);

        }

    }

    /** add some items to cart */
    public void AddItem (Pair<Integer,Integer> ItemCount){


            boolean flag = false;
            for (int a = 0; a < itemsCount.size(); a++) {
                Pair<Integer, Integer> item = itemsCount.get(a);

                if (item.first == ItemCount.first) {
                    Pair<Integer, Integer> newitem = new Pair<Integer, Integer>(item.first, (item.second + ItemCount.second));
                    itemsCount.set(a, newitem);
                    flag = true;
                    break;
                }

            }
            if (!flag) {
                itemsCount.add(ItemCount);
            }
        resetTime();
    }

    /** delete one item from cart */
    public void DeleteOneItem(Integer itemId){

        for(int a=0;a<itemsCount.size();a++){
            Pair<Integer,Integer> pair =itemsCount.get(a);

            if(pair.first==itemId && pair.second>1){
                Pair<Integer,Integer> newPair = new Pair<Integer, Integer>(pair.first,pair.second-1);
                itemsCount.remove(a);
                itemsCount.add(a,newPair);
                break;
            }
        }
        resetTime();


    }

    /** delete item from cart */
    public void DeleteItem(Integer itemId){

        for(int a=0;a<itemsCount.size();a++){
            if(itemsCount.get(a).first==itemId){
                itemsCount.remove(a);
                break;
            }
        }
        resetTime();
    }

    /** get cart expiration status */
    public Boolean isExpired(){
        return (System.currentTimeMillis() / 1000L - startTime) > 120;
    }

    /** get cart items count */
    public Integer getItemsCount(){

        Integer itemsInCart=0;
        for(Pair<Integer,Integer> ic:itemsCount){
            itemsInCart = itemsInCart + ic.second;
        }
        return itemsInCart;
    }

    /** get cart items count by item Id */
    public Integer getItemsCountById(Integer id){

        Integer itemsInCart=0;
        for(Pair<Integer,Integer> ic:itemsCount){
            if (ic.first==id) {
                itemsInCart = itemsInCart + ic.second;
            }
        }
        return itemsInCart;
    }

    /** get items */
    public ArrayList<Pair<Integer,Integer>> getItems(){
        return itemsCount;
    }

    /** get cart total */
    public Integer getCartTotal(){
        Integer total =0;
        for(Pair<Integer,Integer> ic:itemsCount) {
            tcn_verifone_AuxItem AI = myDB.GoodsGetItemById(ic.first);
            if (discountValue > 0) {
                total = total + AI.getPriceWithDiscount(discountValue) * ic.second;
            } else {
                total = total + AI.getPrice() * ic.second;
            }
        }
        return total;
    }

    @Override
    public String toString() {
        try {
            String result = "tcn_verifone_AuxCart{" +
                    "itemsCount=" + itemsCount +
                    ", startTime=" + startTime +
                    ", transaction=" + transaction +
                    ", barcode=" + barcode +
                    ", Paid=" + Paid +
                    ", txid=" + txid +
                    ", discountValue=" + discountValue +
                    '}';
            return result;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
