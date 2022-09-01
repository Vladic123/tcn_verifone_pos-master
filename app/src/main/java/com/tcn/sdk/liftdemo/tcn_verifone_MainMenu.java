package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PostProcessor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tcn.sdk.liftdemo.slider.SliderAdapter;
import com.tcn.sdk.liftdemo.slider.SliderItem;

import java.util.ArrayList;

import static controller.VendApplication.myCart;
import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * main sale menu activity class
 * shows infinite scroll list of products, video panel, handles cart and help buttons
 * @author v.vasilchikov
 */

public class tcn_verifone_MainMenu extends tcn_verifone_CustomActivity {

    private static final int START_PRODUCT = 0;
    private static final int START_CART = 1;

    private SliderView sliderView;
    private SliderAdapter sliderAdapter;

    // private TableRow productRow1;
   // private TableRow productRow2;
    private GridLayout productGrid;
    private Context context;

    private TextView cartText;
    private LinearLayout cartButton;
    private ImageView poggeeLogo;
    private Integer adminClickCounter = 0;
    private Long adminClickTimer;
    private CardView helpButton;

    // main scroll related
    private HorizontalScrollView scrollList;
    private static Handler scrollHandler;
    private RecyclerView mainList;
    private Long scrollTimer = 0L;

    // video panel
    private VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_menu);
        context=this;

        scrollHandler = new Handler();
        scrollList = findViewById(R.id.main_scroll_list);
        scrollList.setSmoothScrollingEnabled(true);
        poggeeLogo=findViewById(R.id.poggeelogo);
        cartText = findViewById(R.id.main_menu_cart_items_text);
        cartButton=findViewById(R.id.main_menu_cart_layout);
        productGrid = findViewById(R.id.product_grid_layout);
        helpButton=findViewById(R.id.helpButton);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            helpButton.getBackground().setAlpha(0);
        } else {
            helpButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        scrollList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            /** product list scrolling */
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                int a= i1;

                int width = scrollList.getWidth();
                int width2 = scrollList.getChildAt(0).getWidth();

                int leftEdge = scrollList.getChildAt(0).getLeft();
                int rightEdge = leftEdge+width2;

                if((i>i2) && (i2 >= width2/2)){
                    scrollList.scrollTo(i2-width2/2,0);
                }else {
                    if ((i <= i2) && ((width2 / 2 - width) > i2)) {
                        scrollList.scrollTo(width2 / 2 +width- i2, 0);
                    }
                }
            }
        });

        // start endless video
        videoview = (VideoView) findViewById(R.id.videoPanel);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video_mp4);
        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoview.start(); //need to make transition seamless.
            }
        });


        /*
         *
         *
         * INIT SLIDER
         *
         * */
        initSlider();
        initClickListeners();
    }


    /*
    *
    *
    * Moved the click listeners from from onCreate to this method to make things cleaner
    *
    *
    * */
    private void initClickListeners(){

        scrollList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollTimer = System.currentTimeMillis()/1000L;
                return motionEvent.getAction() != MotionEvent.ACTION_MOVE;
            }
        });
        /** admin panel enter handler */
        poggeeLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            /** counts clicks on poggee logo and start password dialog */
            public void onClick(View view) {
                if(adminClickCounter==0){
                    adminClickTimer = System.currentTimeMillis()/1000L;
                    adminClickCounter++;
                }else{
                    if(((System.currentTimeMillis()/1000L)-adminClickTimer<10)){
                        adminClickCounter++;
                        if(adminClickCounter>6){
                            adminClickCounter=0;
                            checkAndStartAdmin();
                        }
                    }else{
                        adminClickCounter=0;
                    }
                }
            }
        });
        cartText.setText("("+myCart.getItemsCount()+")");
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,tcn_verifone_Cart.class);
                Log.d("DBG","start Cart from MainMenu on cartButton");

                startActivityForResult(intent,START_CART);
            }
        });


        helpButton.setOnClickListener(new View.OnClickListener() {
            /** shows help dialog */
            @Override
            public void onClick(View view) {
                tcn_verifone_helpDialog FD = new tcn_verifone_helpDialog();
                FD.show(getSupportFragmentManager(),"helpdlg");
            }
        });

        /** product filtering */
        CardView filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tcn_verifone_FilterDialog FD = new tcn_verifone_FilterDialog(new tcn_verifone_FilterDialog.FilterDialogListener() {
                    @Override
                    public void onFinishFilterDialog(Integer flavourId) {
                        processFilterResult(flavourId);
                    }
                });
                FD.show(getSupportFragmentManager(),"filterdlg");
            }
        });
        /** check in dialog */
        CardView checkinButton = findViewById(R.id.check_in_button);
        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tcn_verifone_CheckinDialog CD = new tcn_verifone_CheckinDialog();
                CD.show(getSupportFragmentManager(),"checkindlg");
            }
        });

        /** item by printed number dialog */
        CardView enterItemNumberButton = findViewById(R.id.enter_item_number_button);
        enterItemNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnterNumber ED = new EnterNumber(new EnterNumber.EnterNumberDialogListener() {
                    @Override
                    public void onFinishEnterDialog(Integer lanePrintedId) {

                        Intent intent = new Intent(context,tcn_verifone_product_page.class);
                        intent.putExtra("laneprintedid",lanePrintedId);
                        Log.d("DBG","start productPage from MainMenu on EnterDialog");

                        startActivityForResult(intent,START_PRODUCT);
                    }
                });
                ED.show(getSupportFragmentManager(),"enternumdlg");
            }
        });
    }

    /** scrolling product list */
    private void scrollList(){

        if((System.currentTimeMillis()/1000L-scrollTimer)>10) {
            scrollList.smoothScrollTo(scrollList.getScrollX()+1, 0);
        }
        scrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollList();
            }
        },100L);

    }

    /** ask for admin password, check it and start administration activity */
    private void checkAndStartAdmin(){

        tcn_verifone_AdminPasswordDialog APD = new tcn_verifone_AdminPasswordDialog(new tcn_verifone_AdminPasswordDialog.AdminPasswordDialogListener() {
            @Override
            public void onAdminPasswordDialog(Boolean equal) {
                if(equal) {
                    Intent intent = new Intent(context, tcn_verifone_AdminActivity.class);
                    startActivity(intent);
                }
            }
        });

        APD.show(getSupportFragmentManager(),"adminpassdlg");

    }

    /** handle scrolling */
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        int width = scrollList.getWidth();
        int width2 = scrollList.getChildAt(0).getWidth();
        if(width2/2>width) {
            scrollList.scrollTo(width2/2,0);
        }
    }

    /** rebuild product list on activity resume */
    @Override
    protected void onResume() {
        super.onResume();

        try {
            // start video
            if (videoview != null) {
                videoview.start();
            }

            // remove all old products from the list
            productGrid.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // get list of all enabled products
            ArrayList<tcn_verifone_AuxItem> items = myDB.GoodsGetAllEnabled();

            // sort items assigned to lanes
            ArrayList<tcn_verifone_AuxItem> filteredItems = new ArrayList<tcn_verifone_AuxItem>();
            Boolean first = true;
            for (tcn_verifone_AuxItem item : items) {
                if (item.getEnabled()) {
                    ArrayList<tcn_verifone_AuxLane> lanes = myDB.LanesGetByItemId(item.getId());
                    if (lanes.size() > 0) {
                        filteredItems.add(item);
                    }
                }
            }

            // build scrolling list UI view
            for (int c = 0; c < 2; c++) {
                for (tcn_verifone_AuxItem item : filteredItems) {

                    final tcn_verifone_AuxItem itemClick = item;
                    // make new cell view
                    View cellView = inflater.inflate(R.layout.main_product_element, null, false);

                    Integer maxItems = myDB.LanesCountMaxItems(item.getId().longValue());
                    maxItems = maxItems - myCart.getItemsCountById(item.getId());


                    cellView.setId(View.generateViewId());
                    // generate view id for our elements
                    int productImageId = View.generateViewId();
                    int cardviewId = View.generateViewId();
                    int productBgId = View.generateViewId();
                    int textId = View.generateViewId();
                    int guideLineId = View.generateViewId();
                    int priceId = View.generateViewId();

                    // get and apply product image
                    ImageView productImage = cellView.findViewById(R.id.product_element_image);
                    Bitmap b = myImageCache.getBitmap(item.getImage());
                    Drawable BD = new BitmapDrawable(getResources(), b);
                    productImage.setBackground(BD);
                    productImage.setId(productImageId);

                    if (maxItems != 0) {
                        productImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<tcn_verifone_AuxLane> lanes = myDB.LanesGetByItemId(itemClick.getId());
                                if (lanes.size() > 0) {
                                    Integer laneid = lanes.get(0).getPrinted_num();
                                    Intent intent = new Intent(context, tcn_verifone_product_page.class);
                                    intent.putExtra("laneprintedid", laneid);
                                    Log.d("DBG", "start ProductPage from MainMenu on item click");

                                    startActivityForResult(intent, START_PRODUCT);
                                }
                            }
                        });
                    }
                    // get and apply background color
                    ImageView productBackground = cellView.findViewById(R.id.product_element_background);

                    ColorDrawable bg = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(item.getImage()))));
                    productBackground.setBackground(bg);
                    productBackground.setId(productBgId);

                    // set Text & Price
                    TextView textDsc = cellView.findViewById(R.id.product_element_text);
                    textDsc.setText(item.getDescription());
                    textDsc.setId(textId);

                    TextView textPrice = cellView.findViewById(R.id.product_element_text_price);
                    if (maxItems != 0) {
                        if (myCart.getDiscountValue() > 0) {
                            textPrice.setText(item.getFormattedPriceWithDiscount(myCart.getDiscountValue()));
                            textPrice.setId(priceId);
                            textPrice.setTextColor(Color.parseColor("#E94369"));
                        } else {
                            textPrice.setText(item.getFormattedPrice());
                            textPrice.setId(priceId);
                        }
                    } else {
                        textPrice.setText(getText(R.string.main_menu_sold_out));
                        textPrice.setId(priceId);
                        textPrice.setTextColor(Color.parseColor("#E94369"));
                    }

                    // set guideline
                    Guideline guideline = cellView.findViewById(R.id.product_element_guideline_horizontal);
                    guideline.setId(guideLineId);

                    // get cardview
                    CardView cardView = cellView.findViewById(R.id.product_element_cardview);
                    cardView.setId(cardviewId);

                    Integer fc = 0;
                    ArrayList<Integer> flavours = myDB.FlavoursItemsGetFlavoursByItem(item.getId());
                    if (flavours != null) {
                        fc = flavours.size();
                        if (flavours.size() > 3) {
                            fc = 3;
                        }
                    }

                    for (int a = 0; a < fc; a++) {

                        Bitmap fb = myImageCache.getBitmap(myDB.FlavoursGetById(flavours.get(a)).getImage());
                        ColorDrawable bgColor = new ColorDrawable(Color.parseColor(myDB.ColorsGetColorById(myImageCache.getBgColor(myDB.FlavoursGetById(flavours.get(a)).getImage()))));

                        switch (a) {
                            case 0:
                                ImageView fl1 = cellView.findViewById(R.id.product_flavour_image_1);
                                Drawable BD1 = new BitmapDrawable(null, fb);
                                fl1.setBackground(BD1);
                                fl1.setVisibility(View.VISIBLE);

                            /*CardView flavourBg1 = cellView.findViewById(R.id.product_flavour_circle_back_1);
                            ImageView flavourBgColor1 = cellView.findViewById(R.id.product_flavour_background_1);

                            flavourBgColor1.setBackground(bgColor);
                            flavourBg1.setVisibility(View.VISIBLE);*/

                                break;
                            case 1:
                                ImageView fl2 = cellView.findViewById(R.id.product_flavour_image_2);
                                Drawable BD2 = new BitmapDrawable(null, fb);
                                fl2.setBackground(BD2);
                                fl2.setVisibility(View.VISIBLE);

                        /*    CardView flavourBg2 = cellView.findViewById(R.id.product_flavour_circle_back_2);
                            ImageView flavourBgColor2 = cellView.findViewById(R.id.product_flavour_background_2);

                            flavourBgColor2.setBackground(bgColor);
                            flavourBg2.setVisibility(View.VISIBLE);*/
                                break;
                            case 2:
                                ImageView fl3 = cellView.findViewById(R.id.product_flavour_image_3);
                                Drawable BD3 = new BitmapDrawable(null, fb);
                                fl3.setBackground(BD3);
                                fl3.setVisibility(View.VISIBLE);

                            /*CardView flavourBg3 = cellView.findViewById(R.id.product_flavour_circle_back_3);
                            ImageView flavourBgColor3 = cellView.findViewById(R.id.product_flavour_background_3);

                            flavourBgColor3.setBackground(bgColor);
                            flavourBg3.setVisibility(View.VISIBLE);*/

                                break;
                        }

                    }

                    // add cell to grid layout
                    productGrid.addView(cellView);


                }
            }

            // update cart text
            cartText.setText("(" + myCart.getItemsCount() + ")");

            scrollList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        Log.d("DBG","MainMenu onActivityResult");

        // we returned from cart with paid status and need to close current activity (return to splash via main)

        if (myCart.getPaid()) {
            Log.d("DBG","finish MainMenu on getPaid");
            finish();
        }

        // user have selected product, starting Cart interface
        if(requestCode==START_PRODUCT){

            if(data!=null){

                int mode = data.getIntExtra("mode",-1);
                switch (mode){
                    case 1: //done
                        Intent intent = new Intent(context, tcn_verifone_Cart.class);
                        Log.d("DBG","start Cart from MainMenu onActivityResult");

                        startActivityForResult(intent,START_CART);

                        break;
                    case 2: // back
                        break;
                }

            }

        }

    }

    /** processing of filter result */
    void processFilterResult(Integer flavourId){
        tcn_verifone_FilterResultDialog FRD = new tcn_verifone_FilterResultDialog(new tcn_verifone_FilterResultDialog.FilterResultDialogListener() {
            @Override
            public void onFinishFilterResultDialog(Integer itemId) {
                ArrayList<tcn_verifone_AuxLane> AL = myDB.LanesGetByItemId(itemId);
                if(AL!=null){
                    // show product page for selected item
                    Intent intent = new Intent(context,tcn_verifone_product_page.class);
                    intent.putExtra("laneprintedid",AL.get(0).getPrinted_num());
                    Log.d("DBG","start ProductPage from MainMenu on filter result");

                    startActivity(intent);

                }
            }
        });

        Bundle args = new Bundle();
        args.putInt("id",flavourId);
        FRD.setArguments(args);
        FRD.show(getSupportFragmentManager(),"filterresdlg");
    }


    /*
     *
     * SLIDER
     *
     * */

    private void initSlider() {
        sliderView = findViewById(R.id.imageSlider);
        sliderAdapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        renewItems();

        sliderView.setOnIndicatorClickListener(position -> {
            /*
             *
             * SLIDER CLICK LISTENER FOR LATER IMPLEMENTATIONS
             *
             * */
        });

    }

    public void renewItems() {
        ArrayList<SliderItem> sliderItemList = new ArrayList<>();
        sliderItemList.add(new SliderItem(R.mipmap.one));
        sliderItemList.add(new SliderItem(R.mipmap.three));
        sliderItemList.add(new SliderItem(R.mipmap.four));
        sliderItemList.add(new SliderItem(R.mipmap.five));
        sliderItemList.add(new SliderItem(R.mipmap.six));
        sliderItemList.add(new SliderItem(R.mipmap.two));

        sliderAdapter.renewItems(sliderItemList);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sliderAdapter = null;
    }


}