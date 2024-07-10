package com.example.exe01.ui.permission;

import android.content.Intent;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityPermissionBinding;
import com.example.exe01.ui.home.HomeActivity;
import com.example.exe01.ui.sound.SoundMainActivity;

public class PermissionActivity extends BaseActivity<ActivityPermissionBinding> {


    @Override
    public ActivityPermissionBinding getBinding() {
        return ActivityPermissionBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {

    }

    @Override
    public void bindView() {
        binding.tvContinue.setOnClickListener(v -> {
            startActivity(new Intent(this, SoundMainActivity.class));
            finishAffinity();
        });
    }

    @Override
    public void onBack() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
