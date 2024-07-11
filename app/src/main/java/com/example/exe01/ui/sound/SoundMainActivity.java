package com.example.exe01.ui.sound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;

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
                if (checkAudioPermission() && checkStoragePermission()) {
                    startActivity(new Intent(SoundMainActivity.this, SoundMeterActivity.class));

                }else {
//                    showDialogRequestPermission();
                }
            }
        });

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundMainActivity.this, SettingActivity.class));
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
    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
            }
            return false;
        }
    }
//    @SuppressLint("ClickableViewAccessibility")
//    private void checkSwStorage(DialogPermissionInHomeBinding bindingPer) {
//        if (checkStoragePermission()) {
//            bindingPer.swStorage.setChecked(true);
//            bindingPer.swStorage.setOnTouchListener((view, motionEvent) -> true);
//        } else {
//            bindingPer.swStorage.setChecked(false);
//            bindingPer.swStorage.setOnTouchListener((view, motionEvent) -> false);
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void checkSwAudio(DialogPermissionInHomeBinding bindingPer) {
//        if (checkAudioPermission()) {
//            bindingPer.swAudio.setChecked(true);
//            bindingPer.swAudio.setOnTouchListener((view, motionEvent) -> true);
//        } else {
//            bindingPer.swAudio.setChecked(false);
//            bindingPer.swAudio.setOnTouchListener((view, motionEvent) -> false);
//        }
//    }
//
//    private void showDialogRequestPermission() {
////        binding.nativeHome.setVisibility(View.GONE);
////        dialogPerHome = new Dialog(this);
////        SystemUtil.setLocale(this);
////        bindingPer = DialogPermissionInHomeBinding.inflate(getLayoutInflater());
//        dialogPerHome.setContentView(bindingPer.getRoot());
//
//        if (dialogPerHome.getWindow() != null) {
//            dialogPerHome.getWindow().setGravity(Gravity.CENTER);
//            dialogPerHome.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            dialogPerHome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        dialogPerHome.setCancelable(false);
//        dialogPerHome.setCanceledOnTouchOutside(false);
//
//        checkSwStorage(bindingPer);
//        checkSwAudio(bindingPer);
//
//        bindingPer.swAudio.setOnClickListener(view -> {
//            if (!checkAudioPermission()) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_AUDIO_PERMISSION);
//            }
//        });
//
//        bindingPer.swStorage.setOnClickListener(view -> {
//            if (!checkStoragePermission()) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE_STORAGE_PERMISSION);
//                } else {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
//                }
//            }
//        });
//
//        dialogPerHome.setOnDismissListener(dialogInterface -> {
//            isShowDialog = false;
//            if (IsNetWork.haveNetworkConnection(MainActivity.this) && CommonAdsApi.listIDAdsNativeAll.size() != 0 && RemoteConfig.remote_native_home) {
//                binding.nativeHome.setVisibility(View.VISIBLE);
//            } else {
//                binding.nativeHome.setVisibility(View.GONE);
//            }
//        });
//
//        bindingPer.tvOk.setOnClickListener(view -> dialogPerHome.dismiss());
//
//        if (!isShowDialog) {
//            dialogPerHome.show();
//            isShowDialog = true;
//        }
//    }

    @Override
    public void bindView() {

    }

    @Override
    public void onBack() {

    }
}