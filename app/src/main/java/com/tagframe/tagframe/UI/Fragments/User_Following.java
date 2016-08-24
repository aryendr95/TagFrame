package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.FollowListAdapter;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.SearchUserResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 11/04/2016.
 */
public class User_Following extends Fragment {


    private View mview;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar, footerbar;
    private TextView mTxt_footer;
    private String user_id;
    private ArrayList<FollowModel> followModelArrayList = new ArrayList<>();
    private RelativeLayout mLayout;

    private AppPrefs userinfo;
    private int next_records = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.layout_following, container, false);

        userinfo = new AppPrefs(getActivity());

        mLayout = (RelativeLayout) mview.findViewById(R.id.mLayout_following);
        listView = (ListView) mview.findViewById(R.id.list_following);
        addfooter();

        listView.setAdapter(new FollowListAdapter(getActivity(), followModelArrayList, 0));

        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_following);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                next_records = 0;
                followModelArrayList = new ArrayList<FollowModel>();
                loadUserFollowing();
            }
        });

        progressBar = (ProgressBar) mview.findViewById(R.id.list_following_progress);

        user_id = getArguments().getString("user_id");

        loadUserFollowing();

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
                loadUserFollowing();
            }
        });
    }

    private void loadUserFollowing() {
        if (Networkstate.haveNetworkConnection(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.getUserFollowing(user_id, String.valueOf(next_records)).enqueue(new Callback<SearchUserResponseModel>() {
                @Override
                public void onResponse(Call<SearchUserResponseModel> call, Response<SearchUserResponseModel> response) {
                    try {
                        if (response.body().getStatus().equals("success")) {

                            progressBar.setVisibility(View.GONE);
                            followModelArrayList.addAll(response.body().getArrayList_search_user_model());
                            ((BaseAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();

                            //detect more events are to be loaded or not
                            if (response.body().getArrayList_search_user_model().size() == Constants.PAGE_SIZE) {
                                next_records = next_records + Constants.PAGE_SIZE;
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

                @Override
                public void onFailure(Call<SearchUserResponseModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
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
