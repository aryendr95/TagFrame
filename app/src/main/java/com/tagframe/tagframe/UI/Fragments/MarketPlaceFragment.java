package com.tagframe.tagframe.UI.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.ProductAdapter;
import com.tagframe.tagframe.Adapters.ProductRecyclerAdapter;
import com.tagframe.tagframe.Models.GetProductResponseModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.EndlessRecyclerViewScrollListener;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

import com.tagframe.tagframe.Utils.Utility;
import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 7/11/2016.
 */
public class MarketPlaceFragment extends Fragment {

  private View mView;
  private ArrayList<Product> products = new ArrayList<>();
  private RecyclerView mGridProduct;
  private ProgressBar pbar;
  private RelativeLayout mLayout;
  boolean shouldLoad = true;
  private int next_records = 1;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_marketplace, container, false);
    initView();
    setUpProductGrid();
    getDataAndQuery();
    return mView;
  }

  private void initView() {
    mGridProduct = (RecyclerView) mView.findViewById(R.id.frg_market_grid_product);
    pbar = (ProgressBar) mView.findViewById(R.id.pbar_load_products);
    mLayout = (RelativeLayout) mView.findViewById(R.id.mLayout_market_place);
  }

  private void getDataAndQuery() {
    if (getArguments() != null) {
      products = new ArrayList<>();
      setUpProductGrid();
      searchProduct(getArguments().getString("keyword"));
    } else {
      getProducts();
    }
  }

  private void setUpProductGrid() {
    ProductRecyclerAdapter productAdapter = new ProductRecyclerAdapter(getActivity(), products);
    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
    mGridProduct.setLayoutManager(staggeredGridLayoutManager);
    mGridProduct.setAdapter(productAdapter);

    mGridProduct.addOnScrollListener(
        new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
          @Override public void onLoadMore(int page, int totalItemsCount) {
            if (shouldLoad) {
              getProducts();
            }
          }
        });
  }

  private void searchProduct(String keyword) {

    pbar.setVisibility(View.VISIBLE);


    if (Networkstate.haveNetworkConnection(getActivity())) {
      ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
      Call<GetProductResponseModel> call =
          apiInterface.searchProduct(keyword, String.valueOf(next_records));
      call.enqueue(new Callback<GetProductResponseModel>() {
        @Override public void onResponse(Call<GetProductResponseModel> call,
            Response<GetProductResponseModel> response) {
          if (isAdded()) {
            pbar.setVisibility(View.GONE);
            if (response.body().getStatus().equals("success")) {
              if (response.body().getProductList().size() > 0) {
                products.addAll(response.body().getProductList());
                mGridProduct.getAdapter().notifyDataSetChanged();
                if (response.body().getProductList().size() == Utility.PAGE_SIZE) {
                  next_records = next_records++;
                  shouldLoad = true;
                } else {
                  shouldLoad = false;
                }
              } else {
                PopMessage.makesimplesnack(mLayout, "No products to load..");
              }
            } else {

              PopMessage.makesimplesnack(mLayout, response.body().getStatus());
            }
          }
        }

        @Override public void onFailure(Call<GetProductResponseModel> call, Throwable t) {
          pbar.setVisibility(View.GONE);
        }
      });
    } else {
      PopMessage.makesimplesnack(mLayout, "No Internet Connection");
    }
  }

  private void getProducts() {
    pbar.setVisibility(View.VISIBLE);
    //getting the product list using Retrofit
    if (Networkstate.haveNetworkConnection(getActivity())) {
      ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
      Call<GetProductResponseModel> call = apiInterface.getAllProducts(String.valueOf(next_records));
      call.enqueue(new Callback<GetProductResponseModel>() {
        @Override public void onResponse(Call<GetProductResponseModel> call,
            Response<GetProductResponseModel> response) {
          if (isAdded()) {
            pbar.setVisibility(View.GONE);
            if (response.body().getStatus().equals("success")) {
              if (response.body().getProductList().size() > 0) {
                products.addAll(response.body().getProductList());
                mGridProduct.getAdapter().notifyDataSetChanged();
                if (response.body().getProductList().size() == Utility.PAGE_SIZE) {
                  next_records = next_records++;
                  shouldLoad = true;
                } else {
                  shouldLoad = false;
                }
              } else {
                PopMessage.makesimplesnack(mLayout, "No products to load..");
              }
            } else {

              PopMessage.makesimplesnack(mLayout, response.body().getStatus());
            }
          }
        }

        @Override public void onFailure(Call<GetProductResponseModel> call, Throwable t) {
          if (isAdded()) {
            pbar.setVisibility(View.GONE);
          }
        }
      });
    } else {
      PopMessage.makesimplesnack(mLayout, "No Internet Connection");
    }
  }
}
