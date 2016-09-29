package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SeekBarProgressDrawable extends Drawable {

    private Paint mPaint = new Paint();
    Paint blue=new Paint();
    private float dy;
    ArrayList<FrameList_Model> datamap;
    int totalduration;


    public SeekBarProgressDrawable(Context ctx,ArrayList<FrameList_Model> datamap,int total) {

        blue.setColor(ctx.getResources().getColor(R.color.colorPrimary));
        mPaint.setColor(ctx.getResources().getColor(R.color.white));
        dy = ctx.getResources().getDimension(R.dimen.one);
        this.datamap=datamap;
        this.totalduration=total;
    }

    @Override
    public void draw(Canvas canvas) {
        if (datamap.size() == 0||totalduration==0) {
            //no frame attached
            canvas.drawRect(getBounds().left, getBounds().centerY() - dy / 2, getBounds().right, getBounds().centerY() + dy / 2, mPaint);

        }
        else
        {

                int left = getBounds().left;
                int right = getBounds().right;
                int tot = left + right;
                int i;

                for (i = 0; i < datamap.size(); i++) {
                    FrameList_Model frameList_model = datamap.get(i);

                    canvas.drawRect(left, getBounds().centerY() - dy / 2, givebound(frameList_model.getStarttime(), tot, totalduration), getBounds().centerY() + dy / 2, mPaint);
                    canvas.drawRect(givebound(frameList_model.getStarttime(), tot, totalduration), getBounds().centerY() - dy / 2, givebound(frameList_model.getEndtime(), tot, totalduration), getBounds().centerY() + dy / 2, blue);
                    left = givebound(frameList_model.getEndtime(), tot, totalduration);
                }

                FrameList_Model frameList_model = datamap.get((i - 1));
                canvas.drawRect(givebound(frameList_model.getEndtime(), tot, totalduration), getBounds().centerY() - dy / 2, right, getBounds().centerY() + dy / 2, mPaint);
        }
    }

    public int givebound(int time,int left,int totalduration)
    {

        int i;
        i=(time*left)/totalduration;

        //Log.e("bounds",totalduration+" time:"+time+" total:"+left+" d:"+i);
        return i;
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