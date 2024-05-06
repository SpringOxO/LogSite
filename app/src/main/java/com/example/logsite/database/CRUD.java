package com.example.logsite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CRUD {
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;
    Context mContext;
    private static final String TAG = "CRUD";

    private static final String[] columns = {
        NotesDataBase.ID,
        NotesDataBase.TITLE,
        NotesDataBase.CONTENT,
        NotesDataBase.TIME,
        NotesDataBase.TAG
    };

    public CRUD (Context context){
        dbHandler = new NotesDataBase(context);
        mContext = context;
    }

    public void open (){
        db = dbHandler.getWritableDatabase();
    }

    public void close (){
        dbHandler.close();
    }

    public Note addNote (Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotesDataBase.TITLE, note.getTitle());
        contentValues.put(NotesDataBase.CONTENT, note.getContent());
        contentValues.put(NotesDataBase.TIME, note.getTime());
        contentValues.put(NotesDataBase.TAG, note.getTag());
        long insertId = db.insert(NotesDataBase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    public Note getNote (Long id){
        Cursor cursor = db.query(NotesDataBase.TABLE_NAME, columns, NotesDataBase.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)cursor.moveToFirst();
        Note note = new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        note.setId(id);
        return note;
    }

    public List<Note> getAllNotes (){
        Cursor cursor = db.query(NotesDataBase.TABLE_NAME, columns, null,
                null, null, null, null, null);

        List<Note> notes = new ArrayList<>();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Note note = new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                note.setId(cursor.getLong(0));
                notes.add(note);
            }
        }
//        Toast.makeText(mContext, Integer.toString(notes.size()), Toast.LENGTH_SHORT).show();
        return notes;
    }

    public int updateNote (Note note){
        Log.d(TAG, "updateNote: " + String.valueOf(note.getId()));
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotesDataBase.TITLE, note.getTitle());
        contentValues.put(NotesDataBase.CONTENT, note.getContent());
        contentValues.put(NotesDataBase.TIME, note.getTime());
        contentValues.put(NotesDataBase.TAG, note.getTag());
        return db.update(NotesDataBase.TABLE_NAME, contentValues, NotesDataBase.ID + "=?",
                new String[]{String.valueOf(note.getId())});
    }

    public void removeNote (Note note){
        db.delete(NotesDataBase.TABLE_NAME, NotesDataBase.ID + "=" + String.valueOf(note.getId()), null);
    }

    public boolean checkNoteExist (Long id){
        Cursor cursor = db.query(NotesDataBase.TABLE_NAME, columns, NotesDataBase.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        return cursor.moveToFirst();
    }
}
