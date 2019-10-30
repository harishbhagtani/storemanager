package com.android.example.storemanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import static android.R.attr.id;

public class StoreProvider extends ContentProvider {

    //Declaration of Database and Database helper objects

    SQLiteDatabase storeDBRead;
    StoreDBHelper storeDBObject;
    SQLiteDatabase storeDBWrite;

    //Constants for URI MAtcher

    public static final int ITEM = 101;
    public static final int ITEM_QUERY = 102;

    //Uri Matcher Declaration

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        storeDBObject = new StoreDBHelper(getContext());
        storeDBRead = storeDBObject.getReadableDatabase();
        storeDBWrite = storeDBObject.getWritableDatabase();
        sUriMatcher.addURI(StoreContract.StoreEntry.CONTENT_AUTHORITY, "/store_details/", ITEM);
        sUriMatcher.addURI(StoreContract.StoreEntry.CONTENT_AUTHORITY, "/store_details/#", ITEM_QUERY);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                cursor = storeDBRead.query(StoreContract.StoreEntry.PATH_STORE_DETAILS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_QUERY:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = storeDBRead.query(StoreContract.StoreEntry.PATH_STORE_DETAILS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Parse Uri : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case ITEM_QUERY:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                int rowsDeleted = storeDBWrite.delete(StoreContract.StoreEntry.PATH_STORE_DETAILS, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case ITEM_QUERY:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = storeDBWrite.delete(StoreContract.StoreEntry.PATH_STORE_DETAILS, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Operation Cannot Be Perfomed With Uri : " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return itemUpdate(uri, contentValues, selection, selectionArgs);
            case ITEM_QUERY:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return itemUpdate(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    public int itemUpdate(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }
        int rowUpdated = storeDBWrite.update(StoreContract.StoreEntry.PATH_STORE_DETAILS, contentValues, selection, selectionArgs);
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    public Uri insertItem(Uri uri, ContentValues values) {
        storeDBRead.insert(StoreContract.StoreEntry.PATH_STORE_DETAILS, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
}
