package com.tagframe.tagframe.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.tagframe.tagframe.R;

public class MySwipeLayout extends SwipeRefreshLayout implements AppBarLayout.OnOffsetChangedListener {
        private AppBarLayout appBarLayout;

        public MySwipeLayout(Context context) {
            super(context);
        }

        public MySwipeLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (getContext() instanceof Activity) {
                appBarLayout = (AppBarLayout) ((Activity) getContext()).findViewById(R.id.htab_appbar);
                appBarLayout.addOnOffsetChangedListener(this);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
          if(appBarLayout!=null){
            appBarLayout.removeOnOffsetChangedListener(this);
            appBarLayout = null;
            super.onDetachedFromWindow();
          }
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            this.setEnabled(i == 0);
        }
    }