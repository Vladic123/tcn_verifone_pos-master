package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * product list adapter class
 * @author v.vasilchikov
 */

public class tcn_verifone_product_list_adapter extends BaseAdapter{
    private final Context context;
    private final ArrayList<tcn_verifone_AuxItem> products;

    public tcn_verifone_product_list_adapter(Context context, ArrayList<tcn_verifone_AuxItem> products ) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount(){
        return products.size();
    }

    @Override
    public long getItemId(int position){

        long a = Long.parseLong(products.get(position).getId().toString());
        return a;
    }

    @Override
    public Object getItem(int position){
        return products.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.main_product_element, parent, false);

            }
            // get and apply product image
            ImageView productImage = convertView.findViewById(R.id.product_element_image);
            Bitmap b = myImageCache.getBitmap(products.get(position).getImage());
            Drawable BD = new BitmapDrawable(null, b);
            productImage.setBackground(BD);

            ArrayList<Integer> AF = myDB.FlavoursItemsGetFlavoursByItem(products.get(position).getId());
            if(AF!=null) {
                Integer size = AF.size();
                if (size > 3) {
                    size = 3;
                }
                for (int a = 0; a < size; a++) {

                    switch (a) {
                        case 0:
                            ImageView flavourImage = convertView.findViewById(R.id.product_flavour_image_1);
                            Bitmap b1 = myImageCache.getBitmap(myDB.FlavourGetImageByID(AF.get(a)));
                            Drawable BD1 = new BitmapDrawable(null, b1);
                            flavourImage.setBackground(BD1);
                            flavourImage.setVisibility(View.VISIBLE);


                            break;
                        case 1:
                            ImageView flavourImage2 = convertView.findViewById(R.id.product_flavour_image_2);
                            Bitmap b2 = myImageCache.getBitmap(myDB.FlavourGetImageByID(AF.get(a)));
                            Drawable BD2 = new BitmapDrawable(null, b2);
                            flavourImage2.setBackground(BD2);
                            flavourImage2.setVisibility(View.VISIBLE);

/*                    CardView flavourBg2 = cellView.findViewById(R.id.product_flavour_circle_back_2);
                    ColorDrawable bg2 = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(myDB.FlavourGetImageByID(AF.get(a))))));
                    flavourBg2.setBackground(bg2);
                    flavourBg2.setVisibility(View.VISIBLE);*/

                            break;
                        case 2:
                            ImageView flavourImage3 = convertView.findViewById(R.id.product_flavour_image_3);
                            Bitmap b3 = myImageCache.getBitmap(myDB.FlavourGetImageByID(AF.get(a)));
                            Drawable BD3 = new BitmapDrawable(null, b3);
                            flavourImage3.setBackground(BD3);
                            flavourImage3.setVisibility(View.VISIBLE);

/*                    CardView flavourBg3 = cellView.findViewById(R.id.product_flavour_circle_back_3);
                    ColorDrawable bg3 = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(myDB.FlavourGetImageByID(AF.get(a))))));
                    flavourBg3.setBackground(bg3);
                    flavourBg3.setVisibility(View.VISIBLE);*/

                            break;
                    }


                }
            }
            // get and apply background color
            ImageView productBackground = convertView.findViewById(R.id.product_element_background);

            ColorDrawable bg = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(products.get(position).getImage()))));
            productBackground.setBackground(bg);

            TextView textItem = convertView.findViewById(R.id.product_element_text);
            textItem.setText(products.get(position).getDescription());

            TextView priceItem = convertView.findViewById(R.id.product_element_text_price);
            priceItem.setText(products.get(position).getFormattedPrice());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertView;
    }


}
