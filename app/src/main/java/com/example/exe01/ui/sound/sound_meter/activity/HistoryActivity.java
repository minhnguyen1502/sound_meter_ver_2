package com.example.exe01.ui.sound.sound_meter.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityHistoryBinding;
import com.example.exe01.ui.sound.sound_meter.adapter.SoundAdapter;
import com.example.exe01.ui.sound.sound_meter.database.SoundItemDAO;
import com.example.exe01.ui.sound.sound_meter.model.SoundItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                isMultipleSelectMode = !isMultipleSelectMode;
                soundAdapter.setMultipleSelectMode(isMultipleSelectMode);
                binding.btnDelete.setVisibility(isMultipleSelectMode ? View.VISIBLE : View.INVISIBLE);
                if (!isMultipleSelectMode) {
                    soundAdapter.clearSelections();
                }
                updateSelectIcons();

            }
        });

        // Handle multiple select mode click
        binding.ivMultipleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCount == soundItemList.size()){
                    soundAdapter.clearSelections();

                }else {
                    soundAdapter.selectAllItems();

                }

                updateSelectIcons();

            }
        });

        // Handle delete button click
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }


    @Override
    public void bindView() {
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

    @SuppressLint("NotifyDataSetChanged")
    private void loadData() {
        soundItemList.clear();
        soundItemList.addAll(dao.getAllSoundItems());
        soundAdapter.notifyDataSetChanged();
    }
    private void deleteSelectedItems() {
        List<Integer> selectedIds = soundAdapter.getSelectedIds();
        for (int id : selectedIds) {
            dao.deleteSoundItem(id);
        }
        loadData();
        isMultipleSelectMode = false;
        soundAdapter.setMultipleSelectMode(isMultipleSelectMode);
        updateSelectIcons();
        binding.btnDelete.setVisibility(View.GONE);
    }
    @SuppressLint("SetTextI18n")
    void confirmDelete(){
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_set_name_record);

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtName = dialog.findViewById(R.id.edt_name);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnSave = dialog.findViewById(R.id.btn_save);
        ImageView clear = dialog.findViewById(R.id.btn_clear);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        ImageView line = dialog.findViewById(R.id.iv_line);

        line.setVisibility(View.INVISIBLE);
        tv_title.setText(R.string.delete_the_selected_records_from_your_history);
        edtName.setVisibility(View.GONE);
        btnSave.setText(R.string.confirm);
        clear.setVisibility(View.GONE);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    int selectedCount;
    public void updateSelectIcons() {
        selectedCount = soundAdapter.getSelectedCount();
        binding.tvHeader.setText(isMultipleSelectMode ? getString(R.string.select)+" ("+selectedCount +")" : getString(R.string.history));
        if (selectedCount >= 1) {
            binding.ivSingleSelect.setVisibility(View.INVISIBLE);
            binding.ivMultipleSelect.setVisibility(View.VISIBLE);
        } else {
            binding.ivSingleSelect.setVisibility(View.VISIBLE);
            binding.ivMultipleSelect.setVisibility(View.INVISIBLE);
        }
    }
}
