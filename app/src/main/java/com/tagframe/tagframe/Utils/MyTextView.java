package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tagframe.tagframe.R;

/**
 * Created by abhinav on 04/04/2016.
 */

public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
        public  void init()
        {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), "lat.ttf");
            setTypeface(font);

        }


}
