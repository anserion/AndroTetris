//Copyright 2021 Andrey S. Ionisyan (anserion@gmail.com, asion@mail.ru)
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

//====================================================
// Simple tetris game with SQLite and openweather REST
//====================================================
// SQLite interaction routines for ITSchool pleasure
// intentionally simplified for school use in future
// database has only one row with current game record
// demonstarted methods of creating, reading and updating DB
//====================================================
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

    public int update(int record) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_RECORD, record);
        return db.update(TABLE_NAME, cv, null,null);
    }

    public int select() {
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
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
            query = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_RECORD + ") VALUES (0)";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}