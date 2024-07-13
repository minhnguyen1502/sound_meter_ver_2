package com.example.exe01.ui.splash;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivitySplashBinding;
import com.example.exe01.ui.intro.IntroActivity;
import com.example.exe01.ui.language.LanguageActivity;
import com.example.exe01.ui.language.LanguageStartActivity;
import com.example.exe01.util.SharePrefUtils;
import com.example.exe01.util.Utils;


public class SplashActivity extends BaseActivity<ActivitySplashBinding> {


    @Override
    public ActivitySplashBinding getBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        new Handler(Looper.getMainLooper()).postDelayed(()->{
            runAfterFinish();
        },3000);

    }
    private void runAfterFinish() {
        if (!isFinishing() && !isDestroyed()) {
            Toast.makeText(this, ""+Utils.isLanguageSelected(), Toast.LENGTH_SHORT).show();

            if (!Utils.isLanguageSelected()) {
                Intent intent = new Intent(this, LanguageStartActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
                finish();
                // }
            }
        }
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
