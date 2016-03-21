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

/**
 * Created by hienbx94 on 3/21/16.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_MARKER = "marker";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_ISET = "iset";
    private static final String KEY_FSET = "fset";
    private static final String KEY_FSET3 = "fset3";
    private static final String KEY_STT = "stt";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_MARKER + "("
                + KEY_ID + " INTEGER," + KEY_STT + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IMAGE + " TEXT UNIQUE," + KEY_ISET + " TEXT," + KEY_FSET3 + " TEXT,"
                + KEY_FSET + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);

        // Create tables again
        onCreate(db);
    }

    public void addMarker(Marker marker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, marker.get_id());
        values.put(KEY_NAME, marker.get_name());
        values.put(KEY_IMAGE, marker.get_image());
        values.put(KEY_ISET, marker.get_iset());
        values.put(KEY_FSET, marker.get_fset());
        values.put(KEY_FSET3, marker.get_fset3());

        db.insert(TABLE_MARKER, null, values);
        db.close();
    }

    public List<Marker> getAllMarkers() {
        List<Marker> markerList = new ArrayList<Marker>();

        String selectQuery = "SELECT  * FROM " + TABLE_MARKER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Marker marker = new Marker();
                marker.set_id(Integer.parseInt(cursor.getString(0)));
                marker.set_name(cursor.getString(2));
                marker.set_image(cursor.getString(3));

                markerList.add(marker);
            } while (cursor.moveToNext());
        }
        return markerList;
    }
}