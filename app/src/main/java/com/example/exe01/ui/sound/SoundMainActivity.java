package com.example.exe01.ui.sound;

import android.content.Intent;
import android.view.View;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivitySoundMainBinding;
import com.example.exe01.ui.sound.metal_detector.MetalSensorActivity;
import com.example.exe01.ui.sound.setting.SettingActivity;
import com.example.exe01.ui.sound.sound_meter.activity.SoundMeterActivity;

public class SoundMainActivity extends BaseActivity<ActivitySoundMainBinding> {

    @Override
    public ActivitySoundMainBinding getBinding() {
        return ActivitySoundMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        binding.btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundMainActivity.this, SoundMeterActivity.class));
            }
        });

        binding.btnMetal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundMainActivity.this, MetalSensorActivity.class));
            }
        });
        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundMainActivity.this, SettingActivity.class));
            }
        });
    }

    @Override
    public void bindView() {

    }

    @Override
    public void onBack() {

    }
}