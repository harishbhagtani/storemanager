package com.android.example.storemanager.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.example.storemanager.R;

/**
 * Created by harishbhagtani on 07/08/17.
 */

public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView itemNAme = (TextView) view.findViewById(R.id.text_view_item_name);
        TextView itemSupplierName = (TextView) view.findViewById(R.id.text_view_item_supplier_name);
        final TextView itemQuantity = (TextView) view.findViewById(R.id.text_view_quantity);
        TextView itemPrice = (TextView) view.findViewById(R.id.text_view_item_price);

        //Defining the strings to put the value into the text views

        final String getItemName;
        final String getItemQuantity;
        final String getItemPrice;
        final String getSupplierName;
        final String getSupplierEmail;
        final String imageUri;
        final int id;
        final int isDicountApplicable;
        final int discountPercentage;

        Button sellButton = (Button) view.findViewById(R.id.button_sell);


        //Assigning the values to the declared Strings;

        getItemName = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_ITEM_NAME));
        id = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry._ID));
        getSupplierName = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.SUPPLIER_NAME));
        getItemPrice = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_ITEM_PRICE)));
        getSupplierEmail = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.SUPPLIER_EMAIL));
        isDicountApplicable = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE));
        discountPercentage = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE));
        getItemQuantity = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY)));
        imageUri = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME));

        if (Integer.valueOf(getItemQuantity) < 3 && Integer.valueOf(getItemQuantity) != 0) {
            itemQuantity.setText("Only " + getItemQuantity + " left in stock");
        } else {
            itemQuantity.setText(getItemQuantity + " left in stock");
        }
        if (Integer.valueOf(getItemQuantity) == 0) {
            itemQuantity.setTextColor(Color.RED);
            itemQuantity.setText("Out Of Stock");
        }
        //Assigning All The Values To the Text Views
        itemNAme.setText(getItemName);
        itemSupplierName.setText("by " + getSupplierName);
        itemPrice.setText("₹ " + getItemPrice);
        final Uri itemURI = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(getItemQuantity)>0) {
                    int newQuantity = Integer.valueOf(getItemQuantity) - 1;
                    ContentValues newValues = new ContentValues();
                    newValues.put(StoreContract.StoreEntry._ID, id);
                    newValues.put(StoreContract.StoreEntry.COLUMN_ITEM_NAME, getItemName);
                    newValues.put(StoreContract.StoreEntry.COLUMN_ITEM_PRICE, Integer.valueOf(getItemPrice));
                    newValues.put(StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY, newQuantity);
                    newValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE, isDicountApplicable);
                    newValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE, discountPercentage);
                    newValues.put(StoreContract.StoreEntry.SUPPLIER_NAME, getSupplierName);
                    newValues.put(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME, imageUri);
                    newValues.put(StoreContract.StoreEntry.SUPPLIER_EMAIL, getSupplierEmail);
                    context.getContentResolver().update(itemURI, newValues, null, null);
                }
            }
        });
    }
}
