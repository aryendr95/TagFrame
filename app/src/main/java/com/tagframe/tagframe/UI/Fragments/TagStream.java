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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.TagStream_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
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
    private int next_records = Constants.PAGE_SIZE;
    private ArrayList<TagStream_Model> tagStream_models;
    private ProgressBar footerbar;
    private TextView mTxt_footer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_tagstream, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_tagstream);
        listView = (MyListView) mview.findViewById(R.id.list_tagstream);
        addfooter();

        AppPrefs = new AppPrefs(getActivity());

        tagStream_models = AppPrefs.gettagstreamlist("tagstream");

        listView.setAdapter(new TagStreamEventAdapter(getActivity(), AppPrefs.gettagstreamlist("tagstream")));

        //check if more items are to be loaded
        /*if (AppPrefs.gettagstreamlist("tagstream").size() == Constants.PAGE_SIZE) {
            loadTagStream();
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                tagStream_models = new ArrayList<TagStream_Model>();
                next_records=0;
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


        /*listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            }
        });*/

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


    }


    @Override
    public void onPause() {

        super.onPause();
    }

    public void loadTagStream() {
        if (Networkstate.haveNetworkConnection(getActivity())) {

            footerbar.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ListResponseModel> call = apiInterface.getTagStreamPaginated(AppPrefs.getString(Constants.user_id), String.valueOf(next_records));
            call.enqueue(new Callback<ListResponseModel>() {
                @Override
                public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {
                    if (response.body().getStatus().equals("success")) {

                        //add the items to arraylist
                        ArrayList<TagStream_Model> continued_list = response.body().getTagStreamArrayList();
                        tagStream_models.addAll(continued_list);

                        //notify the adpater
                        ((BaseAdapter)((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        footerbar.setVisibility(View.GONE);
                        //if there are more items to be loaded then increse the offset by pagesize
                        if (continued_list.size() == Constants.PAGE_SIZE) {
                            next_records = next_records + Constants.PAGE_SIZE;
                        } else {
                            mTxt_footer.setOnClickListener(null);
                            mTxt_footer.setText("No more items to load..");
                        }

                    } else {
                        PopMessage.makeshorttoast(getActivity(), response.body().getStatus());
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
