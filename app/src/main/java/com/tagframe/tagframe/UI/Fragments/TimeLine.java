package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 11/04/2016.
 */
public class TimeLine extends Fragment implements ScrollList {


    private View mview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ProgressBar progressBar, footerbar;
    private TextView mTxt_footer;
    private RelativeLayout mLayout;

    private AppPrefs AppPrefs;
    private String user_id, user_name, user_pic;
    private ArrayList<Event_Model> tagStream_models;
    private int next_records = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.layout_timeline, container, false);

        tagStream_models = new ArrayList<>();
        mLayout = (RelativeLayout) mview.findViewById(R.id.mlayout_timeline);
        progressBar = (ProgressBar) mview.findViewById(R.id.list_timeline_progress);
        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_timeline);
        listView = (ListView) mview.findViewById(R.id.list_timeline);
        addfooter();


        AppPrefs = new AppPrefs(getActivity());


        user_id = getArguments().getString("user_id");
        user_name = getArguments().getString("user_name");
        user_pic = getArguments().getString("user_pic");
        tagStream_models = new ArrayList<>();

        listView.setAdapter(new TagStreamEventAdapter(getActivity(), tagStream_models));
        //load_user_events
        loadEvents();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                tagStream_models = new ArrayList<Event_Model>();
                next_records = 0;
                loadEvents();
            }
        });


        return mview;
    }


    public void addfooter() {

        //adding a footer to listview
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        listView.addFooterView(footerView);
        footerbar = (ProgressBar) footerView.findViewById(R.id.pbar_footer);
        mTxt_footer = (TextView) footerView.findViewById(R.id.txt_footer);
        mTxt_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEvents();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void loadEvents() {
        if (Networkstate.haveNetworkConnection(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.getUserTimeLines(user_id, String.valueOf(next_records)).enqueue(new Callback<ListResponseModel>() {
                @Override
                public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {

                    if (isAdded()) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);


                        try {
                            if (response.body().getStatus().equals("success")) {


                                tagStream_models.addAll(response.body().getTagStreamArrayList());
                                AppPrefs.putusereventlist(tagStream_models);
                                ((BaseAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();

                                //detect more events are to be loaded or not
                                if (response.body().getTagStreamArrayList().size() == Utility.PAGE_SIZE) {
                                    next_records = next_records + Utility.PAGE_SIZE;
                                    mTxt_footer.setText("Load more items...");
                                } else {
                                    mTxt_footer.setOnClickListener(null);
                                    mTxt_footer.setText("No more items to load..");
                                }

                            } else {

                            }
                        } catch (Exception e) {

                            PopMessage.makesimplesnack(mLayout, "Error, Please try after some time...");
                        }
                    }
                }

                @Override
                public void onFailure(Call<ListResponseModel> call, Throwable t) {
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

    //scroll to fisrt


    public void scrolltofirst() {

        listView.smoothScrollToPosition(0);
    }

}
