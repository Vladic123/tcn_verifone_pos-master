package com.tcn.sdk.liftdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static controller.VendApplication.myDB;

/**
 * lanes configuration activity class
 * @author v.vasilchikov
 */

public class tcn_verifone_LanesConfig extends AppCompatActivity {

    private EditText lpr = null;
    private EditText rws = null;

    private Integer lanesPerRow =0;
    private Integer rows = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone_lanes_config);

        try {
            lanesPerRow = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_lanes_per_row_main"));
            rows = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_rows_main"));
        }catch(Exception ex){

        }


        lpr = findViewById(R.id.tcn_verifone_lanes_config_lanes_per_row_text_value);
        rws = findViewById(R.id.tcn_verifone_lanes_config_lanes_rows_text_value);

        lpr.setText(lanesPerRow.toString());
        rws.setText(rows.toString());

        Button exitButton = findViewById(R.id.tcn_verifone_lanes_config_exit_button);
        Button applyButton = findViewById(R.id.tcn_verifone_lanes_config_apply_button);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    lanesPerRow = Integer.parseInt(lpr.getText().toString());
                    rows = Integer.parseInt(rws.getText().toString());
                }catch(Exception ex){

                }

                myDB.SettingsUpdateSingleValue("tcn_lanes_per_row_main",lanesPerRow.toString());
                myDB.SettingsUpdateSingleValue("tcn_rows_main",rows.toString());

                tcn_verifone_AuxLane AL = new tcn_verifone_AuxLane();
                for(int a=0;a<rows;a++){
                    for(int b=0;b<lanesPerRow;b++){
                        Integer laneId = (a*lanesPerRow+b)+1; // lanes id started from 1!!!!
                        tcn_verifone_AuxLane ALT = myDB.LanesGetById(laneId.longValue());
                        if(ALT.getId()==-1){
                            AL.setId(laneId);
                            myDB.LanesAddItem(AL);
                        }
                    }
                }

            }
        });

    }
}