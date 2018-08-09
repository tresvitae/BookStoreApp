package com.example.tresvitae.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "bookdata.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
                        BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BookContract.BookEntry.PRODUCT_NAME_COLUMN + " TEXT NOT NULL, " +
                        BookContract.BookEntry.PRICE_COLUMN + " INTEGER NOT NULL DEFAULT 0, " +
                        BookContract.BookEntry.QUANTITY_COLUMN + " INTEGER DEFAULT 0, " +
                        BookContract.BookEntry.SUPPLIER_NAME_COLUMN + " INTEGER DEFAULT 0, " +
                        BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN + " INTEGER)";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        Log.v(LOG_TAG, "onCreate method used which creates table.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
