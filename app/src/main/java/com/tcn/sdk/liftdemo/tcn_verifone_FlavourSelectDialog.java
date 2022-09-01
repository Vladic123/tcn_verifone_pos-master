package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;

import static controller.VendApplication.myDB;

/**
 * flavour select dialog
 * handles flavour selections
 * @author v.vasilchikov
 */

public class tcn_verifone_FlavourSelectDialog extends DialogFragment implements View.OnClickListener {

    private GridLayout flavoursGrid;
    private ArrayList<Integer> Items;
    private Integer currentItemIndex = 0;
    private ImageView productImage;
    private TextView productText;
    private ImageView leftButton;
    private ImageView rightButton;

    public interface FlavourSelectDialogListener {
        void onFlavourSelectDialog(Integer itemId);
    }

    tcn_verifone_FlavourSelectDialog(tcn_verifone_FlavourSelectDialog.FlavourSelectDialogListener listener){
        this.listener=listener;
    }

    private FlavourSelectDialogListener listener;

    private Context context;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        ArrayList<tcn_verifone_AuxFlavour> AF = myDB.FlavoursGetAll();

        View v = inflater.inflate(R.layout.flavour_select_dialog, null);
        ColorDrawable CD = new ColorDrawable(Color.TRANSPARENT);
        getDialog().getWindow().setBackgroundDrawable(CD);

        ListView LV = v.findViewById(R.id.flavour_select_dialog_list);
        tcn_verifone_FlavourSelectDialogAdapter DA = new tcn_verifone_FlavourSelectDialogAdapter(context, AF);
        LV.setAdapter(DA);

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long idl = id;
                listener.onFlavourSelectDialog(idl.intValue());
                dismiss();
            }
        });

        return v;
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
