package com.tagframe.tagframe.UI.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.listops;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by abhinav on 06/04/2016.
 */
public class Account extends Fragment {

    private View mview;

    LinearLayout mLinearLayout, mLinear_chan;
    LinearLayout mLinearLayoutHeader_userinfo, mLinearhear_change;

    ImageView expand_userinfo, expand_change;

    CircularImageView pro_pic;
    TextView username;

    listops listops;
    ImageView changepropic;


    EditText ed_username, ed_realname, ed_email, ed_description;


    //change password

    EditText ed_oldpass, ed_newpass, ed_confirmpass;
    TextView forgotpassword;
    ProgressBar pbarchangepassword;


    private static int RESULT_LOAD_IMAGE = 1;
    String picturePath = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_account, container, false);

        mLinearLayout = (LinearLayout) mview.findViewById(R.id.expandable_userinfo);
        //mLinearLayout.setVisibility(View.GONE);


        mLinearLayoutHeader_userinfo = (LinearLayout) mview.findViewById(R.id.header_userinfo);

        changepropic = (ImageView) mview.findViewById(R.id.acc_change_pro_pic);
        changepropic.setVisibility(View.VISIBLE);

        expand_userinfo = (ImageView) mview.findViewById(R.id.acc_expand_userinfo);
        expand_userinfo.setImageResource(R.drawable.down);

        mLinearLayoutHeader_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLinearLayout.getVisibility() == View.GONE) {
                    expand(mLinearLayout);
                    expand_userinfo.setImageResource(R.drawable.down);
                    changepropic.setVisibility(View.VISIBLE);
                } else {
                    collapse(mLinearLayout);
                    changepropic.setVisibility(View.GONE);
                    expand_userinfo.setImageResource(R.drawable.right);
                }
            }
        });

        //change password

        mLinear_chan = (LinearLayout) mview.findViewById(R.id.expandable_changepassword);
        mLinear_chan.setVisibility(View.GONE);

        mLinearhear_change = (LinearLayout) mview.findViewById(R.id.header_changepassword);

        expand_change = (ImageView) mview.findViewById(R.id.acc_expand_changepassword);

        mLinearhear_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLinear_chan.getVisibility() == View.GONE) {
                    expand(mLinear_chan);
                    expand_change.setImageResource(R.drawable.down);
                } else {
                    collapse(mLinear_chan);

                    expand_change.setImageResource(R.drawable.right);
                }
            }
        });

        listops = new listops(getActivity());

        pro_pic = (CircularImageView) mview.findViewById(R.id.acc_proimage);
        try {
            Picasso.with(getActivity()).load(listops.getString(Constants.user_pic)).into(pro_pic);
        } catch (Exception e) {
            pro_pic.setImageResource(R.drawable.pro_image);
        }
        username = (TextView) mview.findViewById(R.id.acc_username);


        ed_email = (EditText) mview.findViewById(R.id.acc_ed_email);
        ed_email.setText(listops.getString(Constants.user_email));


        ed_username = (EditText) mview.findViewById(R.id.acc_ed_username);
        ed_username.setText(listops.getString(Constants.user_name));


        ed_realname = (EditText) mview.findViewById(R.id.acc_ed_realname);
        ed_realname.setText(listops.getString(Constants.user_realname));

        ed_description = (EditText) mview.findViewById(R.id.acc_ed_description);
        ed_description.setText(listops.getString(Constants.user_descrip));


        Button save = (Button) mview.findViewById(R.id.acc_save_userinfo);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_realname.getText().toString().isEmpty() && !ed_username.getText().toString().isEmpty() &&
                        !ed_email.getText().toString().isEmpty() && !ed_description.getText().toString().isEmpty()) {

                    new changeuserinfo().execute(listops.getString(Constants.user_id), ed_email.getText().toString(), ed_username.getText().toString(), ed_realname.getText().toString(), ed_description.getText().toString(), picturePath);
                } else {
                    MyToast.popmessage("Please fill in all fields", getActivity());
                }
            }
        });

        changepropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });


        //change password

        ed_oldpass = (EditText) mview.findViewById(R.id.acc_ed_current_pass);
        ed_newpass = (EditText) mview.findViewById(R.id.acc_ed_new_pass);
        ed_confirmpass = (EditText) mview.findViewById(R.id.acc_ed_confirm_pass);

        pbarchangepassword = (ProgressBar) mview.findViewById(R.id.pbarchangepassword);
        forgotpassword = (TextView) mview.findViewById(R.id.acc_forgot_password);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpassword();
            }
        });

        Button changepass = (Button) mview.findViewById(R.id.acc_btn_change_pass);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpass = ed_oldpass.getText().toString();
                String newpass = ed_newpass.getText().toString();
                String newpassconfirm = ed_confirmpass.getText().toString();

                if (!oldpass.isEmpty() && !newpass.isEmpty() && !newpassconfirm.isEmpty()) {
                    if (newpass.equals(newpassconfirm)) {
                        new changepassword().execute(listops.getString(Constants.user_id), oldpass, newpass);

                    } else {
                        MyToast.popmessage("Password does not match", getActivity());
                    }
                } else {
                    MyToast.popmessage("Please fill in all fields", getActivity());
                }
            }
        });


        return mview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            picturePath = GetPaths.getPath(getActivity(), selectedImage);


            pro_pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    public void forgotpassword() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_forgot_password);
        final EditText email = (EditText) dialog.findViewById(R.id.forgot_email);

        final TextView mesage = (TextView) dialog.findViewById(R.id.forgot_text);

        dialog.findViewById(R.id.forgot_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.isValidEmail(email.getText().toString())) {
                    new forgotpassword().execute(Constants.forgot_password, email.getText().toString());
                    dialog.dismiss();
                } else {
                    mesage.setText("Please enter a valid email");
                }
            }
        });

        dialog.show();
    }

    class forgotpassword extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        WebServiceHandler webServiceHandler;
        String status;

        listops listops;

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Sending..");
            dialog.show();
            listops = new listops(getActivity());
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                webServiceHandler = new WebServiceHandler(params[0]);
                webServiceHandler.addFormField("email", params[1]);
                Log.e("dasd", webServiceHandler.finish());
                JSONObject toplevel = new JSONObject(webServiceHandler.finish());
                status = toplevel.getString("status");


            } catch (IOException q) {
                status = "url_error";
            } catch (JSONException e) {
                status = "json_error";
            }
            return null;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();
            Log.e("fas", status);

            if (status.equals("success")) {
                MyToast.popmessage("Your password has been sent to your email", getActivity());
            } else {
                MyToast.popmessage("Oops, an error occurred", getActivity());
            }
        }
    }

    private void expand(LinearLayout linearLayout) {
        //set Visible
        linearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearLayout.measure(widthSpec, heightSpec);

        final LinearLayout l = linearLayout;
        ValueAnimator mAnimator = slideAnimator(0, linearLayout.getMeasuredHeight(), l);
        mAnimator.start();
    }

    private void collapse(final LinearLayout linearLayout) {
        int finalHeight = linearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, linearLayout);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final LinearLayout linearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.height = value;
                linearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


    class changepassword extends AsyncTask<String, String, String> {

        WebServiceHandler webServiceHandler;
        String status = "";

        @Override
        protected void onPreExecute() {
            pbarchangepassword.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {


                webServiceHandler = new WebServiceHandler(Constants.change_password);
                webServiceHandler.addFormField("user_id", params[0]);
                webServiceHandler.addFormField("old_password", params[1]);
                webServiceHandler.addFormField("new_password", params[2]);
                JSONObject jsonObject = new JSONObject(webServiceHandler.finish());
                Log.e("das", jsonObject.toString());

                status = jsonObject.getString("status");

            } catch (Exception e) {
                Log.e("das", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbarchangepassword.setVisibility(View.GONE);
            if (status.equals("success")) {
                MyToast.popmessage("Password Changed Successfully, Login to continue", getActivity());
                Intent intent = new Intent(getActivity(), Authentication.class);
                startActivity(intent);
            } else {
                MyToast.popmessage(status, getActivity());
            }
        }
    }

    class changeuserinfo extends AsyncTask<String, String, String> {

        WebServiceHandler webServiceHandler;
        String status = "";

        @Override
        protected void onPreExecute() {
            pbarchangepassword.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {


                webServiceHandler = new WebServiceHandler(Constants.update_identiy);
                webServiceHandler.addFormField("user_id", params[0]);
                webServiceHandler.addFormField("email", params[1]);
                webServiceHandler.addFormField("username", params[2]);
                webServiceHandler.addFormField("first_name", params[3]);
                webServiceHandler.addFormField("description", params[4]);
                if (!picturePath.isEmpty()) {
                    File file = new File(picturePath);
                    webServiceHandler.addFilePart("profile_photo", file, 2, getActivity());
                }

                JSONObject jsonObject = new JSONObject(webServiceHandler.finish());
                Log.e("das", jsonObject.toString());

                status = jsonObject.getString("status");
                if (status.equals("success")) {
                    listops.putString(Constants.user_email, params[1]);
                    listops.putString(Constants.user_name, params[2]);
                    listops.putString(Constants.user_realname, params[3]);
                    listops.putString(Constants.user_descrip, params[4]);
                    listops.putString(Constants.user_pic, jsonObject.getString(Constants.user_pic));

                }

            } catch (Exception e) {
                Log.e("das", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbarchangepassword.setVisibility(View.GONE);
            if (status.equals("success")) {
                //update sliderdata

                ed_email.setText(listops.getString(Constants.user_email));


                ed_username.setText(listops.getString(Constants.user_name));


                ed_realname.setText(listops.getString(Constants.user_realname));


                ed_description.setText(listops.getString(Constants.user_descrip));


                MyToast.popmessage("Successfully updated", getActivity());
            } else {
                MyToast.popmessage(status, getActivity());
            }
        }
    }


}
