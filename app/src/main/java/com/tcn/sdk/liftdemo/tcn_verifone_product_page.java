package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * product page activty
 * shows product image, description,volume, price etc
 * @author v.vasilchikov
 */

public class tcn_verifone_product_page extends tcn_verifone_CustomActivity {

    private static int STAGE_CART = 2;

    private Context context=null;
    private Integer lanePrintedNum=-1;

    private LinearLayout cartButton;
    private TextView count;
    private ImageView icecreamImage;
    private TextView icecreamFlavor;
    private TextView icecreamVolume;
    private TextView icecreamDesc;
    private TextView icecreamDetDesc;
    private TextView icecreamPrice;
    private TextView itemsCounter;
    private TextView itemsPlus;
    private TextView itemsMinus;
    private CardView addToCart;
    private CardView cancel;
    private CardView done;

    private Integer maxItems = 0;
    private Integer itemsCount = 0;

    private tcn_verifone_AuxItem AI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product);

        context = this;
        Intent intent = getIntent();
        lanePrintedNum = intent.getIntExtra("laneprintedid",-1);

        cartButton=findViewById(R.id.cart_button);
        count=findViewById(R.id.text_count);
        icecreamImage=findViewById(R.id.ice_cream_image);
        //icecreamFlavor
        icecreamDetDesc=findViewById(R.id.text_name);
        icecreamVolume=findViewById(R.id.text_volume);
        icecreamDesc=findViewById(R.id.ice_cream_description);
        icecreamPrice=findViewById(R.id.text_price);
        itemsCounter=findViewById(R.id.items_counter);
        itemsPlus=findViewById(R.id.items_plus);
        itemsMinus=findViewById(R.id.items_minus);
        addToCart=findViewById(R.id.add_to_cart_button);
        cancel=findViewById(R.id.cancel_button);
        done=findViewById(R.id.done_button);

        if(myCart.getDiscountValue()>=0.5 && myCart.getItemsCount()>0){
            done.setVisibility(View.VISIBLE);
            addToCart.setVisibility(View.INVISIBLE);
            itemsPlus.setVisibility(View.INVISIBLE);
            itemsMinus.setVisibility(View.INVISIBLE);
            count.setText("0");
        }

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,tcn_verifone_Cart.class);
                Log.d("DBG","start Cart from ProductPage on cartbutton");

                startActivityForResult(intent,STAGE_CART);

            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        tcn_verifone_AuxLane AL = myDB.LanesGetByPrintedId(lanePrintedNum);
        if(AL==null){
            setResult(RESULT_CANCELED);
            Log.d("DBG","finish ProductPage on null AL");

            finish();
        }else {
            AI = AL.getItem();
            // draw item

            icecreamImage.setImageBitmap(myImageCache.getBitmap(AI.getImageLarge()));
            icecreamVolume.setText(AI.getVolume().toString() + "ml");
            icecreamDesc.setText(AI.getDetailed_description());
            icecreamDetDesc.setText(AI.getDescription());
            icecreamPrice.setText(AI.getFormattedPriceWithDiscount(myCart.getDiscountValue()));

            maxItems = myDB.LanesCountMaxItems(AI.getId().longValue());
            maxItems = maxItems - myCart.getItemsCountById(AI.getId());
            if (maxItems != 0) {
                itemsCount = 1;
            } else {
                itemsCount = 0;
            }
            itemsCounter.setText(itemsCount.toString());
/*            ArrayList<Integer> flavours = myDB.FlavoursItemsGetFlavoursByItem(AI.getId());
            String Flavours = "";
            for (Integer flavour : flavours) {
                Flavours = Flavours + myDB.FlavourGetNameByID(flavour) + " ";
            }
            icecreamFlavor.setText(Flavours);
*/
            count.setText("("+myCart.getItemsCount().toString()+")");

            itemsPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (maxItems != 0 && itemsCount < maxItems) {
                        Boolean flagA = false;
                        if(myCart.getDiscountValue()<0.5) {
                            flagA=true;
                        }

                        if(flagA) {
                            itemsCount++;
                            updateUI();
                        }
                    }
                }
            });

            itemsMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (maxItems != 0 && itemsCount > 1) {
                        itemsCount--;
                        updateUI();
                    }
                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pair<Integer, Integer> itemCount = new Pair<Integer, Integer>(AI.getId(), itemsCount);

                    myCart.AddItem(itemCount);
                    count.setText("("+myCart.getItemsCount().toString()+")");
                    addToCart.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.VISIBLE);

                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent result = new Intent();
                    result.putExtra("mode",1);
                    setResult(RESULT_OK,result);
                    Log.d("DBG","finish ProductPage on Done click");
                    finish();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent result = new Intent();
                    result.putExtra("mode",2);
                    setResult(RESULT_OK,result);
                    Log.d("DBG","finish ProductPage on GO BACK click");

                    finish();
                }
            });


        }
    }

    private void updateUI(){
        itemsCounter.setText(itemsCount.toString());
        icecreamPrice.setText(priceConvertToString(AI.getPriceWithDiscount(myCart.getDiscountValue()) * itemsCount));
    }

    private String priceConvertToString (Integer price){

        String result = "";

        String priceS = price.toString();
        if(price!=0) {
            return "$" + priceS.substring(0, priceS.length() - 2) + "." + priceS.substring(priceS.length() - 2);
        }
        return "$0.0";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DBG","finish ProductPage onactivityresult");

        finish();
    }

}