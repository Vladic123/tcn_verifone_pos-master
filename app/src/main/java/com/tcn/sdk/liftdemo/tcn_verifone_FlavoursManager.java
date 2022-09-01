package com.tcn.sdk.liftdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * flavours management activity class
 * used for flavour management (names, images, backgrounds etc)
 * @author v.vasilchikov
 */

public class tcn_verifone_FlavoursManager extends AppCompatActivity {

    private ImageView flavourImage;
    private TextView flavourName;
    private ListView imagesList;
    private ListView flavourList;
    private Button newButton;
    private Button applyButton;
    private Button exitButton;

    private tcn_verifone_AuxFlavour selectedFlavour;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flavours_manager);

        context = this;
        selectedFlavour = new tcn_verifone_AuxFlavour();

        flavourImage = findViewById(R.id.flavour_manager_image);
        flavourName = findViewById(R.id.flavour_manager_name);
        newButton = findViewById(R.id.flavour_manager_new);
        applyButton = findViewById(R.id.flavour_manager_apply);
        exitButton = findViewById(R.id.flavour_manager_exit);
        imagesList = findViewById(R.id.flavour_manager_images_list);
        flavourList = findViewById(R.id.flavour_manager_flavours_list);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFlavour = new tcn_verifone_AuxFlavour();
                updateFlavour();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFlavour.setName(flavourName.getText().toString());
                if(selectedFlavour.getId()==-1){
                    myDB.FlavoursAddFlavour(selectedFlavour);
                }else{
                    myDB.FlavoursUpdateFlavour(selectedFlavour);
                }
                updateList();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();


        ArrayList<tcn_verifone_AuxImage> images = myDB.ImagesGetAll();
        tcn_verifone_ImageManagerImageAdapter IA = new tcn_verifone_ImageManagerImageAdapter(context,images,false);

        imagesList.setAdapter(IA);
        imagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long idL = id;
                setImage(idL.intValue());
                selectedFlavour.setImage(idL.intValue());
            }
        });


        updateList();
    }

    private void setImage(Integer id){


        selectedFlavour.setImage(id);
        updateFlavour();

    }

    protected void onFlavourButtonDelete(Integer id){

        myDB.FlavoursDeleteFlavourById(id);

        updateList();

    }

    private void updateFlavour(){

        Bitmap b = myImageCache.getBitmap(selectedFlavour.getImage());
        Drawable BD = new BitmapDrawable(context.getResources(), b);
        flavourImage.setBackground(BD);

        flavourName.setText(selectedFlavour.getName());
    }

    private void updateList(){
        ArrayList<tcn_verifone_AuxFlavour> flavours = myDB.FlavoursGetAll();

        tcn_verifone_ProductManagerFlavourAdapter FA = new tcn_verifone_ProductManagerFlavourAdapter(context,flavours,true);
        flavourList.setAdapter(FA);

        flavourList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long idl = id;
                tcn_verifone_AuxFlavour FL = myDB.FlavoursGetById(idl.intValue());
                if(FL!=null) {
                    selectedFlavour = FL;
                    updateFlavour();
                }
            }
        });

    }
}