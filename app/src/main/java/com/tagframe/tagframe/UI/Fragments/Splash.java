package com.tagframe.tagframe.UI.Fragments;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 04/04/2016.
 */
public class Splash extends Fragment {


    private View mview;
    AppPrefs AppPrefs;
    ProgressBar progressBar;
    TextView txt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_splash,container,false);

        progressBar=(ProgressBar)mview.findViewById(R.id.splashbar);


        AppPrefs =new AppPrefs(getActivity());
        txt=(TextView)mview.findViewById(R.id.txtsplashmessage);
        new Logintask().execute(Constants.login, AppPrefs.getString(Constants.user_name), AppPrefs.getString(Constants.user_password));

        return mview;
    }

    //background task for logging in
    class Logintask extends AsyncTask<String,String,String>
    {


        WebServiceHandler webServiceHandler;
        String status;
        JSONObject userinfo;


        @Override
        protected void onPreExecute() {





            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {

                webServiceHandler = new WebServiceHandler(params[0]);
                webServiceHandler.addFormField("username", params[1]);
                webServiceHandler.addFormField("password", params[2]);


                JSONObject toplevel=new JSONObject(webServiceHandler.finish());
                status=toplevel.getString("status");
                if(status.equals("success"))
                {
                    userinfo=toplevel.getJSONObject("userinfo");

                }



            }
            catch (IOException q)
            {
                status="url_error";
                Log.e("das", q.getMessage());
            }
            catch(JSONException e)
            {
                status="json_error";
                Log.e("das",e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(status.equals("success"))
            {
                if(userinfo==null)
                {
                    // generate code for telling the backend handling this json error
                    MyToast.popmessage("Oops, there seems to be an error", getActivity());

                    Log.e("das", "hg");

                }
                else
                {
                    try {

                        //storing the userinformation in persistant storage for later use
                        AppPrefs.putString(Constants.user_id, userinfo.getString(Constants.user_id));
                        AppPrefs.putString(Constants.user_name, userinfo.getString(Constants.user_name));
                        AppPrefs.putString(Constants.user_email, userinfo.getString(Constants.user_email));
                        AppPrefs.putString(Constants.user_realname, userinfo.getString(Constants.user_realname));
                        AppPrefs.putString(Constants.user_pic, userinfo.getString(Constants.user_pic));
                        AppPrefs.putString(Constants.user_descrip, userinfo.getString(Constants.user_descrip));
                        AppPrefs.putString(Constants.total_events, userinfo.getString(Constants.total_events));
                        AppPrefs.putString(Constants.total_frames, userinfo.getString(Constants.total_frames));
                        AppPrefs.putString(Constants.number_of_followers, userinfo.getString(Constants.number_of_followers));
                        AppPrefs.putString(Constants.number_of_following, userinfo.getString(Constants.number_of_following));
                        AppPrefs.putString(Constants.loginstatuskey,Constants.loginstatusvalue);

                        /*Intent intent=new Intent(getActivity(), Modules.class);
                        startActivity(intent);
                        getActivity().finish();*/

                        txt.setText("Loading..");
                        loadTagStream();
                    }
                    catch (JSONException e)
                    {
                        Log.e("das",e.getMessage());
                        // generate code for telling the backend handling this json error
                        MyToast.popmessage("Oops, there seems to be an error", getActivity());
                    }
                }
            }
            else if(status.equals("url_error"))
            {
                //generate code for telling backend handling bad url
                MyToast.popmessage("Oops, there seems to be an error", getActivity());

                Log.e("das", "hgj"+status);
            }
            else if(status.equals("json_error"))
            {
                /// generate code for telling the backend handling this json error
                MyToast.popmessage("Oops, there seems to be an error", getActivity());
                AppPrefs.putString(Constants.loginstatuskey, "");
                Log.e("das", "hg"+status);
            }
            else
            {
                MyToast.popmessage(status, getActivity());

                ((Authentication)getActivity()).setadapter();
            }
        }

    }

    public void loadTagStream()
    {
        if(Networkstate.haveNetworkConnection(getActivity()))
        {
            ApiInterface apiInterface=ApiClient.getClient().create(ApiInterface.class);
            Call<ListResponseModel> call=apiInterface.getTagStream(AppPrefs.getString(Constants.user_id));
            call.enqueue(new Callback<ListResponseModel>() {
                @Override
                public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {
                    try {
                        if (response.body().getStatus().equals("success")) {
                            ArrayList<Event_Model> tagStream_models = response.body().getTagStreamArrayList();
                            AppPrefs.puttagstreamlist(tagStream_models);

                            Intent intent = new Intent(getActivity(), Modules.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            PopMessage.makeshorttoast(getActivity(), response.body().getStatus());
                        }
                    }
                    catch (Exception e)
                    {
                        PopMessage.makeshorttoast(getActivity(),"There is some error, try after some time..");
                        getActivity().finish();
                    }
                }

                @Override
                public void onFailure(Call<ListResponseModel> call, Throwable t) {

                }
            });
        }
        else
        {
            PopMessage.makeshorttoast(getActivity(),"No Internet Connection");
            getActivity().finish();
        }
    }





}
