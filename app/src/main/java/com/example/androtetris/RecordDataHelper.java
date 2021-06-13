package com.example.androtetris;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordDataHelper {
    private static final String DATABASE_NAME = "records.db";
    private static final String TABLE_NAME = "records";
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_RECORD = "record";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_RECORD = 1;

    private SQLiteDatabase db;

    public RecordDataHelper(Context context) {
        OpenHelper openHelper = new OpenHelper(context);
        db = openHelper.getWritableDatabase();
    }

    public long insert(int record) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_RECORD, record);
        return db.insert(TABLE_NAME, null, cv);
    }

    public int update(int record) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_RECORD, record);
        return db.update(TABLE_NAME, cv, COLUMN_ID + " = ?",new String[] { String.valueOf(record)});
    }

    public int select(long id) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(NUM_COLUMN_RECORD);
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RECORD + " INTEGER); ";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}