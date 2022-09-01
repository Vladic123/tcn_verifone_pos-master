package com.tcn.sdk.liftdemo;

/**
 * class for handling Color
 * @author v.vasilchikov
 */

public class tcn_verifone_AuxColor {

    /** color name */
    private String color="";

    /** color Id */
    private Integer id=-1;

    /** constructor */
    tcn_verifone_AuxColor(int id, String color){
        this.id=id;
        this.color=color;
    }

    /** set color Id */
    public void setId(Integer id) {
        this.id = id;
    }

    /** set color */
    public void setColor(String color) {
        this.color = color;
    }

    /** get color Id */
    public Integer getId() {
        return id;
    }

    /** get color */
    public String getColor() {
        return color;
    }
}
