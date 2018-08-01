package com.example.tresvitae.bookstoreapp.data;

import android.provider.BaseColumns;

/**
 * API Contract for the Books.
 */
public final class BookContract {

    // prevent from unnecessary actions.
    private BookContract() {
    }

    // Inner class for defines constant values for the books table.
    public static abstract class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME_COLUMN = "product_name"; //Type: TEXT NOT NULL
        public static final String PRICE_COLUMN = "price"; //Type: INTEGER NOT NULL DEFAULT 0
        public static final String QUANTITY_COLUMN = "quantity"; //Type: INTEGER DEFAULT 0
        public static final String SUPPLIER_NAME = "supplier_name"; //Type: INTEGER DEFAULT 0
        public static final String SUPPLIER_PHONE_NUMBER_COLUMN = "supplier_phone_number"; //Type: INTEGER

        /**
         * Possible type of supplier's name;
         */
        public static final int TYPE_UNKNOWN = 0;
        public static final int TYPE_DHL = 1;
        public static final int TYPE_UPS = 2;
        public static final int TYPE_ROYAL_OFFICE = 3;


    }
}


