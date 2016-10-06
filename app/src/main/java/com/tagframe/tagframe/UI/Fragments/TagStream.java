package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyListView;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 05/04/2016.
 */
public class TagStream extends Fragment {

    private View mview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyListView listView;
    private AppPrefs AppPrefs;
    private ImageButton imgbtn;
    private int next_records = Utility.PAGE_SIZE;
    private ArrayList<Event_Model> tagStream_models;
    private ProgressBar footerbar;
    private TextView mTxt_footer;
    private ImageView img_footer;
    private RelativeLayout mLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_tagstream, container, false);

        mLayout = (RelativeLayout) mview.findViewById(R.id.mLayout_tagstream);

        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_tagstream);
        listView = (MyListView) mview.findViewById(R.id.list_tagstream);
        addfooter();

        AppPrefs = new AppPrefs(getActivity());

        tagStream_models = AppPrefs.gettagstreamlist("tagstream");

        listView.setAdapter(new TagStreamEventAdapter(getActivity(), AppPrefs.gettagstreamlist("tagstream")));


        if (AppPrefs.gettagstreamlist("tagstream").size() == 0) {
            mTxt_footer.setText("No feeds for you..");
            img_footer.setImageResource(R.drawable.ic_empty);
        }
        else if(AppPrefs.gettagstreamlist("tagstream").size()==Utility.PAGE_SIZE)
        {
            mTxt_footer.setText("Load more feeds..");
            img_footer.setImageResource(R.drawable.ic_load_more);
        }
        else
        {
            mTxt_footer.setText("No more feeds for you..");
            img_footer.setImageResource(R.drawable.ic_done);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                tagStream_models = new ArrayList<Event_Model>();
                listView.setAdapter(new TagStreamEventAdapter(getActivity(), tagStream_models));
                next_records = 0;
                //load a new list
                loadTagStream();

            }
        });


        final ImageButton imgbtn = (ImageButton) mview.findViewById(R.id.createevent_tagstream);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((Modules) getActivity()).generate_media_chooser(imgbtn);
            }
        });

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
                loadTagStream();
            }
        });
        img_footer=(ImageView)footerView.findViewById(R.id.img_footer);
        img_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTagStream();
            }
        });


    }


    @Override
    public void onPause() {

        super.onPause();
    }

    public void loadTagStream() {
        if (Networkstate.haveNetworkConnection(getActivity())) {

            footerbar.setVisibility(View.VISIBLE);
            img_footer.setImageResource(R.drawable.ic_loading);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ListResponseModel> call = apiInterface.getTagStreamPaginated(AppPrefs.getString(Utility.user_id), String.valueOf(next_records));
            call.enqueue(new Callback<ListResponseModel>() {
                @Override
                public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {
                    if (response.body().getStatus().equals("success")) {

                        //add the items to arraylist
                        ArrayList<Event_Model> continued_list = response.body().getTagStreamArrayList();
                        tagStream_models.addAll(continued_list);

                        //notify the adpater
                        ((BaseAdapter) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        footerbar.setVisibility(View.GONE);
                        //if there are more items to be loaded then increse the offset by pagesize
                        if (continued_list.size() == Utility.PAGE_SIZE) {
                            next_records = next_records + Utility.PAGE_SIZE;
                            mTxt_footer.setText("Load more feeds..");
                            img_footer.setImageResource(R.drawable.ic_load_more);
                        } else {
                            mTxt_footer.setOnClickListener(null);
                            mTxt_footer.setText("No more feeds fo you..");
                            img_footer.setImageResource(R.drawable.ic_done);
                        }

                    } else {
                        PopMessage.makesimplesnack(mLayout, response.body().getStatus());
                        footerbar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<ListResponseModel> call, Throwable t) {
                    footerbar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            PopMessage.makeshorttoast(getActivity(), "No Internet Connection");
            getActivity().finish();
        }
    }

    //scroll to fisrt


    public void scrolltofirst() {

        listView.smoothScrollToPosition(0);
    }


}
