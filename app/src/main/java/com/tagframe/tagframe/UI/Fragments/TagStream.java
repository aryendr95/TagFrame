package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.TagStream_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyListView;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.listops;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by abhinav on 05/04/2016.
 */
public class TagStream extends Fragment {

    private View mview;

    SwipeRefreshLayout swipeRefreshLayout;
    MyListView listView;
    listops listops;

    ImageButton imgbtn;

    loadtagstreamtask loadtagstreamtask;

    int next_records=10;

    ArrayList<TagStream_Model> tagStream_models;

    ProgressBar footerbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.fragment_tagstream,container,false);

        swipeRefreshLayout=(SwipeRefreshLayout)mview.findViewById(R.id.swiperefresh_tagstream);
        listView=(MyListView)mview.findViewById(R.id.list_tagstream);
        addfooter();

        listops=new listops(getActivity());

        tagStream_models=listops.gettagstreamlist("tagstream");

        listView.setAdapter(new TagStreamEventAdapter(getActivity(), listops.gettagstreamlist("tagstream")));



        loadtagstreamtask=new loadtagstreamtask();
        loadtagstreamtask.execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tagStream_models=new ArrayList<TagStream_Model>();
                if(!loadtagstreamtask.isCancelled()) {
                    loadtagstreamtask.cancel(true);
                    new loadtagstreamtask().execute();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);
                    MyToast.popmessage("Please wait,data is loading..", getActivity());
                }

            }
        });



        final ImageButton imgbtn=(ImageButton)mview.findViewById(R.id.createevent_tagstream);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ((Modules)getActivity()).generate_media_chooser(imgbtn);
            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                //making the others incenter false


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

        if(!loadtagstreamtask.isCancelled())
        {
            loadtagstreamtask.cancel(true);
        }
        super.onPause();
    }

    class loadtagstreamtask extends AsyncTask<String,String,String>
    {
        WebServiceHandler webServiceHandler;
        String status;

        int record_length=10;

        @Override
        protected void onPreExecute() {

            footerbar.setVisibility(View.VISIBLE);
            super.onPreExecute();



        }

        @Override
        protected String doInBackground(String... params) {
            try
            {

                webServiceHandler=new WebServiceHandler(Constants.tagstreams_url);
                webServiceHandler.addFormField("user_id",listops.getString(Constants.user_id));
                webServiceHandler.addFormField("next_records","10");
                String result=webServiceHandler.finish();
                Log.e("cxc",result);
                JSONObject jsonObject=new JSONObject(result);
                status=jsonObject.getString("status");


                if(status.equals("success"))
                {
                    JSONArray records=jsonObject.getJSONArray("records");
                    record_length=records.length();
                    Log.e("cxc",record_length+"");

                    for(int i=0;i<records.length();i++) {

                        JSONObject rec=records.getJSONObject(i);
                        TagStream_Model tagStream_model = new TagStream_Model();
                        tagStream_model.setName(rec.getString("name"));
                        tagStream_model.setProfile_picture(rec.getString("profile_picture"));
                        tagStream_model.setThumbnail(rec.getString("thumbnail"));
                        tagStream_model.setDataurl(rec.getString("data_url"));
                        tagStream_model.setEvent_id(rec.getString("event_id"));
                        tagStream_model.setTitle(rec.getString("title"));
                        tagStream_model.setDescription(rec.getString("description"));
                        tagStream_model.setProduct_url(rec.getString("product_url"));
                        tagStream_model.setProduct_image(rec.getString("product_image"));
                        tagStream_model.setPrduct_name(rec.getString("product_name"));
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

                        Log.e("fas", tagStream_models.size()+"");
                    }
                }

            }
            catch (IOException E)
            {

            }
            catch (JSONException E)
            {
                Log.e("fas",E.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listops.puttagstreamlist(tagStream_models);
            listView.setAdapter(new TagStreamEventAdapter(getActivity(), listops.gettagstreamlist("tagstream")));
            swipeRefreshLayout.setRefreshing(false);
            footerbar.setVisibility(View.GONE);


        }
    }

    //scroll to fisrt


    public void scrolltofirst(){

        listView.smoothScrollToPosition(0);
    }



}
