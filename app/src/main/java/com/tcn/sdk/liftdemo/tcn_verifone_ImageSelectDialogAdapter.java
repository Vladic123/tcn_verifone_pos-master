package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import static controller.VendApplication.myImageCache;

/**
 * image select dialog adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageSelectDialogAdapter extends BaseAdapter {

        private Context context=null;
        private final ArrayList<tcn_verifone_AuxImage> images;

        public tcn_verifone_ImageSelectDialogAdapter(Context context, ArrayList<tcn_verifone_AuxImage> images ) {
            this.context = context;
            this.images = images;
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
                convertView = inflater.inflate(R.layout.product_manager_image_element, parent, false);
            }
            ImageView image = convertView.findViewById(R.id.product_manager_image_image);

            Bitmap b = myImageCache.getBitmap(images.get(position).getId());
            Drawable BD = new BitmapDrawable(context.getResources(), b);
            image.setBackground(BD);


            return convertView;
        }

    }


