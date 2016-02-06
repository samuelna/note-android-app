package com.example.smna.notes;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


public class Provider extends ContentProvider {


    static final String AUTHORITY = "com.example.smna.notes.NoteProvider";
    static final String URL = "content://" + AUTHORITY;
    static final Uri CONTENT_URI = Uri.parse(URL);

    // Constant to identify the requested operation
    static final int NOTES = 1;
    static final int NOTES_ID = 2;

    static final String NOTE_TYPE = "Note";

    // helps with content provider with many Uri
    static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "notes", NOTES);
        uriMatcher.addURI(AUTHORITY, "notes/#", NOTES_ID);
    }

    //initialize database
    SQLiteDatabase db;


    @Override
    public boolean onCreate() {
        // create and/or open database for reading and writing
        Context context = getContext();
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
        return true;
    }

    // query the given notes table
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = DatabaseHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        // return in descending order
        /*
            example of the query method arguments
            Cursor c = sqLiteDatabase.query("table1", tableColumns, whereClause, whereArgs, null, null, orderBy);
         */
        // return db.query(DatabaseHelper.TABLE_NOTES, DatabaseHelper.ALL_COLUMNS,
                // selection, null, null, null, DatabaseHelper.NOTE_CREATED + " DESC");
        return db.query(DatabaseHelper.TABLE_NOTES, DatabaseHelper.ALL_COLUMNS, selection, null, null, null, DatabaseHelper.NOTE_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // insert new data into content provider
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        return Uri.parse("notes/" + id);
    }

    // delete data from the content provider
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(DatabaseHelper.TABLE_NOTES, selection, selectionArgs);
    }

    // update the data in content provider
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(DatabaseHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}
