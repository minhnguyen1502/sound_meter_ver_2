package com.example.exe01.ui.language;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityLanguageBinding;
import com.example.exe01.ui.home.HomeActivity;
import com.example.exe01.ui.language.adapter.LanguageAdapter;
import com.example.exe01.ui.language.model.LanguageModel;
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

        binding.viewTop.tvToolBar.setText(getString(R.string.language));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageAdapter languageAdapter = new LanguageAdapter(listLanguage, code -> codeLang = code, this);


        languageAdapter.setCheck(SystemUtil.getPreLanguage(getBaseContext()));

        binding.rcvLang.setLayoutManager(linearLayoutManager);
        binding.rcvLang.setAdapter(languageAdapter);
    }

    @Override
    public void bindView() {
        binding.viewTop.ivCheck.setOnClickListener(view -> {
            SystemUtil.saveLocale(getBaseContext(), codeLang);
            startNextActivity(HomeActivity.class, null);
            finishAffinity();
        });

        binding.viewTop.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBack() {

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishThisActivity();
    }
}
