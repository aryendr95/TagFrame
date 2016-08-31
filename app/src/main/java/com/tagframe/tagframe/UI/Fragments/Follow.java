package com.tagframe.tagframe.UI.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.SearchAdapter;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhinav on 05/04/2016.
 */
public class Follow extends Fragment{

    private View mview;
    ListView searchlist;
    TextView textview;
    ProgressBar progressBar;
    AppPrefs userinfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview=inflater.inflate(R.layout.fragment_follow,container,false);

        final ImageButton imgbtn=(ImageButton)mview.findViewById(R.id.createevent_follow);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ((Modules)getActivity()).generate_media_chooser(imgbtn);
            }
        });

        userinfo=new AppPrefs(getActivity());

        searchlist=(ListView)mview.findViewById(R.id.searchlist);

        textview=(TextView)mview.findViewById(R.id.txtmsg);
        textview.setVisibility(View.VISIBLE);
        textview.setText("Serach user through realname or username");

        progressBar=(ProgressBar)mview.findViewById(R.id.pbarsearch);

        progressBar.setVisibility(View.GONE);

        if(getArguments()!=null)

        {
            new Search().execute(getArguments().getString("keyword"));
        }


        return mview;
    }

    class Search extends AsyncTask<String,String,String>
    {

        WebServiceHandler webServiceHandler;
        String status="";
        ArrayList<FollowModel> followModelArrayList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            followModelArrayList=new ArrayList<>();

            textview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                webServiceHandler=new WebServiceHandler(Utility.search);
                webServiceHandler.addFormField("keyword", params[0]);
                webServiceHandler.addFormField("user_id", userinfo.getString(Utility.user_id));
                JSONObject jsonObject=new JSONObject(webServiceHandler.finish());

                status=jsonObject.getString("status");
                if(status.equals("success"))
                {
                    JSONArray jsonArray=jsonObject.getJSONArray("userinfo");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject res=jsonArray.getJSONObject(i);
                        FollowModel followModel=new FollowModel();
                        followModel.setUserid(res.getString("to_user_id"));
                        followModel.setImage(res.getString("image"));
                        followModel.setUser_name(res.getString("username"));
                        followModel.setFrom_user_id(userinfo.getString(Utility.user_id));
                        followModel.setIs_followed(res.getString("followed"));

                        followModelArrayList.add(followModel);
                    }
                }

            }
            catch (Exception e)
            {
                Log.e("asf",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                if(isAdded()) {
                    progressBar.setVisibility(View.GONE);
                    searchlist.setAdapter(new SearchAdapter(getActivity(), followModelArrayList, 2, Utility.operation_onclicked_direct_follow));

                    if (followModelArrayList.size() == 0) {
                        textview.setVisibility(View.VISIBLE);
                        textview.setText("There is no such user in TagFrame");
                    }
                }

        }
    }
}
