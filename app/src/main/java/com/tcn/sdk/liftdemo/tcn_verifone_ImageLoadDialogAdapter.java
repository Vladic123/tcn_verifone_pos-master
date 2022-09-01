package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import static controller.VendApplication.myImageCache;

/**
 * Image loader dialog adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageLoadDialogAdapter extends BaseAdapter {

    private Context context=null;
    private final ArrayList<Pair<Integer,String>> images;
    private static ImageView colorBg=null;
    private static View rowView;

    public tcn_verifone_ImageLoadDialogAdapter(Context context, ArrayList<Pair<Integer,String>> images ) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount(){
        return images.size();
    }

    @Override
    public long getItemId(int position){

         long a = images.get(position).first;
        return a;
    }

    @Override
    public Object getItem(int position){

        return images.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.image_manager_image_from_disk, parent, false);
            }

            colorBg = convertView.findViewById(R.id.image_manager_image_from_disk_image);
            String exStoragePath = System.getenv("EXTERNAL_STORAGE");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            try {
                colorBg.setImageBitmap(BitmapFactory.decodeFile(exStoragePath + "/Download/" + images.get(position).second, options));
            } catch (Exception ex) {
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return convertView;
    }

}


