package com.example.tresvitae.bookstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tresvitae.bookstoreapp.data.BookContract;
import com.example.tresvitae.bookstoreapp.data.BookDbHelper;

/**
 * main activity which displays list of books.
 */
public class StoreInformationActivity extends AppCompatActivity {

    public static final String LOG_TAG = StoreInformationActivity.class.getSimpleName();

    private BookDbHelper fieldDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_information);

        // Setup FAB for open EditActivity.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreInformationActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        fieldDbHelper = new BookDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Setup example data to insert in books table.
     */
    private void insertExampleBook() {
        SQLiteDatabase sqLiteDatabase = fieldDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.PRODUCT_NAME_COLUMN, "Malinowski - Argonauts of the Western Pacific");
        values.put(BookContract.BookEntry.PRICE_COLUMN, "39");
        values.put(BookContract.BookEntry.QUANTITY_COLUMN, 4);
        values.put(BookContract.BookEntry.SUPPLIER_NAME, 3);
        values.put(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN, 340235213);

        long newRowId = sqLiteDatabase.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, "Add example data.");
    }

    /**
     * Method for display information on activity_store_information.xml
     */
    private void displayDatabaseInfo() {
        SQLiteDatabase sqLiteDatabase = fieldDbHelper.getReadableDatabase();

        // Specifies columns which will be used from books table in bookdata.db
        String[] projection = {BookContract.BookEntry._ID,
                BookContract.BookEntry.PRODUCT_NAME_COLUMN,
                BookContract.BookEntry.PRICE_COLUMN,
                BookContract.BookEntry.QUANTITY_COLUMN,
                BookContract.BookEntry.SUPPLIER_NAME,
                BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN};

        // Perform a query:
        Cursor cursor = sqLiteDatabase.query(
                BookContract.BookEntry.TABLE_NAME,
                projection, null, null,
                null, null, null);

        TextView displayView = (TextView) findViewById(R.id.main_text_view);

        try {
            /**
             * Create a header for display the books table taken from the database == bookdata.db.
             */
            displayView.setText("Please see our table " + "\"" + BookContract.BookEntry.TABLE_NAME + "\"\n\n");
            displayView.append(BookContract.BookEntry._ID + " -- " +
                    BookContract.BookEntry.PRODUCT_NAME_COLUMN + " -- " +
                    BookContract.BookEntry.PRICE_COLUMN + " -- " +
                    BookContract.BookEntry.QUANTITY_COLUMN + " -- " +
                    BookContract.BookEntry.SUPPLIER_NAME + " -- " +
                    BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN + "\n");

            //Find index of each column
            int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_NAME_COLUMN);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRICE_COLUMN);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.QUANTITY_COLUMN);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN);

            //Repeat through all returned rows in the cursor.
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
                int currentSupplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);

                // Display the values from each column from books table
                displayView.append(("\n" + currentID + " -- " +
                        currentProductName + " -- " +
                        currentPrice + " -- " +
                        currentQuantity + " -- " +
                        currentSupplierName + " -- " +
                        currentSupplierPhoneNumber));
            }
        } finally {
            // Close the cursor when reading is finished.
            cursor.close();
            Log.v(LOG_TAG, "Close the Cursor operations");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store_information, menu);
        Log.v(LOG_TAG, "onCreateOptionsMenu used.");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_example_data:
                insertExampleBook();
                displayDatabaseInfo();
                Log.v(LOG_TAG, "Click on onOptionsItemSelected method.");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
