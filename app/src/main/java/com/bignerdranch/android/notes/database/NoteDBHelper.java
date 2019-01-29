package com.bignerdranch.android.notes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bignerdranch.android.notes.database.NoteContract.*;

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notesList.db";
    public static final int DATABASE_VERSION = 1;

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NOTESLIST_TABLE = "CREATE TABLE " +
                NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_NOTESLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
