package com.tcn.sdk.liftdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * product manager activty class
 * used for product management (name, image, background, price, description etc)
 * @author v.vasilchikov
 */

public class tcn_verifone_ProductManager extends AppCompatActivity {

    private Context context;
    private ListView productList;
    private tcn_verifone_AuxItem selectedItem;
    private ArrayList<tcn_verifone_AuxFlavour> selectedFlavours;

    // visual components
    private ImageView itemImage;
    private ImageView itemImageLarge;
    private EditText itemName;
    private EditText itemDescription;
    private EditText itemDetailedDescription;
    private EditText itemVolume;
    private EditText itemPrice;
    private Switch itemEnabled;
    private ListView itemFlavourList;
    private Button addFlavour;
    private Button changeImage;
    private Button changeImageLarge;
    private Button addItem;
    private Button applyButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__product_manager);

        selectedFlavours = new ArrayList<tcn_verifone_AuxFlavour>();

        exitButton=findViewById(R.id.product_manager_exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        itemImage = findViewById(R.id.product_manager_image);
        itemImageLarge = findViewById(R.id.product_manager_image_large);
        itemName = findViewById(R.id.product_manager_name_edit);
        itemDescription = findViewById(R.id.product_manager_description_edit);
        itemDetailedDescription = findViewById(R.id.product_manager_detailed_description_edit);
        itemVolume = findViewById(R.id.product_manager_volume_edit);
        itemPrice = findViewById(R.id.product_manager_price_edit);
        itemEnabled = findViewById(R.id.product_manager_enabled);
        itemFlavourList = findViewById(R.id.product_manager_flavour_list);
        changeImage = findViewById(R.id.product_manager_change_image_button);
        changeImageLarge = findViewById(R.id.product_manager_change_image_large_button);
        addFlavour = findViewById(R.id.product_manager_flavour_add_button);
        addItem = findViewById(R.id.product_manager_product_add_button);
        applyButton = findViewById(R.id.product_manager_product_apply_button);

        selectedItem = new tcn_verifone_AuxItem();

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProductImage();
            }
        });
        changeImageLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProductImageLarge();
            }
        });

        addFlavour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFlavourToItem();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });

        context = this;
        productList = findViewById(R.id.product_manager_product_list);

        updateProductList();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedItem = new tcn_verifone_AuxItem();
                selectedFlavours = new ArrayList<tcn_verifone_AuxFlavour>();

                updateFlavoursList();

                itemName.setText("");
                itemDescription.setText("");
                itemDetailedDescription.setText("");

                itemPrice.setText("");
                itemVolume.setText("");
                itemEnabled.setChecked(false);
                ColorDrawable bg = new ColorDrawable(Color.parseColor("#000000"));
                itemImage.setBackground(bg);
                itemImageLarge.setBackground(bg);

            }
        });

    }

    private void updateProduct(){

        selectedItem.setEnabled(itemEnabled.isChecked());
        selectedItem.setDescription(itemDescription.getText().toString());
        selectedItem.setDetailed_description(itemDetailedDescription.getText().toString());
        selectedItem.setName(itemName.getText().toString());

        Integer price = 0;
        try{
            price = Integer.parseInt(itemPrice.getText().toString());
        }catch(Exception ex){}

        selectedItem.setPrice(price);

        Integer volume = 0;
        try{
            volume = Integer.parseInt(itemVolume.getText().toString());
        }catch(Exception ex){}

        selectedItem.setVolume(volume);

        if(selectedItem.getId()>0){
            // update item
            myDB.GoodsUpdateItem(selectedItem);

            myDB.FlavoursItemsDeleteAllFlavoursByItemId(selectedItem.getId());
        }else{
            // add item
            myDB.GoodsAddItem(selectedItem);

        }

        // add flavours
        for(tcn_verifone_AuxFlavour flavour:selectedFlavours){
            myDB.FlavoursItemsAddFlavourToItemById(selectedItem.getId(),flavour.getId());
        }

        updateProductList();
        updateFlavoursList();

    }

    private void changeProductImage(){

        tcn_verifone_ImageSelectDialog ISD = new tcn_verifone_ImageSelectDialog(new tcn_verifone_ImageSelectDialog.ImageSelectDialoglistener() {
            @Override
            public void onImageSelectDialog(Integer itemId) {
                selectedItem.setImage(itemId);
                Bitmap b = myImageCache.getBitmap(selectedItem.getImage());
                Drawable BD = new BitmapDrawable(context.getResources(), b);
                itemImage.setBackground(BD);
            }
        });

        ISD.show(getSupportFragmentManager(),"imgseldlg");

    }

    private void changeProductImageLarge(){

        tcn_verifone_ImageSelectDialog ISD = new tcn_verifone_ImageSelectDialog(new tcn_verifone_ImageSelectDialog.ImageSelectDialoglistener() {
            @Override
            public void onImageSelectDialog(Integer itemId) {
                selectedItem.setImageLarge(itemId);
                Bitmap b = myImageCache.getBitmap(selectedItem.getImageLarge());
                Drawable BD = new BitmapDrawable(context.getResources(), b);
                itemImageLarge.setBackground(BD);
            }
        });

        ISD.show(getSupportFragmentManager(),"imgseldlg");

    }

    private  void addFlavourToItem(){

        tcn_verifone_FlavourSelectDialog FSD = new tcn_verifone_FlavourSelectDialog(new tcn_verifone_FlavourSelectDialog.FlavourSelectDialogListener() {
            @Override
            public void onFlavourSelectDialog(Integer itemId) {

                // check it was already added
                Boolean flag = false;
                for(tcn_verifone_AuxFlavour flavour:selectedFlavours){
                    if(flavour.getId()==itemId){
                        flag=true;
                        break;
                    }
                }
                if(!flag) {
                    selectedFlavours.add(myDB.FlavoursGetById(itemId));
                    updateFlavoursList();
                }

            }
        });

        FSD.show(getSupportFragmentManager(),"flvseldlg");

    }

    protected void onFlavourButtonDelete(Integer id){
        for(int a=0;a<selectedFlavours.size();a++){
            if(selectedFlavours.get(a).getId()==id){
                selectedFlavours.remove(a);
                break;
            }
        }
        updateFlavoursList();

    }

    private void updateFlavoursList(){
        if(selectedFlavours!=null) {
            tcn_verifone_ProductManagerFlavourAdapter FA = new tcn_verifone_ProductManagerFlavourAdapter(context, selectedFlavours, false);
            itemFlavourList.setAdapter(FA);
        }
    }

    protected void onProductButtonDelete(Integer id){

        myDB.GoodsDeleteItemById(id);

        updateProductList();

    }

    private void updateProductList(){

        ArrayList<tcn_verifone_AuxItem> items = myDB.GoodsGetAll();

        tcn_verifone_ProductManagerProductAdapter PA = new tcn_verifone_ProductManagerProductAdapter(context,items);
        productList.setAdapter(PA);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                processSelectedProduct(id);
            }
        });

    }


    private void processSelectedProduct(Long id){

        selectedItem = myDB.GoodsGetItemById(id.intValue());
        if(selectedItem!=null){
            showSelectedItem();
        }
    }

    private void showSelectedItem(){
        if(selectedItem==null){
            return;
        }

        try {
            itemName.setText(selectedItem.getName());
            itemDescription.setText(selectedItem.getDescription());
            itemDetailedDescription.setText(selectedItem.getDetailed_description());
            itemPrice.setText(selectedItem.getPrice().toString());
            itemVolume.setText(selectedItem.getVolume().toString());
            itemEnabled.setChecked(selectedItem.getEnabled());
            Bitmap b = myImageCache.getBitmap(selectedItem.getImage());
            Drawable BD = new BitmapDrawable(context.getResources(), b);
            itemImage.setBackground(BD);
            Bitmap bl = myImageCache.getBitmap(selectedItem.getImageLarge());
            Drawable BDL = new BitmapDrawable(context.getResources(), bl);
            itemImageLarge.setBackground(BDL);
        }catch(Exception ex){
            int a=0;
            a=a+1;
        }

        ArrayList<Integer> FL = myDB.FlavoursItemsGetFlavoursByItem(selectedItem.getId());
        if(FL != null && FL.size()>0) {
            selectedFlavours = new ArrayList<tcn_verifone_AuxFlavour>();
            for (Integer id:FL) {
                tcn_verifone_AuxFlavour AF = myDB.FlavoursGetById(id);
                selectedFlavours.add(AF);
            }

            updateFlavoursList();
        }

    }

}



