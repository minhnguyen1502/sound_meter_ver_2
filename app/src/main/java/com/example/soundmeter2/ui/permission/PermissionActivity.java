package com.example.soundmeter2.ui.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.soundmeter2.R;
import com.example.soundmeter2.base.BaseActivity;
import com.example.soundmeter2.databinding.ActivityPermissionBinding;
import com.example.soundmeter2.databinding.DialogPermissionBinding;
import com.example.soundmeter2.databinding.DialogPermissionInHomeBinding;
import com.example.soundmeter2.ui.sound.SoundMainActivity;

public class PermissionActivity extends BaseActivity<ActivityPermissionBinding> {

    private final int REQUEST_CODE_STORAGE_PERMISSION = 124;
    private final int REQUEST_CODE_AUDIO_PERMISSION = 129;


    private int countAudio = 0;
    private int countStorage = 0;
    @Override
    public ActivityPermissionBinding getBinding() {
        return ActivityPermissionBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        binding.tvContinue.setOnClickListener(v -> {
            startActivity(new Intent(this, SoundMainActivity.class));
            finishAffinity();
        });
        binding.tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        binding.ivFile.setOnClickListener(view -> {

            if (!checkLFilePermission()) {
                if (countStorage > 1) {
                    showDialogGotoSetting(Build.VERSION.SDK_INT > 33 ? 4 : 5);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE_STORAGE_PERMISSION);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                    }
                }
            }
        });
        binding.ivAudio.setOnClickListener(view -> {
            if (!checkAudioPermission()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_AUDIO_PERMISSION);
            }
        });
    }

    @Override
    public void bindView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllPer();
    }

    @Override
    public void onBack() {
        finishAffinity();
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSwStorage();
            }

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkSwStorage();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//android 13
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO)) {
                            countStorage++;
                            if (countStorage > 1) {
                                showDialogGotoSetting(4);
                            }
                        }
                    } else {
//duoi android 13
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            countStorage++;
                            if (countStorage > 1) {
                                showDialogGotoSetting(3);
                            }
                        }
                    }
                }
            }
        }

        if (requestCode == REQUEST_CODE_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSwAudio();
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkSwAudio();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                        countAudio++;
                        if (countAudio > 1) {
                            showDialogGotoSetting(1);
                        }
                    }
                }
            }
        }
    }

    private void checkAllPer() {
        checkSwAudio();
        checkSwStorage();
    }
    private void showDialogGotoSetting(int type) {
        Dialog dialog = new Dialog(this);
//        SystemUtil.setLocale(this);
        DialogPermissionBinding bindingPer = DialogPermissionBinding.inflate(getLayoutInflater());
        dialog.setContentView(bindingPer.getRoot());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (type == 1) {
            bindingPer.tvContent.setText(R.string.content_dialog_per_1);
        } else if (type == 2) {
            bindingPer.tvContent.setText(R.string.content_dialog_per_2);
        } else if (type == 3) {
            bindingPer.tvContent.setText(R.string.content_dialog_per_3);
        } else if (type == 4) {
            bindingPer.tvContent.setText(R.string.content_dialog_per_4);
        }

        bindingPer.tvStay.setOnClickListener(v -> {
            dialog.dismiss();
        });
        bindingPer.tvAgree.setOnClickListener(v -> {

            dialog.dismiss();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void checkSwStorage() {
        if (checkLFilePermission()) {
            binding.ivFile.setImageResource(R.drawable.ic_sw_on);
            binding.ivFile.setOnTouchListener((view, motionEvent) -> true);
        } else {
            binding.ivFile.setImageResource(R.drawable.ic_sw_off);
            binding.ivFile.setOnTouchListener((view, motionEvent) -> false);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void checkSwAudio() {
        if (checkAudioPermission()) {
            binding.ivAudio.setImageResource(R.drawable.ic_sw_on);
            binding.ivAudio.setOnTouchListener((view, motionEvent) -> true);
        } else {
            binding.ivAudio.setImageResource(R.drawable.ic_sw_off);
            binding.ivAudio.setOnTouchListener((view, motionEvent) -> false);
        }
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkLFilePermission() {
        if (Build.VERSION.SDK_INT > 33) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
        }
    }
}
