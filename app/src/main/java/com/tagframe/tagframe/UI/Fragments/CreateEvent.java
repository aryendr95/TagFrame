package com.tagframe.tagframe.UI.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tagframe.tagframe.Adapters.Menulistadapter;
import com.tagframe.tagframe.Models.Menu;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.SavedEvents;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;

import java.util.ArrayList;

/**
 * Created by abhinav on 01/05/2016.
 */
public class CreateEvent extends Fragment {

    View mview;
    private EditText inputtitle, inputdescription;
    private TextInputLayout inputLayouttittle, inputLayoutdescription;

    private LinearLayout layout_select;
    private ListView ll_select_list;
    private Button btn_create_event, btn_saved_events;

    private RadioGroup radiogroup;
    private String tittle, des, type;

    public int PICK_VIDEO = 1, TAKE_VIDEO = 2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_create_event, container, false);

        init();

        return mview;
    }

    private void init() {
        inputLayouttittle = (TextInputLayout) mview.findViewById(R.id.input_layout_event_tittle);
        inputLayoutdescription = (TextInputLayout) mview.findViewById(R.id.input_layout_event_description);

        inputtitle = (EditText) mview.findViewById(R.id.input_event_title);
        inputdescription = (EditText) mview.findViewById(R.id.input_event_description);

        layout_select = (LinearLayout) mview.findViewById(R.id.ll_select_video);
        layout_select.setVisibility(View.GONE);

        radiogroup = (RadioGroup) mview.findViewById(R.id.typeradiogrp);

        btn_create_event = (Button) mview.findViewById(R.id.btn_create_event);
        btn_create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tittle = inputtitle.getText().toString();
                des = inputdescription.getText().toString();
                int id = radiogroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) mview.findViewById(id);
                type = radioButton.getText().toString();

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (!tittle.isEmpty() && !des.isEmpty()) {
                    expand(layout_select);
                } else {
                    MyToast.popmessage("Please fill in all fields", getActivity());
                }

            }
        });

        btn_saved_events = (Button) mview.findViewById(R.id.btn_saved_event);
        btn_saved_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SavedEvents.class);
                startActivity(intent);


            }
        });


        ll_select_list = (ListView) mview.findViewById(R.id.select_video_list);

        setadapter();

        ll_select_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");

                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
                } else if (position == 1) {
                    Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);


                    // start the Video Capture Intent
                    startActivityForResult(intent1, TAKE_VIDEO);
                }
            }
        });

    }

    private void setadapter() {
        ll_select_list.setAdapter(new Menulistadapter(getActivity(), populatelist()));

    }

    private ArrayList<Menu> populatelist() {

        ArrayList<com.tagframe.tagframe.Models.Menu> menuArrayList = new ArrayList<>();
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Pick a Video", android.R.drawable.ic_menu_gallery));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Take a Video", android.R.drawable.ic_menu_camera));

        return menuArrayList;
    }

    private void expand(LinearLayout linearLayout) {
        //set Visible
        linearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearLayout.measure(widthSpec, heightSpec);

        final LinearLayout l = linearLayout;
        ValueAnimator mAnimator = slideAnimator(0, linearLayout.getMeasuredHeight(), l);
        mAnimator.start();
    }

    private void collapse(final LinearLayout linearLayout) {
        int finalHeight = linearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, linearLayout);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final LinearLayout linearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.height = value;
                linearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == PICK_VIDEO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    Intent intent = new Intent(getActivity(), MakeNewEvent.class);
                    intent.putExtra("data_url", selectedImagePath);
                    intent.putExtra("tittle", tittle);
                    intent.putExtra("des", des);
                    intent.putExtra("type", type);
                    intent.putExtra("eventtype", Utility.eventtype_local);
                    startActivity(intent);
                }
            } else if (requestCode == TAKE_VIDEO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {
                    Intent intent = new Intent(getActivity(), MakeNewEvent.class);
                    intent.putExtra("data_url", selectedImagePath);
                    intent.putExtra("tittle", tittle);
                    intent.putExtra("des", des);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            }
        }


    }


}
