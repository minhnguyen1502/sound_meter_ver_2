package com.example.exe01.ui.sound.sound_meter.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exe01.R;
import com.example.exe01.ui.sound.sound_meter.activity.DetailItemSoundActivity;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.Viewholder> {
    private final Context context;
    private Cursor cursor;
    private boolean isSelected = false;
    private boolean isSelect = false;

    public SoundAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public SoundAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound_meter, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_TITLE));
        float avg = cursor.getFloat(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_AVG));
        String des = cursor.getString(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_DES));
        long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_START_TIME));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_DURATION));
        float min = cursor.getFloat(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_MIN));
        float max = cursor.getFloat(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_MAX));
//        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_IMAGE)); // Assuming you have a column for image URI

        holder.avgView.setText(String.format("%.1f", avg).replace(",", "."));
        holder.desView.setText(des);
        holder.time.setText(formatTime(startTime));
        holder.title.setText(title);

//        if (imageUri != null && !imageUri.isEmpty()) {
//// Load image using Glide
//            Glide.with(context)
//                    .load(imageUri)
//                    .into(holder.chart);
//        } else {
//            holder.chart.setImageResource(R.drawable.default_chart_image); // Set a default image if no image URI is provided
//        }

        if (isSelected) {
            holder.icShare.setImageResource(R.drawable.ic_select_item);
        } else {
            holder.icShare.setImageResource(R.drawable.ic_share);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailItemSoundActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("AVG", avg);
                intent.putExtra("TITLE", title);
                intent.putExtra("DES", des);
                intent.putExtra("START_TIME", startTime);
                intent.putExtra("DURATION", duration);
                intent.putExtra("MIN", min);
                intent.putExtra("MAX", max);
                context.startActivity(intent);
            }
        });
        holder.icShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelect = !isSelect; // Toggle isSelected state

                if (isSelect) {
                    holder.icShare.setImageResource(R.drawable.ic_selected_item);
                } else {
                    holder.icShare.setImageResource(R.drawable.ic_select_item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private String formatTime(long timeMillis) {
        // Format the time in milliseconds to a readable time string (hh:mm a)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timeMillis));
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public void updateIcons(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public TextView avgView, time;
        public TextView desView, title;
        public ImageView icShare, chart;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            avgView = itemView.findViewById(R.id.tv_avg);
            desView = itemView.findViewById(R.id.tv_description);
            icShare = itemView.findViewById(R.id.ic_share);
            time = itemView.findViewById(R.id.tv_time);
            title = itemView.findViewById(R.id.tv_title);
            chart = itemView.findViewById(R.id.iv_chart);
        }
    }
}
