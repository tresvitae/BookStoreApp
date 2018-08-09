package com.example.tresvitae.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    // Database helper object.
    private BookDbHelper fieldBookDbHelper;

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        fieldBookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = fieldBookDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null
        String productName = values.getAsString(BookContract.BookEntry.PRODUCT_NAME_COLUMN);
        if (productName == null) {
            throw new IllegalArgumentException("Book requires a title and author's name");
        }
        Integer price = values.getAsInteger(BookContract.BookEntry.PRICE_COLUMN);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Book requires a value-price");
        }
        Integer quantity = values.getAsInteger(BookContract.BookEntry.QUANTITY_COLUMN);
        if (quantity < 0) {
            throw new IllegalArgumentException("Book requires a quantity");
        }
        Integer supplierName = values.getAsInteger(BookContract.BookEntry.SUPPLIER_NAME_COLUMN);
        if (supplierName == null || !BookContract.BookEntry.isValidSupplierName(supplierName)) {
            throw new IllegalArgumentException("Book requires valid supplier name");
        }
        Integer supplierPhoneNumber = values.getAsInteger(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN);
        if (supplierPhoneNumber < 0) {
            throw new IllegalArgumentException("Book requires real supplier phone number");
        }

        SQLiteDatabase database = fieldBookDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);

            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments.
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check the name text is not null if the PRODUCT_NAME_COLUMN key is present.
        if (values.containsKey(BookContract.BookEntry.PRODUCT_NAME_COLUMN)) {
            String productName = values.getAsString(BookContract.BookEntry.PRODUCT_NAME_COLUMN);
            if (productName == null) {
                throw new IllegalArgumentException("Book requires a title and author's name");
            }
        }

        // Check the price value is valid if PRICE_COLUMN key is present.
        if (values.containsKey(BookContract.BookEntry.PRICE_COLUMN)) {
            // Check that the price is greater than or equal to 0 amount
            Integer price = values.getAsInteger(BookContract.BookEntry.PRICE_COLUMN);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        // check the quantity value is valid if the QUANTITY_COLUMN key is present.
        if (values.containsKey(BookContract.BookEntry.QUANTITY_COLUMN)) {
            Integer quantity = values.getAsInteger(BookContract.BookEntry.QUANTITY_COLUMN);
            if (quantity < 0) {
                throw new IllegalArgumentException("Book requires a quantity");
            }
        }

        // check the supplier name value is valid if the SUPPLIER_NAME_COLUMN key is present.
        if (values.containsKey(BookContract.BookEntry.SUPPLIER_NAME_COLUMN)) {
            Integer supplierName = values.getAsInteger(BookContract.BookEntry.SUPPLIER_NAME_COLUMN);
            if (supplierName == null || !BookContract.BookEntry.isValidSupplierName(supplierName)) {
                throw new IllegalArgumentException("Book requires valid supplier name");
            }
        }

        // check the supplier phone number value is is greater than or equal to 0
        // if the SUPPLIER_PHONE_NUMBER_COLUMN key is present.
        if (values.containsKey(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN)) {
            Integer supplierPhoneNumber = values.getAsInteger(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN);
            if (supplierPhoneNumber < 0) {
                throw new IllegalArgumentException("Book requires real supplier phone number");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the bookdata.
        SQLiteDatabase database = fieldBookDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = fieldBookDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners
        // that the data at the given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
