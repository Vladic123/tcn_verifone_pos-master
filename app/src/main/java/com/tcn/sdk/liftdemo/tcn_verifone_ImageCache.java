package com.tcn.sdk.liftdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * custom image cache class
 * used for images preloading and storage
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageCache
{

    /** cached image object */
    private class CachedImage{

        /** constructor */
        CachedImage(Bitmap bitmap,Integer id, Integer color){
            this.bitmap=bitmap;
            this.id=id;
            this.color=color;
        }

        /** image bitmap */
        Bitmap bitmap=null;
        /** image id */
        Integer id=-1;
        /** bacground color for image */
        Integer color=-1;
    }

    /** array of images */
    private ArrayList<CachedImage> bitmaps;
    /** flag indicates images loaded */
    private Boolean isLoaded = false;

    /** initialization */
    public void Init(){
        bitmaps = new ArrayList<CachedImage>();
        isLoaded=false;
    }

    /** preload images */
    public Boolean preload(ArrayList<tcn_verifone_AuxImage> imageList){

        Boolean result = true;

        Init();
        String exStoragePath = System.getenv("EXTERNAL_STORAGE");

        // get images and add it to list
        for(tcn_verifone_AuxImage img:imageList){
            BitmapFactory.Options options = new BitmapFactory.Options();
            try {
                Bitmap b = BitmapFactory.decodeFile(exStoragePath+"/Download/"+img.getNamepath(), options);
                CachedImage cimg = new CachedImage(b, img.getId(),img.getBgcolor());
                bitmaps.add(cimg);
            }catch(Exception ex){
                result = false;
            }
        }

        if(result){
            isLoaded = true;
        }

        return result;
    }

    /** return image bitmap by image id */
    public Bitmap getBitmap(Integer id){

        Bitmap result = null;

        try {
            if (bitmaps != null) {
                for (CachedImage imgPair : bitmaps) {
                    if (imgPair.id == id) {
                        result = imgPair.bitmap;
                    }
                }
            }
        }catch (Exception ex){
            result = Bitmap.createBitmap(0,0, Bitmap.Config.RGB_565);
        }

        return result;
    }

    /** get background color by image id */
    public Integer getBgColor(Integer id){

        Integer result = -1;

        try {
            for (CachedImage imgPair : bitmaps) {
                if (imgPair.id == id) {
                    result = imgPair.color;
                }
            }
        }catch (Exception ex){
        }

        return result;
    }

    public Boolean getLoaded() {
        return isLoaded;
    }
}
