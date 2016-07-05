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
 * Created by abhinav on 05/04/2016.
 */
public class Notifications extends Fragment {
    private View mview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.fragment_notification,container,false);

        final ImageButton imgbtn=(ImageButton)mview.findViewById(R.id.createevent_notifcation);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ((Modules)getActivity()).generate_media_chooser(imgbtn);
            }
        });

        return mview;
    }
}
