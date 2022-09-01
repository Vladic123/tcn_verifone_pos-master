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
 * flavour selection dialog adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_FlavourSelectDialogAdapter extends BaseAdapter {

    private Context context=null;
    private final ArrayList<tcn_verifone_AuxFlavour> flavours;

    public tcn_verifone_FlavourSelectDialogAdapter(Context context, ArrayList<tcn_verifone_AuxFlavour> flavours ) {
        this.context = context;
        this.flavours = flavours;
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

        ImageView colorBg = convertView.findViewById(R.id.product_manager_flavour_element_image);

        ImageView deleteButton = convertView.findViewById(R.id.product_manager_flavour_element_delete_button);

        TextView flavourname = convertView.findViewById(R.id.product_manager_flavour_element_text);


        Bitmap b = myImageCache.getBitmap(flavours.get(position).getImage());
        Drawable BD = new BitmapDrawable(context.getResources(), b);
        colorBg.setBackground(BD);

        deleteButton.setVisibility(View.INVISIBLE);

        flavourname.setText(flavours.get(position).getName());

        return convertView;
    }

}


