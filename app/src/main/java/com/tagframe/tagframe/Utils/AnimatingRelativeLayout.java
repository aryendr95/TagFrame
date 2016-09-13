package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.tagframe.tagframe.R;

public class AnimatingRelativeLayout extends RelativeLayout
{
    Context context;
    Animation inAnimation;
    Animation outAnimation;
    int id1=R.anim.in_animation,id2=R.anim.out_animation;

    public AnimatingRelativeLayout(Context context)
    {
        super(context);
        this.context = context;
        initAnimations();

    }

    public AnimatingRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AnimatingRelativeLayout,
                0, 0);

        try {
            id1 = a.getResourceId(R.styleable.AnimatingRelativeLayout_inAnimation, R.anim.in_animation);
            id2= a.getResourceId(R.styleable.AnimatingRelativeLayout_outAnimation, R.anim.out_animation);
        } finally {
            a.recycle();
        }
        initAnimations();

    }

    public AnimatingRelativeLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AnimatingRelativeLayout,
                0, 0);

        try {
            id1 = a.getInteger(R.styleable.AnimatingRelativeLayout_inAnimation, R.anim.in_animation);
            id2= a.getInteger(R.styleable.AnimatingRelativeLayout_outAnimation, R.anim.out_animation);
        } finally {
            a.recycle();
        }
        initAnimations();
    }

    private void initAnimations()
    {
        inAnimation = (Animation) AnimationUtils.loadAnimation(context, id1);
        outAnimation = (Animation) AnimationUtils.loadAnimation(context,id2);
    }

    public void show()
    {
        if (isVisible()) return;
        show(true);
    }

    public void show(boolean withAnimation)
    {
        if (withAnimation) this.startAnimation(inAnimation);
        this.setVisibility(View.VISIBLE);
    }

    public void hide()
    {
        if (!isVisible()) return;
        hide(true);
    }

    public void hide(boolean withAnimation)
    {
        if (withAnimation) this.startAnimation(outAnimation);
        this.setVisibility(View.GONE);
    }

    public boolean isVisible()
    {
        return (this.getVisibility() == View.VISIBLE);
    }

    public void overrideDefaultInAnimation(Animation inAnimation)
    {
        this.inAnimation = inAnimation;
    }

    public void overrideDefaultOutAnimation(Animation outAnimation)
    {
        this.outAnimation = outAnimation;
    }
}