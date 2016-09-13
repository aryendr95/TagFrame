package com.tagframe.tagframe.UI.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Menu_Action;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by abhinav on 05/04/2016.
 */
public class ProfileOld extends Fragment {

    private View mview;
    private ImageView pro_user_iamge;
    private TextView pro_user_name, pro_description;
    ;
    private Button pro_edit_profile, pro_save_profile, pro_cancel_profile;


    private LinearLayout pro_timeline, pro_evetns, pro_frames, pro_following, pro_followers;
    // private LinearLayout pro_timeline_bar,pro_evetns_bar,pro_frames_bar,pro_following_bar,pro_followers_bar;

    private LinearLayout edit_profile_options, wholelayout, profile_section;

    private ImageView edit_photo;

    private TextView number_of_events, number_of_frames, number_of_following, number_of_followers, number_of_timeline;

    private FrameLayout mlayout_profile;

    private ProgressBar progressBar;


    AppPrefs userinfo_data;

    String userid, username, userpic;

    int height, usertype = Utility.user_type_self;

    String f_value = "No";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_profile, container, false);

        userinfo_data = new AppPrefs(getActivity());

        init();

        final ImageButton imgbtn = (ImageButton) mview.findViewById(R.id.createevent_profile);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((Modules) getActivity()).generate_media_chooser(imgbtn);
            }
        });

        return mview;
    }

    private void init() {

        //edit_profile_options=(LinearLayout)mview.findViewById(R.id.edit_profile_options);

        edit_photo = (ImageView) mview.findViewById(R.id.edit_profile_picture);

        pro_user_iamge = (ImageView) mview.findViewById(R.id.pro_user_pic);
        //Picasso.with(getActivity()).load(userinfo.getString(Utility.user_pic)).into(pro_user_iamge);

        pro_user_name = (TextView) mview.findViewById(R.id.pro_user_name);
        //pro_user_name.setText(userinfo.getString(Utility.user_name));

        //pro_user_realname = (TextView) mview.findViewById(R.id.pro_user_realname);
        // pro_user_realname.setText(userinfo.getString(Utility.user_realname));

        pro_description = (TextView) mview.findViewById(R.id.pro_user_description);
        //pro_description.setText(userinfo.getString(Utility.user_descrip));

        // Log.e("fasf", userinfo.getString(Utility.user_descrip));

        pro_edit_profile = (Button) mview.findViewById(R.id.btn_edit_profile);
        pro_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usertype == Utility.user_type_self) {
                    Intent intent = new Intent(getActivity(), Menu_Action.class);
                    intent.putExtra("name", "Edit Profile");

                    startActivity(intent);
                    getActivity().finish();
                } else if (usertype == Utility.user_type_following) {

                    if (getActivity() instanceof Modules) {
                        progressBar.setVisibility(View.VISIBLE);
                        Broadcastresults mReciever = ((Modules) getActivity()).register_reviever();
                        Intent intent = new Intent(getActivity(), IntentServiceOperations.class);
                        intent.putExtra("operation", Utility.operation_unfollow_profile);
                        intent.putExtra("from_userid", userinfo_data.getString(Utility.user_id));
                        intent.putExtra("to_userid", userid);
                        intent.putExtra("type", "following");
                        intent.putExtra("receiver", mReciever);
                        getActivity().startService(intent);
                    }

                } else if (usertype == Utility.user_type_followers) {
                    if (getActivity() instanceof Modules) {
                        progressBar.setVisibility(View.VISIBLE);
                        Broadcastresults mReciever = ((Modules) getActivity()).register_reviever();
                        Intent intent = new Intent(getActivity(), IntentServiceOperations.class);
                        intent.putExtra("operation", Utility.operation_follow_profile);
                        intent.putExtra("from_userid", userinfo_data.getString(Utility.user_id));
                        intent.putExtra("to_userid", userid);
                        intent.putExtra("type", "following");
                        intent.putExtra("receiver", mReciever);
                        getActivity().startService(intent);
                    }
                }

            }
        });


        pro_timeline = (LinearLayout) mview.findViewById(R.id.pro_timeline);
       /* pro_timeline_bar=(LinearLayout)mview.findViewById(R.id.pro_timeline_bar);
        pro_evetns_bar=(LinearLayout)mview.findViewById(R.id.pro_event_bar);
        pro_frames_bar=(LinearLayout)mview.findViewById(R.id.pro_frame_bar);
        pro_following_bar=(LinearLayout)mview.findViewById(R.id.pro_following_bar);
        pro_followers_bar=(LinearLayout)mview.findViewById(R.id.pro_followers_bar);*/

        pro_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   pro_timeline_bar.setVisibility(View.VISIBLE);
                pro_evetns_bar.setVisibility(View.INVISIBLE);
                pro_frames_bar.setVisibility(View.INVISIBLE);
                pro_following_bar.setVisibility(View.INVISIBLE);
                pro_followers_bar.setVisibility(View.INVISIBLE);*/

                pro_timeline.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                pro_evetns.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_frames.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_following.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_followers.setBackgroundColor(getActivity().getResources().getColor(R.color.white));


                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.framelayout_profile);
                if (f instanceof TimeLine) {
                    ((TimeLine) f).scrolltofirst();
                } else {
                    TimeLine fr = new TimeLine();
                    changefragment(fr);
                }
            }
        });


        pro_evetns = (LinearLayout) mview.findViewById(R.id.pro_event);

        pro_evetns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* pro_timeline_bar.setVisibility(View.INVISIBLE);
                pro_evetns_bar.setVisibility(View.VISIBLE);
                pro_frames_bar.setVisibility(View.INVISIBLE);
                pro_following_bar.setVisibility(View.INVISIBLE);
                pro_followers_bar.setVisibility(View.INVISIBLE);*/

                pro_timeline.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_evetns.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                pro_frames.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_following.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_followers.setBackgroundColor(getActivity().getResources().getColor(R.color.white));


                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.framelayout_profile);
                if (f instanceof User_Events) {
                    ((User_Events) f).scrolltofirst();
                } else {

                    User_Events fr = new User_Events();
                    changefragment(fr);
                }
            }
        });
        pro_frames = (LinearLayout) mview.findViewById(R.id.pro_frames);
        pro_frames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* pro_timeline_bar.setVisibility(View.INVISIBLE);
                pro_evetns_bar.setVisibility(View.INVISIBLE);
                pro_frames_bar.setVisibility(View.VISIBLE);
                pro_following_bar.setVisibility(View.INVISIBLE);
                pro_followers_bar.setVisibility(View.INVISIBLE);*/

                pro_timeline.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_evetns.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_frames.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                pro_following.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_followers.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                User_Frames fr = new User_Frames();
                changefragment(fr);
            }
        });
        pro_following = (LinearLayout) mview.findViewById(R.id.pro_following);
        pro_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  pro_timeline_bar.setVisibility(View.INVISIBLE);
                pro_evetns_bar.setVisibility(View.INVISIBLE);
                pro_frames_bar.setVisibility(View.INVISIBLE);
                pro_following_bar.setVisibility(View.VISIBLE);
                pro_followers_bar.setVisibility(View.INVISIBLE);*/

                pro_timeline.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_evetns.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_frames.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_following.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                pro_followers.setBackgroundColor(getActivity().getResources().getColor(R.color.white));


                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.framelayout_profile);
                if (f instanceof User_Following) {
                    ((User_Following) f).scrolltofirst();
                } else {
                    User_Following fr = new User_Following();
                    changefragment(fr);
                }

            }
        });
        pro_followers = (LinearLayout) mview.findViewById(R.id.pro_followers);
        pro_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* pro_timeline_bar.setVisibility(View.INVISIBLE);
                pro_evetns_bar.setVisibility(View.INVISIBLE);
                pro_frames_bar.setVisibility(View.INVISIBLE);
                pro_following_bar.setVisibility(View.INVISIBLE);
                pro_followers_bar.setVisibility(View.VISIBLE);*/

                pro_timeline.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_evetns.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_frames.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_following.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                pro_followers.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));

                Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.framelayout_profile);
                if (f instanceof User_Followers) {
                    ((User_Followers) f).scrolltofirst();
                } else {
                    User_Followers fr = new User_Followers();
                    changefragment(fr);
                }

            }
        });


        number_of_events = (TextView) mview.findViewById(R.id.no_of_events);
        number_of_frames = (TextView) mview.findViewById(R.id.no_of_frames);
        number_of_following = (TextView) mview.findViewById(R.id.no_of_following);
        number_of_followers = (TextView) mview.findViewById(R.id.no_of_followers);
        number_of_timeline = (TextView) mview.findViewById(R.id.no_of_timeline);

        mlayout_profile = (FrameLayout) mview.findViewById(R.id.framelayout_profile);

        wholelayout = (LinearLayout) mview.findViewById(R.id.wholelayout);
        wholelayout.setVisibility(View.INVISIBLE);

        profile_section = (LinearLayout) mview.findViewById(R.id.profile_section);

        progressBar = (ProgressBar) mview.findViewById(R.id.loadprofileinformation);


        userid = userinfo_data.getString(Utility.user_id);
        if (getArguments() != null) {
            userid = getArguments().getString("user_id");


            usertype = getArguments().getInt("type");
            if (userid.equals(userinfo_data.getString(Utility.user_id))) {
                usertype = Utility.user_type_self;
            }

            adjustview_wit_type();
            if (usertype != Utility.user_type_self) {
                //flush the profile cache
                userinfo_data.flushProfileInformation();

            }
        }

        new loaduserinformation().execute();


    }

    private void adjustview_wit_type() {

        if (usertype == Utility.user_type_following) {
            pro_edit_profile.setText("UnFollow");
            pro_edit_profile.setTextColor(getResources().getColor(R.color.white));
            pro_edit_profile.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (usertype == Utility.user_type_followers) {
            pro_edit_profile.setText("Follow");

            pro_edit_profile.setTextColor(getResources().getColor(R.color.white));
            pro_edit_profile.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (usertype == Utility.user_type_self) {
            pro_edit_profile.setText("Edit Profile");

            pro_edit_profile.setTextColor(getResources().getColor(R.color.GRAY));
            pro_edit_profile.setBackgroundColor(getResources().getColor(R.color.bluegray));
        }

    }

    public void setprofilestat(int type, String value) {
        if (type == Utility.user_type_followers) {
            number_of_followers.setText(value);
        } else if (type == Utility.user_type_following) {
            number_of_following.setText(value);
        }


    }

    private void changefragment(Fragment fr) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userid);
        bundle.putString("user_name", username);
        bundle.putString("user_pic", userpic);

        fr.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.slide_in_left,
        //     android.R.anim.slide_out_right);
        transaction.replace(R.id.framelayout_profile, fr);

        transaction.commit();
    }

    public void changevisibilty(boolean visibility) {

        if (visibility) {

            // expand(profile_section);
            // profile_section.setVisibility(View.VISIBLE);

            // profile_section.setVisibility(View.VISIBLE);


        } else {


            // profile_section.setVisibility(View.GONE);
            // Prepare the View for the animation

            //collapse(profile_section);
        }
    }

    public static Animation expand(final View v, final boolean expand) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            m.setAccessible(true);
            m.invoke(
                    v,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
        } else {
            v.getLayoutParams().height = initialHeight;
        }
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = 0;
                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }
                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand)
                    v.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(200);
        return a;
    }

    public void changeprofile_ui(int operation_code, int success) {
        progressBar.setVisibility(View.GONE);
        if (success == 1) {

            wholelayout.setVisibility(View.GONE);
            new loaduserinformation().execute();


        } else {
            MyToast.popmessage("Oops an error occured, we will rectify it shortly", getActivity());
        }
    }


    class loaduserinformation extends AsyncTask<String, String, String> {

        WebServiceHandler webServiceHandler;
        String status = "";
        JSONObject userinfo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {


                webServiceHandler = new WebServiceHandler(Utility.users_details);
                webServiceHandler.addFormField("user_id", userid);
                webServiceHandler.addFormField("logged_user_id", userinfo_data.getString(Utility.user_id));
                JSONObject top_level = new JSONObject(webServiceHandler.finish());


                status = top_level.getString("status");
                if (status.equals("success")) {
                    userinfo = top_level.getJSONObject("userinfo");
                }


            } catch (Exception e) {
                Log.e("error:", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isAdded()){

            if (status.equals("success")) {
                try {

                    progressBar.setVisibility(View.GONE);


                    pro_user_name.setText(userinfo.getString("username")+", "+userinfo.getString("realname"));
                    username = userinfo.getString("username");
                    userpic = userinfo.getString("profile_image");
                    //pro_user_realname.setText("Realname:" + " " + userinfo.getString("realname"));
                    pro_description.setText("Description:" + " " + userinfo.getString("description"));
                    number_of_events.setText(userinfo.getString("number_of_event"));
                    number_of_frames.setText(userinfo.getString("number_of_frame"));
                    number_of_following.setText(userinfo.getString("number_of_following"));
                    number_of_followers.setText(userinfo.getString("number_of_followers"));
                    number_of_timeline.setText(userinfo.getString("number_of_timeline"));

                    wholelayout.setVisibility(View.VISIBLE);
                    try {
                        Picasso.with(getActivity()).load(userinfo.getString("profile_image")).error(R.drawable.pro_image).into(pro_user_iamge);
                    } catch (Exception E) {
                        pro_user_iamge.setImageResource(R.drawable.pro_image);


                    }

                    f_value = userinfo.getString("followed");

                    if (f_value.equals("followed")) {
                        usertype = Utility.user_type_following;
                    } else if (f_value.equals("follower")) {
                        usertype = Utility.user_type_followers;
                    } else {
                        if (userid.equals(userinfo_data.getString(Utility.user_id))) {
                            usertype = Utility.user_type_self;
                        } else {
                            usertype = Utility.user_type_followers;
                        }
                    }
                    adjustview_wit_type();


                    //adding timeline fragment to device
                    TimeLine fr = new TimeLine();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", userid);
                    bundle.putString("user_name", username);
                    bundle.putString("user_pic", userpic);

                    fr.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    // transaction.setCustomAnimations(android.R.anim.slide_in_left,
                    //       android.R.anim.slide_out_right);
                    transaction.add(R.id.framelayout_profile, fr);

                    transaction.commit();

                } catch (Exception e) {
                    Log.e("error:", e.getMessage());
                }
            }

        }
    }
        }
}
