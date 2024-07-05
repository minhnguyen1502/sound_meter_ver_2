package com.example.exe01;

import android.app.Application;

import com.example.exe01.util.SharePrefUtils;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SharePrefUtils.init(this);

    }

}

