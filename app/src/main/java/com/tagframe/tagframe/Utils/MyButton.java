package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.tagframe.tagframe.R;

/**
 * Created by abhinav on 04/04/2016.
 */
public class MyButton extends Button {
    public MyButton(Context context) {
        super(context);
        init();
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setTypeface(Typeface.SANS_SERIF);
        setTextColor(Color.WHITE);

    }
}
