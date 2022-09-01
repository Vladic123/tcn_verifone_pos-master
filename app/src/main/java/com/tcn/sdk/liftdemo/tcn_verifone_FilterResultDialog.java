package com.tcn.sdk.liftdemo;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;


/**
 * filter result dailog
 * displays result of items filterig
 * @author v.vasilchikov
 */

public class tcn_verifone_FilterResultDialog extends DialogFragment implements View.OnClickListener {

    private GridLayout flavoursGrid;
    private ArrayList<Integer> Items;
    private Integer currentItemIndex = 0;
    private ImageView productImage;
    private TextView productText;
    private ImageView leftButton;
    private ImageView rightButton;

    public interface FilterResultDialogListener {
        void onFinishFilterResultDialog(Integer itemId);
    }

    tcn_verifone_FilterResultDialog(tcn_verifone_FilterResultDialog.FilterResultDialogListener listener){
        this.listener=listener;
    }

    private tcn_verifone_FilterResultDialog.FilterResultDialogListener listener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        Integer flavourId = args.getInt("id");
        tcn_verifone_AuxFlavour AF = myDB.FlavoursGetById(flavourId);

        View v = inflater.inflate(R.layout.flavour_filter_result, null);
        ColorDrawable CD = new ColorDrawable(Color.TRANSPARENT);
        getDialog().getWindow().setBackgroundDrawable(CD);

        if(AF!=null) {

            ImageView flavourBackground = v.findViewById(R.id.flavour_filter_flavour_background);
            ColorDrawable bg = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(AF.getImage()))));
            flavourBackground.setBackground(bg);
        }

        ImageView flavourImage = v.findViewById(R.id.flavour_filter_flavour_element_image);
        Bitmap b = myImageCache.getBitmap(AF.getImage());
        Drawable BF = new BitmapDrawable(null,b);
        flavourImage.setBackground(BF);

        TextView flavourText = v.findViewById(R.id.flavour_filter_flavour_text);
        flavourText.setText(AF.getName());

        leftButton = v.findViewById(R.id.flavour_filter_left_button);
        rightButton = v.findViewById(R.id.flavour_filter_right_button);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentItemIndex>0){
                    currentItemIndex--;
                    updateItem();
                }
                updateArrows();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentItemIndex<Items.size()-1){
                    currentItemIndex++;
                    updateItem();
                }
                updateArrows();
            }
        });

        Items = myDB.FlavoursItemsGetItemsByFlavour(AF.getId());

        productImage = v.findViewById(R.id.flavour_filter_product_element_image);
        productText = v.findViewById(R.id.flavour_filter_product_element_text);

        CardView productCard = v.findViewById(R.id.flavour_filter_product_cardview);
        productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinishFilterResultDialog(Items.get(currentItemIndex));
            }
        });
        updateItem();

        updateArrows();

        return v;
    }

    private void updateArrows(){
        if(currentItemIndex==Items.size()-1){
            rightButton.setVisibility(View.INVISIBLE);
        }else{
            rightButton.setVisibility(View.VISIBLE);
        }
        if(currentItemIndex==0){
            leftButton.setVisibility(View.INVISIBLE);
        }else{
            leftButton.setVisibility(View.VISIBLE);
        }

    }

    private void updateItem(){
        if(Items!=null) {
            tcn_verifone_AuxItem item = myDB.GoodsGetItemById(Items.get(currentItemIndex));
            Bitmap c = myImageCache.getBitmap(item.getImage());
            Drawable BD = new BitmapDrawable(null, c);
            productImage.setBackground(BD);
            productText.setText(item.getDescription() + "\n" + item.getFormattedPrice());
        }
    }

    private  void processButton(Integer id){

    }

    public void onClick(View v) {
        //Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //Log.d(LOG_TAG, "Dialog 1: onCancel");
    }
}
