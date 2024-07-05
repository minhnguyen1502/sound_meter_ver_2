package com.example.exe01.ui.sound.sound_meter.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivitySoundMeterBinding;
import com.example.exe01.ui.sound.sound_meter.ui.FileUtil;
import com.example.exe01.ui.sound.sound_meter.ui.Recoder;
import com.example.exe01.ui.sound.sound_meter.ui.SoundMeterView;
import com.example.exe01.ui.sound.sound_meter.ui.World;

import java.io.File;
import java.util.ArrayList;

public class SoundMeterActivity extends BaseActivity<ActivitySoundMeterBinding> {

    private Recoder recoder;
    private TextView value, max, min, avg;


    @Override
    public ActivitySoundMeterBinding getBinding() {
        return ActivitySoundMeterBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView() {

        soundView = findViewById(R.id.sound_view);
        recoder = new Recoder();
        value = binding.tvValue;
        max = binding.tvMax;
        min = binding.tvMin;
        avg = binding.tvAvg;
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
            }
        });

        binding.ivPause.setOnClickListener(new View.OnClickListener() {
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
    private boolean isPause = false;
    private float totalSoundLevel = 0.0f;
    private int soundLevelCount = 0;
    private SoundMeterView soundView;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (isPause || this.hasMessages(msgWhat)) {
                return;
            }
            volume = recoder.getMax();
            if (volume > 0 && volume < 10000) {
                float dbCount = World.setDbCount(20 * (float) (Math.log10(volume)));
                totalSoundLevel += dbCount;
                soundLevelCount++;

                float avgSoundLevel = totalSoundLevel / soundLevelCount;
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

    private void updateData(float dbCount) {
    }

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
            Toast.makeText(this,"something wrong", Toast.LENGTH_SHORT).show();
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
        recoder.deleteRecoding();
        handler.hasMessages(msgWhat);
        super.onDestroy();
    }

    public int dpToPx(float dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
    private LineChart mChart;

    private void initChart() {

        mChart = findViewById(R.id.chart);
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
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(true);
        if (isDarkModeEnabled()) {
            x.setTextColor(Color.WHITE);
        } else {
            x.setTextColor(Color.BLACK);
        }//        x.setLabelCount(4, true);
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f s", value + 1);
            }
        });

//      Thiết lập trục Y
        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(7, false);
        if (isDarkModeEnabled()) {
            y.setTextColor(Color.WHITE);
        } else {
            y.setTextColor(Color.BLACK);
        }
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setDrawGridLines(true);
        y.setTypeface(typeface);
        y.setTextSize(10f);
        y.setAxisMinimum(0);
        y.setAxisMaximum(120);
        y.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f dB", value);
            }
        });

        mChart.getAxisRight().setEnabled(false);

        // Thiết lập dữ liệu cho biểu đồ
        yVals = new ArrayList<>();
        LineDataSet set1 = new LineDataSet(yVals, "Sound Level");
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setHighlightEnabled(false);
        set1.setDrawHorizontalHighlightIndicator(false);
        if (isDarkModeEnabled()) {
            set1.setColor(Color.rgb(178, 240, 0));
        } else {
            set1.setColor(Color.rgb(14, 130, 2));
        }
        Drawable drawable;
        if (isDarkModeEnabled()) {
            drawable = ContextCompat.getDrawable(this, R.drawable.bg_chart);
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.bg_chart_light);
        }
        set1.setFillDrawable(drawable);
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

            mChart.notifyDataSetChanged();
            mChart.moveViewToX(data.getEntryCount());
        }
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
}