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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.TagStream_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by abhinav on 11/04/2016.
 */
public class User_Events extends Fragment {

    private View mview;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ProgressBar progressBar;

    AppPrefs AppPrefs;
    String user_id,user_name,user_pic;

    //list scrill detection
    int firstvisible;

    loadeventtask loadeventtask=new loadeventtask();
    int mLastFirstVisibleItem;
    boolean mIsScrollingUp;

    ArrayList<TagStream_Model> tagStream_models=new ArrayList<>();
    private int firstVisibleItem, visibleItemCount,totalItemCount;
    int count=0,flag=0;

    ProgressBar footerbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.layout_events,container,false);

        swipeRefreshLayout=(SwipeRefreshLayout)mview.findViewById(R.id.swiperefresh_eventlist);
        listView=(ListView)mview.findViewById(R.id.list_event);


        AppPrefs =new AppPrefs(getActivity());
        progressBar=(ProgressBar)mview.findViewById(R.id.list_event_progress);

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

                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {

                    if(count==10) {

                        new loadeventtask().execute();
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

                if(mLastFirstVisibleItem<firstVisibleItemm)
                {
                  profile.changevisibilty(false);
                }
                if(mLastFirstVisibleItem>firstVisibleItemm)
                {
                    profile.changevisibilty(true);
                }
                mLastFirstVisibleItem=firstVisibleItemm;

            }
        });



        return mview;
    }

    public void addfooter()
    {

        //adding a footer to listview
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        listView.addFooterView(footerView);
        footerbar=(ProgressBar)footerView.findViewById(R.id.pbar_footer);

    }

    @Override
    public void onPause() {

        Log.e("asf","stop");
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
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            try
            {

                webServiceHandler=new WebServiceHandler(Constants.users_events_url);
                webServiceHandler.addFormField("user_id",user_id);
                webServiceHandler.addFormField("next_records",count+"");
                String result=webServiceHandler.finish();
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("events");
                status=jsonObject1.getString("status");

                if(status.equals("success"))
                {
                    JSONArray records=jsonObject1.getJSONArray("eventdata");
                        count=records.length();
                    for(int i=0;i<records.length();i++) {

                        JSONObject rec=records.getJSONObject(i);
                        TagStream_Model tagStream_model = new TagStream_Model();
                        tagStream_model.setThumbnail(rec.getString("thumbnail"));
                        tagStream_model.setDataurl(rec.getString("data_url"));
                        tagStream_model.setEvent_id(rec.getString("event_id"));
                        tagStream_model.setTitle(rec.getString("title"));
                        tagStream_model.setName(user_name);
                        tagStream_model.setProfile_picture(user_pic);
                        tagStream_model.setDescription(rec.getString("description"));
                        //tagStream_model.set(rec.getString("number_of_event"));
                        tagStream_model.setCreated_at(rec.getString("created_on"));
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
                            //Log.e("dsa",frameobject.getString("frame_data_url")+user_id);
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
                if(isAdded()) {
                    Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.framelayout_profile);
                    if (f instanceof User_Events) {
                        progressBar.setVisibility(View.GONE);

                        if (flag == 0) {
                            listView.setAdapter(new TagStreamEventAdapter(getActivity(), tagStream_models));
                            flag++;
                        } else {
                            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                        }

                        swipeRefreshLayout.setRefreshing(false);

                        firstvisible = listView.getFirstVisiblePosition();
                    }
                }
        }
    }

    //scroll to fisrt


    public void scrolltofirst(){

        listView.smoothScrollToPosition(0);
    }

}
