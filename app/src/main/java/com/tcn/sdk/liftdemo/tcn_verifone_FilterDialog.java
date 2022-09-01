package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * flavours filter dialogue
 * handles products filtering with selected flavours
 * @author v.vasilchikov
 */

public class tcn_verifone_FilterDialog extends DialogFragment implements View.OnClickListener {

    private TableLayout flavoursGrid;
    private TableRow flavoursRow;
    private Context context;
    public interface FilterDialogListener {
        void onFinishFilterDialog(Integer flavourId);
    }

    tcn_verifone_FilterDialog(tcn_verifone_FilterDialog.FilterDialogListener listener){
        this.listener=listener;
    }

    private FilterDialogListener listener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();

        // init UI
        View v = inflater.inflate(R.layout.dialog_filter, null);
        ColorDrawable CD = new ColorDrawable(Color.TRANSPARENT);
        getDialog().getWindow().setBackgroundDrawable(CD);
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);


        flavoursGrid = v.findViewById(R.id.flavour_grid);
        flavoursRow = v.findViewById(R.id.tablerow);
        ViewGroup.LayoutParams TRParams = flavoursRow.getLayoutParams();
        flavoursGrid.removeView(flavoursRow);

        // get flavours list
        ArrayList<tcn_verifone_AuxFlavour> flavours = myDB.FlavoursGetAll();

        int linesCount=0;
        TableRow TR=null;
        ConstraintLayout CL = null;
        // build and display flavours list
        for(final tcn_verifone_AuxFlavour flavour:flavours){

            if(linesCount==0){
                TR = new TableRow(context);
                TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT
                        ,0,1.0f);
                TR.setLayoutParams(tableRowParams);

            }

            View cellView = inflater.inflate(R.layout.flavour_element,null);

            ImageView flavourBackground = cellView.findViewById(R.id.flavour_element_background);
            ColorDrawable bg = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(flavour.getImage()))));
            flavourBackground.setBackground(bg);


            ImageView flavourImage = cellView.findViewById(R.id.flavour_element_image);
            Bitmap b = myImageCache.getBitmap(flavour.getImage());
            Drawable BD = new BitmapDrawable(null,b);
            flavourImage.setBackground(BD);

            TextView flavourText = cellView.findViewById(R.id.flavour_text);
            flavourText.setText(flavour.getName());

            CardView flavourButton = cellView.findViewById(R.id.flavour_element_card);
            if(myDB.FlavoursItemsGetItemsByFlavour(flavour.getId())!=null) {
                flavourButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                processButton(flavour.getId());
                            }
                        }
                );
            }else{
                flavourImage.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                //flavourImage.getBackground().setColorFilter(Color.argb(120,200,200,200), PorterDuff.Mode.DST_ATOP);
            }
            TR.addView(cellView);

            linesCount++;
            if(linesCount==3){

                flavoursGrid.addView(TR);
                linesCount=0;
            }

        }

        if(linesCount>0){
            flavoursGrid.addView(TR);
        }

        return v;

    }

    private  void processButton(Integer id){
        listener.onFinishFilterDialog(id);
        dismiss();
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
