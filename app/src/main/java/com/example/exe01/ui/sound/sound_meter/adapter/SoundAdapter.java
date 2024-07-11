package com.example.exe01.ui.sound.sound_meter.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe01.R;
import com.example.exe01.ui.sound.sound_meter.activity.DetailItemSoundActivity;
import com.example.exe01.ui.sound.sound_meter.model.SoundItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {

    private final Context context;
    private final List<SoundItem> soundRecordings;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoundItem recording = soundRecordings.get(position);

        holder.avgView.setText(String.format("%.1f dB", recording.getAvg()));
        holder.desView.setText(recording.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());

        holder.time.setText(sdf.format(recording.getStartTime()));
        holder.duration.setText(formatDuration(recording.getDuration()));
        holder.title.setText(recording.getTitle());

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
    private String formatDuration(long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    @Override
    public int getItemCount() {
        return soundRecordings.size();
    }

    private void shareRecording(SoundItem recording) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // Prepare text data to share
        StringBuilder shareText = new StringBuilder();
        shareText.append("Title: ").append(recording.getTitle()).append("\n");
        shareText.append("Average dB: ").append(String.format("%.1f dB", recording.getAvg())).append("\n");
        shareText.append("Min dB: ").append(String.format("%.1f dB", recording.getMin())).append("\n");
        shareText.append("Max dB: ").append(String.format("%.1f dB", recording.getMax())).append("\n");
        shareText.append("Start Time: ").append(recording.getStartTime()).append("\n");
        shareText.append("Duration: ").append(recording.getDuration()).append("\n");
        shareText.append("Description: ").append(recording.getDescription()).append("\n");

        // Set text data to the intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        // Prepare image data to share (if available)
        if (recording.getImage() != null && recording.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(recording.getImage(), 0, recording.getImage().length);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Sound Recording", null);
            Uri uri = Uri.parse(path);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        // Start activity to share
        context.startActivity(Intent.createChooser(shareIntent, "Share Sound Recording"));
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
