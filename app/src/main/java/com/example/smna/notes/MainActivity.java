package com.example.smna.notes;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // to identify the intent
    private static final int REQUEST_CODE = 1;
    private CursorAdapter cursorAdapter;
    private String[] from = {DatabaseHelper.NOTE_TEXT};
    private int[] to = {R.id.textview};
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // came with blank activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // floating action button goes to editor activity, adding new note
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            // go to editable activity for new note
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        // set the list of notes to the screen with adapter
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.list_item, null, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(cursorAdapter);

        // for item touch events of the note list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI + "/" + id);
                intent.putExtra(Provider.NOTE_TYPE, uri);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // update the contents and UI
        getLoaderManager().initLoader(0, null, this);

    } // onCreate


    // create new note
    private void insertNote(String txt) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.NOTE_TEXT, txt);
        Uri note_uri = getContentResolver().insert(Provider.CONTENT_URI, values);
        restartLoader();
    }


    // came with blank activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // came with blank activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // menu options in the handle bar
        switch (id) {
            case R.id.create_new_note:
                insertNote("New Note");
                break;
            case R.id.delete_all_notes:
                deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    // delete all notes
    private void deleteAllNotes() {
        getContentResolver().delete(Provider.CONTENT_URI, null, null);
        restartLoader();
        Toast.makeText(this, "All Notes Are Deleted", Toast.LENGTH_SHORT).show();
    }


    // update the content to UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    // loader needs to call this method
    // to update UI of new database changes
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }


    // create loader for cursor
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Provider.CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
