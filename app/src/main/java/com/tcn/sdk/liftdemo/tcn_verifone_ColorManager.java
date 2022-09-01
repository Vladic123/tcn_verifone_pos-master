package com.tcn.sdk.liftdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import static controller.VendApplication.myDB;

/**
 * color manager activity class
 * @author v.vasilchikov
 */

public class tcn_verifone_ColorManager extends AppCompatActivity {

    private Context context;
    private ListView colorList;

    /**
     * UI elements
     */
    private ImageView bgColor;
    private EditText colorText;
    private Button addButton;
    private Button applyButton;
    private Button newButton;
    private Button exitButton;

    private Integer id = -1;

    /**
     * colors list
     */
    private ArrayList<tcn_verifone_AuxColor> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__color_manager);

        /**
         * bind UI elements
         */

        bgColor = findViewById(R.id.color_manager_color);
        colorText = findViewById(R.id.color_manager_color_name_edit);
        addButton = findViewById(R.id.color_manager_color_add_button);
        applyButton = findViewById(R.id.color_manager_color_apply_button);
        newButton = findViewById(R.id.color_manager_color_new_button);
        exitButton = findViewById(R.id.color_manager_exit);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addButton.setVisibility(View.VISIBLE);
        applyButton.setVisibility(View.INVISIBLE);
        newButton.setVisibility(View.INVISIBLE);

        ColorDrawable bg = new ColorDrawable(Color.parseColor("#000000"));
        bgColor.setBackground(bg);

        id=0;
        colorText.setText("#000000");


        colorText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                try{
                    ColorDrawable bg = new ColorDrawable(Color.parseColor(colorText.getText().toString()));
                    bgColor.setBackground(bg);
                }catch(Exception ex){

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        colorList = findViewById(R.id.color_list);

        context = this;

        colors =  myDB.ColorsGetColors();

        tcn_verifone_ColorListAdapter CLA = new tcn_verifone_ColorListAdapter(context,colors);

        colorList.setAdapter(CLA);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check color correct formed
                Boolean flag = true;
                try{
                    Color.parseColor(colorText.getText().toString());
                }catch(Exception ex){
                    flag=false;
                }

                if(flag && id>-1){
                    tcn_verifone_AuxColor CL = new tcn_verifone_AuxColor(id,colorText.getText().toString());
                    myDB.ColorsUpdateColor(CL);
                }else{
                    showError();
                }

                colors =  myDB.ColorsGetColors();
                tcn_verifone_ColorListAdapter CLA = new tcn_verifone_ColorListAdapter(context,colors);
                colorList.setAdapter(CLA);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean flag = true;
                try{
                    Color.parseColor(colorText.getText().toString());
                }catch(Exception ex){
                    flag=false;
                }

                if(flag){
                    tcn_verifone_AuxColor CL = new tcn_verifone_AuxColor(id,colorText.getText().toString());
                    myDB.ColorsAddColor(CL);
                }else{
                    showError();
                }

                colors =  myDB.ColorsGetColors();
                tcn_verifone_ColorListAdapter CLA = new tcn_verifone_ColorListAdapter(context,colors);
                colorList.setAdapter(CLA);

            }


        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable bg = new ColorDrawable(Color.parseColor("#000000"));
                bgColor.setBackground(bg);

                id=0;
                colorText.setText("#000000");

                addButton.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.INVISIBLE);
                newButton.setVisibility(View.VISIBLE);

            }
        });

    }

    private void showError(){

    }

    protected void onButtonDelete(Long id){

        this.id = id.intValue();

        addButton.setVisibility(View.INVISIBLE);
        applyButton.setVisibility(View.VISIBLE);
        newButton.setVisibility(View.VISIBLE);

        myDB.ColorsDeleteColorById(this.id);

        colors =  myDB.ColorsGetColors();
        tcn_verifone_ColorListAdapter CLA = new tcn_verifone_ColorListAdapter(context,colors);
        colorList.setAdapter(CLA);

        ColorDrawable bg = new ColorDrawable(Color.parseColor("#000000"));
        bgColor.setBackground(bg);

        this.id=0;
        colorText.setText("#000000");

    }

    protected void onButtonEdit(Long id){

        this.id = id.intValue();

        addButton.setVisibility(View.INVISIBLE);
        applyButton.setVisibility(View.VISIBLE);
        newButton.setVisibility(View.VISIBLE);

        String color = myDB.ColorsGetColorById(this.id);

        ColorDrawable bg = new ColorDrawable(Color.parseColor(color));
        bgColor.setBackground(bg);

        colorText.setText(color);

    }
}