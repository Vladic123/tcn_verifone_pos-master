package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * class for showing payment result
 * @author v.vasilchikov
 */

public class tcn_verifone_ProcessPaymentResult extends tcn_verifone_CustomActivity {

    private ImageView successImage;
    private TextView paymentSuccessful;
    private ImageView backButton;
    private static final int SHIPPING = 0;
    private Context context;
    private int txid=-1;

    private void startShipping(){

        Intent result = new Intent();
        setResult(RESULT_OK,result);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment_successful);

        context = this;
        stopDisconnectTimer();
        setNoScan();
        successImage = findViewById(R.id.success_image);
        paymentSuccessful = findViewById(R.id.payment_text);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShipping();
            }
        });

        Intent intent = getIntent();
        Integer result = intent.getIntExtra("result", 0);
        if (result == 1) {
            successImage.setVisibility(View.VISIBLE);
            paymentSuccessful.setVisibility(View.VISIBLE);
            autoFinish();
        } else {
            successImage.setImageResource(R.drawable.unsuccessfultransaction);
            successImage.setVisibility(View.VISIBLE);
            paymentSuccessful.setText(R.string.tcn_verifone_process_shipping_payment_unsuccessful);
            autoFinish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    /**
     * activity will be closed in 4 sec after start
     * and shipping will started immediately after it
     * */
    private void autoFinish() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startShipping();
            }
        }, 4000L);

    }

}