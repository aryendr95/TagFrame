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

import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.TagStreamResponseModel;
import com.tagframe.tagframe.Models.TagStream_Model;
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

import org.json.JSONArray;
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
                        new loadtagstreamtask().execute();
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

    class loadtagstreamtask extends AsyncTask<String,String,String>
    {
        WebServiceHandler webServiceHandler;
        String status;
        ArrayList<TagStream_Model> tagStream_models;

        int record_length=10;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();



        }

        @Override
        protected String doInBackground(String... params) {
            try
            {

                webServiceHandler=new WebServiceHandler(Constants.tagstreams_url);
                webServiceHandler.addFormField("user_id", AppPrefs.getString(Constants.user_id));
                webServiceHandler.addFormField("next_records","10");
                String result=webServiceHandler.finish();
                Log.e("cxc",result);
                JSONObject jsonObject=new JSONObject(result);
                status=jsonObject.getString("status");


                if(status.equals("success"))
                {
                    JSONArray records=jsonObject.getJSONArray("records");
                    record_length=records.length();
                    Log.e("cxc",record_length+"");

                    for(int i=0;i<records.length();i++) {

                        JSONObject rec=records.getJSONObject(i);
                        TagStream_Model tagStream_model = new TagStream_Model();
                        tagStream_model.setName(rec.getString("name"));
                        tagStream_model.setProfile_picture(rec.getString("profile_picture"));
                        tagStream_model.setThumbnail(rec.getString("thumbnail"));
                        tagStream_model.setDataurl(rec.getString("data_url"));
                        tagStream_model.setEvent_id(rec.getString("event_id"));
                        tagStream_model.setTitle(rec.getString("title"));
                        tagStream_model.setDescription(rec.getString("description"));
                        tagStream_model.setProduct_url(rec.getString("product_url"));
                        tagStream_model.setProduct_image(rec.getString("product_image"));
                        tagStream_model.setPrduct_name(rec.getString("product_name"));
                        tagStream_model.setNumber_of_likes(rec.getString("count_like"));
                        tagStream_model.setSharelink(rec.getString("website_video_url"));
                        tagStream_model.setLike_video(rec.getString("is_liked"));
                        JSONArray frames=rec.getJSONArray("frames");
                        ArrayList<FrameList_Model> frameList_models=new ArrayList<>();
                        for(int f=0;f<frames.length();f++)
                        {
                            JSONObject frameobject=frames.getJSONObject(f);
                            FrameList_Model frameList_model=new FrameList_Model();
                            frameList_model.setImagepath(frameobject.getString("frame_thumbnail_url"));
                            frameList_model.setName(frameobject.getString("frame_title"));
                            frameList_model.setStarttime(Integer.parseInt(frameobject.getString("frame_start_time")) * 1000);
                            frameList_model.setEndtime(Integer.parseInt(frameobject.getString("frame_end_time")) * 1000);
                            frameList_model.setFrametype((frameobject.getString("frame_media_type").equals("IMAGE") ? Constants.frametype_image : Constants.frametype_video));
                            frameList_model.setFrame_resource_type(Constants.frame_resource_type_internet);
                            frameList_model.setFrame_data_url(frameobject.getString("frame_data_url"));
                            frameList_models.add(frameList_model);
                        }

                        tagStream_model.setFrameList_modelArrayList(frameList_models);

                        tagStream_models.add(tagStream_model);

                        Log.e("fas", tagStream_models.size()+"");
                    }
                }

            }
            catch (IOException E)
            {

            }
            catch (JSONException E)
            {
                Log.e("fas",E.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isAdded()) {
                AppPrefs.puttagstreamlist(tagStream_models);
                Intent intent = new Intent(getActivity(), Modules.class);
                startActivity(intent);
                getActivity().finish();
            }

        }
    }



}
