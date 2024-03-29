package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.ImageAdapter;
import com.tagframe.tagframe.Models.UserFrameResponseModel;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 11/04/2016.
 */
public class User_Frames extends Fragment implements ScrollList {

    private View mview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppPrefs AppPrefs;
    private GridViewWithHeaderAndFooter gridview;
    private ProgressBar progressBar;
    private RelativeLayout mLayout;
    private View footerView;
    private TextView mTxt_footer;
    private ImageView img_footer;
    private String user_id, user_name, user_pic;
    ArrayList<User_Frames_model> user_frames_models;
    private int next_records = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (Utility.isLollipop())
            mview = inflater.inflate(R.layout.layout_frames, container, false);
        else
            mview = inflater.inflate(R.layout.layout_frames_below_21, container, false);

        user_frames_models = new ArrayList<>();

        gridview = (GridViewWithHeaderAndFooter) mview.findViewById(R.id.grid_frame);
        mLayout = (RelativeLayout) mview.findViewById(R.id.mlayout_user_frames);

        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_framelist);
        progressBar = (ProgressBar) mview.findViewById(R.id.list_frame_progress);

        AppPrefs = new AppPrefs(getActivity());
        addfooter();

        gridview.setAdapter(new ImageAdapter(getActivity(), user_frames_models));


        user_id = getArguments().getString("user_id");
        user_name = getArguments().getString("user_name");
        user_pic = getArguments().getString("user_pic");

        loadUserFrames();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                user_frames_models = new ArrayList<>();
                gridview.setAdapter(new ImageAdapter(getActivity(), user_frames_models));
                next_records = 0;
                loadUserFrames();

            }
        });


        return mview;
    }

    public void addfooter() {

        //adding a footer to listview
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_user_frames_footerview, null, false);
        gridview.addFooterView(footerView);
        mTxt_footer = (TextView) footerView.findViewById(R.id.txt_footer);
        mTxt_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserFrames();
            }
        });
        img_footer = (ImageView) footerView.findViewById(R.id.img_footer);
        img_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserFrames();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadUserFrames() {
        if (Networkstate.haveNetworkConnection(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            img_footer.setImageResource(R.drawable.ic_loading);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.getUserFrames(user_id, String.valueOf(next_records)).enqueue(new Callback<UserFrameResponseModel>() {
                @Override
                public void onResponse(Call<UserFrameResponseModel> call, Response<UserFrameResponseModel> response) {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            if (response.body().getStatus().equals("success")) {
                                user_frames_models.addAll(response.body().getUser_frames_models());
                                gridview.setAdapter(new ImageAdapter(getActivity(), user_frames_models));


                                //detect more events are to be loaded or not
                                if (response.body().getUser_frames_models().size() == Utility.PAGE_SIZE) {
                                    next_records = next_records + Utility.PAGE_SIZE;
                                    mTxt_footer.setText("Load frames items...");
                                    img_footer.setImageResource(R.drawable.ic_load_more);

                                } else {

                                    mTxt_footer.setOnClickListener(null);
                                    img_footer.setOnClickListener(null);
                                    img_footer.setImageResource(R.drawable.ic_done);
                                    mTxt_footer.setText("No more frames for you..");
                                }

                            } else {
                                PopMessage.makesimplesnack(mLayout, response.body().getStatus());
                            }
                        } catch (Exception e) {
                            PopMessage.makesimplesnack(mLayout, "Error, Please try after some time...");
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserFrameResponseModel> call, Throwable t) {
                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }
            });

        } else {
            PopMessage.makesimplesnack(mLayout, "No Internet Connection");
        }
    }

    @Override
    public void scrolltofirst() {
        gridview.smoothScrollToPosition(0);
    }
}
