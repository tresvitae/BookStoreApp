package com.example.tresvitae.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tresvitae.bookstoreapp.data.BookContract;

/**
 * The Adapter for a list view that uses the bookdata as data source.
 * This adapter knows how to create list items for each row of bookdata in the Cursor.
 */
public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int productNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_NAME_COLUMN);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRICE_COLUMN);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.QUANTITY_COLUMN);

        String bookFullName = cursor.getString(productNameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final String bookQuantity = cursor.getString(quantityColumnIndex);
        final Long bookId = cursor.getLong(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));

        // when the quantity is 0 the sale button is disabled, otherwise - it is enabled
        if (Integer.parseInt(bookQuantity) == 0) {
            saleButton.setEnabled(false);
        } else {
            saleButton.setEnabled(true);

        }

        productNameTextView.setText(bookFullName);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);

        // setting onClickListener for the sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(bookQuantity) - 1;
                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.QUANTITY_COLUMN, quantity);
                String selection = BookContract.BookEntry._ID + "=?";
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookId);
                String[] selectionArgs = new String[]{String.valueOf(bookId)};
                context.getContentResolver().update(currentBookUri, values, selection, selectionArgs);
            }
        });
    }
}
