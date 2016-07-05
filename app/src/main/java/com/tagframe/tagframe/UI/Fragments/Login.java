package com.tagframe.tagframe.UI.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.listops;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abhinav on 04/04/2016.
 */
public class Login extends Fragment {

    //Views
    private View mview;
    private CheckBox mcheckbox;
    private EditText ed_username,ed_password;
    private Button bt_login;
    private TextView txt_forgotpassword;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_login,container,false);

        //recognising views
        ed_username=(EditText)mview.findViewById(R.id.login_usrname);
        ed_password=(EditText)mview.findViewById(R.id.login_password);

        bt_login=(Button)mview.findViewById(R.id.login_action);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String uname=ed_username.getText().toString();
                String password=ed_password.getText().toString();
                Networkstate networkstate=new Networkstate(getActivity());

                if(networkstate.haveNetworkConnection()) {
                    if (!uname.isEmpty() || !password.isEmpty()) {
                        new Logintask().execute(Constants.login, uname, password);
                    } else {
                        MyToast.popmessage("Please input all the fields", getActivity());
                    }
                }
                else
                {
                    MyToast.popmessage("Sorry, no internet connection", getActivity());
                }


            }
        });

        mcheckbox=(CheckBox)mview.findViewById(R.id.login_show);
        mcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    ed_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    ed_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        //forgot_password

        txt_forgotpassword=(TextView)mview.findViewById(R.id.login_forgot);
        txt_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_forgot_password);
                final EditText email=(EditText)dialog.findViewById(R.id.forgot_email);

                final TextView mesage=(TextView)dialog.findViewById(R.id.forgot_text);

                dialog.findViewById(R.id.forgot_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isValidEmail(email.getText().toString()))
                        {
                            new forgotpassword().execute(Constants.forgot_password,email.getText().toString());
                            dialog.dismiss();
                        }
                        else
                        {
                            mesage.setText("Please enter a valid email");
                        }
                    }
                });

                dialog.show();
            }
        });


        return mview;
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
//background task for logging in
    class Logintask extends AsyncTask<String,String,String>
    {

        ProgressDialog dialog;
        WebServiceHandler webServiceHandler;
        String status;
        JSONObject userinfo;
        listops listops;
        String save;

        @Override
        protected void onPreExecute() {

            dialog=new ProgressDialog(getActivity());
            dialog.setMessage("Signing in..");
            dialog.show();
            listops=new listops(getActivity());





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
                    save=params[2];
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
            dialog.dismiss();
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
                        listops.putString(Constants.user_id, userinfo.getString(Constants.user_id));
                        listops.putString(Constants.user_name, userinfo.getString(Constants.user_name));
                        listops.putString(Constants.user_email, userinfo.getString(Constants.user_email));
                        listops.putString(Constants.user_realname, userinfo.getString(Constants.user_realname));
                        listops.putString(Constants.user_pic, userinfo.getString(Constants.user_pic));
                        listops.putString(Constants.total_events, userinfo.getString(Constants.total_events));
                        listops.putString(Constants.total_frames, userinfo.getString(Constants.total_frames));
                        listops.putString(Constants.number_of_followers, userinfo.getString(Constants.number_of_followers));
                        listops.putString(Constants.number_of_following, userinfo.getString(Constants.number_of_following));
                        listops.putString(Constants.loginstatuskey,Constants.loginstatusvalue);
                        listops.putString(Constants.user_password,save);

                        /*Intent intent=new Intent(getActivity(), Modules.class);
                        startActivity(intent);
                        getActivity().finish();*/

                        ((Authentication)getActivity()).setadapteraddprofile(new Splash());
                    }
                    catch (JSONException e)
                    {

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
                Log.e("das", "hg"+status);
            }

            else
            {
                MyToast.popmessage(status, getActivity());
            }
            }

        }

    //backgrounfd task for forgot password


     class forgotpassword extends AsyncTask<String,String,String>
    {
        ProgressDialog dialog;
        WebServiceHandler webServiceHandler;
        String status;

        listops listops;

        @Override
        protected void onPreExecute() {

            dialog=new ProgressDialog(getActivity());
            dialog.setMessage("Sending..");
            dialog.show();
            listops=new listops(getActivity());
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                webServiceHandler = new WebServiceHandler(params[0]);
                webServiceHandler.addFormField("email", params[1]);
                Log.e("dasd",webServiceHandler.finish());
                JSONObject toplevel=new JSONObject(webServiceHandler.finish());
                status=toplevel.getString("status");




            }
            catch (IOException q)
            {
                status="url_error";
            }
            catch(JSONException e)
            {
                status="json_error";
            }
            return null;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();
            Log.e("fas",status);

            if(status.equals("success"))
            {
                MyToast.popmessage("Your password has been sent to your email",getActivity());
            }
            else
            {
                MyToast.popmessage("Oops, an error occurred",getActivity());
            }
        }
    }

    }

