package com.freedom.augmentedreality.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.freedom.augmentedreality.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "android_api";

    private static final String TABLE_MARKER = "marker";
    private static final String TABLE_USER = "user";


    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String KEY_IMAGE = "image";
    private static final String KEY_ISET = "iset";
    private static final String KEY_FSET = "fset";
    private static final String KEY_FSET3 = "fset3";
    private static final String KEY_STT = "stt";

    private static final String KEY_EMAIL = "email";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER,"
            + KEY_STT + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + "TEXT,"
            + ")";

    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_MARKER + "("
            + KEY_ID + " INTEGER,"
            + KEY_STT + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_IMAGE + " TEXT UNIQUE,"
            + KEY_ISET + " TEXT,"
            + KEY_FSET3 + " TEXT,"
            + KEY_FSET + " TEXT"
            + ")";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_TABLE_USER);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_USER);
        onCreate(db);
    }

    public void addUser(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);


        db.insert(TABLE_USER, null, values);
    }

    public void addMarker(Marker marker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, marker.getId());
        values.put(KEY_NAME, marker.getName());
        values.put(KEY_IMAGE, marker.getImage());
        values.put(KEY_ISET, marker.getIset());
        values.put(KEY_FSET, marker.getFset());
        values.put(KEY_FSET3, marker.getFset3());

        db.insert(TABLE_MARKER, null, values);
    }

    public List<Marker> getAllMarkers() {
        List<Marker> markerList = new ArrayList<Marker>();

        String selectQuery = "SELECT  * FROM " + TABLE_MARKER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Marker marker = new Marker();
                marker.setId(Integer.parseInt(cursor.getString(0)));
                marker.setName(cursor.getString(2));
                marker.setImage(cursor.getString(3));

                markerList.add(marker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return markerList;
    }

    public void deleteMarkers() {
        String selectQuery = "DELETE FROM " + TABLE_MARKER;
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery(selectQuery, null);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}