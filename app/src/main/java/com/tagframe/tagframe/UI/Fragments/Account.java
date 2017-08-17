package com.tagframe.tagframe.UI.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.tagframe.tagframe.Application.TagFrame;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 06/04/2016.
 */
public class Account extends Fragment {

    private View mview;
    private LinearLayout mLinearLayout, mLinear_chan;
    private LinearLayout mLinearLayoutHeader_userinfo, mLinearhear_change;
    private ImageView expand_userinfo, expand_change;
    private CircularImageView pro_pic;
    private TextView username;
    private ImageView changepropic;
    private EditText ed_username, ed_realname, ed_email, ed_description;
    private EditText ed_oldpass, ed_newpass, ed_confirmpass;
    private TextView forgotpassword;
    private ProgressBar pbarchangepassword;
    private Button btDeleteAccount;

    private static int RESULT_LOAD_IMAGE = 1;
    private AppPrefs AppPrefs;
    private String picturePath = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_account, container, false);

        AppPrefs = new AppPrefs(getActivity());

        btDeleteAccount = (Button) mview.findViewById(R.id.acc_btn_delete_pass);
        btDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        mLinearLayout = (LinearLayout) mview.findViewById(R.id.expandable_userinfo);
        //mLinearLayout.setVisibility(View.GONE);


        mLinearLayoutHeader_userinfo = (LinearLayout) mview.findViewById(R.id.header_userinfo);

        changepropic = (ImageView) mview.findViewById(R.id.acc_change_pro_pic);
        changepropic.setVisibility(View.VISIBLE);

        expand_userinfo = (ImageView) mview.findViewById(R.id.acc_expand_userinfo);
        expand_userinfo.setImageResource(R.drawable.ic_keyboard_arrow_down_blue_grey_700_24dp);

        mLinearLayoutHeader_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLinearLayout.getVisibility() == View.GONE) {
                    expand(mLinearLayout);
                    expand_userinfo.setImageResource(R.drawable.ic_keyboard_arrow_down_blue_grey_700_24dp);
                    changepropic.setVisibility(View.VISIBLE);
                } else {
                    collapse(mLinearLayout);
                    changepropic.setVisibility(View.GONE);
                    expand_userinfo.setImageResource(R.drawable.ic_keyboard_arrow_right_blue_grey_700_24dp);
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
                    expand_change.setImageResource(R.drawable.ic_keyboard_arrow_down_blue_grey_700_24dp);
                } else {
                    collapse(mLinear_chan);

                    expand_change.setImageResource(R.drawable.ic_keyboard_arrow_right_blue_grey_700_24dp);
                }
            }
        });


        pro_pic = (CircularImageView) mview.findViewById(R.id.acc_proimage);
        try {
            Picasso.with(getActivity()).load(AppPrefs.getString(Utility.user_pic)).into(pro_pic);
        } catch (Exception e) {
            pro_pic.setImageResource(R.drawable.pro_image);
        }
        username = (TextView) mview.findViewById(R.id.acc_username);


        ed_email = (EditText) mview.findViewById(R.id.acc_ed_email);
        ed_email.setText(AppPrefs.getString(Utility.user_email));


        ed_username = (EditText) mview.findViewById(R.id.acc_ed_username);
        ed_username.setText(AppPrefs.getString(Utility.user_name));


        ed_realname = (EditText) mview.findViewById(R.id.acc_ed_realname);
        ed_realname.setText(AppPrefs.getString(Utility.user_realname));

        ed_description = (EditText) mview.findViewById(R.id.acc_ed_description);
        ed_description.setText(AppPrefs.getString(Utility.user_descrip));


        Button save = (Button) mview.findViewById(R.id.acc_save_userinfo);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_realname.getText().toString().isEmpty() && !ed_username.getText().toString().isEmpty() &&
                        !ed_email.getText().toString().isEmpty()) {

                    new changeuserinfo().execute(AppPrefs.getString(Utility.user_id), ed_email.getText().toString(), ed_username.getText().toString(), ed_realname.getText().toString(), ed_description.getText().toString(), picturePath);
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
                        new changepassword().execute(AppPrefs.getString(Utility.user_id), oldpass, newpass);

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

    private void deleteAccount() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_delete_account);
        final LinearLayout controls = (LinearLayout) dialog.findViewById(R.id.layout_logout_controls);
        final LinearLayout progress = (LinearLayout) dialog.findViewById(R.id.layout_logging_out);


        dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppPrefs listops = new AppPrefs(getActivity());


                controls.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                apiInterface.removeUser(AppPrefs.getString(Utility.user_id)).enqueue(new Callback<ResponsePojo>() {
                    @Override
                    public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {

                        if (response.body().getStatus().equals(Utility.success_response)) {
                            AppPrefs.putString(Utility.loginstatuskey, "");
                            Utility.flushuserinfo(getActivity());
                            Intent intent2 = new Intent(getActivity(), Authentication.class);
                            startActivity(intent2);
                            getActivity().finish();
                        } else {
                            PopMessage.makeshorttoast(getActivity(), "There is a problem deleting account");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePojo> call, Throwable t) {
                        PopMessage.makeshorttoast(getActivity(), "There is a problem deleting account");
                        dialog.dismiss();
                    }
                });


            }
        });
        dialog.findViewById(R.id.nobtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            picturePath = GetPaths.getPath(getActivity(), selectedImage);


            pro_pic.setImageBitmap(BitmapHelper.decodeFile(getActivity(), new File(picturePath)));

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
                if (Utility.isValidEmail(email.getText().toString())) {
                    new forgotpassword().execute(Utility.forgot_password, email.getText().toString());
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

        AppPrefs AppPrefs;

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Sending..");
            dialog.show();
            AppPrefs = new AppPrefs(getActivity());
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


                webServiceHandler = new WebServiceHandler(Utility.change_password);
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


                webServiceHandler = new WebServiceHandler(Utility.update_identiy);
                webServiceHandler.addFormField("user_id", params[0]);
                webServiceHandler.addFormField("email", params[1]);
                webServiceHandler.addFormField("username", params[2]);
                webServiceHandler.addFormField("first_name", params[3]);
                webServiceHandler.addFormField("description", params[4]);
                if (!picturePath.isEmpty()) {
                    File file = new File(picturePath);
                    Log.e("length", file.length() + "");
                    if (file.length() / 1000 > 512) {
                        File fileCache = new File(getActivity().getCacheDir(), "temp.png");
                        Bitmap loadBitmap = BitmapHelper.decodeFile(getActivity(), file);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        loadBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bitmapData = byteArrayOutputStream.toByteArray();

                        FileOutputStream fileOutputStream = new FileOutputStream(fileCache);
                        fileOutputStream.write(bitmapData);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        file = fileCache;

                    }
                    webServiceHandler.addFilePart("profile_photo", file, 3, getActivity());
                }

                JSONObject jsonObject = new JSONObject(webServiceHandler.finish());
                Log.e("error", jsonObject.toString());
                JSONObject userInfo = jsonObject.getJSONObject("userinfo");

                status = jsonObject.getString("status");
                if (status.equals("success")) {
                    User user=AppPrefs.getUser();
                    user.setEmail(userInfo.getString(Utility.user_email));
                    user.setUsername(userInfo.getString(Utility.user_name));
                    user.setRealname(userInfo.getString(Utility.user_realname));
                    user.setDescription(userInfo.getString(Utility.user_descrip));
                    user.setProfile_image(userInfo.getString(Utility.user_pic));
                    AppPrefs.putUser(user);
                    AppPrefs.putString(Utility.user_email, userInfo.getString(Utility.user_email));
                    AppPrefs.putString(Utility.user_name, userInfo.getString(Utility.user_name));
                    AppPrefs.putString(Utility.user_realname, userInfo.getString(Utility.user_realname));
                    AppPrefs.putString(Utility.user_descrip, userInfo.getString(Utility.user_descrip));
                    AppPrefs.putString(Utility.user_pic, userInfo.getString(Utility.user_pic));

                }

            } catch (Exception e) {
                Log.e("das", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isAdded()) {
                pbarchangepassword.setVisibility(View.GONE);
                if (status.equals("success")) {
                    //update sliderdata

                    ed_email.setText(AppPrefs.getString(Utility.user_email));


                    ed_username.setText(AppPrefs.getString(Utility.user_name));


                    ed_realname.setText(AppPrefs.getString(Utility.user_realname));


                    ed_description.setText(AppPrefs.getString(Utility.user_descrip));


                    MyToast.popmessage("Successfully updated", getActivity());
                } else {
                    MyToast.popmessage(status, getActivity());
                }
            }
        }
    }


}
