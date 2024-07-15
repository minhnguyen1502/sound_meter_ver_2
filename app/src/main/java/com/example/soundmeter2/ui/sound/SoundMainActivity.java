package com.example.soundmeter2.ui.sound;

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
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.soundmeter2.R;
import com.example.soundmeter2.base.BaseActivity;
import com.example.soundmeter2.databinding.ActivitySoundMainBinding;
import com.example.soundmeter2.databinding.DialogPermissionBinding;
import com.example.soundmeter2.databinding.DialogPermissionInHomeBinding;
import com.example.soundmeter2.ui.sound.metal_detector.MetalSensorActivity;
import com.example.soundmeter2.ui.sound.setting.SettingActivity;
import com.example.soundmeter2.ui.sound.sound_meter.activity.SoundMeterActivity;

import java.util.Objects;

public class SoundMainActivity extends BaseActivity<ActivitySoundMainBinding> {
    private final int REQUEST_CODE_STORAGE_PERMISSION = 124;
    private final int REQUEST_CODE_AUDIO_PERMISSION = 129;
    private Dialog dialogPerHome;
    private DialogPermissionInHomeBinding bindingPer;
    private boolean isShowDialog = false;
    private int countStorage = 0;
    private int countAudio = 0;
    private boolean isShowDialogGoToSetting = false;
    private boolean isOpenApp = false;
    @Override
    public ActivitySoundMainBinding getBinding() {
        return ActivitySoundMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        binding.btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (checkAudioPermission() && checkLFilePermission()) {
                        startActivity(new Intent(SoundMainActivity.this, SoundMeterActivity.class));
                    } else {
                        showDialogPermissionSoundSensor();
                    }

            }
        });

        binding.ivSetting.setOnClickListener(new View.OnClickListener() {
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
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSwStorage(bindingPer);
            }

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkSwStorage(bindingPer);
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
                checkSwAudio(bindingPer);
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkSwAudio(bindingPer);
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
    private void showDialogGotoSetting(int type) {
        Dialog dialog = new Dialog(this);
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

        bindingPer.tvStay.setOnClickListener(view -> {
            dialog.dismiss();
            dialogPerHome.show(); // Show dialogPerHome again
        });
        bindingPer.tvAgree.setOnClickListener(view -> {
//ads
//            AppOpenManager.getInstance().disableAppResumeWithActivity(com.metaldetector.metalfinder.metaltracker.goldfinder.ui.MainActivity.class);

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            dialog.dismiss();
            startActivity(intent);
        });

        dialog.setOnDismissListener(dialogInterface -> isShowDialogGoToSetting = false);

        if (!isShowDialogGoToSetting) {
            dialog.show();
            dialogPerHome.dismiss(); // Hide dialogPerHome
            isShowDialogGoToSetting = true;
        }
    }
    private void showDialogPermissionSoundSensor() {
        bindingPer = DialogPermissionInHomeBinding.inflate(getLayoutInflater());
        dialogPerHome = new Dialog(this);  // Initialize dialogPerHome
        dialogPerHome.setContentView(bindingPer.getRoot());
        if (dialogPerHome.getWindow() != null) {
            dialogPerHome.getWindow().setGravity(Gravity.CENTER);
            dialogPerHome.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialogPerHome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogPerHome.setCancelable(false);
        dialogPerHome.setCanceledOnTouchOutside(false);

// check xem co quyen khong, neu co thi doi trang thai nut
        checkSwStorage(bindingPer);
        checkSwAudio(bindingPer);

        bindingPer.ivGrantAudio.setOnClickListener(view -> {
            if (!checkAudioPermission()) {
                ActivityCompat.requestPermissions(SoundMainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_AUDIO_PERMISSION);
            }
        });

        bindingPer.ivGrantFile.setOnClickListener(view -> {
            if (!checkLFilePermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }
            }
        });

        dialogPerHome.setOnDismissListener(dialogInterface -> {
            isShowDialog = false;
        });

        bindingPer.tvOk.setOnClickListener(view -> dialogPerHome.dismiss());

        if (!isShowDialog) {
            dialogPerHome.show();
            isShowDialog = true;
        }
    }
    private void checkAllPer() {
        checkSwStorage(bindingPer);
        checkSwAudio(bindingPer);
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
    @SuppressLint("ClickableViewAccessibility")
    private void checkSwStorage(DialogPermissionInHomeBinding bindingPer) {
        if (checkLFilePermission()) {
            bindingPer.ivGrantFile.setImageResource(R.drawable.ic_sw_on);
            bindingPer.ivGrantFile.setOnTouchListener((view, motionEvent) -> true);
        } else {
            bindingPer.ivGrantFile.setImageResource(R.drawable.ic_sw_off);
            bindingPer.ivGrantFile.setOnTouchListener((view, motionEvent) -> false);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void checkSwAudio(DialogPermissionInHomeBinding bindingPer) {
        if (checkAudioPermission()) {
            bindingPer.ivGrantAudio.setImageResource(R.drawable.ic_sw_on);
            bindingPer.ivGrantAudio.setOnTouchListener((view, motionEvent) -> true);
        } else {
            bindingPer.ivGrantAudio.setImageResource(R.drawable.ic_sw_off);
            bindingPer.ivGrantAudio.setOnTouchListener((view, motionEvent) -> false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllPer();
    }

    @Override
    public void bindView() {
        dialogPerHome = new Dialog(this);
        bindingPer = DialogPermissionInHomeBinding.inflate(getLayoutInflater());

    }

    @Override
    public void onBack() {
        if (!isShow) {
            confirmQuitApp();
        }
    }

    private boolean isShow = false;
    private void confirmQuitApp() {
        isShow = true;
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_exit_app);

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout cancel = dialog.findViewById(R.id.btn_cancel_quit_app);
        RelativeLayout quit = dialog.findViewById(R.id.btn_quit_app);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                dialog.dismiss();
                isShow = false;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isShow = false;
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}