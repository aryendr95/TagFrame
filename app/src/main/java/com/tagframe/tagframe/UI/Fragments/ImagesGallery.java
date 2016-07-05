package com.tagframe.tagframe.UI.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;

/**
 * Created by abhinav on 19/04/2016.
 */
public class ImagesGallery extends Fragment {

    private View mview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_image_gallery, container, false);


        return mview;
    }
}
