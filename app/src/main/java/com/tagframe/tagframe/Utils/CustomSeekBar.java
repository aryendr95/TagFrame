package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CustomSeekBar
        extends SeekBar
        implements OnSeekBarChangeListener {
    String TAG = "TOUCHING";
    Context mcontext;
    SeekBar seekbar;

    public CustomSeekBar(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.mcontext = paramContext;
    }

    public static Drawable createDrawable(Context paramContext) {
        ShapeDrawable localShapeDrawable1 = new ShapeDrawable();
        localShapeDrawable1.getPaint().setStyle(Style.FILL);
        localShapeDrawable1.getPaint().setColor(Color.BLUE);
        localShapeDrawable1.getPaint().setStyle(Style.STROKE);
        localShapeDrawable1.getPaint().setStrokeWidth(4.0F);
        localShapeDrawable1.getPaint().setColor(Color.WHITE);
        ShapeDrawable localShapeDrawable2 = new ShapeDrawable();
        localShapeDrawable2.getPaint().setStyle(Style.FILL);
        localShapeDrawable2.getPaint().setColor(Color.CYAN);
        return new LayerDrawable(new Drawable[]{new ClipDrawable(localShapeDrawable2, 3, 1), localShapeDrawable1});
    }

    public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean) {
        paramInt = paramSeekBar.getProgress();
        new StringBuilder().append(paramInt).append("").toString();
        paramSeekBar.setProgressDrawable(createDrawable(this.mcontext));
        Log.d(this.TAG, "Thumb is being touched");
    }

    public void onStartTrackingTouch(SeekBar paramSeekBar) {
    }

    public void onStopTrackingTouch(SeekBar paramSeekBar) {
    }

    public BitmapDrawable writeOnDrawable(int paramInt, String paramString) {
        Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), paramInt).copy(Config.ARGB_8888, true);
        Paint localPaint = new Paint();
        localPaint.setColor(Color.GRAY);
        localPaint.setTextSize(20.0F);
        new Canvas(localBitmap).drawText(paramString, 0.0F, localBitmap.getHeight() / 2, localPaint);
        return new BitmapDrawable(localBitmap);
    }

    public BitmapDrawable writeOnprogressDrawable(int paramInt, String paramString) {
        Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), paramInt).copy(Config.ARGB_8888, true);
        Paint localPaint = new Paint();
        localPaint.setColor(Color.DKGRAY);
        localPaint.setTextSize(20.0F);
        new Canvas(localBitmap).drawText(paramString, 0.0F, localBitmap.getHeight() / 2, localPaint);
        return new BitmapDrawable(localBitmap);
    }
}


/* Location:              E:\2016\Bosswebtech\TagFrame\TagFrame_1.0\classes-dex2jar.jar!\com\example\tagframeboss\Utils\CustomSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */