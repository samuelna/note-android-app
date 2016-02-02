package com.example.smna.notes;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //database schema
    static final String DATABASE_NAME = "notes.db";
    static final int DATABASE_VERSION = 2;

    public static final String NOTE_ID = "_id"; // column id
    public static final String NOTE_TITLE = "noteTitle"; // column noteTitle
    public static final String NOTE_TEXT = "noteText"; // column noteText
    public static final String NOTE_CREATED = "noteCreated"; // column noteCreated which is the time


    public static final String[] ALL_COLUMNS = {NOTE_ID, NOTE_TITLE, NOTE_TEXT, NOTE_CREATED};


    // table columns
    public static final String TABLE_NOTES = "notes"; // title of the database table
    // when creating new notes
    /*
    (creates an empty table called notes)
            notes
            ------------------------------------------
            | id  | noteTitle |noteText  | noteCreated |
            ------------------------------------------
            |     |           |          |            |
            |     |           |          |            |
            |     |           |          |            |
            -------------------------------------------
     */
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES +
                    " (" +                                                       // definitions of SQLite data types
                                                                                // examples:    "TEXT NOT NULL", "REAL NOT NULL"
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +      // INTEGER PRIMARY KEY AUTOINCREMENT -> type is INTEGER
                                                                            // PRIMARY KEY -> the identification for the database table
                                                                            // and AUTOINCREMENT -> increments every new data
                    NOTE_TITLE + " TEXT, " +                                   // the type is text only
                    NOTE_TEXT + " TEXT, " +                                 // the type is text only
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +      // type is TEXT, default value of current timestamp
                    "); " ;


    // constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }


    // called when database_version variable changes
    // it deletes table if they exists, then create again
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
} // db open helper
