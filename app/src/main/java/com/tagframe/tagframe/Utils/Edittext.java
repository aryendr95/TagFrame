package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.tagframe.tagframe.R;

/**
 * Created by abhinav on 04/04/2016.
 */

public class Edittext extends EditText {

    public Edittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Edittext(Context context) {
        super(context);
        init();
    }

    public Edittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public  void init()
    {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "lat.ttf");
        setTypeface(font);
    }
}
