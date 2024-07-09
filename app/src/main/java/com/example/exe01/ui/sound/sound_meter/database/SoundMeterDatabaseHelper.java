package com.example.exe01.ui.sound.sound_meter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SoundMeterDatabaseHelper extends SQLiteOpenHelper {
    private final Context context;

    private static final String DATABASE_NAME = "sound_meter.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_RECORDINGS = "recordings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_MAX = "max";
    public static final String COLUMN_AVG = "avg";
    public static final String COLUMN_DES = "des";

    public static final String COLUMN_IMAGE = "image";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_RECORDINGS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_START_TIME + " INTEGER, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_MIN + " REAL, " +
                    COLUMN_MAX + " REAL, " +
                    COLUMN_AVG + " REAL, " +
                    COLUMN_DES + " TEXT," +
                    COLUMN_IMAGE + " TEXT" + // Add this line

                    ");";

    public SoundMeterDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDINGS);
        onCreate(db);
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_RECORDINGS);
    }

    public void updateTitle(int id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, newTitle);
        db.update(TABLE_RECORDINGS, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_RECORDINGS, COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor readAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_RECORDINGS;
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
