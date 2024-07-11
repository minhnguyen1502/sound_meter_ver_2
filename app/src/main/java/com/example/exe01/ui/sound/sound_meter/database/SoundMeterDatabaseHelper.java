package com.example.exe01.ui.sound.sound_meter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoundMeterDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SoundMeter.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "SoundItems";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_START_TIME = "startTime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_MAX = "max";
    public static final String COLUMN_AVG = "avg";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE = "image";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_START_TIME + " INTEGER, " +
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_MIN + " REAL, " +
                    COLUMN_MAX + " REAL, " +
                    COLUMN_AVG + " REAL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_IMAGE + " BLOB);";

    public SoundMeterDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
