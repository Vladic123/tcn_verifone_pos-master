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
 * product manager product list adapter
 * @author v.vasilchikov
 */

public class tcn_verifone_ProductManagerProductAdapter extends BaseAdapter {


    private Context context=null;
    private final ArrayList<tcn_verifone_AuxItem> items;

    public tcn_verifone_ProductManagerProductAdapter(Context context, ArrayList<tcn_verifone_AuxItem> items ) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public long getItemId(int position){

        long a = items.get(position).getId();
        return a;
    }

    @Override
    public Object getItem(int position){

        return items.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.product_manager_product_list_element, parent, false);
        }

        ImageView itemImage = convertView.findViewById(R.id.product_manager_product_list_image);
        TextView itemName =  convertView.findViewById(R.id.product_manager_product_list_name);
        TextView itemDsc = convertView.findViewById(R.id.product_manager_product_list_description);
        TextView itemPrice = convertView.findViewById(R.id.product_manager_product_list_price);
        ImageView deleteItem = convertView.findViewById(R.id.product_manager_product_list_delete);

        itemName.setText(items.get(position).getName());
        itemDsc.setText(items.get(position).getDescription());
        itemPrice.setText(items.get(position).getFormattedPrice());

        Bitmap b = myImageCache.getBitmap(items.get(position).getImage());
        Drawable BD = new BitmapDrawable(context.getResources(), b);
        itemImage.setBackground(BD);

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer a = 0+ items.get(position).getId();

                ((tcn_verifone_ProductManager)context).onProductButtonDelete(a);
            }
        });


        return convertView;
    }

}

