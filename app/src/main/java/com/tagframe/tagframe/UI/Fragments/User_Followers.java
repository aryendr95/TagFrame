package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.EventListRecyclerAdapter;
import com.tagframe.tagframe.Adapters.FollowListAdapter;
import com.tagframe.tagframe.Adapters.FollowRecyclerAdapter;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.SearchUserResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.EndlessRecyclerViewScrollListener;
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
public class User_Followers extends Fragment implements ScrollList {
    private View mview;
    private RecyclerView rcFollwers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    //variables
    private String user_id;
    private ArrayList<FollowModel> followModelArrayList;
    private RelativeLayout mLayout;
    private AppPrefs userinfo;
    private int next_records = 0;
    private boolean shouldLoad = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prepare();
        mview = inflater.inflate(R.layout.layout_followers, container, false);
        initViews();
        functionalizeList();
        setUpSwipeReferesh();
        loadUserFollower();
        return mview;
    }

    private void prepare() {
        followModelArrayList = new ArrayList<>();
        userinfo = new AppPrefs(getActivity());
        user_id = getArguments().getString("user_id");
    }

    private void initViews() {
        mLayout = (RelativeLayout) mview.findViewById(R.id.mLayout_followers);
        rcFollwers = (RecyclerView) mview.findViewById(R.id.list_followers);
        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_followers);
        progressBar = (ProgressBar) mview.findViewById(R.id.list_followers_progress);
    }

    private void functionalizeList() {
        LinearLayoutManager llayoutManager = new LinearLayoutManager(getActivity());
        rcFollwers.setLayoutManager(llayoutManager);
        rcFollwers.setAdapter(new FollowRecyclerAdapter(getActivity(), followModelArrayList, 1));
        rcFollwers.addOnScrollListener(new EndlessRecyclerViewScrollListener(llayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (shouldLoad) {
                    loadUserFollower();
                }
            }
        });
    }

    private void setUpSwipeReferesh() {
        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_followers);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                next_records = 0;
                followModelArrayList = new ArrayList<FollowModel>();
                functionalizeList();
                loadUserFollower();
            }
        });
    }

    private void loadUserFollower() {
        if (Networkstate.haveNetworkConnection(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.getUserFollowers(user_id, String.valueOf(next_records))
                    .enqueue(new Callback<SearchUserResponseModel>() {
                        @Override
                        public void onResponse(Call<SearchUserResponseModel> call,
                                               Response<SearchUserResponseModel> response) {
                            if (isAdded()) {
                                try {
                                    if (response.body().getStatus().equals("success")) {
                                        progressBar.setVisibility(View.GONE);
                                        swipeRefreshLayout.setRefreshing(false);
                                        followModelArrayList.addAll(response.body().getArrayList_search_user_model());
                                        rcFollwers.getAdapter().notifyDataSetChanged();

                                        //detect more events are to be loaded or not
                                        if (response.body().getArrayList_search_user_model().size() == Utility.PAGE_SIZE) {
                                            next_records = next_records + Utility.PAGE_SIZE;
                                            shouldLoad = true;
                                        } else {
                                            shouldLoad = false;
                                        }
                                    } else {

                                    }
                                } catch (Exception e) {
                                    PopMessage.makesimplesnack(mLayout, "Error, Please try after some time...");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchUserResponseModel> call, Throwable t) {
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
        rcFollwers.smoothScrollToPosition(0);
    }
}
