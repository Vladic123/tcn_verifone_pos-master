package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.Long.parseLong;

/**
 * one button array adpter
 * @author v.vasilchikov
 */

public class tcn_verifone_OneButtonArrayAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<tcn_verifone_AuxLane> buttons;

    public tcn_verifone_OneButtonArrayAdapter(Context context, ArrayList<tcn_verifone_AuxLane> buttons ) {
        this.context = context;
        this.buttons = buttons;
    }

    @Override
    public int getCount(){
        return buttons.size();
    }

    @Override
    public long getItemId(int position){

        long a = buttons.get(position).getId();
        return a;
    }

    @Override
    public Object getItem(int position){

        return buttons.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null) {
            convertView= inflater.inflate(R.layout.one_button_list_view, parent, false);
        }
        Button Button1 = convertView.findViewById(R.id.one_button_array_button);
        TextView id1 = convertView.findViewById(R.id.one_button_array_id);

        tcn_verifone_AuxItem AI = buttons.get(position).getItem();

        String price = AI.getFormattedPrice();
//        price = "$"+price.substring(0,price.length()-2)+"."+price.substring(price.length()-2,price.length());

        String text = "Lane "+buttons.get(position).getId()+"\n"+AI.getDescription()+"\n"+price;

        Button1.setText(text);
        id1.setText(buttons.get(position).getId().toString());

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long a = 0L+ buttons.get(position).getId();

                ((tcn_verifone_SaleActivity)context).onButtonSelect(a);
              

            }
        });

        return convertView;
    }

}

