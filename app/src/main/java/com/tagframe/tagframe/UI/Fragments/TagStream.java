package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.EventListRecyclerAdapter;
import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Adapters.TagStreamRecyclerAdapter;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.EndlessRecyclerViewScrollListener;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyListView;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tagframe.tagframe.R.id.img_footer;

/**
 * Created by karanveer on 05/04/2016.
 */
public class TagStream extends Fragment {
  private View mview;
  private SwipeRefreshLayout swipeRefreshLayout;
  private RecyclerView rcTagStream;
  private AppPrefs appPrefs;
  private int next_records = Utility.PAGE_SIZE;
  private ArrayList<Event_Model> tagStream_models;
  private RelativeLayout mLayout;
  private boolean shouldLoad = false;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mview = inflater.inflate(R.layout.fragment_tagstream, container, false);
    prepare();
    initViews();
    functionalizeList();
    initSwipeRefresh();
    return mview;
  }

  private void prepare() {
    appPrefs = new AppPrefs(getActivity());
    tagStream_models = appPrefs.gettagstreamlist("tagstream");
  }

  private void initViews() {
    mLayout = (RelativeLayout) mview.findViewById(R.id.mLayout_tagstream);
    swipeRefreshLayout = (SwipeRefreshLayout) mview.findViewById(R.id.swiperefresh_tagstream);
    rcTagStream = (RecyclerView) mview.findViewById(R.id.list_tagstream);
  }

  private void functionalizeList() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    rcTagStream.setLayoutManager(layoutManager);
    rcTagStream.setAdapter(new TagStreamRecyclerAdapter(getActivity(), tagStream_models));
    rcTagStream.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
      @Override public void onLoadMore(int page, int totalItemsCount) {
        if (shouldLoad) {
          loadTagStream();
        }
      }
    });
  }

  private void initSwipeRefresh() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {

        tagStream_models = new ArrayList<Event_Model>();
        next_records = 0;
        functionalizeList();
        //load a new list
        loadTagStream();
      }
    });


  }

  public void loadTagStream() {
    if (Networkstate.haveNetworkConnection(getActivity())) {

      ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
      Call<ListResponseModel> call =
          apiInterface.getTagStreamPaginated(appPrefs.getString(Utility.user_id),
              String.valueOf(next_records));
      call.enqueue(new Callback<ListResponseModel>() {
        @Override
        public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {
          if (response.body().getStatus().equals("success")) {
            //add the items to arraylist
            ArrayList<Event_Model> continued_list = response.body().getTagStreamArrayList();
            tagStream_models.addAll(continued_list);
            rcTagStream.getAdapter().notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            //if there are more items to be loaded then increse the offset by pagesize
            if (continued_list.size() == Utility.PAGE_SIZE) {
              next_records = next_records + Utility.PAGE_SIZE;
              shouldLoad=true;
            } else {
              shouldLoad=false;
            }
          } else {
            PopMessage.makesimplesnack(mLayout, response.body().getStatus());
            swipeRefreshLayout.setRefreshing(false);
          }
        }

        @Override public void onFailure(Call<ListResponseModel> call, Throwable t) {
          swipeRefreshLayout.setRefreshing(false);
        }
      });
    } else {
      PopMessage.makeshorttoast(getActivity(), "No Internet Connection");
      getActivity().finish();
    }
  }

  public void scrolltofirst() {

    rcTagStream.smoothScrollToPosition(0);
  }


}
