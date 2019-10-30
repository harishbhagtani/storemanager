package com.android.example.storemanager;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.example.storemanager.data.StoreContract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AddData extends AppCompatActivity {

    //All the UI elements

    EditText mEditTextItemName;
    EditText mEditTextItemPrice;
    EditText mEditTextItemQuantity;
    EditText mEditTextDiscountPercent;
    EditText mEditTextSupplierName;
    EditText mEditTextSupplierEmail;

    ImageView productImage;
    Button openImageActivity;
    View belowImageView;

    RadioGroup isDiscountAvailableOptions;
    RadioButton radioButtonDiscountAvailable;
    RadioButton radioButtonDisountNotAvailable;

    //Flag for discount Availability

    boolean isDiscountAvailable;

    //Data Holders to Hold the value provided by edit texrs

    String mProductName;
    String mSupplierName;
    String mSupplierEmail;
    int mProductPrice;
    int mProductStock;
    int mProductDiscountPercentage;
    int mDiscountAvailable;

    // Variable to store the info for whether Activity needs to be finished after pressing save button
    // Activity will be closed if all the data is entered correctly
    // If the value if Variable is equal to the number of fields then activity can destroy itself

    int itemStatus;

    //SQLite Database Holder

    SQLiteDatabase itemDatabase;

    //Content Values To add data

    ContentValues itemValues;

    //URI For Image Recieved

    Uri imageLoadedUri;
    public static final String STATE_URI = "STATE_URI";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int PICK_IMAGE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        mEditTextItemName = (EditText) findViewById(R.id.edit_text_item_name);
        mEditTextItemPrice = (EditText) findViewById(R.id.edit_text_item_price);
        mEditTextItemQuantity = (EditText) findViewById(R.id.edit_text_item_quantity);
        mEditTextDiscountPercent = (EditText) findViewById(R.id.edit_text_discount_percentage);
        mEditTextSupplierName = (EditText) findViewById(R.id.edit_text_supplier_name);
        belowImageView = (View) findViewById(R.id.view_below_image_view);
        mEditTextSupplierEmail = (EditText) findViewById(R.id.edit_text_item_supplier_email);
        isDiscountAvailableOptions = (RadioGroup) findViewById(R.id.radio_group_discount_status);
        radioButtonDiscountAvailable = (RadioButton) findViewById(R.id.radio_button_discount_available);
        radioButtonDisountNotAvailable = (RadioButton) findViewById(R.id.radio_button_discount_unavailable);
        productImage = (ImageView) findViewById(R.id.image_view_enter_product_image);
        openImageActivity = (Button) findViewById(R.id.button_add_image);
        getSupportActionBar().setTitle(R.string.string_add_product);
        itemStatus = 0;
        isDiscountAvailable = false;
        radioButtonDisountNotAvailable.setChecked(true);
        productImage.setVisibility(View.GONE);
        belowImageView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                itemValues = new ContentValues();
                if (!mEditTextItemName.getText().toString().equals("")) {
                    mProductName = mEditTextItemName.getText().toString();
                    itemStatus++;
                } else {
                    Toast.makeText(this, "Product Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    mEditTextItemName.setHintTextColor(Color.RED);
                }
                if (!mEditTextItemPrice.getText().toString().isEmpty()) {
                    if (Integer.valueOf(mEditTextItemPrice.getText().toString()) >= 0) {
                        mProductPrice = Integer.valueOf(mEditTextItemPrice.getText().toString());
                        itemStatus++;
                    } else {
                        Toast.makeText(this, "Item Price Cannot Be Negative", Toast.LENGTH_SHORT).show();
                        mEditTextItemPrice.setText("");
                        mEditTextItemPrice.setHintTextColor(Color.RED);
                        mEditTextItemPrice.setHint("Positive Item Price");
                    }
                } else {
                    Toast.makeText(this, "Price Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    mEditTextItemPrice.setHintTextColor(Color.RED);
                }
                if (!mEditTextItemQuantity.getText().toString().isEmpty()) {
                    if (Integer.valueOf(mEditTextItemQuantity.getText().toString()) > 0) {
                        mProductStock = Integer.valueOf(mEditTextItemQuantity.getText().toString());
                        itemStatus++;
                    }
                    if (Integer.valueOf(mEditTextItemQuantity.getText().toString()) == 0) {
                        mProductStock = Integer.valueOf(mEditTextItemQuantity.getText().toString());
                        Toast.makeText(this, "Item Added But Quantity Set to zero", Toast.LENGTH_SHORT).show();
                        itemStatus++;
                    }
                } else {
                    Toast.makeText(this, "Quantity Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    mEditTextItemQuantity.setHintTextColor(Color.RED);
                }
                int radioButtonPressedResourceID = isDiscountAvailableOptions.getCheckedRadioButtonId();
                switch (radioButtonPressedResourceID) {
                    case R.id.radio_button_discount_available:
                        isDiscountAvailable = true;
                        mDiscountAvailable = 1;
                        break;
                    case R.id.radio_button_discount_unavailable:
                        isDiscountAvailable = false;
                        mDiscountAvailable = 0;
                        break;
                }
                if (!mEditTextDiscountPercent.getText().toString().isEmpty()) {
                    if (Integer.valueOf(mEditTextDiscountPercent.getText().toString()) >= 0) {
                        itemStatus++;
                        mProductDiscountPercentage = Integer.valueOf(mEditTextDiscountPercent.getText().toString());
                    } else {
                        Toast.makeText(this, "Disocunt Percent Cannot Be Less than Zero", Toast.LENGTH_SHORT).show();
                        mEditTextDiscountPercent.setHintTextColor(Color.RED);
                    }
                } else {
                    if (isDiscountAvailable) {
                        Toast.makeText(this, "Set Discount to \'NO\' if no discount Available", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!mEditTextSupplierName.getText().toString().isEmpty()) {
                    itemStatus++;
                    mSupplierName = mEditTextSupplierName.getText().toString();
                } else {
                    Toast.makeText(this, "Supplier Name Cannot Be empty", Toast.LENGTH_SHORT).show();
                    mEditTextSupplierName.setHintTextColor(Color.RED);
                }
                if (!mEditTextSupplierEmail.getText().toString().isEmpty()) {
                    itemStatus++;
                    mSupplierEmail = mEditTextSupplierEmail.getText().toString();
                } else {
                    Toast.makeText(this, "Supplier Email Is Mandatory", Toast.LENGTH_SHORT).show();
                    mEditTextSupplierEmail.setHintTextColor(Color.RED);
                }
                if (itemStatus >= 5) {
                    itemValues.put(StoreContract.StoreEntry.COLUMN_ITEM_NAME, mProductName);
                    itemValues.put(StoreContract.StoreEntry.COLUMN_ITEM_PRICE, mProductPrice);
                    itemValues.put(StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY, mProductStock);
                    itemValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE, mDiscountAvailable);
                    if (isDiscountAvailable) {
                        itemValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE, mProductDiscountPercentage);
                    } else {
                        itemValues.put(StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE, 0);
                    }
                    itemValues.put(StoreContract.StoreEntry.SUPPLIER_NAME, mSupplierName);
                    itemValues.put(StoreContract.StoreEntry.SUPPLIER_EMAIL, mSupplierEmail);
                    if (imageLoadedUri != null) {
                        itemValues.put(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME, String.valueOf(imageLoadedUri));
                    }
                    Uri rowUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, itemValues);
                    long rowID = ContentUris.parseId(rowUri);
                    if (rowID == -1) {
                        Toast.makeText(this, "Error Saving the store data", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    itemStatus = 0;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDiscountAvailable(View v) {
        mEditTextDiscountPercent.setVisibility(View.VISIBLE);
    }

    public void performImageAddingOperation(View v) {
        openImageActivity.setText("CHANGE IMAGE");
        getImageUri();
        productImage.setVisibility(View.VISIBLE);
        belowImageView.setVisibility(View.VISIBLE);
    }

    public void getImageUri() {
        openImageSelector();
    }

    public void onBackPressed() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Are you sure to go back? All your form data will be deleted. Please save the form data to store it in database before closing window.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (imageLoadedUri != null)
            outState.putString(STATE_URI, imageLoadedUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) &&
                !savedInstanceState.getString(STATE_URI).equals("")) {
            imageLoadedUri = Uri.parse(savedInstanceState.getString(STATE_URI));

            ViewTreeObserver viewTreeObserver = productImage.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    productImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    productImage.setImageBitmap(getBitmapFromUri(imageLoadedUri));
                }
            });
        }
    }


    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = productImage.getWidth();
        int targetH = productImage.getHeight();

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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                imageLoadedUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + imageLoadedUri.toString());

                productImage.setImageBitmap(getBitmapFromUri(imageLoadedUri));
            }
        }
    }


    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    public void setDiscountUnavailable(View view) {
        mEditTextDiscountPercent.setVisibility(View.GONE);
    }
}
