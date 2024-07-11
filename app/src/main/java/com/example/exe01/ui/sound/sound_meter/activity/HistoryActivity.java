package com.example.exe01.ui.sound.sound_meter.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityHistoryBinding;
import com.example.exe01.ui.sound.sound_meter.adapter.SoundAdapter;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;

public class HistoryActivity extends BaseActivity<ActivityHistoryBinding> {
    private SoundMeterDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private SoundAdapter adapter;
    private boolean isSelected = false;
    private boolean all = false;
    @Override
    public ActivityHistoryBinding getBinding() {
        return ActivityHistoryBinding.inflate(getLayoutInflater());
    }
    private void loadData() {
        dbHelper = new SoundMeterDatabaseHelper(this);
        Cursor cursor = dbHelper.readAllData();
        if (adapter == null) {
            adapter = new SoundAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }
        if (adapter.hasSelectedItem()) {
            binding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            binding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void initView() {
       binding.ivBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBack();
           }
       });

       binding.ivSingleSelect.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               isSelected = !isSelected;
               adapter.updateIcons(isSelected);
               if (isSelected){
                   binding.ivMultipleSelect.setVisibility(View.VISIBLE);
                   binding.btnDelete.setVisibility(View.VISIBLE);
                   binding.ivSingleSelect.setVisibility(View.GONE);
               }else {
                   binding.ivMultipleSelect.setVisibility(View.GONE);
                   binding.btnDelete.setVisibility(View.GONE);
                   binding.ivSingleSelect.setVisibility(View.VISIBLE);
               }
           }
       });

        binding.ivMultipleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all = !all; // Toggle the all flag

                if (all) {
                    adapter.selectAllItems(true); // Select all items
                } else {
                    adapter.selectAllItems(false); // Deselect all items
                }
                adapter.updateIcons(all);
            }
        });
       binding.btnDelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               confirmDelete();
           }
       });
    }


    @Override
    public void bindView() {
        dbHelper = new SoundMeterDatabaseHelper(this);
        recyclerView = findViewById(R.id.rcv_list_sound);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SoundMeterDatabaseHelper.TABLE_RECORDINGS, null, null, null, null, null, null);

        adapter = new SoundAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
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
    void confirmDelete(){
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_set_name_record);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtName = dialog.findViewById(R.id.edt_name);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnSave = dialog.findViewById(R.id.btn_save);
        ImageView clear = dialog.findViewById(R.id.btn_clear);
        TextView tv_title = dialog.findViewById(R.id.tv_title);
        ImageView line = dialog.findViewById(R.id.iv_line);

        line.setVisibility(View.INVISIBLE);
        tv_title.setText("Delete the selected records from your history ?");
        edtName.setVisibility(View.GONE);
        btnSave.setText("Confirm");
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
                // Xoá các mục đã chọn từ database
                SoundMeterDatabaseHelper myDB = new SoundMeterDatabaseHelper(HistoryActivity.this);
                myDB.deleteSelectedData();

                // Refresh activity
                Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }
}