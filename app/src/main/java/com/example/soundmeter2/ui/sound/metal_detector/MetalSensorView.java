package com.example.soundmeter2.ui.sound.metal_detector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.soundmeter2.R;

public class MetalSensorView extends View {
    public MetalSensorView(Context context) {
        super(context);
    }

    public MetalSensorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MetalSensorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w,h);
    }
    private int newWidth, newHeight;
    private final Matrix matrix = new Matrix();
    private Bitmap indiBitmap;
    private Paint paint = new Paint();

    private void init(int width, int height){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_needle);
        int bitmapH = bitmap.getHeight();
        int bitmapW= bitmap.getWidth();

        newHeight = height;
        newWidth = width;

        float scaleWidth = ((float) newWidth) / ((float) bitmapW);
        float scaleHeight = ((float) newHeight) / ((float) bitmapH);
        matrix.postScale(scaleWidth, scaleHeight);
        indiBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapW, bitmapH, matrix, true);

        paint = new Paint();
        paint.setTextSize(40);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
     }

    static final long ANIMATION_INTERVAL = 20;
    public void refresh() {
        postInvalidateDelayed(ANIMATION_INTERVAL);
    }

    private float metalValue = 0;
    public void setMetalValue(float metalValue) {
        if (metalValue > 6000) {
            metalValue = 6000;
        }
        this.metalValue = metalValue;
        refresh();
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (indiBitmap != null) {
            matrix.setRotate(getAngle(metalValue), (float) newWidth / 2, newHeight * 229f / 460);
            canvas.drawBitmap(indiBitmap, matrix, paint);
        }
    }

    private float getAngle(float value) {
        float min = 0;
        float max = 6000;

        return (value - min) * 205f / (max - min);
    }
}
