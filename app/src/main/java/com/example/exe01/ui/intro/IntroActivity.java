package com.example.exe01.ui.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityIntroBinding;
import com.example.exe01.ui.permission.PermissionActivity;
import com.example.exe01.ui.sound.SoundMainActivity;
import com.example.exe01.util.SharePrefUtils;
import com.example.exe01.util.Utils;


public class IntroActivity extends BaseActivity<ActivityIntroBinding> {
    ImageView[] dots = null;
    IntroAdapter introAdapter;

    @Override
    public ActivityIntroBinding getBinding() {
        return ActivityIntroBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        dots = new ImageView[]{binding.ivCircle01, binding.ivCircle02, binding.ivCircle03};

        introAdapter = new IntroAdapter(this);

        binding.viewPager2.setAdapter(introAdapter);

        binding.viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeContentInit(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void bindView() {
        binding.btnNext.setOnClickListener(view -> binding.viewPager2.setCurrentItem(binding.viewPager2.getCurrentItem() + 1));

        binding.btnStart.setOnClickListener(view -> goToNextScreen());
    }

    @Override
    public void onBack() {
    finishAffinity();
    }

    private void changeContentInit(int position) {
        for (int i = 0; i < 3; i++) {
            if (i == position) dots[i].setImageResource(R.drawable.ic_intro_s);
            else dots[i].setImageResource(R.drawable.ic_intro_sn);
        }

        switch (position) {
            case 0:
            case 1:
                binding.btnNext.setVisibility(View.VISIBLE);
                binding.btnStart.setVisibility(View.GONE);
                break;
            case 2:
                binding.btnNext.setVisibility(View.GONE);
                binding.btnStart.setVisibility(View.VISIBLE);
                break;
        }
    }
    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else return true;
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
    public void goToNextScreen() {
//        Utils.setFirstOpenApp(true);
        if(checkAudioPermission()&&checkLFilePermission()&&checkNotificationPermission()){
            Intent intent = new Intent(IntroActivity.this, SoundMainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(IntroActivity.this, PermissionActivity.class);
            startActivity(intent);

        }
}

    @Override
    protected void onStart() {
        super.onStart();
        changeContentInit(binding.viewPager2.getCurrentItem());
    }
}