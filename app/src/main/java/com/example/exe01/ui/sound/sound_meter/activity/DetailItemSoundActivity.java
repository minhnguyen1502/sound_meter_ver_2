package com.example.exe01.ui.sound.sound_meter.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;

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
        byte[] imageBlob = getIntent().getByteArrayExtra("IMG");



        binding.tvMax.setText(String.format("%.1f", max).replace(",", "."));
        binding.tvMin.setText(String.format("%.1f", min).replace(",", "."));
        binding.tvAvg.setText(String.format("%.1f", avg).replace(",", "."));
        binding.description.setText(""+ description);
        binding.duration.setText(""+ formatDuration(duration));
        binding.tvTime.setText(""+ formatTime(startTime));
        if (imageBlob != null && imageBlob.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            binding.ivChart.setImageBitmap(bitmap);
        } else {
            binding.ivChart.setImageResource(R.drawable.default_chart_image);
        }
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
        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo nội dung chia sẻ
                String shareText = "Title: " + title + "\n"
                        + "Start time: " + startTime + "\n"
                        + "Duration: " + duration + "\n"
                        + "Description: " + description + "\n"
                        + "Max: " + max + "\n"
                        + "Min: " + min + "\n"
                        + "Average: " + String.format("%.1f", avg) + "\n";

                if (imageBlob != null && imageBlob.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(DetailItemSoundActivity.this, bitmap));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                } else {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }

            }
        });
    }
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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
                    // Update database
                    SoundMeterDatabaseHelper myDB = new SoundMeterDatabaseHelper(DetailItemSoundActivity.this);
                    myDB.updateTitle(id, newName);

                    // Update UI
                    binding.tvTitle.setText(newName);
                    title = newName;

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
        tv_title.setText("Delete the "+ title +" ?");
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
                SoundMeterDatabaseHelper myDB = new SoundMeterDatabaseHelper(DetailItemSoundActivity.this);
                myDB.deleteOneRow(String.valueOf(id));
                finish();
            }
        });

        dialog.show();
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