package com.example.exe01.ui.sound.sound_meter.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exe01.R;
import com.example.exe01.ui.sound.sound_meter.activity.DetailItemSoundActivity;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;

import java.io.ByteArrayOutputStream;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.Viewholder> {
    private final Context context;
    private Cursor cursor;
    private boolean isSelected = false;
    private boolean isChoose = false;
    private final SoundMeterDatabaseHelper dbHelper;


    public SoundAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        dbHelper = new SoundMeterDatabaseHelper(context);
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
        byte[] img = cursor.getBlob(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_IMAGE));
        int selected = cursor.getInt(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_SELECTED));

        holder.avgView.setText(String.format("%.1f", avg).replace(",", "."));
        holder.desView.setText(des);
        holder.time.setText(formatTime(startTime));
        holder.title.setText(title);

        if (img != null && img.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

            holder.chart.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    holder.chart.getViewTreeObserver().removeOnPreDrawListener(this);

                    int width = holder.chart.getWidth();
                    int height = holder.chart.getHeight();

                    if (width > 0 && height > 0) {
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                        holder.chart.setImageBitmap(scaledBitmap);
                    } else {
                        holder.chart.setImageResource(R.drawable.default_chart_image);
                    }

                    return true;
                }
            });
        } else {
            holder.chart.setImageResource(R.drawable.default_chart_image);
        }

        if (isSelected) {
            holder.icShare.setVisibility(View.INVISIBLE);
            holder.icSelectItem.setVisibility(View.VISIBLE);
        } else {
            holder.icShare.setVisibility(View.VISIBLE);
            holder.icSelectItem.setVisibility(View.INVISIBLE);
        }

        if (selected == 1) {
            holder.icSelectItem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_selected_item));
        } else {
            holder.icSelectItem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_select_item));
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
                intent.putExtra("IMG", img);
                context.startActivity(intent);
            }
        });
        holder.icShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo nội dung chia sẻ
                String shareText = "Title: " + title + "\n"
                        + "Start time: " + startTime + "\n"
                        + "Duration: " + duration + "\n"
                        + "Description: " + des + "\n"
                        + "Max: " + String.format("%.1f", max) + "\n"
                        + "Min: " + String.format("%.1f", min) + "\n"
                        + "Average: " + String.format("%.1f", avg) + "\n";

                if (img != null && img.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(context, bitmap));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                } else {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            }
        });


        holder.icSelectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChoose = !isChoose;

                if (isChoose) {
                    holder.icSelectItem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_selected_item));
                    updateSelectedState(id, 1);
                } else {
                    holder.icSelectItem.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_select_item));
                    updateSelectedState(id, 0);
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

    private void updateSelectedState(int id, int selected) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SoundMeterDatabaseHelper.COLUMN_SELECTED, selected);
        db.update(SoundMeterDatabaseHelper.TABLE_RECORDINGS, cv, SoundMeterDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public boolean hasSelectedItem() {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int selected = cursor.getInt(cursor.getColumnIndexOrThrow(SoundMeterDatabaseHelper.COLUMN_SELECTED));
                if (selected == 1) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }
    public void selectAllItems(boolean select) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SoundMeterDatabaseHelper.COLUMN_SELECTED, select ? 1 : 0);
        db.update(SoundMeterDatabaseHelper.TABLE_RECORDINGS, cv, null, null);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private String formatTime(long timeMillis) {
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
        public ImageView icShare, chart, icSelectItem;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
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
