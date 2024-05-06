package com.example.logsite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NotesDataBase extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "Notes";
    public static final String ID = "_id";
    public static final String CONTENT = "content";
    public static final String TITLE = "title";
    public static final String TIME = "time";
    public static final String TAG = "tag";

    private static final String CREATE_NOTES = "create table " + TABLE_NAME +  " ("
            + ID + " integer primary key autoincrement, "
            + TITLE  + " text, "
            + CONTENT + " text, "
            + TIME + " text, "
            + TAG + " text)";

    private Context mContext;

    public NotesDataBase(@Nullable Context context) {
        super(context, "Notes", null, 1);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
