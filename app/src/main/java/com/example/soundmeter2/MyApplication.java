package com.example.soundmeter2;

import android.app.Application;

import com.example.soundmeter2.util.SharePrefUtils;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SharePrefUtils.init(this);

    }

}

