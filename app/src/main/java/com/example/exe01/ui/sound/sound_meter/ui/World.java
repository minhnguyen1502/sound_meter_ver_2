package com.example.exe01.ui.sound.sound_meter.ui;

public class World {
    public static float dbCount = 0.0f ;
    public static float MIN = 140 ;
    public static float MAX = 0 ;
    public static float lastDB = dbCount;
    private static float mindb = 0.5f;
    private static float value = 0;
    public static float setDbCount(float mValue) {
        if (mValue > lastDB) {
            value = mValue - lastDB > mindb ? mValue - lastDB : mindb;
        } else {
            value = mValue - lastDB < -mindb ? mValue - lastDB : mindb;
        }
        dbCount = lastDB + value * 0.2f;
        lastDB = dbCount;
        if (dbCount < MIN) MIN = dbCount;
        if (dbCount > MAX) MAX = dbCount;
        return dbCount;
    }
    public static float getAvg() {
        return (MAX + MIN) / 2;
    }

    public static void reset() {
        dbCount = 0.0f;
        MIN = 80;
        MAX = 0;
        lastDB = dbCount;
    }
}
