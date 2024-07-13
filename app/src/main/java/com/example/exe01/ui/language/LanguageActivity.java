package com.example.exe01.ui.language;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityLanguageBinding;
import com.example.exe01.ui.intro.IntroActivity;
import com.example.exe01.ui.language.adapter.LanguageAdapter;
import com.example.exe01.ui.language.model.LanguageModel;
import com.example.exe01.ui.sound.SoundMainActivity;
import com.example.exe01.ui.sound.setting.SettingActivity;
import com.example.exe01.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageActivity extends BaseActivity<ActivityLanguageBinding> {

    List<LanguageModel> listLanguage;
    String codeLang;

    @Override
    public ActivityLanguageBinding getBinding() {
        return ActivityLanguageBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        initData();
        codeLang = Locale.getDefault().getLanguage();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageAdapter languageAdapter = new LanguageAdapter(listLanguage, code -> codeLang = code, this);


        languageAdapter.setCheck(SystemUtil.getPreLanguage(getBaseContext()));

        binding.rcvLang.setLayoutManager(linearLayoutManager);
        binding.rcvLang.setAdapter(languageAdapter);
        binding.ivCheck.setOnClickListener(view -> {
            SystemUtil.saveLocale(getBaseContext(), codeLang);
            startActivity(new Intent(LanguageActivity.this, SoundMainActivity.class));
            finishAffinity();
        });
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

    private void initData() {
        listLanguage = new ArrayList<>();
        listLanguage.add(new LanguageModel("English", "en", false));
        listLanguage.add(new LanguageModel("China", "zh", false));
        listLanguage.add(new LanguageModel("French", "fr", false));
        listLanguage.add(new LanguageModel("German", "de", false));
        listLanguage.add(new LanguageModel("Hindi", "hi", false));
        listLanguage.add(new LanguageModel("Indonesia", "in", false));
        listLanguage.add(new LanguageModel("Portuguese", "pt", false));
        listLanguage.add(new LanguageModel("Spanish", "es", false));
    }

}