package com.tcn.sdk.liftdemo;

/**
 * class for handling lane object
 * @author v.vasilchikov
 */

public class tcn_verifone_AuxLane {

    /** lane id */
    private Integer id = -1;
    /** verifone item */
    private tcn_verifone_AuxItem item = null;
    /** items amount */
    private Integer amount = 0;
    /** lane status */
    private Integer status = 0;
    /** lane printed number */
    private Integer printed_num = -1;

    /** printed number setter */
    public void setPrinted_num(Integer printed_num) {
        this.printed_num = printed_num;
    }

    /** printed number getter */
    public Integer getPrinted_num() {
        return printed_num;
    }

    /** id setter */
    public void setId(Integer id) {
        this.id = id;
    }

    /** id getter */
    public Integer getId() {
        return id;
    }

    /** amount setter */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /** status setter */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /** verifone item getter */
    public tcn_verifone_AuxItem getItem() {
        return item;
    }

    /** amount getter */
    public Integer getAmount() {
        return amount;
    }

    /** status getter */
    public Integer getStatus() {
        return status;
    }

    /** verifone item setter */
    public void setItem(tcn_verifone_AuxItem item) {
        this.item = item;
    }

}
