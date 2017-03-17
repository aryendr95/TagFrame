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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.tagframe.tagframe.Adapters.EventListRecyclerAdapter;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
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
public class User_Events extends Fragment implements ScrollList {
  private View mview;
  private SwipeRefreshLayout swipeRefreshLayout;
  private RecyclerView rcEvents;
  private ProgressBar progressBar;
  private RelativeLayout mLayout;
  //variables
  private AppPrefs appPrefs;
  private String user_id, user_name, user_pic;
  private ArrayList<Event_Model> events_models;
  private int next_records = 0;
  private boolean shouldLoad = false;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    prepare();
    mview = inflater.inflate(R.layout.layout_events, container, false);
    initViews();
    functionalizeList();
    setUpSwipeReferesh();
    loadUserEvents();
    return mview;
  }

  private void initViews() {
    mLayout = (RelativeLayout) mview.findViewById(R.id.mLayout_user_events);
    progressBar = (ProgressBar) mview.findViewById(R.id.list_event_progress);
    swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_eventlist);
    rcEvents = (RecyclerView) mview.findViewById(R.id.list_event);
  }

  private void prepare() {
    appPrefs = new AppPrefs(getActivity());
    events_models = new ArrayList<>();
    user_id = getArguments().getString("user_id");
    user_name = getArguments().getString("user_name");
    user_pic = getArguments().getString("user_pic");
  }

  private void functionalizeList() {
    LinearLayoutManager llayoutManager = new LinearLayoutManager(getActivity());
    rcEvents.setLayoutManager(llayoutManager);
    rcEvents.setAdapter(new EventListRecyclerAdapter(getActivity(), events_models));
    rcEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(llayoutManager) {
      @Override public void onLoadMore(int page, int totalItemsCount) {
        if (shouldLoad) {
          loadUserEvents();
        }
      }
    });
  }

  private void setUpSwipeReferesh() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        events_models = new ArrayList<Event_Model>();
        next_records = 0;
        functionalizeList();
        loadUserEvents();
      }
    });
  }

  public void loadUserEvents() {
    if (Networkstate.haveNetworkConnection(getActivity())) {
      progressBar.setVisibility(View.VISIBLE);
      ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
      retrofitService.getUserEvents(user_id, String.valueOf(next_records))
          .enqueue(new Callback<ListResponseModel>() {
            @Override public void onResponse(Call<ListResponseModel> call,
                Response<ListResponseModel> response) {

              if (isAdded()) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                try {
                  if (response.body().getStatus().equals("success")) {

                    events_models.addAll(response.body().getTagStreamArrayList());
                    rcEvents.getAdapter().notifyDataSetChanged();
                    //detect more events are to be loaded or not
                    if (response.body().getTagStreamArrayList().size() == Utility.PAGE_SIZE) {
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

            @Override public void onFailure(Call<ListResponseModel> call, Throwable t) {
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

    rcEvents.smoothScrollToPosition(0);
  }
}
