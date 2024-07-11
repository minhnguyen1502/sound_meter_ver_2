package com.example.exe01.ui.sound.sound_meter.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityHistoryBinding;
import com.example.exe01.ui.sound.sound_meter.adapter.SoundAdapter;
import com.example.exe01.ui.sound.sound_meter.database.SoundItemDAO;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;
import com.example.exe01.ui.sound.sound_meter.model.SoundItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity<ActivityHistoryBinding> {

    private SoundAdapter soundAdapter;
    SoundItemDAO dao;
    private List<SoundItem> soundItemList;
    private boolean isMultipleSelectMode = false;

    @Override
    public ActivityHistoryBinding getBinding() {
        return ActivityHistoryBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        dao = new SoundItemDAO(this);

        // Initialize RecyclerView
        soundItemList = new ArrayList<>();
        soundAdapter = new SoundAdapter(this, soundItemList);
        binding.rcvListSound.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvListSound.setAdapter(soundAdapter);

        // Load data from the database
        loadData();

        // Handle back button click
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

        // Handle single select mode click
        binding.ivSingleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Handle multiple select mode click
        binding.ivMultipleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Handle delete button click
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void bindView() {
        // Additional view bindings if necessary
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onBack() {
        finish();
    }

    private void loadData() {
        soundItemList.clear();
        soundItemList.addAll(dao.getAllSoundItems());
        soundAdapter.notifyDataSetChanged();
    }


}
