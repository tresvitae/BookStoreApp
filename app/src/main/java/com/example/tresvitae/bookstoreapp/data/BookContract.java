package com.example.tresvitae.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Books.
 */
public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.tresvitae.bookstoreapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

    // prevent from unnecessary actions.
    private BookContract() {
    }

    // Inner class for defines constant values for the books table.
    public static abstract class BookEntry implements BaseColumns {

        // Create a full URI for the class as a constant called CONTENT_URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME_COLUMN = "product_name";                    //Type: TEXT NOT NULL
        public static final String PRICE_COLUMN = "price";                                  //Type: INTEGER NOT NULL DEFAULT 0
        public static final String QUANTITY_COLUMN = "quantity";                            //Type: INTEGER DEFAULT 0
        public static final String SUPPLIER_NAME_COLUMN = "supplier_name";                         //Type: INTEGER DEFAULT 0
        public static final String SUPPLIER_PHONE_NUMBER_COLUMN = "supplier_phone_number";  //Type: INTEGER

        /**
         * Possible type of supplier's name;
         */
        public static final int TYPE_UNKNOWN = 0;
        public static final int TYPE_DHL = 1;
        public static final int TYPE_UPS = 2;
        public static final int TYPE_ROYAL_OFFICE = 3;

        /**
         * Returns whether or not the given supplier name is {@link #TYPE_UNKNOWN}, {@link #TYPE_DHL},
         * or {@link #TYPE_UPS}, or {@link #TYPE_ROYAL_OFFICE}.
         */
        public static boolean isValidSupplierName(int supplierName) {
            if (supplierName == TYPE_UNKNOWN || supplierName == TYPE_DHL ||
                    supplierName == TYPE_UPS || supplierName == TYPE_ROYAL_OFFICE) {
                return true;
            }
            return false;
        }
    }
}


