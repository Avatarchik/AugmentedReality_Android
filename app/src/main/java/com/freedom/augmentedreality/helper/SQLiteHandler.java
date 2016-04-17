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
    private static final String KEY_NAME = "name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CREATED_AT = "crated_at";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_ISET = "iset";
    private static final String KEY_FSET = "fset";
    private static final String KEY_FSET3 = "fset3";

    private static final String CREATE_MARKER_TABLE = "CREATE TABLE " + TABLE_MARKER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PAGENO + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_CREATED_AT + " TEXT,"
            + KEY_CONTENT + " TEXT,"
            + KEY_IMAGE + " TEXT,"
            + KEY_ISET + " TEXT,"
            + KEY_FSET + " TEXT,"
            + KEY_FSET3 + " TEXT"
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


    public void addMarker(int id, int pageNo, String content, String name, String username,
                          String created_at, String image, String iset, String fset, String fset3) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_ID, id);
        values.put(KEY_PAGENO, pageNo);
        values.put(KEY_CONTENT, content);
        values.put(KEY_NAME, name);
        values.put(KEY_USERNAME, username);
        values.put(KEY_CREATED_AT, created_at);
        values.put(KEY_IMAGE, image);
        values.put(KEY_ISET, iset);
        values.put(KEY_FSET, fset);
        values.put(KEY_FSET3, fset3);

        db.insert(TABLE_MARKER, null, values);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public HashMap<String, String> getAllContentMarkers() {
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

    public ArrayList<Marker> getAllMarkers() {
        ArrayList<Marker> markers = new ArrayList<Marker>();
        String selectQuery = "SELECT  * FROM " + TABLE_MARKER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Marker marker = new Marker();
                marker.setId(cursor.getInt(0));
                marker.setName(cursor.getString(2));
                marker.setUsername(cursor.getString(3));
                marker.setCreatedAt(cursor.getString(4));
                marker.setContent(cursor.getString(5));
                marker.setImage(cursor.getString(6));
                marker.setIset(cursor.getString(7));
                marker.setFset(cursor.getString(8));
                marker.setFset3(cursor.getString(9));
                markers.add(marker);
            } while (cursor.moveToNext());
        }
        return markers;
    }
}