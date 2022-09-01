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
import android.widget.TextView;

import java.util.ArrayList;

import static controller.VendApplication.myImageCache;

/**
 * product manager flavor adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_ProductManagerFlavourAdapter extends BaseAdapter {

    private Context context=null;
    private final ArrayList<tcn_verifone_AuxFlavour> flavours;
    private Boolean deleteFlag;

    public tcn_verifone_ProductManagerFlavourAdapter(Context context, ArrayList<tcn_verifone_AuxFlavour> flavours, Boolean delete ) {
        this.context = context;
        this.flavours = flavours;
        deleteFlag=delete;
    }

    @Override
    public int getCount(){
        return flavours.size();
    }

    @Override
    public long getItemId(int position){

        long a = flavours.get(position).getId();
        return a;
    }

    @Override
    public Object getItem(int position){

        return flavours.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.product_manager_flavour_element, parent, false);
        }

        TextView idText = convertView.findViewById(R.id.product_manager_flavour_element_text);
        ImageView colorBg = convertView.findViewById(R.id.product_manager_flavour_element_image);

        ImageView deleteButton = convertView.findViewById(R.id.product_manager_flavour_element_delete_button);


        idText.setText(flavours.get(position).getName());

        Bitmap b = myImageCache.getBitmap(flavours.get(position).getImage());
        Drawable BD = new BitmapDrawable(context.getResources(), b);
        colorBg.setBackground(BD);


        if(deleteFlag){
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer a = 0 + flavours.get(position).getId();

                    ((tcn_verifone_FlavoursManager) context).onFlavourButtonDelete(a);
                }
            });
        }else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer a = 0 + flavours.get(position).getId();

                    ((tcn_verifone_ProductManager) context).onFlavourButtonDelete(a);
                }
            });
        }


        return convertView;
    }

}

