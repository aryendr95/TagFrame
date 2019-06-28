package com.tagframe.tagframe.Utils;

import android.content.IntentFilter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brajendr on 7/11/2016.
 */
public class AnimUtils extends Animation {
    int targetHeight;
    View view;

    public AnimUtils(View view, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);


    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
