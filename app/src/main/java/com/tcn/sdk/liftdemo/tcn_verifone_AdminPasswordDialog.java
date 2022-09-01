package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.TransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import static controller.VendApplication.myDB;

/**
 * class to handle admin password dialog
 * @author v.vasilchikov
 */

public class tcn_verifone_AdminPasswordDialog extends DialogFragment implements View.OnClickListener {

    private Context context;
    private CardView button;
    private EditText password;

    public interface AdminPasswordDialogListener {
        void onAdminPasswordDialog(Boolean equal);
    }

    tcn_verifone_AdminPasswordDialog(tcn_verifone_AdminPasswordDialog.AdminPasswordDialogListener listener){
        this.listener=listener;
    }

    private AdminPasswordDialogListener listener;

    private class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '*';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();

        View v = inflater.inflate(R.layout.admin_password_filter, null);
        ColorDrawable CD = new ColorDrawable(Color.TRANSPARENT);
        getDialog().getWindow().setBackgroundDrawable(CD);
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);

        button = v.findViewById(R.id.passwordbutton);
        password = v.findViewById(R.id.editTextTextPassword);
        password.setTransformationMethod(new HiddenPassTransformationMethod());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();

                if(pass.equals(myDB.SettingsGetSingleValue("adminpassword"))){
                    processResult(true);
                }else{
                    processResult(false);
                }
            }
        });

        return v;
    }

    private void processResult(Boolean equal){
        listener.onAdminPasswordDialog(equal);
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
