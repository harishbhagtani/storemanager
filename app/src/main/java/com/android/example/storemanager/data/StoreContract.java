package com.android.example.storemanager.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class StoreContract {

    public StoreContract() {
    }

    public static final class StoreEntry implements BaseColumns {

        //Path to the store database

        public static final String PATH_STORE_DETAILS = "store_details";
        public static final String CONTENT_AUTHORITY = "com.android.example.storemanager";
        public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        //Get Type

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE_DETAILS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE_DETAILS;

        //Content Uri

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_STORE_DETAILS);

        //Unique ID Number for Item Details

        public static final String _ID = BaseColumns._ID;


        //Column For Item Name

        public static final String COLUMN_ITEM_NAME = "item_name";

        //Column for item Price

        public static final String COLUMN_ITEM_PRICE = "item_price";

        //Column for item quantity

        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";

        //Column for "IS DISCOUNT AVAILABLE"
        //Possible valure : 0 - Discount Not Available 1 - Discount Available

        public static final String COLUMN_DISCOUNT_APPLICABLE = "is_discount";

        //Discount Percantage

        public static final String COLUMN_DISCOUNT_PERCENTAGE = "discount_percentage";

        //Needs to be ordered

        public static final String COLUMN_ORDER = "need_order";

        //Storing Image Names

        public static final String COLUMN_PRODUCT_IMAGE_NAME = "product_image_name";

        //STORING PRODUCT IMAGE DATA

        public static final String COLUMN_PRODUCT_IMAGE_DATA = "product_image_data";

        //Storing Supplier Name

        public static final String SUPPLIER_NAME = "supplier_name";

        //Storng Supplier Email Address for order purposes

        public static final String SUPPLIER_EMAIL = "supplier_email";


        //VALUE OF CONSTANTS

        public static final int DISCOUNT_APPLICABLE = 1;
        public static final int DISCOUNT_NOT_APPLICABLE = 0;


    }
}
