package com.tcn.sdk.liftdemo;

/**
 * class for handling images
 */

public class tcn_verifone_AuxImage {

    /** image id */
    private Integer id = -1;
    /** image name and path on the disk */
    private String namepath = "";
    /** image background color id */
    private Integer bgcolor = -1;

    /** id getter */
    public Integer getId() {
        return id;
    }

    /** background color getter */
    public Integer getBgcolor() {
        return bgcolor;
    }

    /** name and path getter */
    public String getNamepath() {
        return namepath;
    }

    /** background setter */
    public void setBgcolor(Integer bgcolor) {
        this.bgcolor = bgcolor;
    }

    /** id setter */
    public void setId(Integer id) {
        this.id = id;
    }

    /** name and path setter */
    public void setNamepath(String namepath) {
        this.namepath = namepath;
    }
}
