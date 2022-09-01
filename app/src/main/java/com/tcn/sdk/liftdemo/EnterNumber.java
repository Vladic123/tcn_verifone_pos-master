package com.tcn.sdk.liftdemo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

public class EnterNumber extends DialogFragment implements View.OnClickListener {

    public interface EnterNumberDialogListener {
        void onFinishEnterDialog(Integer lanePrintedId);
    }

    EnterNumber(EnterNumberDialogListener listener){
        this.listener=listener;
    }

    private EnterNumberDialogListener listener;

    private CardView b0;
    private CardView b1;
    private CardView b2;
    private CardView b3;
    private CardView b4;
    private CardView b5;
    private CardView b6;
    private CardView b7;
    private CardView b8;
    private CardView b9;
    private CardView bclear;
    private CardView bconfirm;

    private TextView t0;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private TextView t7;
    private TextView t8;
    private TextView t9;

    private int onColor;

    private Integer itemQuantity = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.dialog_enter_number, null);
        ColorDrawable CD = new ColorDrawable(Color.TRANSPARENT);
        getDialog().getWindow().setBackgroundDrawable(CD);
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);

        b0 = v.findViewById(R.id.enter_number_0);
        b1 = v.findViewById(R.id.enter_number_1);
        b2 = v.findViewById(R.id.enter_number_2);
        b3 = v.findViewById(R.id.enter_number_3);
        b4 = v.findViewById(R.id.enter_number_4);
        b5 = v.findViewById(R.id.enter_number_5);
        b6 = v.findViewById(R.id.enter_number_6);
        b7 = v.findViewById(R.id.enter_number_7);
        b8 = v.findViewById(R.id.enter_number_8);
        b9 = v.findViewById(R.id.enter_number_9);
        bclear = v.findViewById(R.id.enter_number_clear);
        bconfirm = v.findViewById(R.id.enter_number_confirm);
        t0 = v.findViewById(R.id.enter_number_0_text);
        t1 = v.findViewById(R.id.enter_number_1_text);
        t2 = v.findViewById(R.id.enter_number_2_text);
        t3 = v.findViewById(R.id.enter_number_3_text);
        t4 = v.findViewById(R.id.enter_number_4_text);
        t5 = v.findViewById(R.id.enter_number_5_text);
        t6 = v.findViewById(R.id.enter_number_6_text);
        t7 = v.findViewById(R.id.enter_number_7_text);
        t8 = v.findViewById(R.id.enter_number_8_text);
        t9 = v.findViewById(R.id.enter_number_9_text);

        onColor = t0.getCurrentTextColor();

        b0.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        bclear.setOnClickListener(this);
        bconfirm.setOnClickListener(this);

        b0.setTag(0);
        b1.setTag(1);
        b2.setTag(2);
        b3.setTag(3);
        b4.setTag(4);
        b5.setTag(5);
        b6.setTag(6);
        b7.setTag(7);
        b8.setTag(8);
        b9.setTag(9);
        bclear.setTag(-1);
        bconfirm.setTag(-2);

        return v;
    }

    public void onClick(View v) {
        //Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
        final Integer btn = (Integer)v.getTag();

        switch (btn){
            case -1:
                // clear
                clearButtons();
                itemQuantity=0;
                break;
            case -2:
                // confirm
                listener.onFinishEnterDialog(itemQuantity);
                dismiss();
                break;
            default:
                // number
                if((itemQuantity==0 && btn!=0)||(itemQuantity>0 && (itemQuantity*10+btn)<100)) {
                    itemQuantity = itemQuantity*10+btn;
                    setButtonDisabled(btn);
                }
                break;
        }


        //dismiss();
    }

    private void clearButtons(){
        b0.setEnabled(true);
        b1.setEnabled(true);
        b2.setEnabled(true);
        b3.setEnabled(true);
        b4.setEnabled(true);
        b5.setEnabled(true);
        b6.setEnabled(true);
        b7.setEnabled(true);
        b8.setEnabled(true);
        b9.setEnabled(true);


        t0.setEnabled(true);
        t0.setTextColor(onColor);
        t1.setEnabled(true);
        t1.setTextColor(onColor);
        t2.setEnabled(true);
        t2.setTextColor(onColor);
        t3.setEnabled(true);
        t3.setTextColor(onColor);
        t4.setEnabled(true);
        t4.setTextColor(onColor);
        t5.setEnabled(true);
        t5.setTextColor(onColor);
        t6.setEnabled(true);
        t6.setTextColor(onColor);
        t7.setEnabled(true);
        t7.setTextColor(onColor);
        t8.setEnabled(true);
        t8.setTextColor(onColor);
        t9.setEnabled(true);
        t9.setTextColor(onColor);
    }

    private void setButtonDisabled(Integer btnNum){
        switch(btnNum){
            case 0:
                t0.setEnabled(false);
                b0.setEnabled(false);
                t0.setTextColor(Color.GRAY);
                break;
            case 1:
                t1.setEnabled(false);
                b1.setEnabled(false);
                t1.setTextColor(Color.GRAY);
                break;
            case 2:
                t2.setEnabled(false);
                b2.setEnabled(false);
                t2.setTextColor(Color.GRAY);
                break;
            case 3:
                t3.setEnabled(false);
                b3.setEnabled(false);
                t3.setTextColor(Color.GRAY);
                break;
            case 4:
                t4.setEnabled(false);
                b4.setEnabled(false);
                t4.setTextColor(Color.GRAY);
                break;
            case 5:
                t5.setEnabled(false);
                b5.setEnabled(false);
                t5.setTextColor(Color.GRAY);
                break;
            case 6:
                t6.setEnabled(false);
                b6.setEnabled(false);
                t6.setTextColor(Color.GRAY);
                break;
            case 7:
                t7.setEnabled(false);
                b7.setEnabled(false);
                t7.setTextColor(Color.GRAY);
                break;
            case 8:
                t8.setEnabled(false);
                b8.setEnabled(false);
                t8.setTextColor(Color.GRAY);
                break;
            case 9:
                t9.setEnabled(false);
                b9.setEnabled(false);
                t9.setTextColor(Color.GRAY);
                break;
        }

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
