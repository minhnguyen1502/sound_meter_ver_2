package com.example.exe01.ui.sound.sound_meter.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivitySoundMeterBinding;
import com.example.exe01.ui.sound.sound_meter.database.SoundMeterDatabaseHelper;
import com.example.exe01.ui.sound.sound_meter.ui.FileUtil;
import com.example.exe01.ui.sound.sound_meter.ui.Recoder;
import com.example.exe01.ui.sound.sound_meter.ui.SoundMeterView;
import com.example.exe01.ui.sound.sound_meter.ui.World;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class SoundMeterActivity extends BaseActivity<ActivitySoundMeterBinding> {

    private Recoder recoder;
    private TextView value, max, min, avg;
    private boolean isPause = false;

    @Override
    public ActivitySoundMeterBinding getBinding() {
        return ActivitySoundMeterBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {
        startTime = System.currentTimeMillis();  // Initialize start time

        soundView = findViewById(R.id.sound_view);
        value = binding.tvValue;
        max = binding.tvMax;
        min = binding.tvMin;
        avg = binding.tvAvg;
        mChart = findViewById(R.id.chart); // Initialize mChart here

    }


    @Override
    public void bindView() {

        binding.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundMeterActivity.this, InfoSoundMeterActivity.class));
            }
        });
        binding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
                resetMeter();
            }
        });

        binding.ivReset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                binding.ivPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                isPause = false;
                resetMeter();
                startListen();

            }
        });
        binding.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();

            }
        });
        binding.ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SoundMeterActivity.this, HistoryActivity.class));
            }
        });
        binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                isPause = !isPause;
                if (isPause) {
                    pauseMeter();
                    binding.ivPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_resume, null));
                } else {
                    resumeMeter();
                    binding.ivPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, null));

                }
            }
        });
        recoder = new Recoder();
        typeface = ResourcesCompat.getFont(this, R.font.pro__400);
        initChart();

    }
    private void showSaveDialog() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_set_name_record);

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtName = dialog.findViewById(R.id.edt_name);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnSave = dialog.findViewById(R.id.btn_save);
        ImageView clear = dialog.findViewById(R.id.btn_clear);

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
                String recordingName = edtName.getText().toString().trim();
                if (!recordingName.isEmpty()) {
                    saveRecording(recordingName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(SoundMeterActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
    private void saveRecording(String title) {
        long time = System.currentTimeMillis();
//        long duration = (time - startTime) / 1000;
        float min = Float.parseFloat(binding.tvMin.getText().toString());
        float max = Float.parseFloat(binding.tvMax.getText().toString());
        float avg = Float.parseFloat(binding.tvAvg.getText().toString());
        String description = getDescription(avg);

        Bitmap chartBitmap = captureChartImage();
        byte[] image = getBytesFromBitmap(chartBitmap);

        // Save the data to SQLite
        SoundMeterDatabaseHelper dbHelper = new SoundMeterDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("startTime", time);
        values.put("duration", duration);
        values.put("min", min);
        values.put("max", max);
        values.put("avg", avg);
        values.put("description", description);
        values.put("image", image);
        long newRowId = db.insert("SoundItems", null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, getString(R.string.recording_saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  getString(R.string.error_saving_recording), Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap captureChartImage() {
        mChart.invalidate();
        mChart.setDrawingCacheEnabled(true);
        mChart.buildDrawingCache();
        Bitmap chartBitmap = Bitmap.createBitmap(mChart.getDrawingCache());
        mChart.setDrawingCacheEnabled(false);
        return chartBitmap;
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    private String getDescription(float avg) {
        if (avg >= 0 && avg < 20) {
            return getString(R.string.normal_breathing);
        } else if (avg >= 20 && avg < 30) {
            return getString(R.string.rustling_leaves_mosquito);
        } else if (avg >= 30 && avg < 40) {
            return getString(R.string.whisper_rustling_leaves);
        } else if (avg >= 40 && avg < 50) {
            return getString(R.string.moderate_stream);
        } else if (avg >= 50 && avg < 60) {
            return getString(R.string.refrigerator);
        } else if (avg >= 60 && avg < 70) {
            return getString(R.string.conversation_quite_office);
        } else if (avg >= 70 && avg < 80) {
            return getString(R.string.car_city_traffic);
        } else if (avg >= 80 && avg < 90) {
            return getString(R.string.truck_city_traffic_noise);
        } else if (avg >= 90 && avg < 100) {
            return getString(R.string.hairdryer_lawnmower);
        } else if (avg >= 100 && avg < 110) {
            return getString(R.string.helicopter_train);
        } else if (avg >= 110 && avg < 120) {
            return getString(R.string.trombone);
        } else if (avg >= 120 && avg < 130) {
            return getString(R.string.police_siren_boom_box);
        } else if (avg >= 130 && avg < 140) {
            return getString(R.string.jet_takeoff_shotgun_firing);
        } else if (avg >= 140) {
            return getString(R.string.fireworks);
        } else {
            return getString(R.string.unknown);
        }
    }


    @SuppressLint("DefaultLocale")
    private void resetMeter() {
        soundView.refresh();

        totalSoundLevel = 0.0f;
        soundLevelCount = 0;

        World.reset();
        binding.tvValue.setText(String.format("%.1f", World.dbCount).replace(",", "."));
        binding.tvMax.setText(String.format("%.1f", World.MAX).replace(",", "."));
        binding.tvMin.setText(String.format("%.1f", World.MIN).replace(",", "."));
        binding.tvAvg.setText(String.format("%.1f", World.getAvg()).replace(",", "."));
        mChart.clear();
        initChart();
        startTime = System.currentTimeMillis();  // Initialize start time

    }

    private void resumeMeter() {
        startListen();
    }

    private void pauseMeter() {
        isPause = true;
        handler.removeMessages(msgWhat);
    }

    @Override
    public void onBack() {
        finish();
    }

    private static final int msgWhat = 0x1001;
    private static final int refreshTime = 100;
    private float volume = 10000;
    private float totalSoundLevel = 0.0f;
    private int soundLevelCount = 0;
    private SoundMeterView soundView;
    float dbCount;
    float avgSoundLevel;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("DefaultLocale")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (isPause || this.hasMessages(msgWhat)) {
                return;
            }
            volume = recoder.getMax();
            if (volume > 0 && volume < 10000) {
                dbCount = World.setDbCount(20 * (float) (Math.log10(volume)));
                totalSoundLevel += dbCount;
                soundLevelCount++;

                avgSoundLevel = totalSoundLevel / soundLevelCount;
                value.setText(String.format("%.1f dB", dbCount).replace(",", "."));
                max.setText(String.format("%.1f", World.MAX).replace(",", "."));
                min.setText(String.format("%.1f", World.MIN).replace(",", "."));
                avg.setText(String.format("%.1f", avgSoundLevel).replace(",", "."));
                soundView.refresh();

                updateData(dbCount);
            }
            startListen();
        }
    };

    private void startListen() {
        if (!isPause) {
            handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
        }
    }

    public void startRecode(File mFile) {
        try {
            recoder.setmFile(mFile);
            if (recoder.startRecoding()) {
                startListen();
            } else {
                World.dbCount = 0;
                binding.soundView.refresh();
                Toast.makeText(this, "failed recoding", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "something wrong", Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        File file = FileUtil.createFile("sound_meter.amr", this);
        startRecode(file);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(msgWhat);
        recoder.deleteRecoding();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recoder.deleteRecoding();
        handler.hasMessages(msgWhat);
    }

    public int dpToPx(float dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private LineChart mChart;
    private Typeface typeface;
    private ArrayList<Entry> yVals;
    private long startTime;

    private void initChart() {

        mChart.setViewPortOffsets(dpToPx(38, this), dpToPx(10, this), 0, dpToPx(18, this));
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        // Thiết lập trục X
        XAxis x = mChart.getXAxis();
        x.setEnabled(true);
        x.setTypeface(typeface);
        x.setTextSize(10f);
        x.enableGridDashedLine(10f, 10f, 0f); // Set dashed lines
        x.setGridColor(Color.parseColor("#E4D342")); // Set grid line color
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(true);
        x.setTextColor(Color.parseColor("#E4D342"));
        x.setValueFormatter(new ValueFormatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f s", value + 1);
            }
        });

        //      Thiết lập trục Y
        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(8, false);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(true);
        y.setTextColor(Color.parseColor("#E4D342"));
        y.setTypeface(typeface);
        y.enableGridDashedLine(10f, 10f, 0f); // Set dashed lines
        y.setGridColor(Color.parseColor("#E4D342")); // Set grid line color
        y.setTextSize(10f);
        y.setAxisMinimum(0);
        y.setAxisMaximum(140);

        mChart.getAxisRight().setEnabled(false);

        // Thiết lập dữ liệu cho biểu đồ
        yVals = new ArrayList<>();
        LineDataSet set1 = new LineDataSet(yVals, "Sound Level");
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setHighlightEnabled(false);
        set1.setColor(Color.parseColor("#FFE400"));
        set1.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.bg_chart));
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });

        LineData data = new LineData(set1);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        mChart.setData(data);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();
    }
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Sound Level");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawFilled(true);
        set.setDrawCircles(false);
        set.setHighlightEnabled(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });
        return set;
    }

    long duration;
    private void updateData(float dbCount) {

        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount() * 0.1f, dbCount), 0);
            data.notifyDataChanged();
            duration = (long) (set.getEntryCount()*0.1f);
            mChart.notifyDataSetChanged();
            mChart.moveViewToX(data.getEntryCount());
//            Bitmap chartBitmap = captureChartImage();
//            chartBitmaps.add(chartBitmap);
        }
    }

//    private ArrayList<Bitmap> chartBitmaps = new ArrayList<>();
//
//    private void saveChartImagesAsVideo() {
//        // Kiểm tra xem có hình ảnh nào được chụp không
//        if (chartBitmaps.isEmpty()) {
//            return;
//        }
//
//        // Lưu hình ảnh thành video hoặc tệp hình ảnh liên tiếp
//        // Ví dụ: lưu thành các tệp PNG liên tiếp
//        for (int i = 0; i < chartBitmaps.size(); i++) {
//            saveBitmapToFile(chartBitmaps.get(i), "chart_frame_" + i + ".png");
//        }
//    }
//
//    // Phương thức để lưu Bitmap thành tệp hình ảnh
//    private void saveBitmapToFile(Bitmap bitmap, String filename) {
//        File file = new File(getExternalFilesDir(null), filename);
//        try (FileOutputStream out = new FileOutputStream(file)) {
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Lưu dưới dạng PNG
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}