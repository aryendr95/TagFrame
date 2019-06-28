package com.tagframe.tagframe.UI.Acitivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.SearchAdapter;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Models.SearchUserResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserActivity extends AppCompatActivity {
    private EditText ed_search;
    private ImageView img_search;
    private ListView listView_user;
    private TextView txt_message;
    private ProgressBar pbar;
    private RelativeLayout mLayout;

    private String product_id, user_id;
    private int operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_endorse);

        operation = getIntent().getIntExtra("operation", Utility.operation_onclicked_direct_endorse);
        product_id = getIntent().getStringExtra("product_id");

        ed_search = (EditText) findViewById(R.id.ed_search_text_direct_endorse);
        img_search = (ImageView) findViewById(R.id.img_search_user);
        listView_user = (ListView) findViewById(R.id.list_direct_endorse_users);
        txt_message = (TextView) findViewById(R.id.txt_message_direct_endorse);
        pbar = (ProgressBar) findViewById(R.id.pbar_direct_endorse);
        mLayout = (RelativeLayout) findViewById(R.id.mlayout_direct_endorse);

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ed_search.getText().toString();
                if (!str.isEmpty()) {
                    //making page number from starting
                    searchUser(str);
                    ed_search.setText("");
                }
            }
        });

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = ed_search.getText().toString();
                if ((str.length() >= 3) && (str.length() < 10)) {
                    searchUser(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!ed_search.getText().toString().isEmpty()) {
                        searchUser(ed_search.getText().toString());
                        ed_search.setText("");

                    } else {
                        PopMessage.makesimplesnack(mLayout, "Please provide a search keyword");
                    }
                }
                return false;
            }
        });

        //set up the serach message
        if (operation == Utility.operation_onclicked_tagged_user) {
            txt_message.setText(getResources().getString(R.string.tag_user_message));
        } else if (operation == Utility.operation_onclicked_direct_endorse) {
            txt_message.setText(getResources().getString(R.string.direct_endorse_message));
        }
    }

    private void searchUser(String str) {
        if (Networkstate.haveNetworkConnection(this)) {

            pbar.setVisibility(View.VISIBLE);
            //get user_id to make the call
            AppPrefs appPrefs = new AppPrefs(this);
            user_id = appPrefs.getString(Utility.user_id);

            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.searchUser(user_id, str).enqueue(new Callback<SearchUserResponseModel>() {
                @Override
                public void onResponse(Call<SearchUserResponseModel> call, Response<SearchUserResponseModel> response) {

                    pbar.setVisibility(View.GONE);

                    if (response.body().getStatus().equals("success")) {
                        ArrayList<FollowModel> seachUserModels = response.body().getArrayList_search_user_model();


                        if (seachUserModels.size() > 0) {
                            txt_message.setVisibility(View.GONE);
                            listView_user.setVisibility(View.VISIBLE);
                            //set the adapter
                            listView_user.setAdapter(new SearchAdapter(SearchUserActivity.this, seachUserModels, 2, operation));

                        } else {
                            listView_user.setVisibility(View.GONE);
                            txt_message.setText("No Such User");
                            txt_message.setVisibility(View.VISIBLE);
                        }
                    } else {
                        PopMessage.makesimplesnack(mLayout, response.body().getStatus());
                    }
                }

                @Override
                public void onFailure(Call<SearchUserResponseModel> call, Throwable t) {
                    pbar.setVisibility(View.GONE);
                    PopMessage.makesimplesnack(mLayout, "Error");
                }
            });
        } else {
            PopMessage.makesimplesnack(mLayout, "No Internet Connection");
        }

    }

    public void directEndorse(String to_user_id, String message) {
        if (Networkstate.haveNetworkConnection(this)) {

            pbar.setVisibility(View.VISIBLE);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.directEndorse(user_id, to_user_id, product_id, message).enqueue(new Callback<ResponsePojo>() {
                @Override
                public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                    pbar.setVisibility(View.GONE);
                    if (response.body().getStatus().equals(Utility.success_response)) {
                        PopMessage.makesimplesnack(mLayout, "Product is successfully endorsed.");


                    } else {
                        PopMessage.makesimplesnack(mLayout, "Error");
                    }
                }

                @Override
                public void onFailure(Call<ResponsePojo> call, Throwable t) {
                    Log.e("error", t.getMessage());
                    pbar.setVisibility(View.GONE);
                }
            });

        } else {
            PopMessage.makesimplesnack(mLayout, "No Internet Connection");
        }
    }


}
