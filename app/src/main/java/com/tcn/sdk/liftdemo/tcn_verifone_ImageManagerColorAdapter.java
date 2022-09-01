package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * image manager color adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageManagerColorAdapter extends BaseAdapter {

    private Context context=null;
    private ArrayList<tcn_verifone_AuxColor> colors;

    public tcn_verifone_ImageManagerColorAdapter(Context context, ArrayList<tcn_verifone_AuxColor> colors ) {
        this.context = context;
        this.colors = colors;
    }

    @Override
    public int getCount(){
        return colors.size();
    }

    @Override
    public long getItemId(int position){

        long a = colors.get(position).getId();
        return a;
    }

    @Override
    public Object getItem(int position){

        return colors.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.image_manager_color_adapter, parent, false);
        }


        ImageView colorBg = convertView.findViewById(R.id.image_manager_color_element);


        ColorDrawable bg = new ColorDrawable(Color.parseColor(colors.get(position).getColor()));
        colorBg.setBackground(bg);


        return convertView;
    }

}

