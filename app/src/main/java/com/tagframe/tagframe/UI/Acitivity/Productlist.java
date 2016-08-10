package com.tagframe.tagframe.UI.Acitivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.ProductAdapter;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Fragments.Follow;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Productlist extends FragmentActivity {

    private EditText ed_product;
    private ImageView img;

    private ProgressBar pbar;
    private GridView gridview_product;
    private RelativeLayout mlayout;

    public static int Noframepostion=110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        ed_product = (EditText) findViewById(R.id.mod_search_text_product);
        img = (ImageView) findViewById(R.id.mod_search_product);
        pbar = (ProgressBar) findViewById(R.id.pbar_product);
        gridview_product = (GridView) findViewById(R.id.gird_product);
        mlayout=(RelativeLayout)findViewById(R.id.mlayout_product);

        new GetProductlist().execute("");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ed = ed_product.getText().toString();
                if (!ed.isEmpty()) {
                    Modules.hideKeyboard(Productlist.this);
                    new GetProductlist().execute(ed);
                }
            }
        });

        ed_product.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(!ed_product.getText().toString().isEmpty()) {

                        new GetProductlist().execute(ed_product.getText().toString());
                    }
                    else
                    {
                        PopMessage.makesimplesnack(mlayout,"Please provide a serach keyword");
                    }
                }
                return false;
            }
        });
    }






    public class GetProductlist extends AsyncTask<String, String, String> {
        WebServiceHandler webServiceHandler;
        ArrayList<Product> productArrayList;
        String status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setVisibility(View.VISIBLE);
            productArrayList = new ArrayList<>();


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                webServiceHandler = new WebServiceHandler(Constants.search_product);
                webServiceHandler.addFormField("product_title", params[0]);
                JSONObject jsonObject = new JSONObject(webServiceHandler.finish());
                status = jsonObject.getString("status");
                if (status.equals("success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productobj = jsonArray.getJSONObject(i);
                        Product product = new Product();
                        product.setId(productobj.getString("product_id"));
                        product.setName(productobj.getString("product_name"));
                        product.setUrl(productobj.getString("product_url"));
                        product.setImage(productobj.getString("product_image"));
                        productArrayList.add(product);

                    }
                }

            } catch (Exception e) {
                Log.e("dasd", e.getMessage());
                productArrayList = new ArrayList<>();

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbar.setVisibility(View.GONE);
            if(Productlist.this!=null)
            gridview_product.setAdapter(new ProductAdapter(Productlist.this, productArrayList));

        }
    }


}
