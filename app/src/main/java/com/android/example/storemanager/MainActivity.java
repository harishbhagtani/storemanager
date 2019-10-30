package com.android.example.storemanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.storemanager.data.StoreContract;
import com.android.example.storemanager.data.StoreCursorAdapter;
import com.android.example.storemanager.data.StoreDBHelper;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.android.example.storemanager.R.id.fab;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SQLiteDatabase itemDB;
    ListView lv;
    StoreCursorAdapter storeAdapter;

    //UI Elements

    ImageView sadFaceImage;
    TextView noItemText;

    public static final int BILL_LOADER_MANAGER = 1;
    public boolean isFirst = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sadFaceImage = (ImageView) findViewById(R.id.sad_face);
        noItemText = (TextView) findViewById(R.id.no_item_text);
        StoreDBHelper mStoreDBHelper = new StoreDBHelper(this);
        itemDB = mStoreDBHelper.getReadableDatabase();
        lv = (ListView) findViewById(R.id.list);
        getLoaderManager().initLoader(BILL_LOADER_MANAGER, null, this);
        storeAdapter = new StoreCursorAdapter(this, null);
        lv.setAdapter(storeAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Uri itemUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id);
                Intent openShowData = new Intent(MainActivity.this, ShowData.class);
                openShowData.setData(itemUri);
                startActivity(openShowData);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAddItemActivity = new Intent(MainActivity.this, AddData.class);
                startActivity(openAddItemActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_data:
                Intent openAddDataActivity = new Intent(MainActivity.this, AddData.class);
                startActivity(openAddDataActivity);
                break;
            case R.id.action_delete:
                final boolean confirm = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("All your save store data will be deleted and you will not be able to recover that after. Do you want to continue?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                int listItemCount = lv.getAdapter().getCount();
                                if (listItemCount == 0) {
                                } else {
                                    getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI, null, null);
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_ITEM_NAME,
                StoreContract.StoreEntry.COLUMN_ITEM_PRICE,
                StoreContract.StoreEntry.COLUMN_ITEM_QUANTITY,
                StoreContract.StoreEntry.COLUMN_DISCOUNT_APPLICABLE,
                StoreContract.StoreEntry.COLUMN_DISCOUNT_PERCENTAGE,
                StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_NAME,
                StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE_DATA,
                StoreContract.StoreEntry.SUPPLIER_NAME,
                StoreContract.StoreEntry.SUPPLIER_EMAIL};
        return new CursorLoader(this, StoreContract.StoreEntry.CONTENT_URI, projection, null, null, null);
    }


    @Override
    public void onBackPressed() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you really want to close Application? ")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        dialog.dismiss();
                        finishAffinity();
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        storeAdapter.swapCursor(cursor);
        int listItemCount = lv.getAdapter().getCount();
        if (listItemCount == 0) {
            sadFaceImage.setVisibility(View.VISIBLE);
            noItemText.setVisibility(View.VISIBLE);
            isFirst = false;
        } else {
            sadFaceImage.setVisibility(View.GONE);
            noItemText.setVisibility(View.GONE);
            if (isFirst) {
                Toast.makeText(this, "Total " + listItemCount + " Items in the list", Toast.LENGTH_SHORT).show();
                isFirst = false;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        storeAdapter.swapCursor(null);
    }


}
