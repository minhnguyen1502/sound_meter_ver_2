package com.example.exe01.ui.sound.metal_detector;

import static java.lang.Math.sqrt;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exe01.R;
import com.example.exe01.base.BaseActivity;
import com.example.exe01.databinding.ActivityMetalSensorBinding;

import java.util.ArrayList;

public class MetalSensorActivity extends BaseActivity<ActivityMetalSensorBinding> implements SensorEventListener {


    @Override
    public ActivityMetalSensorBinding getBinding() {
        return ActivityMetalSensorBinding.inflate(getLayoutInflater());
    }

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
            Log.e("MetalSensorActivity", "No Magnetometer found!");
            Toast.makeText(this, "No Magnetometer found on this device!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            setupRotationRunnable();
            handler.post(rotationRunnable);
        }
    }

    private Handler handler = new Handler();
    private Runnable rotationRunnable;

    private void setupRotationRunnable() {
        rotationRunnable = new Runnable() {
            @Override
            public void run() {
                if (!magneticFieldValues.isEmpty()) {
                    binding.metalView.refresh();
                }
                handler.postDelayed(this, 100); // Update every 100 milliseconds
            }
        };
    }

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
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private SensorManager sensorManager;
    private Sensor magnetometer;
    private float[] gravity = new float[3];
    private ArrayList<Double> magneticFieldValues = new ArrayList<>();


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý thay đổi độ chính xác
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] filteredValues = lowPassFilter(event.values.clone(), gravity);
        float x = filteredValues[0];
        float y = filteredValues[1];
        float z = filteredValues[2];
        double magnitude = sqrt(x * x + y * y + z * z);
        magneticFieldValues.add(magnitude);

        binding.metalView.setMetalValue((float) magnitude);

        String value = String.format("%.0f", magnitude);
        binding.tvValue.setText(value + "");
//        binding.metalView.refresh();
        binding.tvX.setText(String.format("%.2f", x));
        binding.tvY.setText(String.format("%.2f", y));
        binding.tvZ.setText(String.format("%.2f", z));

        updateStats();
    }

    private void updateStats() {
        if (magneticFieldValues.isEmpty()) {
            return;
        }

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double sum = 0;

        for (double value : magneticFieldValues) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
            sum += value;
        }

        double avg = sum / magneticFieldValues.size();

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

//        input.setFilters(new InputFilter[]{new InputThreshold(3, 1)});

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isShow = false;

            }
        });

//        input.setText("" + threshold);
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String numberStr = input.getText().toString();
//
//                if (numberStr.isEmpty()) {
//                    Toast.makeText(MetalSensorActivity.this, "input cannot be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                try {
//                    float number = Float.parseFloat(numberStr);
//
//                    if (number < 0 || number > 140) {
//
//                        Toast.makeText(MetalSensorActivity.this, "R.string.toast_0_140", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    threshold = number;
//
//                    SharedPreferences preferences = getSharedPreferences("sound_meter_prefs", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putFloat("threshold", threshold);
//                    editor.apply();
//
//                    Toast.makeText(MetalSensorActivity.this, "getString(R.string.threshold_set_to)" + threshold, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    isShow = false;
//                } catch (NumberFormatException e) {
//                    Toast.makeText(MetalSensorActivity.this, "getString(R.string.invalid_input)", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        dialog.show();
    }

}