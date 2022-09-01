package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.gridlayout.widget.GridLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * image manager load from disk dialog
 * @author v.vasilchikov
 */

public class tcn_verifone_ImageManagerLoadFromDiskDialog extends DialogFragment implements View.OnClickListener {

    private GridLayout flavoursGrid;
    private ArrayList<Integer> Items;
    private Integer currentItemIndex = 0;
    private ImageView productImage;
    private TextView productText;
    private ImageView leftButton;
    private ImageView rightButton;

    public interface ImageManagerLoadFromDiskDialoglistener {
        void onImageLoadFromDiskDialog(String filename);
    }

    tcn_verifone_ImageManagerLoadFromDiskDialog(ImageManagerLoadFromDiskDialoglistener listener){
        this.listener=listener;
    }

    private ImageManagerLoadFromDiskDialoglistener listener;

    private Context context;
    private ArrayList<Pair<Integer,String>> images;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        };

        String exStoragePath = System.getenv("EXTERNAL_STORAGE");
        File directory = new File(exStoragePath+"/Download");
        File[] files = directory.listFiles(filter);

        images = new ArrayList<Pair<Integer,String>>();

        Boolean bigFiles = false;

        for(int a = 0;a<files.length;a++){
            if(files[a].length()<=1048576) {
                Pair<Integer, String> pair = new Pair<Integer, String>(a, files[a].getName());
                images.add(pair);
            }else{
                bigFiles=true;
            }
        }

        if(bigFiles) {
            Toast.makeText(context, R.string.image_manager_big_files, Toast.LENGTH_LONG).show();
        }

        View v = inflater.inflate(R.layout.image_from_disk_select_dialog, null);
        ColorDrawable CD = new ColorDrawable(Color.TRANSPARENT);
        getDialog().getWindow().setBackgroundDrawable(CD);

        ListView LV = v.findViewById(R.id.image_from_disk_select_dialog_list);
        tcn_verifone_ImageLoadDialogAdapter DA = new tcn_verifone_ImageLoadDialogAdapter(context, images);
        LV.setAdapter(DA);

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long idl = id;

                for(Pair<Integer,String> pair:images){
                    if(idl.intValue()==pair.first){
                        listener.onImageLoadFromDiskDialog(pair.second);
                        dismiss();
                    }
                }
                dismiss();
            }
        });

        return v;
    }

    public void onClick(View v) {
        //Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //Log.d(LOG_TAG, "Dialog 1: onCancel");
    }
}
