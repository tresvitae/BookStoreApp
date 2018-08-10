package com.example.tresvitae.bookstoreapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tresvitae.bookstoreapp.data.BookContract;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the bookdata loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri fieldCurrentBookUri;
    /**
     * EditText fields to enter the book's name, price, quantity, supplier name and supplier's no
     */
    private EditText fieldProductNameEditText;
    private EditText fieldPriceEditText;
    private EditText fieldQuantityEditText;
    private Spinner fieldSupplierNameSpinner;
    private EditText fieldSupplierPhoneNumberEditText;
    private Button fieldIncreaseButton;
    private Button fieldDecreaseButton;
    private Button fieldCallButton;
    // Set the "Unknown type" of supplierName.
    private int fieldSupplierName = BookContract.BookEntry.TYPE_UNKNOWN;

    private boolean fieldBookHasChanged = false;

    private View.OnTouchListener fieldTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            fieldBookHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (fieldCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        fieldCurrentBookUri = intent.getData();

        if (fieldCurrentBookUri == null) {
            setTitle(getString(R.string.edit_activity_see_details));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_activity_edit_details));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        fieldProductNameEditText = (EditText) findViewById(R.id.edit_book_name);
        fieldPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        fieldQuantityEditText = (EditText) findViewById(R.id.edit_quantity_of_book);
        fieldSupplierNameSpinner = (Spinner) findViewById(R.id.edit_spinner_supplier_name);
        fieldSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        fieldDecreaseButton = (Button) findViewById(R.id.decrease_button);
        fieldIncreaseButton = (Button) findViewById(R.id.increase_button);
        fieldCallButton = (Button) findViewById(R.id.call_button);

        fieldProductNameEditText.setOnTouchListener(fieldTouchListener);
        fieldPriceEditText.setOnTouchListener(fieldTouchListener);
        fieldQuantityEditText.setOnTouchListener(fieldTouchListener);
        fieldSupplierNameSpinner.setOnTouchListener(fieldTouchListener);
        fieldSupplierPhoneNumberEditText.setOnTouchListener(fieldTouchListener);

        fieldDecreaseButton.setOnTouchListener(fieldTouchListener);
        fieldIncreaseButton.setOnTouchListener(fieldTouchListener);
        fieldCallButton.setOnTouchListener(fieldTouchListener);

        increaseButton();
        decreaseButton();
        callButton();
        setupSpinner();
    }

    // Increase value of total quantity of the product.
    private void increaseButton() {
        fieldIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(fieldQuantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(fieldQuantityEditText.getText().toString());
                }
                quantity++;
                fieldQuantityEditText.setText("" + quantity);
            }
        });
    }

    // Decrease value of total quantity of the product.
    private void decreaseButton() {
        fieldDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(fieldQuantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(fieldQuantityEditText.getText().toString());
                }
                if (quantity > 0) {
                    quantity--;
                }
                fieldQuantityEditText.setText("" + quantity);
            }
        });
    }

    //     Call to the supplier to order the product.
    private void callButton() {
        fieldCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhoneNumberString = fieldSupplierPhoneNumberEditText.getText().toString();
                // Here, thisActivity is the current activity
                if (!TextUtils.isEmpty(supplierPhoneNumberString)) {
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + supplierPhoneNumberString));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(EditActivity.this, "Permission Call Phone denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            fieldCallButton.setEnabled(true);
        } else {
            fieldCallButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fieldCallButton.setEnabled(true);
                    Toast.makeText(this, "You can call the number by clicking on the button", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter supplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_name_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        supplierNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        fieldSupplierNameSpinner.setAdapter(supplierNameSpinnerAdapter);

        // Set the integer mSelected to the constant values
        fieldSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.dhl_supplier))) {
                        fieldSupplierName = BookContract.BookEntry.TYPE_DHL;
                    } else if (selection.equals(getString(R.string.ups_supplier))) {
                        fieldSupplierName = BookContract.BookEntry.TYPE_UPS;
                    } else if (selection.equals(getString(R.string.royal_supplier)))
                        fieldSupplierName = BookContract.BookEntry.TYPE_ROYAL_OFFICE;
                } else {
                    fieldSupplierName = BookContract.BookEntry.TYPE_UNKNOWN;
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fieldSupplierName = BookContract.BookEntry.TYPE_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void getDataFromUI() {
        // Read from input fields and use trim to eliminate leading of trailing white space
        String productNameString = fieldProductNameEditText.getText().toString().trim();
        String priceString = fieldPriceEditText.getText().toString().trim();
        String quantityString = fieldQuantityEditText.getText().toString().trim();
        String supplierPhoneNumberString = fieldSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a book and check if all the fields in the editor are blank
        if (fieldCurrentBookUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierPhoneNumberString)
                && fieldSupplierName == BookContract.BookEntry.TYPE_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new book.
            return;
        }

        // Pop up toast message if any of EditText is not filled.
        if (TextUtils.isEmpty(productNameString) || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, getString(R.string.fill_in_all_in_book_failed),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where column names are the keys, and add book atributes from the editor.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.PRODUCT_NAME_COLUMN, productNameString);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(BookContract.BookEntry.PRICE_COLUMN, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BookContract.BookEntry.QUANTITY_COLUMN, quantity);
        values.put(BookContract.BookEntry.SUPPLIER_NAME_COLUMN, fieldSupplierName);

        int supplierPhoneNumber = 0;
        if (!TextUtils.isEmpty(supplierPhoneNumberString)) {
            supplierPhoneNumber = Integer.parseInt(supplierPhoneNumberString);
        }
        values.put(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN, supplierPhoneNumber);

        // Determine if this is a new or existing book by checking if fieldCurrentBookUri is null or not
        if (fieldCurrentBookUri == null) {
            // This is a new book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update book  with content URI: fieldCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because fieldCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(fieldCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                getDataFromUI();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                if (!fieldBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!fieldBookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.PRODUCT_NAME_COLUMN,
                BookContract.BookEntry.PRICE_COLUMN,
                BookContract.BookEntry.QUANTITY_COLUMN,
                BookContract.BookEntry.SUPPLIER_NAME_COLUMN,
                BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN};

        return new CursorLoader(this,
                fieldCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {

            /**
             * Find the columns of book attributes that we're interested in and extract out
             * the value from the Cursor for the given column indext and update the views
             * on the UI with set values from database = bookdata.
             */

            int productNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRODUCT_NAME_COLUMN);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.PRICE_COLUMN);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.QUANTITY_COLUMN);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NAME_COLUMN);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_PHONE_NUMBER_COLUMN);

            String productName = cursor.getString(productNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);
            int supplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);

            fieldProductNameEditText.setText(productName);
            fieldPriceEditText.setText(Integer.toString(price));
            fieldQuantityEditText.setText(Integer.toString(quantity));
            fieldSupplierPhoneNumberEditText.setText(Integer.toString(supplierPhoneNumber));

            switch (supplierName) {
                case BookContract.BookEntry.TYPE_DHL:
                    fieldSupplierNameSpinner.setSelection(1);
                    break;
                case BookContract.BookEntry.TYPE_UPS:
                    fieldSupplierNameSpinner.setSelection(2);
                    break;
                case BookContract.BookEntry.TYPE_ROYAL_OFFICE:
                    fieldSupplierNameSpinner.setSelection(3);
                    break;
                default:
                    fieldSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        fieldProductNameEditText.setText("");
        fieldPriceEditText.setText("");
        fieldQuantityEditText.setText("");
        fieldSupplierNameSpinner.setSelection(0);
        fieldSupplierPhoneNumberEditText.setText(" ");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of book in database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (fieldCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            int rowsDeleted = getContentResolver().delete(fieldCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}
