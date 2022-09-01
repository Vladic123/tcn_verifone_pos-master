package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * color list adapter class
 * @author v.vasilchikov
 */

public class tcn_verifone_ColorListAdapter extends BaseAdapter {

    private  Context context=null;
    private final ArrayList<tcn_verifone_AuxColor> colors;

    public tcn_verifone_ColorListAdapter(Context context, ArrayList<tcn_verifone_AuxColor> colors ) {
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
            convertView = inflater.inflate(R.layout.tcn_verifone_color_list_element, parent, false);
        }

        /**
         * bind UI list elements
         * */
        TextView idText = convertView.findViewById(R.id.color_element_id_text);
        ImageView colorBg = convertView.findViewById(R.id.color_element_color);
        Button editButton = convertView.findViewById(R.id.color_element_edit_button);
        Button deleteButton = convertView.findViewById(R.id.color_element_delete_button);


        tcn_verifone_AuxColor AC = colors.get(position);

        String color = AC.getColor();
        Integer id = AC.getId();

        idText.setText(id.toString());

        ColorDrawable bg = new ColorDrawable(Color.parseColor(color));
        colorBg.setBackground(bg);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long a = 0L+ colors.get(position).getId();

                ((tcn_verifone_ColorManager)context).onButtonEdit(a);


            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long a = 0L+ colors.get(position).getId();

                ((tcn_verifone_ColorManager)context).onButtonDelete(a);


            }
        });


        return convertView;
    }

}

