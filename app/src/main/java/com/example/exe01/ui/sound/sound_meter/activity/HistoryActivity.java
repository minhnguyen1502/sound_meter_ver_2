package com.example.exe01.ui.sound.sound_meter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
    }
    @Override
    public void initView() {
       binding.ivBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBack();
           }
       });

       binding.ivSelect.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               isSelected = !isSelected; // Toggle mode

               if (isSelected) {
                   binding.ivSelect.setImageResource(R.drawable.ic_multiple_select);
                   binding.btnDelete.setVisibility(View.VISIBLE);
               } else {
                   binding.ivSelect.setImageResource(R.drawable.ic_select);
                   binding.btnDelete.setVisibility(View.GONE);

               }

               // Update icons in RecyclerView items
               adapter.updateIcons(isSelected);

//               confirmDialog();
           }
       });

       binding.btnDelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(HistoryActivity.this, "delete", Toast.LENGTH_SHORT).show();
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
        // Tải lại dữ liệu và cập nhật RecyclerView
        loadData();
    }
    @Override
    public void onBack() {
        finish();
    }
    public void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SoundMeterDatabaseHelper myDB = new SoundMeterDatabaseHelper(HistoryActivity.this);
                myDB.deleteAllData();
                //Refresh Activity
                Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}