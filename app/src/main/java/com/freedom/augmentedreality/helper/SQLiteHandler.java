package com.freedom.augmentedreality.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.freedom.augmentedreality.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "android_api";

    private static final String TABLE_MARKER = "marker";
    private static final String KEY_ID = "id";
    private static final String KEY_PAGENO = "pageNo";
    private static final String KEY_CONTENT = "content";

    private static final String CREATE_MARKER_TABLE = "CREATE TABLE " + TABLE_MARKER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PAGENO + " INTEGER,"
            + KEY_CONTENT + " TEXT"
            + ")";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MARKER_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);
        onCreate(db);
    }


    public void addMarker(int pageNo, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PAGENO, pageNo);
        values.put(KEY_CONTENT, content);

        db.insert(TABLE_MARKER, null, values);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public String getContent(int pageNo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT  * FROM " + TABLE_MARKER + " WHERE " + KEY_PAGENO + " =" + String.valueOf(pageNo);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor.getString(2);
    }

    public HashMap<String, String> getAllMarkers() {
        HashMap<String, String> markers = new HashMap<String, String>();

        String selectQuery = "SELECT  * FROM " + TABLE_MARKER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                markers.put(cursor.getString(1), cursor.getString(2));

            } while (cursor.moveToNext());
        }

        return markers;
    }

}