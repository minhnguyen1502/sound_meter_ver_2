package com.example.soundmeter2.ui.sound.setting.about;

import android.view.View;

import com.example.soundmeter2.base.BaseActivity;
import com.example.soundmeter2.databinding.ActivityAboutBinding;

public class AboutActivity extends BaseActivity<ActivityAboutBinding> {

    @Override
    public ActivityAboutBinding getBinding() {
        return  ActivityAboutBinding.inflate(getLayoutInflater());
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