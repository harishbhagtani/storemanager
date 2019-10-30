package com.android.example.storemanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.storemanager.data.StoreContract;
import com.android.example.storemanager.data.StoreDBHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.id;

public class ShowData extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Constant for Loader

    public static final int PRODUCT_INFO_LOADER = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //All UI Elements

    TextView itemName;
    TextView itemQuantity;
    TextView itemPrice;
    TextView isDiscountAvailable;
    Button deleteButton;
    ImageView imageViewProductImage;
    LinearLayout discountPercentageLayout;

    //Database Object and Database Helper Object

    SQLiteDatabase productDatabase;
    StoreDBHelper productDBHelper;

    // Variables Holding the product data

    String getProductName;
    String getSupplierName;
    String getIsDiscountAvailable;
    String getSupplierEmail;
    String productImageUri;
    int getItemQuantity;
    int getItemPrice;
    int getDiscountPercentage;

    // URI to get item address from MainActivity when user clicks the listview item

    Uri productAddressUri;

    //Cursor to hold the values given by the database

    Cursor productData;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        getSupportActionBar().setTitle("Product Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        itemName = (TextView) findViewById(R.id.item_name_text_view);
        itemQuantity = (TextView) findViewById(R.id.text_view_item_quantity_show);
        itemPrice = (TextView) findViewById(R.id.text_view_item_price_show);
        isDiscountAvailable = (TextView) findViewById(R.id.text_view_is_discount_available_show);
        imageViewProductImage = (ImageView) findViewById(R.id.prodcut_image);
        deleteButton = (Button) findViewById(R.id.button_delete);
        productDBHelper = new StoreDBHelper(this);
        productDatabase = productDBHelper.getReadableDatabase();
        productAddressUri = getIntent().getData();
        getLoaderManager().initLoader(PRODUCT_INFO_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_ITEM_NAME,
                StoreContract.StoreEntry.COLUMN_ITEM_PRICE,
                StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE,
                StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE,
                StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY,
                StoreContract.StoreEntry.SUPPLIER_NAME,
                StoreContract.StoreEntry.SUPPLIER_EMAIL,
                StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME};
        String selection = StoreContract.StoreEntry._ID + "=?";
        String[] stringArgs = new String[]{String.valueOf(ContentUris.parseId(productAddressUri))};
        return new CursorLoader(this, productAddressUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        productData = cursor;
        if (cursor.moveToFirst()) {
            getProductName = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_ITEM_NAME));
            int discount = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE));
            if (discount == 1) {
                getIsDiscountAvailable = "Yes";
            } else {
                getIsDiscountAvailable = "No";
                isDiscountAvailable.setTextColor(Color.RED);
            }
            getItemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY));
            getItemPrice = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_ITEM_PRICE));
            getDiscountPercentage = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE));
            getSupplierName = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.SUPPLIER_NAME));
            productImageUri = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME));
            getSupplierEmail = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.SUPPLIER_EMAIL));
            itemName.setText(getProductName + " by " + getSupplierName);
            itemQuantity.setText(String.valueOf(": " + getItemQuantity) + " Units");
            if (getItemQuantity == 0) {
                itemQuantity.setTextColor(Color.RED);
            }
            itemPrice.setText(String.valueOf(": " + getItemPrice));
            if (discount == 1) {
                isDiscountAvailable.setText(": " + getIsDiscountAvailable + " " + getDiscountPercentage + "%");
            } else {
                isDiscountAvailable.setText(": " + getIsDiscountAvailable);
            }
            if (productImageUri != null) {
                imageUri = Uri.parse(productImageUri);
            }
            if (imageUri != null) {
                ViewTreeObserver viewTreeObserver = imageViewProductImage.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imageViewProductImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        imageViewProductImage.setImageBitmap(getBitmapFromUri(imageUri));
                    }
                });
            } else {
                imageViewProductImage.setImageResource(R.drawable.null_image);
            }
        }
    }

    public void sendOrderIntent(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto: "));
        emailIntent.setType("text/plain");
        String[] TO = {getSupplierEmail};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ORDER REQUEST FOR " + getProductName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your Product " + getProductName + " is about to get out of stock in my store. I need you to send some units of this product as soon as possible.\n\nThank You");
        startActivity(emailIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_data_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_one_unit:
                updateQuantityRecord(getItemQuantity + 1);
                break;
            case R.id.action_subtract_one_unit:
                updateQuantityRecord(getItemQuantity - 1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void performDeleteOperation(View view) {
        boolean confirm = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you really want to delete " + getProductName + " from the database permanently? The deleted data will not be recovered.")
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        int rowsDeleted = getContentResolver().delete(productAddressUri, null, null);
                        finish();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    public void updateQuantityRecord(int getItemQuantity) {
        if (Integer.valueOf(getItemQuantity) > 0) {
            ContentValues newValues = new ContentValues();
            newValues.put(StoreContract.StoreEntry.COLUMN_ITEM_NAME, getProductName);
            newValues.put(StoreContract.StoreEntry.COLUMN_ITEM_PRICE, getItemPrice);
            newValues.put(StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY, getItemQuantity);
            newValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE, getIsDiscountAvailable);
            newValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE, getDiscountPercentage);
            newValues.put(StoreContract.StoreEntry.SUPPLIER_NAME, getSupplierName);
            newValues.put(StoreContract.StoreEntry.SUPPLIER_EMAIL, getSupplierEmail);
            newValues.put(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME, productImageUri);
            getContentResolver().update(productAddressUri, newValues, null, null);
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageViewProductImage.getWidth();
        int targetH = imageViewProductImage.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
