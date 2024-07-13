package com.example.exe01.ui.sound.sound_meter.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityDetailItemSoundBinding;
import com.example.exe01.ui.sound.sound_meter.database.SoundItemDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class DetailItemSoundActivity extends BaseActivity<ActivityDetailItemSoundBinding> {
    SoundItemDAO dao;
    int id;
    String title, description;
    float avg, min, max;
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
        binding.tvAvg.setText(String.format(Locale.getDefault(), "%.1f", avg));
        binding.tvMin.setText(String.format(Locale.getDefault(), "%.1f", min));
        binding.tvMax.setText(String.format(Locale.getDefault(), "%.1f", max));

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

    @SuppressLint("SetTextI18n")
    private void confirmDelete() {
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
        tv_title.setText(getString(R.string.delete_the) + title + " ?");
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
                dao.deleteSoundItem(id);
                finish();
            }
        });

        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private String formatDuration(long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void bindView() {
        dao = new SoundItemDAO(this);
    }

    @Override
    public void onBack() {
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void editName() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_set_name_record);

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtName = dialog.findViewById(R.id.edt_name);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnSave = dialog.findViewById(R.id.btn_save);
        ImageView clear = dialog.findViewById(R.id.btn_clear);
        TextView tv_title = dialog.findViewById(R.id.tv_title);

        tv_title.setText(R.string.rename);
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
                    dao.updateTitle(id, newName);

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        String formattedDuration = formatDuration(duration);
        @SuppressLint("DefaultLocale") String shareText = "Title: " + title + "\n" +
                "Average: " + String.format("%.1f dB", avg) + "\n" +
                "Min: " + String.format("%.1f dB", min) + "\n" +
                "Max: " + String.format("%.1f dB", max) + "\n" +
                "Start Time: " + sdf.format(startTime) + "\n" +
                "Duration: " + formattedDuration + "\n" +
                "Description: " + description + "\n";

        // Set text data to the intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Prepare image data to share (if available)
        if (imageBlob != null && imageBlob.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

            // Save bitmap to cache directory
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs();
            File imageFile = new File(cachePath, "shared_image.png");

            try (FileOutputStream stream = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get URI for the image file using FileProvider
            Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", imageFile);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // Grant temporary read permission to the content URI
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // Start activity to share
        startActivity(Intent.createChooser(shareIntent, "Share Sound Recording"));
    }


}