package com.example.soundmeter2.ui.sound.setting;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.soundmeter2.R;
import com.example.soundmeter2.base.BaseActivity;
import com.example.soundmeter2.databinding.ActivitySettingBinding;
import com.example.soundmeter2.dialog.rate.IClickDialogRate;
import com.example.soundmeter2.dialog.rate.RatingDialog;
import com.example.soundmeter2.ui.language.LanguageActivity;
import com.example.soundmeter2.ui.policy.PolicyActivity;
import com.example.soundmeter2.ui.sound.setting.about.AboutActivity;
import com.example.soundmeter2.util.SharePrefUtils;
import com.example.soundmeter2.util.SystemUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    @Override
    public ActivitySettingBinding getBinding() {
        return ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {

        binding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        binding.constraintLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, LanguageActivity.class));
            }
        });
        binding.layoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
            }
        });
        if (SharePrefUtils.isRated(this)) {
            binding.layoutRate.setVisibility(View.GONE);
        }
        binding.layoutRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
            }
        });
        binding.layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
        String currentLanguageCode = SystemUtil.getPreLanguage(this);
        String currentLanguage = getLanguageNameByCode(currentLanguageCode);
        binding.tvChangeLanguage.setText(currentLanguage);
        binding.layoutPriprivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PolicyActivity.class));
            }
        });
    }

    private String getLanguageNameByCode(String code) {
        switch (code) {
            case "en":
                return "English";
            case "zh":
                return "China";
            case "fr":
                return "French";
            case "de":
                return "German";
            case "hi":
                return "Hindi";
            case "in":
                return "Indonesia";
            case "pt":
                return "Portuguese";
            case "es":
                return "Spanish";
            default:
                return "Unknown";
        }
    }

    @Override
    public void bindView() {

    }

    private void shareApp() {
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intentShare.putExtra(Intent.EXTRA_TEXT, "Download application :" + "https://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(intentShare, "Share with"));
    }

    ReviewInfo reviewInfo;
    ReviewManager manager;

    private void rateApp() {
        RatingDialog ratingDialog = new RatingDialog(SettingActivity.this, true);
        ratingDialog.init(new IClickDialogRate() {
            @Override
            public void send() {
                binding.layoutRate.setVisibility(View.GONE);
                ratingDialog.dismiss();
                String uriText = "mailto:" + SharePrefUtils.email + "?subject=" + "Review for " + SharePrefUtils.subject + "&body=" + SharePrefUtils.subject + "\nRate : " + ratingDialog.getRating() + "\nContent: ";
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.Send_Email)));
                    SharePrefUtils.forceRated(SettingActivity.this);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingActivity.this, getString(R.string.There_is_no), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void rate() {
                manager = ReviewManagerFactory.create(SettingActivity.this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewInfo = task.getResult();
                        Task<Void> flow = manager.launchReviewFlow(SettingActivity.this, reviewInfo);
                        flow.addOnSuccessListener(result -> {
                            binding.layoutRate.setVisibility(View.GONE);
                            SharePrefUtils.forceRated(SettingActivity.this);
                            ratingDialog.dismiss();
                        });
                    } else {
                        ratingDialog.dismiss();
                    }
                });
            }

            @Override
            public void later() {
                ratingDialog.dismiss();
            }

        });
        try {
            ratingDialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBack() {
        finish();
    }

}