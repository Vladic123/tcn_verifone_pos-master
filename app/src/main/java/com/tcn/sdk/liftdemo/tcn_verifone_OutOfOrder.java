package com.tcn.sdk.liftdemo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static controller.VendApplication.myDB;

/**
 * out of order activity class
 * shows "out of order" message with email address
 * called in case of fatal errors
 * @author v.vasilchikov
 */

public class tcn_verifone_OutOfOrder extends AppCompatActivity {

    private TextView outOfOrderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__out_of_order);

        outOfOrderText=findViewById(R.id.outoforder_text);
        outOfOrderText.setText(getText(R.string.outoforder_message_text) + " " + myDB.SettingsGetSingleValue("email"));

    }

    @Override
    public void onBackPressed(){

    }
}