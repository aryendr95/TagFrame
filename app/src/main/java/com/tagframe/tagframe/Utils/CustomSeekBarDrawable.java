package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.tagframe.tagframe.R;

/**
 * Created by abhinav on 26/04/2016.
 */
public class CustomSeekBarDrawable extends Drawable{


    public CustomSeekBarDrawable()
    {

    }
    @Override
    public void draw(Canvas canvas) {
            Paint paint=new Paint();
        paint.setColor(Color.BLUE);
        Rect rect=new Rect(50, 50, 50, 50);
        canvas.drawRect(rect,paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
