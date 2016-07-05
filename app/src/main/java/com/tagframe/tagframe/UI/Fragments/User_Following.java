package com.tagframe.tagframe.UI.Fragments;

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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tagframe.tagframe.Adapters.FollowListAdapter;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.listops;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhinav on 11/04/2016.
 */
public class User_Following extends Fragment {


    private View mview;
    private ListView followlistl;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    String user_id;
    loadfollow loadfollow=new loadfollow();
    listops userinfo;

    private int firstVisibleItem, visibleItemCount,totalItemCount;
    int count=0,flag=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.layout_following,container,false);

        userinfo=new listops(getActivity());
        followlistl=(ListView)mview.findViewById(R.id.list_following);
        swipeRefreshLayout=(SwipeRefreshLayout)mview.findViewById(R.id.swiperefresh_following);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(loadfollow.isCancelled()) {
                    loadfollow.execute();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    MyToast.popmessage("Please wait,data is loading..", getActivity());
                }
            }
        });

        progressBar=(ProgressBar)mview.findViewById(R.id.list_following_progress);

        user_id=getArguments().getString("user_id");

         loadfollow.execute();

        followlistl.setOnScrollListener(new AbsListView.OnScrollListener() {

            Profile profile = (Profile) getActivity().getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {

                    if (count == 10) {

                        new loadfollow().execute();
                    }
                    //get next 10-20 items(your choice)items

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItemm,
                                 int visibleItemCountt, int totalItemCountt) {

                firstVisibleItem = firstVisibleItemm;
                visibleItemCount = visibleItemCountt;
                totalItemCount = totalItemCountt;

                if (mLastFirstVisibleItem < firstVisibleItemm) {
                    profile.changevisibilty(false);
                }
                if (mLastFirstVisibleItem > firstVisibleItemm) {
                    profile.changevisibilty(true);
                }
                mLastFirstVisibleItem = firstVisibleItemm;

            }
        });

        return mview;
    }


    @Override
    public void onPause() {
        if(!loadfollow.isCancelled())
        {
            loadfollow.cancel(true);
        }
        super.onPause();
    }

    class loadfollow extends AsyncTask<String,String,String>
    {
        String status="";
        WebServiceHandler webServiceHandler;
        ArrayList<FollowModel> followModelslist;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            followModelslist=new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                webServiceHandler = new WebServiceHandler(Constants.following);
                webServiceHandler.addFormField("user_id", user_id);
                webServiceHandler.addFormField("next_records",count+"");
                JSONObject wrapper_object=new JSONObject(webServiceHandler.finish());


                JSONObject top_level=wrapper_object.getJSONObject("following");


                Log.e("sda",top_level.toString());
                status=top_level.getString("status");


                if(status.equals("success")) {
                    JSONArray userinfoarray = top_level.getJSONArray("userinfo");

                    count=userinfoarray.length();
                    for (int i = 0; i < userinfoarray.length(); i++) {
                        JSONObject userinfo=userinfoarray.getJSONObject(i);
                        FollowModel followModel=new FollowModel();

                        followModel.setUserid(userinfo.getString("user_id"));
                        followModel.setFirst_name(userinfo.getString("first_name"));
                        followModel.setUser_name(userinfo.getString("username"));
                        followModel.setEmail(userinfo.getString("email"));
                        followModel.setImage(userinfo.getString("image"));
                        followModel.setNumber(userinfo.getString("number_of_following"));
                        followModel.setFrom_user_id(user_id);
                        followModelslist.add(followModel);

                    }
                }

            }
            catch (Exception e)
            {
                Log.e("da", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isAdded()) {


                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (flag == 0) {
                        followlistl.setAdapter(new FollowListAdapter(getActivity(), followModelslist, 0));
                        flag++;
                    } else {
                        ((BaseAdapter) followlistl.getAdapter()).notifyDataSetChanged();
                    }


                }


        }
    }
}
