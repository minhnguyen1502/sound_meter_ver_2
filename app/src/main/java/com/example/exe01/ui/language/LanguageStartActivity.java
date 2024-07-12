package com.example.exe01.ui.language;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityLanguageStartBinding;
import com.example.exe01.ui.intro.IntroActivity;
import com.example.exe01.ui.language.adapter.LanguageStartAdapter;
import com.example.exe01.ui.language.model.LanguageModel;
import com.example.exe01.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageStartActivity extends BaseActivity<ActivityLanguageStartBinding> {

    List<LanguageModel> listLanguage;
    String codeLang;

    @Override
    public ActivityLanguageStartBinding getBinding() {
        return ActivityLanguageStartBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        initData();
        codeLang = Locale.getDefault().getLanguage();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageStartAdapter languageStartAdapter = new LanguageStartAdapter(listLanguage, code -> codeLang = code, this);


        languageStartAdapter.setCheck(SystemUtil.getPreLanguage(getBaseContext()));

        binding.rcvLangStart.setLayoutManager(linearLayoutManager);
        binding.rcvLangStart.setAdapter(languageStartAdapter);
    }

    @Override
    public void bindView() {
        binding.ivCheck.setOnClickListener(view -> {
            SystemUtil.saveLocale(getBaseContext(), codeLang);
            startNextActivity(IntroActivity.class, null);
            finishAffinity();
        });
    }

    @Override
    public void onBack() {
        finishAffinity();

    }

    private void initData() {
        listLanguage = new ArrayList<>();
//        String lang = Locale.getDefault().getLanguage();
        listLanguage.add(new LanguageModel("English", "en", false));
        listLanguage.add(new LanguageModel("China", "zh", false));
        listLanguage.add(new LanguageModel("French", "fr", false));
        listLanguage.add(new LanguageModel("German", "de", false));
        listLanguage.add(new LanguageModel("Hindi", "hi", false));
        listLanguage.add(new LanguageModel("Indonesia", "in", false));
        listLanguage.add(new LanguageModel("Portuguese", "pt", false));
        listLanguage.add(new LanguageModel("Spanish", "es", false));

//        for (int i = 0; i < listLanguage.size(); i++) {
//            if (listLanguage.get(i).getCode().equals(lang)) {
//                listLanguage.add(0, listLanguage.get(i));
//                listLanguage.remove(i + 1);
//            }
//        }
    }

}