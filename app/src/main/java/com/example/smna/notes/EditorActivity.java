package com.example.smna.notes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private EditText title;
    // keep track of note, used for updating note
    private String noteFilter;
    private String oldText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // comes with blank activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // title inside the toolbar
        toolbar.setTitle("Note");
        setSupportActionBar(toolbar);
        // needed to go to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (EditText) findViewById(R.id.title);
        editor = (EditText)findViewById(R.id.body_of_notes);

        // get where the last intent came from
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(Provider.NOTE_TYPE);

        if (uri == null) {
            // if null then it's a new note
            action = Intent.ACTION_INSERT;
        }
        else {
            // not a new note, therefore editing
            action = Intent.ACTION_EDIT;
            noteFilter = DatabaseHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            // place the cursor to the end of text and get focus
            Cursor cursor = getContentResolver().query(uri, DatabaseHelper.ALL_COLUMNS,
                    noteFilter, null, null);

            // to the first row
            cursor.moveToFirst();
            // allows editor to hold the saved data and focus at the end
            oldText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_TEXT));
            editor.setText(oldText);
            editor.requestFocus();
        }

    } // onCreate


    // for trash icon to delete
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu when editing only; this adds items to the action bar if it is present.
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editing, menu);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // back button
            case android.R.id.home:
                finishEditing();
                break;
            // trash icon
            case R.id.delete_icon:
                deleteNote();
                break;
        }

        return true;
    }


    // if the update means no more text, then note is deleted
    // if no change, nothing
    // if added, update note
    private void finishEditing() {
        // newTxt is title of note
        String newTxt = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newTxt.length() == 0) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    insertNote(newTxt);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newTxt.length() == 0) {
                    deleteNote();
                }
                else if (oldText.equals(newTxt)) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateNote(newTxt);
                }
        }
        finish();
    }

    // delete a note in the contents provider
    private void deleteNote() {
        getContentResolver().delete(Provider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }


    // update a note to table
    private void updateNote(String txt) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_TEXT, txt);
        getContentResolver().update(Provider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    // new note
    private void insertNote(String txt) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_TEXT, txt);
        getContentResolver().insert(Provider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }


    // to save the editing note
    @Override
    public void onBackPressed() {
        finishEditing();
    }


}