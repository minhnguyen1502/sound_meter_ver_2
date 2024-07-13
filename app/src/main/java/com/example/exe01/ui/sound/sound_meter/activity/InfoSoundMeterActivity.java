package com.example.exe01.ui.sound.sound_meter.activity;

import android.view.View;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityInfoSoundMeterBinding;

public class InfoSoundMeterActivity extends BaseActivity<ActivityInfoSoundMeterBinding> {

    @Override
    public ActivityInfoSoundMeterBinding getBinding() {
        return ActivityInfoSoundMeterBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
    }

    @Override
    public void bindView() {

    }

    @Override
    public void onBack() {
        finish();
    }

}