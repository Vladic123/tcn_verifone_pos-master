package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;

/**
 * class for displaying Cart
 * @author v.vasilchikov
 */

public class tcn_verifone_Cart extends tcn_verifone_CustomActivity {

    /** UI elements */
    private TextView textCount;
    private Integer index=0;

    private TextView productName1;
    private TextView productName2;
    private TextView productName3;
    private TextView productName4;
    private TextView productName5;

    private TextView productQuantity1;
    private TextView productQuantity2;
    private TextView productQuantity3;
    private TextView productQuantity4;
    private TextView productQuantity5;

    private TextView productPrice1;
    private TextView productPrice2;
    private TextView productPrice3;
    private TextView productPrice4;
    private TextView productPrice5;

    private ImageView upButton;
    private ImageView downButton;

    private TextView totalText;

    private CardView payButton;
    private CardView cancelButton;

    private TextView quantityMinus1;
    private TextView quantityPlus1;
    private TextView quantityDelete1;
    private TextView quantityMinus2;
    private TextView quantityPlus2;
    private TextView quantityDelete2;
    private TextView quantityMinus3;
    private TextView quantityPlus3;
    private TextView quantityDelete3;
    private TextView quantityMinus4;
    private TextView quantityPlus4;
    private TextView quantityDelete4;

    private ArrayList<Pair<Integer,Integer>> pairs;
    private Context context;

    /** sale stage constant */
    private static final int SALE_STAGE = 0;
    private static final int SHIPPING_STAGE = 1;
    /** exit stage constant */
    private static final int EXIT_STAGE = 2;

    private Boolean paymentInProcess = false;

