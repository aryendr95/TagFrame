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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tagframe.tagframe.Adapters.Time_Line_Adapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.TimeLine_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.listops;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by abhinav on 11/04/2016.
 */
public class TimeLine extends Fragment {

    private View mview;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ProgressBar progressBar;

    com.tagframe.tagframe.Utils.listops listops;
    String user_id,user_name,user_pic;

    //list scrill detection
    int firstvisible;

    loadeventtask loadeventtask=new loadeventtask();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.layout_timeline,container,false);
        swipeRefreshLayout=(SwipeRefreshLayout)mview.findViewById(R.id.swiperefresh_timeline);
        listView=(ListView)mview.findViewById(R.id.list_timeline);

        listops=new listops(getActivity());
        progressBar=(ProgressBar)mview.findViewById(R.id.list_timeline_progress);

        user_id=getArguments().getString("user_id");
        user_name=getArguments().getString("user_name");
        user_pic=getArguments().getString("user_pic");



        loadeventtask.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (loadeventtask.isCancelled()) {
                    loadeventtask.execute();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    MyToast.popmessage("Please wait,data is loading..", getActivity());
                }

            }
        });


        //list scroll detection


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            Profile profile=(Profile)getActivity().getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    profile.changevisibilty(false);
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    profile.changevisibilty(true);
                }
                mLastFirstVisibleItem=firstVisibleItem;

            }
        });




        return mview;
    }


    @Override
    public void onPause() {

        Log.e("asf", "paue");
        if(!loadeventtask.isCancelled())
        {
            loadeventtask.cancel(true);
        }
        super.onPause();
    }



    class loadeventtask extends AsyncTask<String,String,String>
    {
        WebServiceHandler webServiceHandler;
        String status;
        ArrayList<TimeLine_Model> tagStream_models;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tagStream_models=new ArrayList<>();

        }

        @Override
        protected String doInBackground(String... params) {
            try
            {

                webServiceHandler=new WebServiceHandler(Constants.users_timeline);
                webServiceHandler.addFormField("user_id",user_id);
                String result=webServiceHandler.finish();
                Log.e("res",result);
                JSONObject jsonObject=new JSONObject(result);
                //JSONObject jsonObject1=jsonObject.getJSONObject("events");
                status=jsonObject.getString("status");

                if(status.equals("success"))
                {
                    JSONArray records=jsonObject.getJSONArray("records");

                    for(int i=0;i<records.length();i++) {

                        JSONObject rec=records.getJSONObject(i);
                        TimeLine_Model tagStream_model = new TimeLine_Model();
                        tagStream_model.setThumbnail(rec.getString("thumbnail"));
                        tagStream_model.setData_url(rec.getString("data_url"));
                        tagStream_model.setEvent_id(rec.getString("event_id"));
                        tagStream_model.setTittle(rec.getString("title"));
                        tagStream_model.setUser_name(user_name);
                        tagStream_model.setProfile_picture(user_pic);
                        tagStream_model.setDescription(rec.getString("description"));
                        tagStream_model.setNumber_of_event(rec.getString("number_of_event"));
                        tagStream_model.setCreated_on(rec.getString("created_on"));
                        tagStream_model.setTags(rec.getString("tags"));
                        tagStream_model.setType(rec.getString("event_type"));
                        tagStream_model.setNumber_of_likes(rec.getString("count_like"));
                        tagStream_model.setSharelink(rec.getString("website_video_url"));

                        tagStream_model.setLike_video(rec.getString("is_liked"));
                        JSONArray frames=rec.getJSONArray("frames");
                        ArrayList<FrameList_Model> frameList_models=new ArrayList<>();
                        for(int f=0;f<frames.length();f++)
                        {
                            JSONObject frameobject=frames.getJSONObject(f);
                            FrameList_Model frameList_model=new FrameList_Model();
                            frameList_model.setImagepath(frameobject.getString("frame_thumbnail_url"));
                            frameList_model.setName(frameobject.getString("frame_title"));
                            frameList_model.setStarttime(Integer.parseInt(frameobject.getString("frame_start_time")) * 1000);
                            frameList_model.setEndtime(Integer.parseInt(frameobject.getString("frame_end_time")) * 1000);
                            frameList_model.setFrametype((frameobject.getString("frame_media_type").equals("IMAGE") ? Constants.frametype_image : Constants.frametype_video));
                            frameList_model.setFrame_resource_type(Constants.frame_resource_type_internet);
                            frameList_model.setFrame_data_url(frameobject.getString("frame_data_url"));
                            frameList_models.add(frameList_model);
                        }

                        tagStream_model.setFrameList_modelArrayList(frameList_models);
                        tagStream_models.add(tagStream_model);
                    }
                }

            }
            catch (IOException E)
            {

            }
            catch (JSONException E)
            {
                Log.e("fas", E.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //check if current fragment is user_events

            Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.framelayout_profile);
            if (f instanceof TimeLine) {
                progressBar.setVisibility(View.GONE);
                listView.setAdapter(new Time_Line_Adapter(getActivity(), tagStream_models));
                swipeRefreshLayout.setRefreshing(false);



                firstvisible = listView.getFirstVisiblePosition();
            }
        }
    }

    //scroll to fisrt


    public void scrolltofirst(){

        listView.smoothScrollToPosition(0);
    }
}
