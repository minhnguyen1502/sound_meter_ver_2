package com.example.exe01.ui.sound.sound_meter.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityDetailItemSoundBinding;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;

public class DetailItemSoundActivity extends BaseActivity<ActivityDetailItemSoundBinding> {
    int id;
    String title;
    @Override
    public ActivityDetailItemSoundBinding getBinding() {
        return ActivityDetailItemSoundBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        binding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        id = getIntent().getIntExtra("ID", 0);
        float avg = getIntent().getFloatExtra("AVG", 0);
        String description = getIntent().getStringExtra("DES");
        title = getIntent().getStringExtra("TITLE");
        long startTime = getIntent().getLongExtra("START_TIME", 0);
        long duration = getIntent().getLongExtra("DURATION", 0);
        float min = getIntent().getFloatExtra("MIN", 0);
        float max = getIntent().getFloatExtra("MAX", 0);


        binding.tvMax.setText(String.format("%.1f", max).replace(",", "."));
        binding.tvMin.setText(String.format("%.1f", min).replace(",", "."));
        binding.tvAvg.setText(String.format("%.1f", avg).replace(",", "."));
        binding.description.setText(""+ description);
        binding.duration.setText(""+ formatDuration(duration));
        binding.tvTime.setText(""+ formatTime(startTime));

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
        binding.tvTitle.setText(title);

        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName();
            }
        });
    }

    private void editName() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_set_name_record);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtName = dialog.findViewById(R.id.edt_name);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnSave = dialog.findViewById(R.id.btn_save);
        ImageView clear = dialog.findViewById(R.id.btn_clear);
        TextView tv_title = dialog.findViewById(R.id.tv_title);

        tv_title.setText("Rename");
        edtName.setText(title);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtName.setText("");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtName.getText().toString().trim();
                if (!newName.isEmpty()) {
                    // Update the title in the database
                    SoundMeterDatabaseHelper myDB = new SoundMeterDatabaseHelper(DetailItemSoundActivity.this);
                    myDB.updateTitle(id, newName);

                    // Update the title in the UI
                    binding.tvTitle.setText(newName);
                    title = newName; // Update the title variable

                    dialog.dismiss();
                    Toast.makeText(DetailItemSoundActivity.this, "Record renamed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailItemSoundActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "  + " ?");
        builder.setMessage("Are you sure you want to delete "  + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SoundMeterDatabaseHelper myDB = new SoundMeterDatabaseHelper(DetailItemSoundActivity.this);
                myDB.deleteOneRow(String.valueOf(id));
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
    @Override
    public void bindView() {

    }

    @Override
    public void onBack() {
        finish();
    }
    private String formatTime(long timeMillis) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm a", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timeMillis));
    }

    private String formatDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes + ":" + seconds;
    }
}