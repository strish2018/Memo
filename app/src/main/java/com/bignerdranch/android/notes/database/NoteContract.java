package com.bignerdranch.android.notes.database;

import android.provider.BaseColumns;

public class NoteContract {

    public NoteContract(){}

    public static final class NoteEntry implements BaseColumns{

        public static final String TABLE_NAME = "notesList";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }

}
