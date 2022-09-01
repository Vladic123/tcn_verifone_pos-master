package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * image manager image list adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageManagerImageAdapter extends BaseAdapter {

    private Context context=null;
    private ArrayList<tcn_verifone_AuxImage> images;
    private Boolean deleteFlag;

    public tcn_verifone_ImageManagerImageAdapter(Context context, ArrayList<tcn_verifone_AuxImage> images, Boolean delete ) {
        this.context = context;
        this.images = images;
        deleteFlag = delete;
    }

    @Override
    public int getCount(){
        return images.size();
    }

    @Override
    public long getItemId(int position){

        long a = images.get(position).getId();
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

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.image_manager_image_adapter, parent, false);
        }


        ImageView colorBg = convertView.findViewById(R.id.image_manager_adapter_bg_color);
        ImageView mainImage = convertView.findViewById(R.id.image_manager_adapter_main_image);
        ImageView deleteButton = convertView.findViewById(R.id.image_manager_adapter_delete_button);


        ColorDrawable bg = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(images.get(position).getBgcolor())));
        colorBg.setBackground(bg);

        mainImage.setImageBitmap(myImageCache.getBitmap(images.get(position).getId()));

        if(deleteFlag) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer a = 0 + images.get(position).getId();

                    ((tcn_verifone_ImageManager) context).onImageButtonDelete(a);
                }
            });
        }else{
            deleteButton.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}

