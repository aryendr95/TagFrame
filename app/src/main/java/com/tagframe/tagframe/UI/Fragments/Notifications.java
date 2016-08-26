package com.tagframe.tagframe.UI.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.NotificationAdapter;
import com.tagframe.tagframe.Models.NotificationResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.fragment_notification,container,false);

        list_notification=(RecyclerView)mview.findViewById(R.id.list_notification);
        pbar=(ProgressBar)mview.findViewById(R.id.pbar_notification);
        txt_message=(TextView)mview.findViewById(R.id.txt_message_notification);
        mLayout=(RelativeLayout)mview.findViewById(R.id.mLayout_notification);

        getNotifications();

        return mview;
    }

    private void getNotifications() {

        if(Networkstate.haveNetworkConnection(getActivity()))
        {
            AppPrefs appPrefs=new AppPrefs(getActivity());
            String user_id=appPrefs.getString(Constants.user_id);

            pbar.setVisibility(View.VISIBLE);
            ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
            apiInterface.getNotifications(user_id).enqueue(new Callback<NotificationResponseModel>() {
                @Override
                public void onResponse(Call<NotificationResponseModel> call, Response<NotificationResponseModel> response) {
                    String status=response.body().getStatus();
                    pbar.setVisibility(View.GONE);
                    if(status.equals(Constants.success_response))
                    {
                        if(response.body().getNotificationModelArrayList().size()>0)
                        {
                            RecyclerView.LayoutManager layoutManagers=new LinearLayoutManager(getActivity());
                            list_notification.setLayoutManager(layoutManagers);
                            list_notification.setAdapter(new NotificationAdapter(response.body().getNotificationModelArrayList(),getActivity()));
                        }
                        else
                        {
                            txt_message.setVisibility(View.VISIBLE);
                            //txt_message.setText(getActivity().getResources().getString(R.string.no_notification_message));
                        }
                    }
                    else
                    {
                        PopMessage.makesimplesnack(mLayout,"Error, please try after some time..");
                    }
                }

                @Override
                public void onFailure(Call<NotificationResponseModel> call, Throwable t) {
                    pbar.setVisibility(View.GONE);
                    PopMessage.makesimplesnack(mLayout,"Error, please try after some time..");

                }
            });
        }
        else
        {
            PopMessage.makesimplesnack(mLayout, Constants.message_no_internet);
        }
    }
}
