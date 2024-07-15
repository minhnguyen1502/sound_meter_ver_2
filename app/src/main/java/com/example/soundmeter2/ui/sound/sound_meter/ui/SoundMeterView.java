package com.example.soundmeter2.ui.sound.sound_meter.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.soundmeter2.R;

public class SoundMeterView extends View {
    public SoundMeterView(Context context) {
        super(context);
    }

    public SoundMeterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SoundMeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w, h);
    }

    private int newHeight, newWidth;
    private final Matrix matrix = new Matrix();
    private Bitmap bitmap;
    private Paint paint;

    private void init(int height, int width) {
        Bitmap mbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_needle_sound);
        int bitmapH = mbitmap.getHeight();
        int bitmapW = mbitmap.getWidth();

        newHeight = height;
        newWidth = width;

        float scaleHeight = ((float) newHeight) / ((float) bitmapH);
        float scaleWidth = ((float) newWidth) / ((float) bitmapW);

        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(mbitmap, 0, 0, bitmapH, bitmapW, matrix, true);

        paint = new Paint();
        paint.setAntiAlias(true);

    }

    static final long ANIMATION_INTERVAL = 20;

    public void refresh() {
        postInvalidateDelayed(ANIMATION_INTERVAL);
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            matrix.setRotate(getAngle(World.dbCount), (float) newWidth / 2, newHeight * 229f / 460);
            canvas.drawBitmap(bitmap, matrix, paint);
        }
    }

    private float getAngle(float dbCount) {
        float minDb = 0;
        float maxDb = 140;

        return (dbCount - minDb)  * 280.0f / (maxDb - minDb);
    }
}
