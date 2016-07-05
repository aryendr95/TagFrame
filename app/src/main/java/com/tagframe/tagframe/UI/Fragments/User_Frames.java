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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tagframe.tagframe.Adapters.ImageAdapter;
import com.tagframe.tagframe.Adapters.UserEventAdapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.UserEventsModel;
import com.tagframe.tagframe.Models.User_Frames_model;
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
public class User_Frames extends Fragment {

    private View mview;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    com.tagframe.tagframe.Utils.listops listops;
    String user_id,user_name,user_pic;

    private GridView gridview;
    ProgressBar progressBar;

    loadeventtask loadeventtask=new loadeventtask();
    int count=0,flag=0;
    ArrayList<User_Frames_model> tagStream_models=new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.layout_frames,container,false);
        gridview=(GridView)mview.findViewById(R.id.grid_frame);

        swipeRefreshLayout=(SwipeRefreshLayout)mview.findViewById(R.id.swiperefresh_framelist);
        progressBar=(ProgressBar)mview.findViewById(R.id.list_frame_progress);

        listops=new listops(getActivity());

        user_id=getArguments().getString("user_id");
        user_name=getArguments().getString("user_name");
        user_pic=getArguments().getString("user_pic");

        loadeventtask.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(loadeventtask.isCancelled()) {
                    loadeventtask.execute();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    MyToast.popmessage("Please wait,data is loading..",getActivity());
                }

            }
        });


        return mview;
    }

    @Override
    public void onPause() {

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


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try
            {

                webServiceHandler=new WebServiceHandler(Constants.user_frame);
                webServiceHandler.addFormField("user_id",user_id);
                webServiceHandler.addFormField("next_records",count+"");
                String result=webServiceHandler.finish();
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("frames");
                status=jsonObject1.getString("status");

                if(status.equals("success"))
                {
                    JSONArray records=jsonObject1.getJSONArray("framedata");
                    count=records.length();
                    flag++;

                    for(int i=0;i<records.length();i++) {

                        JSONObject rec=records.getJSONObject(i);
                        User_Frames_model tagStream_model = new User_Frames_model();
                        tagStream_model.setThumbnail_url(rec.getString("thumbnail_url"));
                        tagStream_model.setData_url(rec.getString("data_url"));
                        tagStream_model.setFrame_id(rec.getString("frame_id"));
                        tagStream_model.setTitle(rec.getString("title"));

                        tagStream_model.setNumber_of_frames(rec.getString("number_of_frames"));
                        tagStream_model.setCreated_on(rec.getString("created_on"));

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
                            frameList_models.add(frameList_model);
                        }

                        tagStream_model.setFrameList_modelArrayList(frameList_models);

                        tagStream_models.add(tagStream_model);
                    }
                }

            }

            catch (Exception E)
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
            if (f instanceof User_Frames) {
                progressBar.setVisibility(View.GONE);

                if(flag==1)
                gridview.setAdapter(new ImageAdapter(getActivity(), tagStream_models));
                swipeRefreshLayout.setRefreshing(false);

            }




        }
    }
}
