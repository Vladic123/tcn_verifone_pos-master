package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import static controller.VendApplication.myDB;

/**
 * product activty class
 * @author v.vasilchikov
 */

public class tcn_verifone_Products extends AppCompatActivity {

    private TableLayout prodTable;
    private Context context = null;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone_products);

        context = this;

        exitButton = findViewById(R.id.tcn_verifone_products_exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        prodTable = findViewById(R.id.tcn_verifone_products_table);

        Integer lanesPerRow = 0;
        Integer rows = 0;

        try {
            lanesPerRow = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_lanes_per_row_main"));
            rows = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_rows_main"));
        }catch(Exception ex){

        }

        Integer laneNumber = 1;
        for(int row=1;row<=rows;row++) {
                TableRow trow = new TableRow(context);
                for (int lane = 1; lane <= lanesPerRow; lane++) {
                    if ((laneNumber % 10) != 0) {
                        final Button button = new Button(context);
                        button.setTextSize(29);
                        final Integer btnNumber = laneNumber;
                        button.setText("LN " + laneNumber.toString());
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                processButton(btnNumber);
                            }
                        });
                        trow.addView(button);
                    }
                    laneNumber++;

                }

                prodTable.addView(trow);

        }
    }

    private void processButton(Integer buttonId){

        Intent intent = new Intent(this,tcn_verifone_ProductsLanes.class);
        intent.putExtra("lanenum",buttonId);
        startActivity(intent);

    }
}