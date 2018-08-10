package com.example.tresvitae.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tresvitae.bookstoreapp.data.BookContract;

/**
 * main activity which displays list of books.
 */
public class StoreInformationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = StoreInformationActivity.class.getSimpleName();
    private static final int BOOK_LOADER = 0;
    BookCursorAdapter fieldBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_information);

        // Setup FAB for open EditActivity.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreInformationActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the bookdata
        ListView bookListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        fieldBookCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(fieldBookCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(StoreInformationActivity.this, EditActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Setup example data to insert in books table.
     */
    private void insertExampleBook() {
        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.PRODUCT_NAME_COLUMN, "Malinowski - Argonauts of the Western Pacific");
        values.put(BookContract.BookEntry.PRICE_COLUMN, "29");
        values.put(BookContract.BookEntry.QUANTITY_COLUMN, 44);
        values.put(BookContract.BookEntry.SUPPLIER_NAME_COLUMN, BookContract.BookEntry.TYPE_UPS);
        values.put(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN, 34523213);

        Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all book from database and inventory UI.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from book database");
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
                Log.v(LOG_TAG, "Click on Add example data Button from onOptionsItemSelected method.");
                return true;

            case R.id.action_delete_all_entries:
                deleteAllBooks();
                Log.v(LOG_TAG, "Click on Delete all books Button from onOptionsItemSelected method.");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.PRODUCT_NAME_COLUMN,
                BookContract.BookEntry.PRICE_COLUMN,
                BookContract.BookEntry.QUANTITY_COLUMN};

        return new CursorLoader(this,
                BookContract.BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        fieldBookCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fieldBookCursorAdapter.swapCursor(null);
    }
}
