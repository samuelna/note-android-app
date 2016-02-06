package com.example.smna.notes;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // to identify the intent
    private static final int REQUEST_CODE = 1;
    private CursorAdapter cursorAdapter;
    private String[] from = {DatabaseHelper.NOTE_TITLE};
    private int[] to = {R.id.textview};
    private ListView listView;


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

        // registering for context menu
        registerForContextMenu(listView);
        // update the contents and UI
        getLoaderManager().initLoader(0, null, this);

    } // onCreate

    // came with blank activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // create context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
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
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.delete_some_notes:
                // show boxes to check off from the list and delete only the checked notes
                deleteSomeNotes();
                break;
            case R.id.delete_all_notes:
                // check if list is empty
                if (cursorAdapter.getCount() == 0) {
                    // do nothing
                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    // alert dialog asking for confirmation if the user wants to delete all notes
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setMessage("Delete All Notes?");
                    alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllNotes();
                        }
                    });
                    alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // when context menu is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        // actions when selecting context menu item
        switch (id) {
            case R.id.context_edit:
                // same action as item click event
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI + "/" + id);
                intent.putExtra(Provider.NOTE_TYPE, uri);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.context_share_note:
                Toast.makeText(this, "Share test", Toast.LENGTH_SHORT).show();
                break;
            case R.id.context_delete:
                Toast.makeText(this, "Delete test", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    // delete all notes
    private void deleteAllNotes() {
        getContentResolver().delete(Provider.CONTENT_URI, null, null);
        restartLoader();
        Toast.makeText(this, "All Notes Are Deleted", Toast.LENGTH_SHORT).show();
    }

    // delete the checked notes
    private void deleteSomeNotes() {
        // set the notes in choice mode
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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
