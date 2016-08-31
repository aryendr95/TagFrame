package com.tagframe.tagframe.UI.Acitivity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.ProductAdapter;
import com.tagframe.tagframe.Models.GetProductResponseModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Productlist extends FragmentActivity {

    private EditText ed_product;
    private ImageView img;

    private int page_number = 1;
    private ArrayList<Product> products = new ArrayList<>();
    private GridViewWithHeaderAndFooter mGridProduct;
    private ProgressBar pbar;
    private RelativeLayout mLayout;
    private View footerView;
    private String search_term="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        ed_product = (EditText) findViewById(R.id.mod_search_text_product);
        img = (ImageView) findViewById(R.id.mod_search_product);
        pbar = (ProgressBar) findViewById(R.id.pbar_product);
        mGridProduct = (GridViewWithHeaderAndFooter) findViewById(R.id.grid_product);
        pbar = (ProgressBar) findViewById(R.id.pbar_product);
        mLayout = (RelativeLayout) findViewById(R.id.mlayout_product);
        //add the footer to the
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        footerView = layoutInflater.inflate(R.layout.layout_gridview_product_footer, null);
        mGridProduct.addFooterView(footerView);

        footerView.findViewById(R.id.load_more_products).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incrementing page size
                page_number++;
                if (!search_term.isEmpty()) {

                    searchProduct(search_term);
                } else {

                    getProducts();
                }

            }
        });


        getProducts();


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ed = ed_product.getText().toString();
                if (!ed.isEmpty()) {
                    //making page number from starting
                    page_number = 1;
                    products = new ArrayList<Product>();
                    search_term=ed_product.getText().toString();
                    Modules.hideKeyboard(Productlist.this);
                    searchProduct(search_term);
                }
            }
        });

        ed_product.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (!ed_product.getText().toString().isEmpty()) {
                        page_number = 1;
                        products = new ArrayList<Product>();
                        search_term=ed_product.getText().toString();
                        searchProduct(search_term);
                    } else {
                        PopMessage.makesimplesnack(mLayout, "Please provide a search keyword");
                    }
                }
                return false;
            }
        });
    }

    private void searchProduct(String keyword) {


        pbar.setVisibility(View.VISIBLE);

        if (Networkstate.haveNetworkConnection(this)) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetProductResponseModel> call = apiInterface.searchProduct(keyword, String.valueOf(page_number));
            call.enqueue(new Callback<GetProductResponseModel>() {
                @Override
                public void onResponse(Call<GetProductResponseModel> call, Response<GetProductResponseModel> response) {


                    if (response.body().getStatus().equals("success")) {
                        if (response.body().getProductList().size() > 0) {
                            products.addAll(response.body().getProductList());
                            ProductAdapter productAdapter = new ProductAdapter(Productlist.this, products);
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
        if (Networkstate.haveNetworkConnection(this)) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetProductResponseModel> call = apiInterface.getAllProducts(String.valueOf(page_number));
            call.enqueue(new Callback<GetProductResponseModel>() {
                @Override
                public void onResponse(Call<GetProductResponseModel> call, Response<GetProductResponseModel> response) {


                    if (response.body().getStatus().equals("success")) {
                        if (response.body().getProductList().size() > 0) {
                            products.addAll(response.body().getProductList());
                            ProductAdapter productAdapter = new ProductAdapter(Productlist.this, products);
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
