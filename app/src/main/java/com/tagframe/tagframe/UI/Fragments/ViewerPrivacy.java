package com.tagframe.tagframe.UI.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tagframe.tagframe.R;

/**
 * Created by abhinav on 06/04/2016.
 */
public class ViewerPrivacy extends Fragment {

    private View mview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.fragment_viewer_privacy,container,false);

        return mview;
    }
}
