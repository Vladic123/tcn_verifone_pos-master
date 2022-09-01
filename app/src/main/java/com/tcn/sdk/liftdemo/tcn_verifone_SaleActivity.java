package com.tcn.sdk.liftdemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import static controller.VendApplication.myDB;

/**
 * deprecated class
 * not used
 */

public class tcn_verifone_SaleActivity extends AppCompatActivity {

    private static final int PROCESS_SALE = 0;
    private static final int PROCESS_VEND = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__sale);

        tcn_verifone_AuxLane[] AL = myDB.LanesGetAll();

        ArrayList<tcn_verifone_AuxLane> ALArray= new ArrayList<tcn_verifone_AuxLane>();

        for(tcn_verifone_AuxLane i : AL){
            if(i!=null) {
                ALArray.add(i);
            }
        }

        tcn_verifone_OneButtonArrayAdapter adapter = new tcn_verifone_OneButtonArrayAdapter(this,ALArray);
        ListView SaleList = findViewById(R.id.tcn_verifone_sale_list_view);
        SaleList.setAdapter(adapter);

    }

    void onButtonSelect(Long laneId){

        Intent intent = new Intent(this,tcn_verifone_ProcessVend.class);
        intent.putExtra("id",laneId);

//        startActivityForResult(intent,PROCESS_SALE);
        startActivityForResult(intent,PROCESS_VEND);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PROCESS_SALE:{
                if(resultCode==RESULT_OK){
                    Integer id = (Integer)data.getSerializableExtra("id");
                    String amount = (String)data.getSerializableExtra("amount");
                    String txid = (String)data.getSerializableExtra("txid");

                    Intent intent = new Intent();//this,tcn_verifone_ProcessVend.class);
                    intent.putExtra("id",id);
                    intent.putExtra("txid",txid);
                    intent.putExtra("amount",amount);
                    setResult(RESULT_OK,intent);
                    finish();
                    //startActivityForResult(intent,PROCESS_VEND);

                }else{
                    // todo make something
                }
            }
            break;
            case PROCESS_VEND:{
                if(resultCode==RESULT_OK){

                }else{

                }
            }
            break;
        }
    }
}