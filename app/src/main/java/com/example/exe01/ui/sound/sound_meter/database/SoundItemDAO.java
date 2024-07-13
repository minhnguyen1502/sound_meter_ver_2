package com.example.exe01.ui.sound.sound_meter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exe01.ui.sound.sound_meter.model.SoundItem;

import java.util.ArrayList;
import java.util.List;

public class SoundItemDAO {
    private final SQLiteDatabase database;

    public SoundItemDAO(Context context) {
        SoundMeterDatabaseHelper dbHelper = new SoundMeterDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void deleteSoundItem(int id) {
        database.delete(SoundMeterDatabaseHelper.TABLE_NAME,
                SoundMeterDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<SoundItem> getAllSoundItems() {
        List<SoundItem> soundItems = new ArrayList<>();
        Cursor cursor = database.query(SoundMeterDatabaseHelper.TABLE_NAME,
                null, null, null, null, null, SoundMeterDatabaseHelper.COLUMN_START_TIME + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_ID));
                long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_START_TIME));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_TITLE));
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_DURATION));
                float min = cursor.getFloat(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_MIN));
                float max = cursor.getFloat(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_MAX));
                float avg = cursor.getFloat(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_AVG));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_DESCRIPTION));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_IMAGE));

                SoundItem soundItem = new SoundItem(id, startTime, title, duration, min, max, avg, description, image);
                soundItems.add(soundItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return soundItems;
    }
    public void updateTitle(int id, String newTitle) {
        ContentValues values = new ContentValues();
        values.put(SoundMeterDatabaseHelper.COLUMN_TITLE, newTitle);

        database.update(SoundMeterDatabaseHelper.TABLE_NAME, values,
                SoundMeterDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}