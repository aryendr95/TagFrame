package com.tagframe.tagframe.UI.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tagframe.tagframe.Application.TagFrame;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.ResponseModels.RmAuthentication;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.AppSingleton;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.UI.Acitivity.ModulesNew;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.sql.Time;
import java.util.TimeZone;

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
    private ProgressBar progressBar;
    private TextView txt;
    private String android_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_splash, container, false);

        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        progressBar = (ProgressBar) mview.findViewById(R.id.splashbar);
        AppPrefs = new AppPrefs(getActivity());
        txt = (TextView) mview.findViewById(R.id.txtsplashmessage);

        if (getArguments() != null) {
            txt.setText("Loading...");
            loadTagStream();
        } else {
            loginUser();
        }
        return mview;
    }

    public void loginUser() {
        final AppPrefs appPrefs = new AppPrefs(getActivity());
        final User user = appPrefs.getUser();
        TimeZone tz = TimeZone.getDefault();
        Utility.getApiCaller()
                .login(user.getUsername(), user.getPassword(), AppSingleton.android_id, appPrefs.getString(
                        Utility.shp_user_Token), tz.getID())
                .enqueue(new Callback<RmAuthentication>() {
                    @Override
                    public void onResponse(Call<RmAuthentication> call, Response<RmAuthentication> response) {
                        if (isAdded()) {
                            if (response.body().getStatus().equals("success")) {
                                User user1 = response.body().getUser();
                                user1.setPassword(appPrefs.getString(Utility.user_password));
                                user1.setLoggedin(true);
                                appPrefs.putUser(user1);
                                txt.setText("Loading..");
                                loadTagStream();
                            } else {
                                PopMessage.makeshorttoast(getContext(), response.body().getStatus());
                                ((Authentication) getActivity()).setadapter();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RmAuthentication> call, Throwable t) {
                        if (isAdded()) {
                            PopMessage.makeshorttoast(getContext(), Utility.ERROR_MESSAGE);
                            getActivity().finish();
                        }
                    }
                });
    }

    //background task for logging in
    class Logintask extends AsyncTask<String, String, String> {
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
                webServiceHandler.addFormField("device_id", android_id);
                webServiceHandler.addFormField(Utility.shp_user_Token, AppPrefs.getString(Utility.shp_user_Token));

                JSONObject toplevel = new JSONObject(webServiceHandler.finish());
                status = toplevel.getString("status");
                if (status.equals("success")) {
                    userinfo = toplevel.getJSONObject("userinfo");
                }
            } catch (IOException q) {
                status = "url_error";
                Log.e("das", q.getMessage());
            } catch (JSONException e) {
                status = "json_error";
                Log.e("das", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (status.equals("success")) {
                if (userinfo == null) {
                    // generate code for telling the backend handling this json error
                    MyToast.popmessage("Oops, there seems to be an error", getActivity());

                    Log.e("das", "hg");
                } else {
                    try {

                        //storing the userinformation in persistant storage for later use
                        AppPrefs.putString(Utility.user_id, userinfo.getString(Utility.user_id));
                        AppPrefs.putString(Utility.user_name, userinfo.getString(Utility.user_name));
                        AppPrefs.putString(Utility.user_email, userinfo.getString(Utility.user_email));
                        AppPrefs.putString(Utility.user_realname, userinfo.getString(Utility.user_realname));
                        AppPrefs.putString(Utility.user_pic, userinfo.getString(Utility.user_pic));
                        AppPrefs.putString(Utility.user_descrip, userinfo.getString(Utility.user_descrip));
                        AppPrefs.putString(Utility.total_events, userinfo.getString(Utility.total_events));
                        AppPrefs.putString(Utility.total_frames, userinfo.getString(Utility.total_frames));
                        AppPrefs.putString(Utility.number_of_followers,
                                userinfo.getString(Utility.number_of_followers));
                        AppPrefs.putString(Utility.number_of_following,
                                userinfo.getString(Utility.number_of_following));
                        AppPrefs.putString(Utility.loginstatuskey, Utility.loginstatusvalue);
                        AppPrefs.putString(Utility.unread_notifications,
                                userinfo.getString(Utility.unread_notifications));

                        /*Intent intent=new Intent(getActivity(), Modules.class);
                        startActivity(intent);
                        getActivity().finish();*/

                        txt.setText("Loading..");
                        loadTagStream();
                    } catch (JSONException e) {
                        Log.e("das", e.getMessage());
                        // generate code for telling the backend handling this json error
                        MyToast.popmessage("Oops, there seems to be an error", getActivity());
                    }
                }
            } else if (status.equals("url_error")) {
                //generate code for telling backend handling bad url
                MyToast.popmessage("Oops, there seems to be an error", getActivity());

                Log.e("das", "hgj" + status);
            } else if (status.equals("json_error")) {
                /// generate code for telling the backend handling this json error
                MyToast.popmessage("Oops, there seems to be an error", getActivity());
                AppPrefs.putString(Utility.loginstatuskey, "");
                Log.e("das", "hg" + status);
            } else {
                MyToast.popmessage(status, getActivity());

                ((Authentication) getActivity()).setadapter();
            }
        }
    }

    public void loadTagStream() {
        if (Networkstate.haveNetworkConnection(getActivity())) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ListResponseModel> call = apiInterface.getTagStream(AppPrefs.getString(Utility.user_id));
            call.enqueue(new Callback<ListResponseModel>() {
                @Override
                public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {
                    if (isAdded()) {
                        try {

                            if (response.body().getStatus().equals("success")) {
                                ArrayList<Event_Model> tagStream_models = response.body().getTagStreamArrayList();
                                AppPrefs.puttagstreamlist(tagStream_models);

                                Intent intent = new Intent(getActivity(), ModulesNew.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                PopMessage.makeshorttoast(getActivity(), response.body().getStatus());
                            }
                        } catch (Exception e) {
                            PopMessage.makeshorttoast(getActivity(),
                                    "There is some error, try after some time..");
                            getActivity().finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ListResponseModel> call, Throwable t) {

                }
            });
        } else {
            PopMessage.makeshorttoast(getActivity(), "No Internet Connection");
            getActivity().finish();
        }
    }
}
