package com.tcn.sdk.liftdemo;

/**
 * class to handle Items
 * @author v.vasilchikov
 */

public class tcn_verifone_AuxItem {

    /** item id */
    private Integer id=-1;
    /** item name */
    private String name="";
    /** short item description */
    private String description="";
    /** long item description */
    private String detailed_description="";
    /** item image */
    private Integer image=-1;
    /** large item image */
    private Integer image_large=-1;
    /** item price */
    private Integer price=0;
    /** item volume */
    private Integer volume = -1;
    /** flag indicates item can be sold */
    private Boolean enabled = false;

    /** description setter */
    public void setDetailed_description(String detailed_description) {
        this.detailed_description = detailed_description;
    }

    /** description getter */
    public String getDetailed_description() {
        return detailed_description;
    }

    /** item flag getter */
    public Boolean getEnabled() {
        return enabled;
    }

    /** item flag setter */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /** volume getter */
    public Integer getVolume() {
        return volume;
    }

    /** volume setetr */
    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    /** id getter */
    public Integer getId() {
        return id;
    }

    /** price getter */
    public Integer getPrice() {
        return price;
    }

    /** full price getter */
    public Integer getPriceWithDiscount(Double discount) {
        Double a = (price-price*discount);
        return a.intValue();
    }

    /** formatted price with discount applied getter */
    public String getFormattedPriceWithDiscount(Double discount){

        String priceS = getPriceWithDiscount(discount).toString();

        // some code from India, sorry :)
        if(priceS.length()==1){
            priceS="00"+priceS;
        }else{
            if(priceS.length()==2){
                priceS="0"+priceS;
            }
        }


        if(price!=0 && !priceS.equals("0")) {
            return "$" + priceS.substring(0, priceS.length() - 2) + "." + priceS.substring(priceS.length() - 2);
        }
        return "$0.0";
    }

    /** description getter */
    public String getDescription() {
        return description;
    }

    /** image getter */
    public Integer getImage() {
        return image;
    }

    /** large image getter */
    public Integer getImageLarge() {
        return image_large;
    }

    /** name getter */
    public String getName() {
        return name;
    }

    /** description setter */
    public void setDescription(String description) {
        this.description = description;
    }

    /** id setter */
    public void setId(Integer id) {
        this.id = id;
    }

    /** image setter */
    public void setImage(Integer image) {
        this.image = image;
    }

    /** large image setter */
    public void setImageLarge(Integer image_large) {
        this.image_large = image_large;
    }

    /** name setter */
    public void setName(String name) {
        this.name = name;
    }

    /** price setter */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /** formatted price getter */
    public String getFormattedPrice(){

        String priceS = price.toString();
        // some code from India, sorry :)
        if(priceS.length()==1){
            priceS="00"+priceS;
        }else{
            if(priceS.length()==2){
                priceS="0"+priceS;
            }
        }

        if(price!=0) {
            return "$" + priceS.substring(0, priceS.length() - 2) + "." + priceS.substring(priceS.length() - 2);
        }
        return "$0.0";
    }
}
