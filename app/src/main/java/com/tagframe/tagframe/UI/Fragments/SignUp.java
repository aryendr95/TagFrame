package com.tagframe.tagframe.UI.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by abhinav on 04/04/2016.
 */
public class SignUp extends Fragment {


    private View mview;
    private CheckBox mcheckbox;
    private EditText ed_username,ed_password,ed_realname,ed_email;
    private Button bt_login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.fragment_signup,container,false);

        ed_username=(EditText)mview.findViewById(R.id.sign_usrname);
        ed_password=(EditText)mview.findViewById(R.id.sign_password);
        ed_email=(EditText)mview.findViewById(R.id.sign_email);
        ed_realname=(EditText)mview.findViewById(R.id.sign_realname);

        bt_login=(Button)mview.findViewById(R.id.sign_action);



        mcheckbox=(CheckBox)mview.findViewById(R.id.sign_show);
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


       /* ed_username.setVisibility(View.GONE);
        ed_password.setVisibility(View.GONE);
        ed_realname.setVisibility(View.GONE);
        mcheckbox.setVisibility(View.GONE);
        ed_email.setVisibility(View.GONE);
        bt_login.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ed_username.setVisibility(View.VISIBLE);
                ed_password.setVisibility(View.VISIBLE);
                ed_realname.setVisibility(View.VISIBLE);
                mcheckbox.setVisibility(View.VISIBLE);
                ed_email.setVisibility(View.VISIBLE);
                bt_login.setVisibility(View.VISIBLE);

                    new SlideInAnimation(ed_username).setDirection(Animation.DIRECTION_UP).animate();
                new SlideInAnimation(ed_password).setDirection(Animation.DIRECTION_UP).animate();
                new SlideInAnimation(ed_realname).setDirection(Animation.DIRECTION_UP).animate();
                new SlideInAnimation(ed_email).setDirection(Animation.DIRECTION_UP).animate();
                new SlideInAnimation(mcheckbox).setDirection(Animation.DIRECTION_UP).animate();
                new SlideInAnimation(bt_login).setDirection(Animation.DIRECTION_UP).animate();
            }
        },200);*/



        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname=ed_username.getText().toString();
                String password=ed_password.getText().toString();
                String realname=ed_realname.getText().toString();
                String email=ed_email.getText().toString();


                if(Networkstate.haveNetworkConnection(getActivity())) {
                    if (!uname.isEmpty() && !password.isEmpty() &&!realname.isEmpty() && !email.isEmpty() ) {

                        if(Utility.isValidEmail(email)) {
                            new SignUpuser().execute(Utility.signup, email, uname,realname ,password);
                        }
                        else
                        {
                            MyToast.popmessage("Please input a valid email", getActivity());
                        }
                    } else {

                        ((Authentication)getActivity()).setadapter();
                        MyToast.popmessage("Please input all the fields", getActivity());

                    }
                }
                else
                {
                    MyToast.popmessage("Sorry, no internet connection", getActivity());
                }


            }
        });

        return mview;
    }
    class SignUpuser extends AsyncTask<String,String,String>
    {

        ProgressDialog dialog;
        WebServiceHandler webServiceHandler;
        String status;
        JSONObject userinfo;
        AppPrefs AppPrefs;
        String save;

        @Override
        protected void onPreExecute() {

            dialog=new ProgressDialog(getActivity());
            dialog.setMessage("Signing up..");
            dialog.show();
            AppPrefs =new AppPrefs(getActivity());





            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {

                webServiceHandler = new WebServiceHandler(params[0]);
                webServiceHandler.addFormField("email", params[1]);
                webServiceHandler.addFormField("username", params[2]);
                webServiceHandler.addFormField("first_name", params[3]);
                webServiceHandler.addFormField("password", params[4]);



                JSONObject toplevel=new JSONObject(webServiceHandler.finish());
                status=toplevel.getString("status");
                if(status.equals("success"))
                {
                    userinfo=toplevel.getJSONObject("userinfo");
                    save=params[4];
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
            if (isAdded()) {
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

                            AppPrefs.putString(Utility.loginstatuskey, Utility.loginstatusvalue);
                            AppPrefs.putString(Utility.user_password, save);

                        /*Intent intent=new Intent(getActivity(), Modules.class);
                        startActivity(intent);
                        getActivity().finish();*/

                            ((Authentication) getActivity()).setadapteraddprofile(new AddProfilePhoto());
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
                    Log.e("das", "hg" + status);
                } else {
                    MyToast.popmessage(status, getActivity());
                }
            }
        }

    }


}
