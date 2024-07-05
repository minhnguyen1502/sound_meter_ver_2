package com.example.exe01.ui.sound.setting;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivitySettingBinding;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    @Override
    public ActivitySettingBinding getBinding() {
        return ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {

    }

    @Override
    public void bindView() {

    }

    @Override
    public void onBack() {
        finish();
    }

}