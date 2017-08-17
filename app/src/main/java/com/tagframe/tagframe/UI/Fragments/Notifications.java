package com.tagframe.tagframe.UI.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.NotificationAdapter;
import com.tagframe.tagframe.Models.NotificationModel;
import com.tagframe.tagframe.Models.NotificationResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.EndlessRecyclerViewScrollListener;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 05/04/2016.
 */
public class Notifications extends Fragment {
    private View mview;
    private RecyclerView list_notification;
    private ProgressBar pbar;
    private TextView txt_message;
    private RelativeLayout mLayout;
    private int next_records = 0;
    private ArrayList<NotificationModel> notificationModels;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean shouldLoad=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_notification, container, false);
        initViews(mview);
        setUpNotificationList();
        setUpSwipeRefresh();
        getNotifications();
        return mview;
    }

    private void setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                notificationModels = new ArrayList<>();
                RecyclerView.LayoutManager layoutManagers = new LinearLayoutManager(getActivity());
                list_notification.setLayoutManager(layoutManagers);
                list_notification.setAdapter(new NotificationAdapter(notificationModels, getActivity()));
                next_records = 0;
                getNotifications();
            }
        });
    }

    private void setUpNotificationList() {
        notificationModels = new ArrayList<>();
        LinearLayoutManager layoutManagers = new LinearLayoutManager(getActivity());
        list_notification.setLayoutManager(layoutManagers);
        list_notification.setAdapter(new NotificationAdapter(notificationModels, getActivity()));
        list_notification.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManagers) {
            @Override public void onLoadMore(int page, int totalItemsCount) {
                if (shouldLoad) {
                    getNotifications();
                }
            }
        });
    }

    private void initViews(View mview) {
        list_notification = (RecyclerView) mview.findViewById(R.id.list_notification);
        list_notification.setNestedScrollingEnabled(false);
        pbar = (ProgressBar) mview.findViewById(R.id.pbar_notification);
        txt_message = (TextView) mview.findViewById(R.id.txt_message_notification);
        mLayout = (RelativeLayout) mview.findViewById(R.id.mLayout_notification);
        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_notification);
    }

    private void getNotifications() {

        if (Networkstate.haveNetworkConnection(getActivity())) {
            final AppPrefs appPrefs = new AppPrefs(getActivity());
            String user_id = appPrefs.getString(Utility.user_id);

            pbar.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.getNotifications(user_id, String.valueOf(next_records)).enqueue(new Callback<NotificationResponseModel>() {
                @Override
                public void onResponse(Call<NotificationResponseModel> call, Response<NotificationResponseModel> response) {
                    if (isAdded()) {
                        String status = response.body().getStatus();
                        String unread_notifications=response.body().getUnread_notifications();
                        appPrefs.putString(Utility.unread_notifications,unread_notifications);
                        pbar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        if (status.equals(Utility.success_response)) {
                            if (response.body().getNotificationModelArrayList().size() > 0) {
                                notificationModels.addAll(response.body().getNotificationModelArrayList());
                                list_notification.getAdapter().notifyDataSetChanged();

                                if (response.body().getNotificationModelArrayList().size() == Utility.PAGE_SIZE) {
                                    next_records = next_records + Utility.PAGE_SIZE;

                                } else {

                                }

                            } else {
                                txt_message.setVisibility(View.VISIBLE);
                                //txt_message.setText(getActivity().getResources().getString(R.string.no_notification_message));
                            }
                        } else {
                            PopMessage.makesimplesnack(mLayout, "Error, please try after some time..");
                        }
                    }
                }

                @Override
                public void onFailure(Call<NotificationResponseModel> call, Throwable t) {
                    if (isAdded()) {
                        pbar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        PopMessage.makesimplesnack(mLayout, "Error, please try after some time..");

                    }
                }
            });
        } else {
            PopMessage.makesimplesnack(mLayout, Utility.message_no_internet);
        }
    }
}
