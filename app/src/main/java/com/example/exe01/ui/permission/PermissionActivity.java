package com.example.exe01.ui.permission;

import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityPermissionBinding;
import com.example.exe01.ui.home.HomeActivity;

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
            startNextActivity(HomeActivity.class, null);
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
