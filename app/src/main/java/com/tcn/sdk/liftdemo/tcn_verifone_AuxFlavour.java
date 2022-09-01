package com.tcn.sdk.liftdemo;

/**
 * class for handling flavours
 * @author v.vasilchikov
 */

public class tcn_verifone_AuxFlavour {

    /** flavour Id */
    private Integer id=-1;
    /** flavour name */
    private String name="";
    /** flavour image Id */
    private Integer image=-1;

    /** id getter */
    public Integer getId() {
        return id;
    }

    /** name getter */
    public String getName() {
        return name;
    }

    /** image getter */
    public Integer getImage() {
        return image;
    }

    /** id setter */
    public void setId(Integer id) {
        this.id = id;
    }

    /** image setter */
    public void setImage(Integer image) {
        this.image = image;
    }

    /** name setter */
    public void setName(String name) {
        this.name = name;
    }
}
