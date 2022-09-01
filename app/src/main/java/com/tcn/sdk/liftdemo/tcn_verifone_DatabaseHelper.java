package com.tcn.sdk.liftdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class tcn_verifone_DatabaseHelper extends SQLiteOpenHelper {

    public static SQLiteDatabase db;

    private static String DB_NAME = "local.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 68;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    private static tcn_verifone_DatabaseHelper sInstance;

    public tcn_verifone_DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION );
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();

    }

    public void updateDataBase() {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {

        InputStream mInput = mContext.getResources().openRawResource(R.raw.local);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (db != null)
            db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }


    public boolean OpenDB() {

        try {
            this.updateDataBase();
        } catch (Exception ex) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            db = this.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        if (db.isOpen()) {
            Log.d("db is open", "");
            return true;
        }

        return false;
    }

    public void CloseDB() {

        db.close();
        Log.d("db closed", "");
    }

    public final class SettingsContract{
        private SettingsContract(){}

        public class SettingsEntry implements BaseColumns{
            public static final String TABLE_NAME = "settings";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_VALUE = "value";
        }
    }

    private void GetAllSettings(){
        if (db.isOpen()) {

            String[] Projection = {
                    SettingsContract.SettingsEntry.COLUMN_NAME_VALUE
            };

            Cursor myCursor = db.query(
                    SettingsContract.SettingsEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            String Value = "";
            String Name = "";
            if (myCursor.getCount() > 0) {

                myCursor.moveToFirst();

                do {
                    Value = myCursor.getString(myCursor.getColumnIndex(SettingsContract.SettingsEntry.COLUMN_NAME_VALUE));
                    Name = myCursor.getString(myCursor.getColumnIndex(SettingsContract.SettingsEntry.COLUMN_NAME_NAME));
                }while(myCursor.moveToNext());
            }

            myCursor.close();

        }

    }

    public String SettingsGetSingleValue(String ValueName) {

     //   GetAllSettings();

        if (db.isOpen()) {

            String[] Projection = {
                    SettingsContract.SettingsEntry.COLUMN_NAME_VALUE
            };
            String Selection = SettingsContract.SettingsEntry.COLUMN_NAME_NAME + " = ?";
            String[] SelectionsArg = {ValueName};

            Cursor myCursor = db.query(
                    SettingsContract.SettingsEntry.TABLE_NAME,
                    Projection,
                    Selection,
                    SelectionsArg,
                    null,
                    null,
                    null
            );
            String Value = "";
            if (myCursor.getCount() > 0) {

                myCursor.moveToFirst();
                Value = myCursor.getString(myCursor.getColumnIndex(SettingsContract.SettingsEntry.COLUMN_NAME_VALUE));

            }

            myCursor.close();

            if (Value != null) {

                Log.d("Name", ValueName);
                Log.d("Value", Value);

                return Value;
            }
        }

        return "";
    }

    public boolean SettingsUpdateSingleValue(String Name, String Value) {

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(SettingsContract.SettingsEntry.COLUMN_NAME_NAME, Name);
            Values.put(SettingsContract.SettingsEntry.COLUMN_NAME_VALUE, Value);


            String Selection = SettingsContract.SettingsEntry.COLUMN_NAME_NAME + " like ?";
            String[] SelectionArgs = {Name};

            int Count = db.update(
                    SettingsContract.SettingsEntry.TABLE_NAME,
                    Values,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;
    }

    public final class GoodsContract{
        private GoodsContract(){}

        public class GoodsEntry implements BaseColumns{
            public static final String TABLE_NAME = "goods";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_DESCRIPTION = "description";
            public static final String COLUMN_NAME_DETAILED_DESCRIPTION = "detailed_description";
            public static final String COLUMN_NAME_IMAGE = "image";
            public static final String COLUMN_NAME_IMAGE_LARGE = "image_large";
            public static final String COLUMN_NAME_PRICE = "price";
            public static final String COLUMN_NAME_ENABLED = "enabled";
            public static final String COLUMN_NAME_VOLUME = "volume";


        }
    }

    public ArrayList<tcn_verifone_AuxItem> GoodsGetAll(){
        tcn_verifone_AuxItem Result = new tcn_verifone_AuxItem();

        ArrayList <tcn_verifone_AuxItem> result = new ArrayList<tcn_verifone_AuxItem>();
        if (db.isOpen()) {


            Cursor myCursor = db.query(
                    GoodsContract.GoodsEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (myCursor.getCount() > 0) {

                myCursor.moveToFirst();

                do {
                    tcn_verifone_AuxItem tResult = new tcn_verifone_AuxItem();

                    tResult.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_ID))));
                    tResult.setDescription(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_DESCRIPTION)));
                    tResult.setDetailed_description(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_DETAILED_DESCRIPTION)));
                    tResult.setImage(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE))));
                    tResult.setImageLarge(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE_LARGE))));
                    tResult.setName(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_NAME)));
                    tResult.setPrice(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_PRICE))));
                    tResult.setVolume(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_VOLUME))));

                    Boolean enabled = false;
                    if(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED)))==1)
                    {
                        enabled = true;
                    }
                    tResult.setEnabled(enabled);

                    result.add(tResult);
                } while (myCursor.moveToNext());

                myCursor.close();
            }

        }
        return result;

    }

    public ArrayList<tcn_verifone_AuxItem> GoodsGetAllEnabled(){
        tcn_verifone_AuxItem Result = new tcn_verifone_AuxItem();

        ArrayList <tcn_verifone_AuxItem> result = new ArrayList<tcn_verifone_AuxItem>();
        if (db.isOpen()) {

            String Selection =  GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED+ " = ? ";
            String [] SelectionArgs = {"1"};
            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        GoodsContract.GoodsEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    do {
                        tcn_verifone_AuxItem tResult = new tcn_verifone_AuxItem();

                        tResult.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_ID))));
                        tResult.setDescription(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_DESCRIPTION)));
                        tResult.setDetailed_description(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_DETAILED_DESCRIPTION)));
                        tResult.setImage(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE))));
                        tResult.setImageLarge(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE_LARGE))));
                        tResult.setName(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_NAME)));
                        tResult.setPrice(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_PRICE))));
                        tResult.setVolume(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_VOLUME))));

                        Boolean enabled = false;
                        if (Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED))) == 1) {
                            enabled = true;
                        }
                        tResult.setEnabled(enabled);

                        result.add(tResult);
                    } while (myCursor.moveToNext());

                }
            }finally {
                if(myCursor!=null){
                    myCursor.close();
                }
            }

        }
        return result;

    }

    public tcn_verifone_AuxItem GoodsGetItemById(Integer item_id){

        tcn_verifone_AuxItem Result = new tcn_verifone_AuxItem();

        if (db.isOpen()) {


            String Selection =  GoodsContract.GoodsEntry.COLUMN_NAME_ID+ " = ? ";
            String [] SelectionArgs = {item_id.toString()};

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        GoodsContract.GoodsEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    do {

                        Result.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_ID))));
                        Result.setDescription(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_DESCRIPTION)));
                        Result.setDetailed_description(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_DETAILED_DESCRIPTION)));
                        Result.setImage(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE))));
                        Result.setImageLarge(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE_LARGE))));
                        Result.setName(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_NAME)));
                        Result.setPrice(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_PRICE))));
                        Result.setVolume(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_VOLUME))));

                        Boolean enabled = false;
                        if (Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED))) == 1) {
                            enabled = true;
                        }
                        Result.setEnabled(enabled);

                    } while (myCursor.moveToNext());

                }
            }finally {
                if(myCursor!=null){
                    myCursor.close();
                }
            }

        }
        return Result;

    }
    public Boolean GoodsDeleteItemById(Integer item_id){

        if (db.isOpen()) {


            String Selection =  GoodsContract.GoodsEntry.COLUMN_NAME_ID+ " = ? ";
            String [] SelectionArgs = {item_id.toString()};

            int count = db.delete(
                    GoodsContract.GoodsEntry.TABLE_NAME,
                    Selection,
                    SelectionArgs
            );

            return count > 0;

        }
        return false;

    }

    public Boolean GoodsUpdateItem(tcn_verifone_AuxItem item){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_DESCRIPTION, item.getDescription());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_DETAILED_DESCRIPTION, item.getDetailed_description());
            if(item.getEnabled()) {
                Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED, 1);
            }else{
                Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED, 0);
            }
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE, item.getImage());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE_LARGE, item.getImageLarge());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_NAME, item.getName());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_PRICE, item.getPrice());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_VOLUME, item.getVolume());


            String Selection = GoodsContract.GoodsEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {item.getId().toString()};

            int Count = db.update(
                    GoodsContract.GoodsEntry.TABLE_NAME,
                    Values,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;
    }

    Boolean GoodsAddItem(tcn_verifone_AuxItem item){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_DESCRIPTION, item.getDescription());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_DETAILED_DESCRIPTION, item.getDetailed_description());

            if(item.getEnabled()) {
                Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED, 1);
            }else{
                Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_ENABLED, 0);
            }
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE, item.getImage());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_IMAGE_LARGE, item.getImageLarge());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_NAME, item.getName());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_PRICE, item.getPrice());
            Values.put(GoodsContract.GoodsEntry.COLUMN_NAME_VOLUME, item.getVolume());

            return db.insert(GoodsContract.GoodsEntry.TABLE_NAME, null, Values) != -1;
        }

        return false;
    }

    public final class LanesContract{
        private LanesContract(){}

        public class LanesEntry implements BaseColumns{
            public static final String TABLE_NAME = "lanes";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_ITEM_ID = "item_id";
            public static final String COLUMN_NAME_AMOUNT = "amount";
            public static final String COLUMN_NAME_STATUS = "status";
            public static final String COLUMN_NAME_PRINTED_NUM = "printed_num";
        }
    }

    public tcn_verifone_AuxLane[] LanesGetAll(){

        tcn_verifone_AuxLane[] Result = new tcn_verifone_AuxLane[1024];

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        LanesContract.LanesEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                int count = 0;
                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    do {

                        tcn_verifone_AuxLane tempAL = new tcn_verifone_AuxLane();
                        tempAL.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT))));
                        tempAL.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ID))));
                        tempAL.setStatus(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_STATUS))));
                        tempAL.setItem(GoodsGetItemById(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID)))));
                        tempAL.setPrinted_num(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM))));

                        Result[count] = tempAL;
                        count++;
                    } while (myCursor.moveToNext());

                }
            }finally {
                if(myCursor!=null){
                    myCursor.close();
                }

            }
        }

        return Result;

    }


    public tcn_verifone_AuxLane LanesGetById(Long laneId){

        if(laneId==null){
            return null;
        }

        tcn_verifone_AuxLane Result = new tcn_verifone_AuxLane();

        String Selection =  LanesContract.LanesEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {laneId.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        LanesContract.LanesEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );


                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    tcn_verifone_AuxLane tempAL = new tcn_verifone_AuxLane();
                    tempAL.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT))));
                    tempAL.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ID))));
                    tempAL.setStatus(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_STATUS))));
                    tempAL.setItem(GoodsGetItemById(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID)))));
                    try {
                        tempAL.setPrinted_num(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM))));
                    } catch (Exception ex) {

                    }

                    Result = tempAL;

                    myCursor.close();
                }

            }finally {
                if(myCursor!=null){
                    myCursor.close();
                }

            }
        }

        return Result;

    }

    public tcn_verifone_AuxLane LanesGetByPrintedId(Integer lanePrintedId){

        tcn_verifone_AuxLane Result = null;


        String Selection =  LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM+ " = ? ";
        String [] SelectionArgs = {lanePrintedId.toString()};

        if (db.isOpen()) {

            Cursor myCursor=null;
            try {
                myCursor = db.query(
                        LanesContract.LanesEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    tcn_verifone_AuxLane tempAL = new tcn_verifone_AuxLane();
                    tempAL.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT))));
                    tempAL.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ID))));
                    tempAL.setStatus(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_STATUS))));
                    tempAL.setItem(GoodsGetItemById(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID)))));
                    try {
                        tempAL.setPrinted_num(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM))));
                    } catch (Exception ex) {

                    }
                    Result = new tcn_verifone_AuxLane();
                    Result = tempAL;

                }
            }finally {
                if(myCursor!=null){
                    myCursor.close();
                }
            }
        }

        return Result;

    }

    public ArrayList<tcn_verifone_AuxLane> LanesGetByItemId(Integer itemId){

        ArrayList<tcn_verifone_AuxLane> result = new ArrayList<tcn_verifone_AuxLane>();

        String Selection =  LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID+ " = ? ";
        String [] SelectionArgs = {itemId.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        LanesContract.LanesEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    do {
                        tcn_verifone_AuxLane tempAL = new tcn_verifone_AuxLane();
                        tempAL.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT))));
                        tempAL.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ID))));
                        tempAL.setStatus(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_STATUS))));
                        tempAL.setItem(GoodsGetItemById(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID)))));
                        try {
                            tempAL.setPrinted_num(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM))));
                        } catch (Exception ex) {

                        }

                        result.add(tempAL);
                    } while (myCursor.moveToNext());

                }
            }finally {
                if(myCursor!=null){
                    myCursor.close();
                }

            }

        }

        return result;

    }

    public Integer LanesCountMaxItems(Long itemId){

        Integer result = 0;

        tcn_verifone_AuxLane Result = new tcn_verifone_AuxLane();

        String Selection =  LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID+ " = ? ";
        String [] SelectionArgs = {itemId.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        LanesContract.LanesEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    do {
                        result = result + Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT)));
                    } while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }

        return result;

    }


    public boolean LanesUpdateItem(tcn_verifone_AuxLane AL){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT, AL.getAmount());
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID, AL.getItem().getId());
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_STATUS, AL.getStatus());
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM, AL.getPrinted_num());


            String Selection = LanesContract.LanesEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {AL.getId().toString()};

            int Count = db.update(
                    LanesContract.LanesEntry.TABLE_NAME,
                    Values,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;
    }

    public boolean LanesAddItem(tcn_verifone_AuxLane AL){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_AMOUNT, AL.getAmount());

            // item can be null
            try {
                Values.put(LanesContract.LanesEntry.COLUMN_NAME_ITEM_ID, AL.getItem().getId());
            }catch(Exception ex){}

            Values.put(LanesContract.LanesEntry.COLUMN_NAME_STATUS, AL.getStatus());
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_ID,AL.getId().toString());
            Values.put(LanesContract.LanesEntry.COLUMN_NAME_PRINTED_NUM, AL.getPrinted_num());

            return db.insert(LanesContract.LanesEntry.TABLE_NAME, null, Values) != -1;
        }

        return false;


    }

    public final class ImagesContract{
        private ImagesContract(){}

        public class ImageEntry implements BaseColumns{
            public static final String TABLE_NAME = "images";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_NAME = "namepath";
            public static final String COLUMN_NAME_BG_COLOR = "background_color";

        }
    }

    public tcn_verifone_AuxImage ImagesGetImageById(Integer id){

        tcn_verifone_AuxImage result=null;
        String Selection =  ImagesContract.ImageEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {id.toString()};

        if (db.isOpen()) {

            Cursor myCursor=null;
            try {
                myCursor = db.query(
                        ImagesContract.ImageEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );
                int count = 0;
                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    tcn_verifone_AuxImage img = new tcn_verifone_AuxImage();
                    img.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(ImagesContract.ImageEntry.COLUMN_NAME_ID))));
                    img.setNamepath(myCursor.getString(myCursor.getColumnIndex(ImagesContract.ImageEntry.COLUMN_NAME_NAME)));
                    try {
                        img.setBgcolor(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(ImagesContract.ImageEntry.COLUMN_NAME_BG_COLOR))));
                    } catch (Exception ex) {
                        img.setBgcolor(-1);
                    }
                    result = img;

                }
            }finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }
        }
        return result;

    }

    public Integer ImagesAddImage(tcn_verifone_AuxImage image){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(ImagesContract.ImageEntry.COLUMN_NAME_NAME, image.getNamepath());
            Values.put(ImagesContract.ImageEntry.COLUMN_NAME_BG_COLOR, image.getBgcolor());

            Long lstId = db.insert(ImagesContract.ImageEntry.TABLE_NAME, null, Values);
            if ( lstId!= -1) {
                return lstId.intValue();
            }
        }
        return 0;

    }

    public Boolean ImagesUpdateImage(tcn_verifone_AuxImage image){
        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(ImagesContract.ImageEntry.COLUMN_NAME_NAME, image.getNamepath());
            Values.put(ImagesContract.ImageEntry.COLUMN_NAME_BG_COLOR, image.getBgcolor());

            String Selection = ImagesContract.ImageEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {image.getId().toString()};

            int Count = db.update(
                    ImagesContract.ImageEntry.TABLE_NAME,
                    Values,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;
    }

    public Boolean ImagesDeleteImageById(Integer id){
        if (db.isOpen()) {

            String Selection = ImagesContract.ImageEntry.COLUMN_NAME_ID + " = ?";
            String[] SelectionArgs = {id.toString()};

            int Count = db.delete(
                    ImagesContract.ImageEntry.TABLE_NAME,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }
        return false;
    }

    public ArrayList<tcn_verifone_AuxImage> ImagesGetAll(){

        ArrayList<tcn_verifone_AuxImage> result = new ArrayList<tcn_verifone_AuxImage>();
        if (db.isOpen()) {

            Cursor myCursor=null;
            try {
                myCursor = db.query(
                        ImagesContract.ImageEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                int count = 0;
                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    do {
                        tcn_verifone_AuxImage img = new tcn_verifone_AuxImage();
                        img.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(ImagesContract.ImageEntry.COLUMN_NAME_ID))));
                        img.setNamepath(myCursor.getString(myCursor.getColumnIndex(ImagesContract.ImageEntry.COLUMN_NAME_NAME)));
                        try {
                            img.setBgcolor(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(ImagesContract.ImageEntry.COLUMN_NAME_BG_COLOR))));
                        } catch (Exception ex) {
                            img.setBgcolor(-1);
                        }
                        result.add(img);
                    } while (myCursor.moveToNext());

                }
            }finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }
        }
        return result;
    }

    public final class FlavoursContract{
        private FlavoursContract(){}

        public class FlavourEntry implements BaseColumns{
            public static final String TABLE_NAME = "flavours";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_IMAGE = "image";
        }
    }

    public ArrayList<tcn_verifone_AuxFlavour> FlavoursGetAll(){

        ArrayList<tcn_verifone_AuxFlavour> result=null;

        if (db.isOpen()) {

            Cursor myCursor=null;
            try {
                myCursor = db.query(
                        FlavoursContract.FlavourEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    result = new ArrayList<tcn_verifone_AuxFlavour>();

                    myCursor.moveToFirst();

                    do {
                        tcn_verifone_AuxFlavour flavour = new tcn_verifone_AuxFlavour();

                        flavour.setName(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_NAME)));
                        flavour.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_ID))));
                        flavour.setImage(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_IMAGE))));
                        result.add(flavour);

                    } while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return result;
    }

    public tcn_verifone_AuxFlavour FlavoursGetById(Integer id){

        tcn_verifone_AuxFlavour result=null;
        String Selection =  FlavoursContract.FlavourEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {id.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        FlavoursContract.FlavourEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {


                    myCursor.moveToFirst();

                    result = new tcn_verifone_AuxFlavour();

                    result.setName(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_NAME)));
                    result.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_ID))));
                    result.setImage(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_IMAGE))));

   }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return result;
    }


    public String FlavourGetNameByID(Integer id){

        String result="";
        String Selection =  FlavoursContract.FlavourEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {id.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        FlavoursContract.FlavourEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    result = myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_NAME));
                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return result;
    }

    public Integer FlavourGetImageByID(Integer id){
        Integer result=-1;
        String Selection =  FlavoursContract.FlavourEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {id.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        FlavoursContract.FlavourEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    result = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursContract.FlavourEntry.COLUMN_NAME_IMAGE)));
                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }
        }
        return result;
    }


    public final class FlavoursItemsContract{
        private FlavoursItemsContract(){}

        public class FlavourItemEntry implements BaseColumns{
            public static final String TABLE_NAME = "flavours_items";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_ITEM = "item";
            public static final String COLUMN_NAME_FLAVOUR = "flavour";
        }
    }

    public ArrayList<Integer> FlavoursItemsGetFlavoursByItem(Integer itemId){
        ArrayList<Integer> result = null;
        String Selection =  FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_ITEM+ " = ? ";
        String [] SelectionArgs = {itemId.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        FlavoursItemsContract.FlavourItemEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {
                    result = new ArrayList<Integer>();

                    myCursor.moveToFirst();
                    do {
                        result.add(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_FLAVOUR))));
                    }
                    while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return result;
    }

    public ArrayList<Integer> FlavoursItemsGetItemsByFlavour(Integer flavourId){
        ArrayList<Integer> result = null;
        String Selection =  FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_FLAVOUR+ " = ? ";
        String [] SelectionArgs = {flavourId.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        FlavoursItemsContract.FlavourItemEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {
                    result = new ArrayList<Integer>();

                    myCursor.moveToFirst();
                    do {
                        result.add(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_ITEM))));
                    }
                    while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }
        }
        return result;
    }

    public Boolean FlavoursItemsAddFlavourToItemById(Integer itemId, Integer flavourId){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_ITEM, itemId.toString());
            Values.put(FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_FLAVOUR, flavourId.toString());


            return db.insert(FlavoursItemsContract.FlavourItemEntry.TABLE_NAME, null, Values) != -1;
        }
        return false;
    }

    public Boolean FlavoursItemsDeleteAllFlavoursByItemId(Integer itemId){

        if (db.isOpen()) {

            String Selection = FlavoursItemsContract.FlavourItemEntry.COLUMN_NAME_ITEM + " = ?";
            String[] SelectionArgs = {itemId.toString()};

            int Count = db.delete(
                    FlavoursItemsContract.FlavourItemEntry.TABLE_NAME,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }
        return false;
    }

    public Boolean FlavoursUpdateFlavour(tcn_verifone_AuxFlavour flavour){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(FlavoursContract.FlavourEntry.COLUMN_NAME_IMAGE, flavour.getImage());
            Values.put(FlavoursContract.FlavourEntry.COLUMN_NAME_NAME, flavour.getName());


            String Selection = FlavoursContract.FlavourEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {flavour.getId().toString()};

            int Count = db.update(
                    FlavoursContract.FlavourEntry.TABLE_NAME,
                    Values,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;

    }

    public Boolean FlavoursAddFlavour(tcn_verifone_AuxFlavour flavour){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(FlavoursContract.FlavourEntry.COLUMN_NAME_NAME, flavour.getName());
            Values.put(FlavoursContract.FlavourEntry.COLUMN_NAME_IMAGE, flavour.getImage());


            return db.insert(FlavoursContract.FlavourEntry.TABLE_NAME, null, Values) != -1;
        }

        return false;

    }

    public Boolean FlavoursDeleteFlavourById(Integer id){

        if (db.isOpen()) {

            String Selection = FlavoursContract.FlavourEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {id.toString()};

            int Count = db.delete(
                    FlavoursContract.FlavourEntry.TABLE_NAME,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;

    }

    public final class ColorsContract{
        private ColorsContract(){}

        public class ColorEntry implements BaseColumns{
            public static final String TABLE_NAME = "colors";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_COLOR = "color";
        }
    }

    public ArrayList<tcn_verifone_AuxColor> ColorsGetColors(){

        ArrayList<tcn_verifone_AuxColor> result = null;

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        ColorsContract.ColorEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {
                    result = new ArrayList<tcn_verifone_AuxColor>();
                    myCursor.moveToFirst();
                    do {
                        Integer id = myCursor.getInt(myCursor.getColumnIndex(ColorsContract.ColorEntry.COLUMN_NAME_ID));
                        String res = myCursor.getString(myCursor.getColumnIndex(ColorsContract.ColorEntry.COLUMN_NAME_COLOR));
                        tcn_verifone_AuxColor recolor = new tcn_verifone_AuxColor(id, res);
                        result.add(recolor);

                    } while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }

        return result;

    }

    public String ColorsGetColorById(Integer id){

        String result = "#000000";

        String Selection =  ColorsContract.ColorEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {id.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        ColorsContract.ColorEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();
                    result = myCursor.getString(myCursor.getColumnIndex(ColorsContract.ColorEntry.COLUMN_NAME_COLOR));

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }
        }

        return result;

    }
    public tcn_verifone_AuxColor ColorsGetAuxColorById(Integer id){

        tcn_verifone_AuxColor result = null;

        String Selection =  ColorsContract.ColorEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {id.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        ColorsContract.ColorEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();
                    String color = myCursor.getString(myCursor.getColumnIndex(ColorsContract.ColorEntry.COLUMN_NAME_COLOR));
                    Integer idc = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(ColorsContract.ColorEntry.COLUMN_NAME_ID)));
                    result = new tcn_verifone_AuxColor(idc, color);

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }
        }

        return result;

    }

    public Boolean ColorsAddColor(tcn_verifone_AuxColor color){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(ColorsContract.ColorEntry.COLUMN_NAME_COLOR, color.getColor());


            return db.insert(ColorsContract.ColorEntry.TABLE_NAME, null, Values) != -1;
        }

        return false;

    }


    public Boolean ColorsUpdateColor(tcn_verifone_AuxColor color){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            //Values.put(ColorsContract.ColorEntry.COLUMN_NAME_ID,color.getId() );
            Values.put(ColorsContract.ColorEntry.COLUMN_NAME_COLOR, color.getColor());


            String Selection = ColorsContract.ColorEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {color.getId().toString()};

            int Count = db.update(
                    ColorsContract.ColorEntry.TABLE_NAME,
                    Values,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;
    }

    public Boolean ColorsDeleteColorById(Integer id){

        if (db.isOpen()) {

            String Selection = ColorsContract.ColorEntry.COLUMN_NAME_ID + " like ?";
            String[] SelectionArgs = {id.toString()};

            int Count = db.delete(
                    ColorsContract.ColorEntry.TABLE_NAME,
                    Selection,
                    SelectionArgs);

            return Count > 0;
        }

        return false;

    }

    public final class TransactionsContract{
        private TransactionsContract(){}

        public class TransactionEntry implements BaseColumns{
            public static final String TABLE_NAME = "transactions";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_DATETIME = "datetime";
            public static final String COLUMN_NAME_AMOUNT = "amount";
            public static final String COLUMN_NAME_TXID = "txid";
            public static final String COLUMN_NAME_MID = "mid";
            public static final String COLUMN_NAME_RECEIPT = "receipt";
            public static final String COLUMN_NAME_RESPCODE = "respcode";
            public static final String COLUMN_NAME_ONLINEFLAG = "onlineflag";
            public static final String COLUMN_NAME_SUCCESSFUL = "successful";
            public static final String COLUMN_NAME_UNLOADED = "unloaded";
            public static final String COLUMN_NAME_DISCOUNT_UID = "discountuid";
            public static final String COLUMN_NAME_DISCOUNT_VALUE = "discountvalue";
            public static final String COLUMN_NAME_CARD_TYPE = "cardtype";

        }
    }

    public tcn_verifone_AuxTransaction TransactionsGetTransactionById(Integer id){
        tcn_verifone_AuxTransaction AT = null;

        String Selection =  TransactionsContract.TransactionEntry.COLUMN_NAME_TXID+ " = ? ";
        String [] SelectionArgs = {id.toString()};


        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        TransactionsContract.TransactionEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {


                    myCursor.moveToFirst();

                    AT = new tcn_verifone_AuxTransaction();

                    AT.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_ID))));
                    AT.setDatetime(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DATETIME)));
                    AT.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT))));
                    AT.setTxid(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_TXID))));
                    AT.setMid(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_MID))));
                    AT.setReceipt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_RECEIPT)));
                    AT.setRespcode(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_RESPCODE)));
                    AT.setOnlineflag(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_ONLINEFLAG)));
                    AT.setSuccessful(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_SUCCESSFUL))));
                    AT.setUnloaded(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_UNLOADED))));
                    AT.setDiscountUid(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_UID)));
                    AT.setDiscountValue(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_VALUE)));
                    AT.setCardType(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_CARD_TYPE)));

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return AT;


    }

    public ArrayList<tcn_verifone_AuxTransaction> TransactionsGetAll(){

        ArrayList<tcn_verifone_AuxTransaction> TL = null;

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        TransactionsContract.TransactionEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (myCursor != null && myCursor.getCount() > 0) {

                    TL = new ArrayList<tcn_verifone_AuxTransaction>();

                    myCursor.moveToFirst();

                    do {
                        tcn_verifone_AuxTransaction AT = new tcn_verifone_AuxTransaction();

                        AT.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_ID))));
                        AT.setDatetime(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DATETIME)));
                        AT.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT))));
                        AT.setTxid(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_TXID))));
                        AT.setMid(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_MID))));
                        AT.setReceipt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_RECEIPT)));
                        AT.setRespcode(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_RESPCODE)));
                        AT.setOnlineflag(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_ONLINEFLAG)));
                        AT.setSuccessful(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_SUCCESSFUL))));
                        AT.setUnloaded(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_UNLOADED))));
                        AT.setDiscountUid(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_UID)));
                        AT.setDiscountValue(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_VALUE)));
                        AT.setCardType(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_CARD_TYPE)));

                        TL.add(AT);

                    } while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return TL;
    }
    public ArrayList<tcn_verifone_AuxTransaction> TransactionsGetAllForUnload(){

        ArrayList<tcn_verifone_AuxTransaction> TL = null;

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                String Selection =  TransactionsContract.TransactionEntry.COLUMN_NAME_UNLOADED+ " = ? ";
                String [] SelectionArgs = {"0"};

                myCursor = db.query(
                        TransactionsContract.TransactionEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor != null && myCursor.getCount() > 0) {

                    TL = new ArrayList<tcn_verifone_AuxTransaction>();

                    myCursor.moveToFirst();

                    do {
                        tcn_verifone_AuxTransaction AT = new tcn_verifone_AuxTransaction();

                        AT.setId(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_ID))));
                        AT.setDatetime(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DATETIME)));
                        AT.setAmount(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT))));
                        AT.setTxid(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_TXID))));
                        AT.setMid(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_MID))));
                        AT.setReceipt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_RECEIPT)));
                        AT.setRespcode(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_RESPCODE)));
                        AT.setOnlineflag(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_ONLINEFLAG)));
                        AT.setSuccessful(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_SUCCESSFUL))));
                        AT.setUnloaded(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_UNLOADED))));
                        AT.setDiscountUid(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_UID)));
                        AT.setDiscountValue(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_VALUE)));
                        AT.setCardType(myCursor.getString(myCursor.getColumnIndex(TransactionsContract.TransactionEntry.COLUMN_NAME_CARD_TYPE)));

                        TL.add(AT);

                    } while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }
        return TL;
    }

    protected Boolean TransactionCheckByDiscountUID(String uid) {

        String Selection = TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_UID + " = ? ";
        String[] SelectionArgs = {uid.toString()};

        Boolean result = false;

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        TransactionsContract.TransactionEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    result = true;


                }

                myCursor.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
    }


    public Boolean TransactionsStore(tcn_verifone_AuxTransaction AT){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DATETIME, AT.getDatetime());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT, AT.getAmount());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_TXID, AT.getTxid());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_MID, AT.getMid());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_RECEIPT, AT.getReceipt());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_RESPCODE, AT.getRespcode());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_ONLINEFLAG, AT.getOnlineflag());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_SUCCESSFUL, AT.getSuccessful());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_UNLOADED, AT.getUnloaded());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_UID, AT.getDiscountUid());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DISCOUNT_VALUE, AT.getDiscountValue());
            Values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CARD_TYPE, AT.getCardType());


            Long last = db.insert(TransactionsContract.TransactionEntry.TABLE_NAME, null, Values);
            if (last > -1) {

                ArrayList<Pair<Integer,Integer>> items = AT.getItems();
                for(Pair<Integer,Integer> item:items){
                    TransactionsItemsAdd(last,item.first,item.second);
                }

                return true;
            }
        }

        return false;

    }

    public Boolean TransactionsRemove(tcn_verifone_AuxTransaction trans){
        if (db.isOpen()) {


            String Selection =  TransactionsContract.TransactionEntry.COLUMN_NAME_ID+ " = ? ";
            String [] SelectionArgs = {trans.getId().toString()};

            int count = db.delete(
                    TransactionsContract.TransactionEntry.TABLE_NAME,
                    Selection,
                    SelectionArgs
            );

            return count > 0;

        }
        return false;
    }

    public final class TransactionsItemsContract{
        private TransactionsItemsContract(){}

        public class TransactionItemEntry implements BaseColumns{
            public static final String TABLE_NAME = "transactions_items";
            public static final String COLUMN_NAME_ID = "id";
            public static final String COLUMN_NAME_TRANSACTION = "transactionid";
            public static final String COLUMN_NAME_ITEM = "item";
            public static final String COLUMN_NAME_QUANTITY = "quantity";

        }
    }

    public Boolean TransactionsItemsAdd(Long trans, Integer item, Integer quantity){

        if (db.isOpen()) {
            ContentValues Values = new ContentValues();
            Values.put(TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_TRANSACTION, trans);
            Values.put(TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_ITEM, item);
            Values.put(TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_QUANTITY, quantity);


            return db.insert(TransactionsItemsContract.TransactionItemEntry.TABLE_NAME, null, Values) != -1;
        }

        return false;

    }

    public ArrayList<Integer> TransactionsItemsGetItemsByTransaction(Integer trans){

        ArrayList<Integer> IL = null;

        String Selection =  TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_TRANSACTION+ " = ? ";
        String [] SelectionArgs = {trans.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        TransactionsItemsContract.TransactionItemEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    IL = new ArrayList<Integer>();
                    myCursor.moveToFirst();

                    do {
                        IL.add(Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_ITEM))));
                    } while (myCursor.moveToNext());

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }

        return IL;
    }

    public Integer TransactionsItemsGetItemsQuantityByItemId(Integer itemId){
        Integer result = -1;

        String Selection =  TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_ID+ " = ? ";
        String [] SelectionArgs = {itemId.toString()};

        if (db.isOpen()) {

            Cursor myCursor = null;
            try {
                myCursor = db.query(
                        TransactionsItemsContract.TransactionItemEntry.TABLE_NAME,
                        null,
                        Selection,
                        SelectionArgs,
                        null,
                        null,
                        null
                );

                if (myCursor.getCount() > 0) {

                    myCursor.moveToFirst();

                    result = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(TransactionsItemsContract.TransactionItemEntry.COLUMN_NAME_QUANTITY)));

                }
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }

            }

        }

        return result;
    }

}


