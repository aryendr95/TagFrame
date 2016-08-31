package com.tagframe.tagframe.UI.Acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.EndorseListAdapter;
import com.tagframe.tagframe.Models.EndorseListResponseModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEndorsement extends AppCompatActivity {


    private RelativeLayout mLayout;
    private RecyclerView list_endorseList;
    private TextView txt_message;
    private ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_endorsement);

        mLayout = (RelativeLayout) findViewById(R.id.mLayout_endorselist);
        txt_message=(TextView)findViewById(R.id.txt_message_direct_endorse_list);
        pbar=(ProgressBar)findViewById(R.id.pbar_direct_endorse_list);

        findViewById(R.id.my_endorsement_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyEndorsement.this,Modules.class);
                startActivity(intent);
                finish();
            }
        });


        list_endorseList = (RecyclerView) findViewById(R.id.endorse_list);
        if (Networkstate.haveNetworkConnection(this)) {
            AppPrefs appPrefs = new AppPrefs(this);
            getEndorseList(appPrefs.getString(Utility.user_id));
        } else {
            PopMessage.makesimplesnack(mLayout, Utility.message_no_internet);
        }
    }

    private void getEndorseList(String user_id) {

        pbar.setVisibility(View.VISIBLE);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getDirectEndorseList(user_id).enqueue(new Callback<EndorseListResponseModel>() {
            @Override
            public void onResponse(Call<EndorseListResponseModel> call, Response<EndorseListResponseModel> response) {
                pbar.setVisibility(View.GONE);
                if(response.body().getStatus().equals(Utility.success_response)) {
                    if (response.body().getEndorseListModels().size() > 0) {
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyEndorsement.this);
                        list_endorseList.setLayoutManager(layoutManager);
                        list_endorseList.setAdapter(new EndorseListAdapter(response.body().getEndorseListModels(), MyEndorsement.this));
                        txt_message.setVisibility(View.GONE);
                    }
                    else
                    {
                        txt_message.setVisibility(View.VISIBLE);
                    }

                }
                else
                {
                    PopMessage.makesimplesnack(mLayout,"Error");
                }
            }

            @Override
            public void onFailure(Call<EndorseListResponseModel> call, Throwable t) {
                pbar.setVisibility(View.GONE);
                PopMessage.makesimplesnack(mLayout,"Error");
            }
        });

    }
}
