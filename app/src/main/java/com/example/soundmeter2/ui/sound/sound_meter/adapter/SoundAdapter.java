package com.example.soundmeter2.ui.sound.sound_meter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundmeter2.R;
import com.example.soundmeter2.ui.sound.sound_meter.activity.DetailItemSoundActivity;
import com.example.soundmeter2.ui.sound.sound_meter.activity.HistoryActivity;
import com.example.soundmeter2.ui.sound.sound_meter.model.SoundItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {

    private final Context context;
    private final List<SoundItem> soundRecordings;
    private final List<Integer> selectedIds = new ArrayList<>();
    private boolean isMultipleSelectMode = false;


    public SoundAdapter(Context context, List<SoundItem> soundRecordings) {
        this.context = context;
        this.soundRecordings = soundRecordings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound_meter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoundItem recording = soundRecordings.get(position);

        holder.icShare.setVisibility(isMultipleSelectMode ? View.INVISIBLE : View.VISIBLE);
        holder.icSelectItem.setVisibility(isMultipleSelectMode ? View.VISIBLE : View.INVISIBLE);
        holder.icSelectItem.setImageResource(selectedIds.contains(recording.getId()) ? R.drawable.ic_selected_item : R.drawable.ic_select_item);

        holder.avgView.setText(String.format("%.1f", recording.getAvg()));
        holder.desView.setText(recording.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());

        holder.time.setText(sdf.format(recording.getStartTime()));
        holder.duration.setText(formatDuration(recording.getDuration()));
        holder.title.setText(recording.getTitle());

        holder.icSelectItem.setOnClickListener(v -> {
            if (selectedIds.contains(recording.getId())) {
                selectedIds.remove(Integer.valueOf(recording.getId()));
                holder.icSelectItem.setImageResource(R.drawable.ic_select_item);
            } else {
                selectedIds.add(recording.getId());
                holder.icSelectItem.setImageResource(R.drawable.ic_selected_item);
            }
            ((HistoryActivity) context).updateSelectIcons(); // Notify the activity to update icons

        });

        // Convert byte array to Bitmap and set to ImageView
        Bitmap bitmap = BitmapFactory.decodeByteArray(recording.getImage(), 0, recording.getImage().length);
        holder.chart.setImageBitmap(bitmap);

        holder.icShare.setOnClickListener(v -> shareRecording(recording));
        holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailItemSoundActivity.class);
                intent.putExtra("id", recording.getId());
                intent.putExtra("title", recording.getTitle());
                intent.putExtra("min", recording.getMin());
                intent.putExtra("max", recording.getMax());
                intent.putExtra("avg", recording.getAvg());
                intent.putExtra("des", recording.getDescription());
                intent.putExtra("duration", recording.getDuration());
                intent.putExtra("time", recording.getStartTime());
                intent.putExtra("img", recording.getImage());
                context.startActivity(intent);
        });
    }
    @SuppressLint("DefaultLocale")
    private String formatDuration(long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    @Override
    public int getItemCount() {
        return soundRecordings.size();
    }

    @SuppressLint("DefaultLocale")
    private void shareRecording(SoundItem recording) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // Prepare text data to share
        String shareText = "Title: " + recording.getTitle() + "\n" +
                "Average: " + String.format("%.1f dB", recording.getAvg()) + "\n" +
                "Min: " + String.format("%.1f dB", recording.getMin()) + "\n" +
                "Max: " + String.format("%.1f dB", recording.getMax()) + "\n" +
                "Start Time: " + recording.getStartTime() + "\n" +
                "Duration: " + recording.getDuration() + "\n" +
                "Description: " + recording.getDescription() + "\n";

        // Set text data to the intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Prepare image data to share (if available)
        if (recording.getImage() != null && recording.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(recording.getImage(), 0, recording.getImage().length);

            // Save bitmap to cache directory
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();
            File imageFile = new File(cachePath, "shared_image.png");

            try (FileOutputStream stream = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get URI for the image file using FileProvider
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", imageFile);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // Grant temporary read permission to the content URI
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // Start activity to share
        context.startActivity(Intent.createChooser(shareIntent, "Share Sound Recording"));
    }

    public List<Integer> getSelectedIds() {
        return selectedIds;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setMultipleSelectMode(boolean isMultipleSelectMode) {
        this.isMultipleSelectMode = isMultipleSelectMode;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelections() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAllItems() {
        selectedIds.clear();
        for (SoundItem item : soundRecordings) {
            selectedIds.add(item.getId());
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView avgView, time, desView, title, duration;
        public ImageView icShare, chart, icSelectItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            duration = itemView.findViewById(R.id.tv_duration);
            avgView = itemView.findViewById(R.id.tv_avg);
            desView = itemView.findViewById(R.id.tv_description);
            icShare = itemView.findViewById(R.id.ic_share);
            time = itemView.findViewById(R.id.tv_time);
            title = itemView.findViewById(R.id.tv_title);
            chart = itemView.findViewById(R.id.iv_chart);
            icSelectItem = itemView.findViewById(R.id.ic_select_item);
        }
    }
}
