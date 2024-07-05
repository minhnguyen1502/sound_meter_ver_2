package com.example.exe01.ui.splash;

import android.os.Handler;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivitySplashBinding;
import com.example.exe01.ui.language.LanguageStartActivity;
import com.example.exe01.util.SharePrefUtils;


public class SplashActivity extends BaseActivity<ActivitySplashBinding> {


    @Override
    public ActivitySplashBinding getBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        SharePrefUtils.increaseCountOpenApp(this);

        new Handler().postDelayed(() -> {
            startNextActivity(LanguageStartActivity.class, null);
            finishAffinity();
        }, 3000);

    }


    @Override
    public void bindView() {

    }

    @Override
    public void onBack() {

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}
