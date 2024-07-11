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
import com.example.exe01.ui.sound.sound_meter.database.SoundItemDAO;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;
import com.example.exe01.ui.sound.sound_meter.model.SoundItem;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailItemSoundActivity extends BaseActivity<ActivityDetailItemSoundBinding> {
    private SoundMeterDatabaseHelper dbHelper;
    private SoundItem soundItem;
    SoundItemDAO dao;
    int id;
    String title, description;
    float avg, min,max;
    long startTime, duration;
    byte[] imageBlob;


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
        binding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        id = getIntent().getIntExtra("id", 0);
        avg = getIntent().getFloatExtra("avg", 0);
        description = getIntent().getStringExtra("des");
        title = getIntent().getStringExtra("title");
        startTime = getIntent().getLongExtra("time", 0);
        duration = getIntent().getLongExtra("duration", 0);
        min = getIntent().getFloatExtra("min", 0);
        max = getIntent().getFloatExtra("max", 0);
        imageBlob = getIntent().getByteArrayExtra("img");
        binding.tvTitle.setText(title);
        binding.tvAvg.setText(String.format(Locale.getDefault(), "%.1f dB", avg));
        binding.tvMin.setText(String.format(Locale.getDefault(), "%.1f dB", min));
        binding.tvMax.setText(String.format(Locale.getDefault(), "%.1f dB", max));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        binding.tvTime.setText(sdf.format(startTime));
        String formattedDuration = formatDuration(duration);
        binding.duration.setText(formattedDuration);
        binding.description.setText(description);

        if (imageBlob != null && imageBlob.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            binding.ivChart.setImageBitmap(bitmap);
        } else {
            binding.ivChart.setImageResource(R.drawable.default_chart_image); // Placeholder image if there's no chart image
        }

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });

        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName();
            }
        });
        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRecording();
            }
        });
    }

    private void confirmDelete() {
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
                dao.deleteSoundItem(id);
                finish();
            }
        });

        dialog.show();
    }

    private String formatDuration(long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void bindView() {
        dbHelper = new SoundMeterDatabaseHelper(this);
        dao = new SoundItemDAO(this);
    }

    @Override
    public void onBack() {
        finish();
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
                    Toast.makeText(DetailItemSoundActivity.this, "Record renamed successfully", Toast.LENGTH_SHORT).show();
                    dao.updateTitle(id,newName);

                    // Update UI
                    binding.tvTitle.setText(newName);
                    title = newName;
                    dialog.dismiss();

                } else {
                    Toast.makeText(DetailItemSoundActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
    }
    private void shareRecording() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // Prepare text data to share
        StringBuilder shareText = new StringBuilder();
        shareText.append("Title: ").append(title).append("\n");
        shareText.append("Average: ").append(String.format("%.1f dB", avg)).append("\n");
        shareText.append("Min: ").append(String.format("%.1f dB", min)).append("\n");
        shareText.append("Max: ").append(String.format("%.1f dB", max)).append("\n");
        shareText.append("Start Time: ").append(startTime).append("\n");
        shareText.append("Duration: ").append(duration).append("\n");
        shareText.append("Description: ").append(description).append("\n");

        // Set text data to the intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        // Prepare image data to share (if available)
        if (imageBlob != null && imageBlob.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Sound Recording", null);
            Uri uri = Uri.parse(path);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        // Start activity to share
        startActivity(Intent.createChooser(shareIntent, "Share Sound Recording"));
    }


}