package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

/**
 * Customer help activity class
 * displays and handles help screen
 * @author v.vasilchikov
 */

public class tcn_verifone_helpDialog extends DialogFragment implements View.OnClickListener {

    private Context context;
    public interface FilterDialogListener {
        void onFinishFilterDialog(Integer flavourId);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();

        View v = inflater.inflate(R.layout.dialog_helpdialog, null);

        CardView outerFrame = v.findViewById(R.id.outerframe);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            outerFrame.getBackground().setAlpha(0);
        } else {
            outerFrame.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        return v;

    }

    private  void processButton(Integer id){
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

