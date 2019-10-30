package com.android.example.storemanager.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

public class StoreDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "store_items_database";
    public static final int DATABASE_VERSION = 1;

    public StoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + StoreContract.StoreEntry.PATH_STORE_DETAILS + "("
                + StoreContract.StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StoreContract.StoreEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL DEFAULT \"UNIDENTIFIED ITEM\","
                + StoreContract.StoreEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_ORDER + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME + " TEXT,"
                + StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_DATA + " BLOB,"
                + StoreContract.StoreEntry.SUPPLIER_NAME + " TEXT NOT NULL,"
                + StoreContract.StoreEntry.SUPPLIER_EMAIL + " TEXT NOT NULL);";

        //Executing the SQL Command

        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
