package com.tcn.sdk.liftdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * class form Image management
 * add images from disk, set name, bg color etc
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageManager extends AppCompatActivity {

    private ImageView mainImage;
    private ImageView bgColor;
    private ListView colorsList;
    private ListView imagesList;
    private Button loadFromDiskButton;
    private Button saveButton;
    private Button exitButton;
    private Button newButton;

    private ArrayList<tcn_verifone_AuxColor> colors;
    private ArrayList<tcn_verifone_AuxImage> images;

    private tcn_verifone_ImageManagerImageAdapter IA;

    private Context context;
    private tcn_verifone_AuxImage selectedImage;
    private tcn_verifone_AuxColor selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__image_manager);

        context = this;

        selectedColor = new tcn_verifone_AuxColor(-1,"#000000");
        selectedImage = new tcn_verifone_AuxImage();

        mainImage = findViewById(R.id.image_manager_main_image);
        bgColor = findViewById(R.id.image_manager_bg_color);
        colorsList = findViewById(R.id.image_manager_bgcolor_list);
        imagesList = findViewById(R.id.image_manager_image_list);
        loadFromDiskButton = findViewById(R.id.image_manager_add_from_disk_button);
        saveButton = findViewById(R.id.image_manager_save_button);
        exitButton = findViewById(R.id.image_manager_wxit_button);
        newButton = findViewById(R.id.image_manager_new_button);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedColor = new tcn_verifone_AuxColor(-1,"#000000");
                selectedImage = new tcn_verifone_AuxImage();
                updateImageColors();
            }
        });

        loadFromDiskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tcn_verifone_ImageManagerLoadFromDiskDialog LD = new tcn_verifone_ImageManagerLoadFromDiskDialog(new tcn_verifone_ImageManagerLoadFromDiskDialog.ImageManagerLoadFromDiskDialoglistener() {
                        @Override
                        public void onImageLoadFromDiskDialog(String filename) {
                            selectedImage.setNamepath(filename);
                            String exStoragePath = System.getenv("EXTERNAL_STORAGE");
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            try {
                                Bitmap b = BitmapFactory.decodeFile(exStoragePath + "/Download/" + selectedImage.getNamepath(), options);
                                Drawable BD = new BitmapDrawable(context.getResources(), b);
                                mainImage.setBackground(BD);
                            } catch (Exception ex) {
                            }

                        }
                    });
                    LD.show(getSupportFragmentManager(), "imgloaddlf");
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    selectedImage.setBgcolor(selectedColor.getId());
                    if (selectedImage.getId() == -1) {
                        Integer newId = myDB.ImagesAddImage(selectedImage);
                        selectedImage.setId(newId);
                    } else {
                        myDB.ImagesUpdateImage(selectedImage);
                    }
                    updateImageColors();
                    updateImageList();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            // draw color list
            ArrayList<tcn_verifone_AuxColor> colors = myDB.ColorsGetColors();
            tcn_verifone_ImageManagerColorAdapter CA = new tcn_verifone_ImageManagerColorAdapter(context, colors);
            colorsList.setAdapter(CA);

            colorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Long idL = id;
                    selectedColor = myDB.ColorsGetAuxColorById(idL.intValue());


                    ColorDrawable bg = new ColorDrawable(Color.parseColor(selectedColor.getColor()));
                    bgColor.setBackground(bg);
                }
            });

            // draw imagelist
            images = myDB.ImagesGetAll();
            IA = new tcn_verifone_ImageManagerImageAdapter(context, images, true);
            imagesList.setAdapter(IA);

            imagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Long idL = id;
                    selectedImage = myDB.ImagesGetImageById(idL.intValue());
                    selectedColor.setColor(myDB.ColorsGetColorById(selectedImage.getBgcolor()));

                    updateImageColors();

                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void updateImageList(){
        try {
            images = myDB.ImagesGetAll();
            IA = new tcn_verifone_ImageManagerImageAdapter(context, images, true);
            imagesList.setAdapter(IA);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void updateImageColors(){

        try {
            if (selectedImage != null) {
                ArrayList<tcn_verifone_AuxImage> AI = myDB.ImagesGetAll();
                myImageCache.preload(AI);
                Bitmap b = myImageCache.getBitmap(selectedImage.getId());
                Drawable BD = new BitmapDrawable(context.getResources(), b);
                mainImage.setBackground(BD);
            }

            if (selectedColor != null) {
                ColorDrawable bg = new ColorDrawable(Color.parseColor(selectedColor.getColor()));
                bgColor.setBackground(bg);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    protected void onImageButtonDelete(Integer id){

        try {
            myDB.ImagesDeleteImageById(id);
            updateImageList();
            updateImageColors();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}