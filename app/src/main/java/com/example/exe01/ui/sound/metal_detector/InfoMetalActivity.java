package com.example.exe01.ui.sound.metal_detector;

import android.view.View;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityInfoMetalBinding;

public class InfoMetalActivity extends BaseActivity<ActivityInfoMetalBinding> {


    @Override
    public ActivityInfoMetalBinding getBinding() {
        return ActivityInfoMetalBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {

        binding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
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