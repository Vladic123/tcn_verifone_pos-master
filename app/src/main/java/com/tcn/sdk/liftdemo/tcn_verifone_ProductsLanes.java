package com.tcn.sdk.liftdemo;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static controller.VendApplication.myDB;
import static controller.VendApplication.myImageCache;

/**
 * product to lanes activty class
 * used for assignment of products to lanes
 * @author v.vasilchikov
 */

public class tcn_verifone_ProductsLanes extends AppCompatActivity {

    private ListView productList;
    private EditText items;
    private TableLayout productTable;
    private Context context;
    private ArrayList<tcn_verifone_AuxItem> products;
    private View productFrameRowView;
    private Integer itemId = -1;
    private Button applyButton;
    private tcn_verifone_AuxLane AL;
    private Integer laneid;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__products_lanes);

        context = this;

        products = myDB.GoodsGetAll();

        items = findViewById(R.id.tcn_verifone_product_lanes_items);
        productList = findViewById(R.id.tcn_verifone_products_lanes_product_list);
        productTable = findViewById(R.id.tcn_verifone_product_lanes_table);
        exitButton = findViewById(R.id.tcn_verifone_products_lanes_product_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        laneid = intent.getIntExtra("lanenum", -1);

        if (laneid > -1) {
            Integer quantity = myDB.LanesGetById(laneid.longValue()).getAmount();
            items.setText(quantity.toString());
        }

        AL = myDB.LanesGetById(laneid.longValue());


            //TableRow TR = new TableRow(this);
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            productFrameRowView = inflater.inflate(R.layout.tcn_verifone_product_list, null, false);
            ImageView image = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_image);
            TextView name = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_name);
            TextView desc = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_desc);
            TextView price = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_price);
            TextView flavour = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_flavour);

        if (AL.getItem() != null) {

            tcn_verifone_AuxItem AI = myDB.GoodsGetItemById(AL.getItem().getId());
            itemId=AI.getId();

            image.setImageBitmap(myImageCache.getBitmap(AI.getImage()));
            name.setText(AI.getName());
            desc.setText(AI.getDescription());
            price.setText(AI.getPrice().toString());
            //flavour.setText(myDB.FlavourGetNameByID(AI.getFlavour()));

// Creates a new drag event listener

        } else{
            Bitmap b = BitmapFactory.decodeResource(getResources(),R.raw.noproduct);
            image.setImageBitmap(b);
            name.setText("");
            desc.setText("");
            price.setText("");
            flavour.setText("");

        }
        myDragEventListener dragListen = new myDragEventListener();

        productTable.setOnDragListener(dragListen);

        productTable.addView(productFrameRowView,1);

        tcn_verifone_product_list_adapter adapter = new tcn_verifone_product_list_adapter(this, products);

        productList.setAdapter(adapter);

        productList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View v, int i, long l) {
                Integer k = i;
                // Create a new ClipData.Item from the ImageView object's tag
                ClipData.Item item = new ClipData.Item(k.toString());
                // Create a new ClipData using the tag as a label, the plain text MIME type, and
                // the already-created item. This will create a new ClipDescription object within the
                // ClipData, and set its MIME type entry to "text/plain"
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(k.toString(), mimeTypes, item);
                // Instantiates the drag shadow builder.
                View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(v);
                // Starts the drag
                v.startDrag(data        // data to be dragged
                        , dragshadow   // drag shadow builder
                        , v           // local data about the drag and drop operation
                        , 0          // flags (not currently used, set to 0)
                );
                return true;
            }
        });
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Integer pos=i;
                updateProduct(pos.toString());


            }
        });

        applyButton = findViewById(R.id.tcn_verifone_product_lane_apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tcn_verifone_AuxItem AI = myDB.GoodsGetItemById(itemId);
                boolean flag = true;
                Integer amount = -1;
                try {
                    amount = Integer.parseInt(items.getText().toString());
                }catch (Exception ex){
                    Toast.makeText(context,"Wrong amount",Toast.LENGTH_LONG).show();
                    flag=false;
                }
                if(flag) {
                    AL.setAmount(amount);
                    AL.setItem(AI);
                    AL.setId(laneid);

                    // check if lane exist
                    tcn_verifone_AuxLane ALT = myDB.LanesGetById(AL.getId().longValue());
                    if(ALT.getId()==AL.getId()) {
                        myDB.LanesUpdateItem(AL);
                    }else{
                        myDB.LanesAddItem(AL);
                    }
                }
            }
        });


    }

    private void updateProduct(String position){
        tcn_verifone_AuxItem AI = products.get(Integer.parseInt(position));

        itemId=AI.getId();
        productTable.removeView(productFrameRowView);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        productFrameRowView = inflater.inflate(R.layout.tcn_verifone_product_list, null, false);
        ImageView image = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_image);
        TextView name = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_name);
        TextView desc = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_desc);
        TextView price = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_price);
        TextView flavour = productFrameRowView.findViewById(R.id.tcn_verifone_product_list_flavour);

        image.setImageBitmap(myImageCache.getBitmap(AI.getImage()));
        itemId=AI.getId();
        name.setText(AI.getName());
        desc.setText(AI.getDescription());
        price.setText(AI.getFormattedPrice());
//        flavour.setText(myDB.FlavourGetNameByID(AI.getFlavour()));

// Creates a new drag event listener
        myDragEventListener dragListen = new myDragEventListener();
        productTable.setOnDragListener(dragListen);


        productTable.addView(productFrameRowView,1);

    }

    protected class myDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        //v.setColorFilter(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a green tint to the View. Return true; the return value is ignored.

                    //v.setColorFilter(Color.GREEN);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    //v.setColorFilter(Color.BLUE);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:

                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    String dragData = item.getText().toString();

                    updateProduct(dragData);

                    // Displays a message containing the dragged data.
                    Toast.makeText(context, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();

                    // Turns off any color tints
                    //v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting
                    //v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (event.getResult()) {
                        Toast.makeText(context, "The drop was handled.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context, "The drop didn't work.", Toast.LENGTH_LONG).show();

                    }

                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }

    }
}