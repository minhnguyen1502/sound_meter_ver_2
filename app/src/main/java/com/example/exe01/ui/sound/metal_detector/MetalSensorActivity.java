package com.example.exe01.ui.sound.metal_detector;

import static java.lang.Math.sqrt;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityMetalSensorBinding;
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

import java.util.ArrayList;

public class MetalSensorActivity extends BaseActivity<ActivityMetalSensorBinding> implements SensorEventListener {


    @Override
    public ActivityMetalSensorBinding getBinding() {
        return ActivityMetalSensorBinding.inflate(getLayoutInflater());
    }
    private boolean isPause = false;
    private boolean isSpeak = false;
    @Override
    public void initView() {
        binding.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MetalSensorActivity.this, InfoMetalActivity.class));
            }
        });

        binding.ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        binding.ivThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow) {
                    openDialog();
                }
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer == null) {
            Toast.makeText(this, "No Magnetometer found on this device!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (!values.isEmpty()) {
                        binding.metalView.refresh();
                    }
                    handler.postDelayed(this, 100);
                }
            };
            handler.post(runnable);
        }
        typeface = ResourcesCompat.getFont(this, R.font.pro__400);

        initChart();
        binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPause = !isPause;
                if (isPause) {
                    pauseSensor();
                    binding.ivPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_resume, null));
                } else {
                    resumeSensor();
                    binding.ivPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, null));

                }
            }
        });
        binding.ivSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
                
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        binding.ivReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPause = false;
                isSpeak = false;
                binding.ivSound.setImageDrawable(getResources().getDrawable(R.drawable.ic_off_sound));
                binding.ivPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                if (handler != null) {
                    sensorManager.registerListener(MetalSensorActivity.this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
                    handler.post(runnable);
                }
                resetSensor();
            }
        });

    }
    private void resetSensor() {
        values.clear();
        binding.tvValue.setText("0");
        binding.tvX.setText("0.00");
        binding.tvY.setText("0.00");
        binding.tvZ.setText("0.00");
        binding.tvMax.setText("0");
        binding.tvMin.setText("0");
        binding.tvAvg.setText("0");
        mChart.clear();
        mChart.invalidate();
        initChart();
        startTime = System.currentTimeMillis();
    }
    private void playSound() {
        isSpeak = !isSpeak;
        binding.ivSound.setImageResource(isSpeak ? R.drawable.ic_on_sound : R.drawable.ic_off_sound);
        if (!isSpeak && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    private void resumeSensor() {
        startTime += (System.currentTimeMillis() - pauseTime);

        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        handler.post(runnable);
    }

    private void pauseSensor() {
        pauseTime = System.currentTimeMillis();

        sensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public void bindView() {

    }
    @Override
    public void onBack() {
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isPause) {
            if (handler != null) {
                sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
                handler.post(runnable);
            }
        }
        startTime = System.currentTimeMillis();
        SharedPreferences preferences = getSharedPreferences("metal_prefs", Context.MODE_PRIVATE);
        threshold = preferences.getFloat("threshold", 50.0f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);

    }

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private float[] gravity = new float[3];
    private ArrayList<Double> values = new ArrayList<>();


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý thay đổi độ chính xác
    }
    private static final int WINDOW_SIZE = 10;
    private ArrayList<Float> windowX = new ArrayList<>();
    private ArrayList<Float> windowY = new ArrayList<>();
    private ArrayList<Float> windowZ = new ArrayList<>();

    private float movingAverage(ArrayList<Float> window, float newValue) {
        if (window.size() >= WINDOW_SIZE) {
            window.remove(0);
        }
        window.add(newValue);

        float sum = 0;
        for (Float value : window) {
            sum += value;
        }
        return sum / window.size();
    }
    private MediaPlayer mediaPlayer;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] filteredValues = lowPassFilter(event.values.clone(), gravity);
        float x = movingAverage(windowX, filteredValues[0]);
        float y = movingAverage(windowY, filteredValues[1]);
        float z = movingAverage(windowZ, filteredValues[2]);
        double magnitude = sqrt(x * x + y * y + z * z);
        values.add(magnitude);

        binding.metalView.setMetalValue((float) magnitude);

        String value = String.format("%.0f", magnitude);
        binding.tvValue.setText(value + "");
        binding.tvX.setText(String.format("%.2f", x));
        binding.tvY.setText(String.format("%.2f", y));
        binding.tvZ.setText(String.format("%.2f", z));

        updateStats();
        updateData((float) magnitude);

        if (isSpeak && magnitude > threshold && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        } else if (isSpeak && magnitude <= threshold && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }
    private void updateStats() {
        if (values.isEmpty()) {
            return;
        }

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double sum = 0;

        for (double value : values) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
            sum += value;
        }

        double avg = sum / values.size();

        binding.tvMax.setText(String.format("%.0f", max));
        binding.tvMin.setText(String.format("%.0f", min));
        binding.tvAvg.setText(String.format("%.0f", avg));
    }

    private static final float ALPHA = 0.25f;

    private float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private boolean isShow = false;
    private float threshold = 50.0f;

    private void openDialog() {
        isShow = true;
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_threshold);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText input = dialog.findViewById(R.id.edt);
        TextView ok = dialog.findViewById(R.id.btn_ok);
        TextView cancel = dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isShow = false;

            }
        });

        input.setText("" + threshold);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberStr = input.getText().toString();

                if (numberStr.isEmpty()) {
                    Toast.makeText(MetalSensorActivity.this, "input cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    float number = Float.parseFloat(numberStr);

                    if (number < 0 || number > 6000) {

                        Toast.makeText(MetalSensorActivity.this, "from 0 to 6000", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    threshold = number;

                    SharedPreferences preferences = getSharedPreferences("metal_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putFloat("threshold", threshold);
                    editor.apply();

                    Toast.makeText(MetalSensorActivity.this, "threshold set to)" + threshold, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    isShow = false;
                } catch (NumberFormatException e) {
                    Toast.makeText(MetalSensorActivity.this, "invalid input)", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
    }

    public int dpToPx(float dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private LineChart mChart;
    private Typeface typeface;
    private ArrayList<Entry> yVals;
    private long startTime;
    private long pauseTime;


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
        x.enableGridDashedLine(10f, 10f, 0f); // Set dashed lines
        x.setGridColor(Color.parseColor("#E4D342")); // Set grid line color
        x.setAxisLineColor(Color.parseColor("#00000000"));
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(true);
        x.setTextColor(Color.parseColor("#E4D342"));
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f s", value + 1);
            }
        });

//      Thiết lập trục Y
        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(7, false);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(true);
        y.setTextColor(Color.parseColor("#E4D342"));
        y.setTypeface(typeface);
        y.enableGridDashedLine(10f, 10f, 0f); // Set dashed lines
        y.setGridColor(Color.parseColor("#E4D342")); // Set grid line color
        y.setAxisLineColor(Color.parseColor("#E4D342"));
        y.setTextSize(10f);
        y.setAxisMinimum(0);
        y.setAxisMaximum(6000);
        y.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value).replace(",", "");
            }
        });

        mChart.getAxisRight().setEnabled(false);

        // Thiết lập dữ liệu cho biểu đồ
        yVals = new ArrayList<>();
        LineDataSet set1 = new LineDataSet(yVals, "Metal");
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

    private void updateData(float value) {
        long elapsedTime = System.currentTimeMillis() - startTime;

        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(elapsedTime * 0.001f, value), 0);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Metal");
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