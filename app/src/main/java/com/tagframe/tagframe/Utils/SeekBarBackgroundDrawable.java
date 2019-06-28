package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.tagframe.tagframe.R;

public class SeekBarBackgroundDrawable extends Drawable {
    private Paint mPaint = new Paint();
    private float dy;

    public SeekBarBackgroundDrawable(Context ctx) {
        mPaint.setColor(Color.WHITE);
        dy = ctx.getResources().getDimension(R.dimen.one);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds().left, getBounds().centerY() - dy / 2, getBounds().right, getBounds().centerY() + dy / 2, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}