    @Override
    public void onStart(){
        super.onStart();
        Log.d("DBG","Cart onStart");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DBG","Cart onPause");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);

        context = this;

        /** bind UI elements */

        Intent intent = getIntent();

        productName1 = findViewById(R.id.product_name_1);
        productName2 = findViewById(R.id.product_name_2);
        productName3 = findViewById(R.id.product_name_3);
        productName4 = findViewById(R.id.product_name_4);

        productQuantity1 = findViewById(R.id.product_quantity_1);
        productQuantity2 = findViewById(R.id.product_quantity_2);
        productQuantity3 = findViewById(R.id.product_quantity_3);
        productQuantity4 = findViewById(R.id.product_quantity_4);

        productPrice1 = findViewById(R.id.product_price_1);
        productPrice2 = findViewById(R.id.product_price_2);
        productPrice3 = findViewById(R.id.product_price_3);
        productPrice4 = findViewById(R.id.product_price_4);

        quantityDelete1 = findViewById(R.id.product_delete_1);
        quantityDelete2 = findViewById(R.id.product_delete_2);
        quantityDelete3 = findViewById(R.id.product_delete_3);
        quantityDelete4 = findViewById(R.id.product_delete_4);

        quantityMinus1 = findViewById(R.id.product_quantity_change_minus_1);
        quantityMinus2 = findViewById(R.id.product_quantity_change_minus_2);
        quantityMinus3 = findViewById(R.id.product_quantity_change_minus_3);
        quantityMinus4 = findViewById(R.id.product_quantity_change_minus_4);

        quantityPlus1 = findViewById(R.id.product_quantity_change_plus_1);
        quantityPlus2 = findViewById(R.id.product_quantity_change_plus_2);
        quantityPlus3 = findViewById(R.id.product_quantity_change_plus_3);
        quantityPlus4 = findViewById(R.id.product_quantity_change_plus_4);


        /** up button handling */
        upButton = findViewById(R.id.cart_up_button);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemsCount = myCart.getItemsCount();

                if(index>0){
                    index--;
                    updateTable();
                    updateButtons();
                }
            }
        });

        /** down button handling */
        downButton = findViewById(R.id.cart_down_button);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(index+4<pairs.size()){
                    index++;
                    updateTable();
                    updateButtons();
                }

            }
        });

        totalText = findViewById(R.id.total_price_text);

        textCount = findViewById(R.id.text_count);


        pairs = myCart.getItems();

        if(pairs==null){
            Log.d("DBG","finish Cart on null pairs");
            finish();
        }

        /** pay button handling */
        payButton=findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    /** check if payment process already started and cart is not empty */
                    if (!paymentInProcess && myCart.getItemsCount() > 0) {
                        Log.d("DBG", "Cart" + myCart.toString());
                        paymentInProcess = true;
                        stopDisconnectTimer();
                        Intent intent = new Intent(context, tcn_verifone_ProcessPayment.class);
                        Log.d("DBG", "start ProcessPayment from Cart");
                        startActivityForResult(intent, SALE_STAGE);

                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cancelButton=findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DBG","finish Cart on back");

                finish();
            }
        });

        updateTable();
        updateButtons();

    }

    /** update items table */
    protected void updateTable(){


        try {
            if (myCart.getItemsCount() == 0) {
                Log.d("DBG", "finish Cart on update table");
                finish();
                return;
            }
            //
            String[] pName = new String[4];
            String[] pQuantity = new String[4];
            String[] pPrice = new String[4];
            Boolean[] pMinus = new Boolean[4];
            Boolean[] pPlus = new Boolean[4];
            final Boolean[] pItem = new Boolean[4];
            final Integer[] pItemNum = new Integer[4];
            for (int i = 0; i < 4; i++) {
                pName[i] = "";
                pQuantity[i] = "";
                pPrice[i] = "";
                pMinus[i] = false;
                pPlus[i] = false;

                pItem[i] = false;
            }

            int counter = 0;
            if (pairs.size() > 0) {
                for (int a = index; a < index + 4; a++) {

                    /** fill table with actual data */
                    pItem[counter] = true;
                    tcn_verifone_AuxItem AI = myDB.GoodsGetItemById(pairs.get(a).first);
                    pItemNum[counter] = AI.getId();
                    pName[counter] = AI.getDescription();
                    pQuantity[counter] = "x" + pairs.get(a).second.toString();
                    if (myCart.getDiscountValue() > 0) {
                        pPrice[counter] = AI.getFormattedPriceWithDiscount(myCart.getDiscountValue());
                    } else {
                        pPrice[counter] = AI.getFormattedPrice();
                    }
                    // check minimum
                    if (pairs.get(a).second > 1) {
                        pMinus[counter] = true;
                    }

                    // check maximum
                    Integer maxItemsCount = myDB.LanesCountMaxItems(pairs.get(a).first.longValue());

                    if (pairs.get(a).second < maxItemsCount) {
                        pPlus[counter] = true;
                    }
                    counter++;
                    if (counter == 4 || counter == pairs.size()) {
                        break;
                    }

                }
            }
            productName1.setText(pName[0]);
            productName2.setText(pName[1]);
            productName3.setText(pName[2]);
            productName4.setText(pName[3]);

            productQuantity1.setText(pQuantity[0]);
            productQuantity2.setText(pQuantity[1]);
            productQuantity3.setText(pQuantity[2]);
            productQuantity4.setText(pQuantity[3]);

            productPrice1.setText(pPrice[0]);
            productPrice2.setText(pPrice[1]);
            productPrice3.setText(pPrice[2]);
            productPrice4.setText(pPrice[3]);

            /** handling item minus button */
            if (pMinus[0]) {
                quantityMinus1.setVisibility(View.VISIBLE);
                quantityMinus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartMinus(pItemNum[0]);
                    }
                });
            } else {
                quantityMinus1.setVisibility(View.INVISIBLE);
            }
            /** handling item plus button */
            if (pPlus[0]) {
                quantityPlus1.setVisibility(View.VISIBLE);
                quantityPlus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartPlus(pItemNum[0]);
                    }
                });
            } else {
                quantityPlus1.setVisibility(View.INVISIBLE);
            }
            /** item delete button */
            if (pItem[0]) {
                quantityDelete1.setVisibility(View.VISIBLE);
                quantityDelete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartDelete(pItemNum[0]);

                    }
                });
            } else {
                quantityDelete1.setVisibility(View.INVISIBLE);
            }


            if (pMinus[1]) {
                quantityMinus2.setVisibility(View.VISIBLE);
                quantityMinus2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartMinus(pItemNum[1]);
                    }
                });
            } else {
                quantityMinus2.setVisibility(View.INVISIBLE);
            }
            if (pPlus[1]) {
                quantityPlus2.setVisibility(View.VISIBLE);
                quantityPlus2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartPlus(pItemNum[1]);

                    }
                });
            } else {
                quantityPlus2.setVisibility(View.INVISIBLE);
            }
            if (pItem[1]) {
                quantityDelete2.setVisibility(View.VISIBLE);
                quantityDelete2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartDelete(pItemNum[1]);

                    }
                });
            } else {
                quantityDelete2.setVisibility(View.INVISIBLE);
            }


            if (pMinus[2]) {
                quantityMinus3.setVisibility(View.VISIBLE);
                quantityMinus3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartMinus(pItemNum[2]);
                    }
                });
            } else {
                quantityMinus3.setVisibility(View.INVISIBLE);
            }
            if (pPlus[2]) {
                quantityPlus3.setVisibility(View.VISIBLE);
                quantityPlus3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartPlus(pItemNum[2]);

                    }
                });
            } else {
                quantityPlus3.setVisibility(View.INVISIBLE);
            }
            if (pItem[2]) {
                quantityDelete3.setVisibility(View.VISIBLE);
                quantityDelete3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartDelete(pItemNum[2]);

                    }
                });
            } else {
                quantityDelete3.setVisibility(View.INVISIBLE);
            }


            if (pMinus[3]) {
                quantityMinus4.setVisibility(View.VISIBLE);
                quantityMinus4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartMinus(pItemNum[3]);
                    }
                });
            } else {
                quantityMinus4.setVisibility(View.INVISIBLE);
            }
            if (pPlus[3]) {
                quantityPlus4.setVisibility(View.VISIBLE);
                quantityPlus4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartPlus(pItemNum[3]);

                    }
                });
            } else {
                quantityPlus4.setVisibility(View.INVISIBLE);
            }
            if (pItem[3]) {
                quantityDelete4.setVisibility(View.VISIBLE);
                quantityDelete4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartDelete(pItemNum[3]);

                    }
                });
            } else {
                quantityDelete4.setVisibility(View.INVISIBLE);
            }

            Double price = myCart.getCartTotal().doubleValue();
      /*  if(myCart.getDiscountValue()>0){
            price = price-price*myCart.getDiscountValue();
        }
*/
            /** get and format price */
            Integer priceSint = price.intValue();

            String priceS = priceSint.toString();

            if(priceS.length()==1){
                priceS="00"+priceS;
            }else{
                if(priceS.length()==2){
                    priceS="0"+priceS;
                }
            }

            /** show prepared text */
            if (price != 0) {
                totalText.setText("$" + priceS.substring(0, priceS.length() - 2) + "." + priceS.substring(priceS.length() - 2));
            } else {
                totalText.setText("$0.0");
            }

            textCount.setText("Items: " + myCart.getItemsCount().toString());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /** decrease items count */
    private void cartMinus(Integer itemId){

        myCart.DeleteOneItem(itemId);
        updateTable();

    }
    /** increase items count */
    private void cartPlus(Integer itemId){

        myCart.AddOneItem(itemId);
        updateTable();

    }
    /** delete item */
    private void cartDelete(Integer itemId){

        myCart.DeleteItem(itemId);
        updateTable();
    }

    /** update buttons */
    private void updateButtons(){

        if(index==0){
            upButton.setVisibility(View.INVISIBLE);
        }else{
            upButton.setVisibility(View.VISIBLE);
        }

        if((index+4)>=pairs.size()){
            downButton.setVisibility(View.INVISIBLE);
        }else{
            downButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data){
        super.onActivityResult(requestCode, responseCode, data);
        switch(requestCode){
            case SALE_STAGE:
                if (myCart.getPaid()) {
                    /** paid */
                    Intent intent = new Intent(context, tcn_verifone_ProcessPaymentResult.class);
                    Log.d("DBG","start ProcessPaymentResult from Cart");
                    intent.putExtra("result", 1);
                    startActivityForResult(intent, EXIT_STAGE);
                } else {
                    /** not paid */
                    Intent intent = new Intent(context, tcn_verifone_ProcessPaymentResult.class);
                    intent.putExtra("result", 0);
                    Log.d("DBG","start ProcessPaymentResult from Cart");
                    startActivityForResult(intent, EXIT_STAGE);
                }
                break;
            case EXIT_STAGE:
                Log.d("DBG","finish Cart on EXIT_STAGE");
                finish();
                break;
        }
    }
}