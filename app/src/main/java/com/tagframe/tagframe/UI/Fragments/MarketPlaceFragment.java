package com.tagframe.tagframe.UI.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.ProductAdapter;
import com.tagframe.tagframe.Models.GetProductResponseModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 7/11/2016.
 */
public class MarketPlaceFragment extends Fragment {

    private View mView;
    private int page_number = 1;
    private ArrayList<Product> products = new ArrayList<>();
    private GridViewWithHeaderAndFooter mGridProduct;
    private ProgressBar pbar;
    private RelativeLayout mLayout;
    private View footerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_marketplace, container, false);

        mGridProduct = (GridViewWithHeaderAndFooter) mView.findViewById(R.id.frg_market_grid_product);
        pbar = (ProgressBar) mView.findViewById(R.id.pbar_load_products);
        mLayout = (RelativeLayout) mView.findViewById(R.id.mLayout_market_place);
        //add the footer to the
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        footerView = layoutInflater.inflate(R.layout.layout_gridview_product_footer, null);
        mGridProduct.addFooterView(footerView);

        footerView.findViewById(R.id.load_more_products).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_number++;
                if (getArguments() != null) {

                    searchProduct(getArguments().getString("keyword"));
                } else {

                    getProducts();
                }
            }
        });

        //check if the prodcut is searched
        if (getArguments() != null) {
            //make the list empty before searching
            products = new ArrayList<>();
            page_number = 1;
            searchProduct(getArguments().getString("keyword"));
        } else {
            getProducts();
        }

        return mView;
    }

    private void searchProduct(String keyword) {


        pbar.setVisibility(View.VISIBLE);

        if (Networkstate.haveNetworkConnection(getActivity())) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetProductResponseModel> call = apiInterface.searchProduct(keyword, String.valueOf(page_number));
            call.enqueue(new Callback<GetProductResponseModel>() {
                @Override
                public void onResponse(Call<GetProductResponseModel> call, Response<GetProductResponseModel> response) {


                    if (response.body().getStatus().equals("success")) {
                        if (response.body().getProductList().size() > 0) {
                            products.addAll(response.body().getProductList());
                            ProductAdapter productAdapter = new ProductAdapter(getActivity(), products);
                            mGridProduct.setAdapter(productAdapter);
                            pbar.setVisibility(View.GONE);
                        } else {
                            pbar.setVisibility(View.GONE);
                            mGridProduct.removeFooterView(footerView);
                            PopMessage.makesimplesnack(mLayout, "No products to load");

                        }
                    } else {

                        PopMessage.makesimplesnack(mLayout, response.body().getStatus());

                    }
                }

                @Override
                public void onFailure(Call<GetProductResponseModel> call, Throwable t) {
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
            Call<GetProductResponseModel> call = apiInterface.getAllProducts(String.valueOf(page_number));
            call.enqueue(new Callback<GetProductResponseModel>() {
                @Override
                public void onResponse(Call<GetProductResponseModel> call, Response<GetProductResponseModel> response) {


                    if (response.body().getStatus().equals("success")) {
                        if (response.body().getProductList().size() > 0) {
                            products.addAll(response.body().getProductList());
                            ProductAdapter productAdapter = new ProductAdapter(getActivity(), products);
                            mGridProduct.setAdapter(productAdapter);
                            pbar.setVisibility(View.GONE);
                        } else {
                            pbar.setVisibility(View.GONE);
                            mGridProduct.removeFooterView(footerView);
                            PopMessage.makesimplesnack(mLayout, "No products to load");

                        }
                    } else {

                        PopMessage.makesimplesnack(mLayout, response.body().getStatus());

                    }
                }

                @Override
                public void onFailure(Call<GetProductResponseModel> call, Throwable t) {
                    pbar.setVisibility(View.GONE);
                }
            });
        } else {
            PopMessage.makesimplesnack(mLayout, "No Internet Connection");
        }
    }
}
