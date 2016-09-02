package com.tagframe.tagframe.UI.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import com.tagframe.tagframe.Adapters.ViewPagerAdapter;
import com.tagframe.tagframe.Models.ProfileResponseModel;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Menu_Action;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhinav on 05/04/2016.
 */
public class Profile extends Fragment {

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


    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mview = inflater.inflate(R.layout.fragment_profile_new, container, false);

        userinfo_data = new AppPrefs(getActivity());

        init();


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
        loadUserInformation();

    }

    private void loadUserInformation() {

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Info..");
        progressDialog.show();

          viewPager = (ViewPager) mview.findViewById(R.id.htab_viewpager);

          tabLayout = (TabLayout) mview.findViewById(R.id.htab_tabs);



        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.getProfileInfo(userid, userinfo_data.getString(Utility.user_id)).enqueue(new Callback<ProfileResponseModel>() {
            @Override
            public void onResponse(Call<ProfileResponseModel> call, Response<ProfileResponseModel> response) {
                if(isAdded()) {
                    progressDialog.hide();
                    if (response.body().getStatus().equals(Utility.success_response)) {
                        User user = response.body().getUsers();

                        //setting username and description
                        pro_user_name.setText(user.getUsername() + ", " + user.getRealname());
                        pro_description.setText(user.getDescription());
                        //setting user_image
                        try {
                            Picasso.with(getActivity()).load(user.getProfile_image()).into(pro_user_iamge);
                        } catch (Exception e) {
                            pro_user_iamge.setImageResource(R.drawable.pro_image);
                        }
                        //setting tabs
                        ArrayList<String> numbers = new ArrayList<String>();
                        numbers.add(user.getNumber_of_timeline());
                        numbers.add(user.getNumber_of_event());
                        numbers.add(user.getNumber_of_frame());
                        numbers.add(user.getNumber_of_following());
                        numbers.add(user.getNumber_of_followers());

                        setupViewPager(viewPager, numbers);
                        setupTabLayout(viewPager, tabLayout);


                    } else {
                        Log.e("ds", response.body().getStatus());
                    }
                }
            }


            @Override
            public void onFailure(Call<ProfileResponseModel> call, Throwable t) {
                if(isAdded()) {
                    progressDialog.hide();
                    Log.e("ds", "failure" + t.getMessage());
                }
            }
        });

    }

    private void setupTabLayout(ViewPager viewPager,TabLayout tabLayout) {

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                Fragment fragment =(Fragment) viewPagerAdapter.getItem(tab.getPosition());
                if(fragment instanceof ScrollList)
                {
                    ((ScrollList)fragment).scrolltofirst();
                }
            }
        });

        int length = tabLayout.getTabCount();
        for (int i = 0; i < length; i++) {
            tabLayout.getTabAt(i).setCustomView(viewPagerAdapter.getTabView(i));
        }
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

    private void setupViewPager(ViewPager viewPager, ArrayList<String> number) {
        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        viewPagerAdapter.addFragment(new TimeLine(), getBundle(), "TimeLine", number.get(0));
        viewPagerAdapter.addFragment(new User_Events(), getBundle(), "Events", number.get(1));
        viewPagerAdapter.addFragment(new User_Frames(), getBundle(), "Frames", number.get(2));
        viewPagerAdapter.addFragment(new User_Following(), getBundle(), "Followings", number.get(3));
        viewPagerAdapter.addFragment(new User_Followers(), getBundle(), "Followers", number.get(4));
        viewPager.setAdapter(viewPagerAdapter);
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userid);
        bundle.putString("user_name", username);
        bundle.putString("user_pic", userpic);

        return bundle;
    }

    public void changeprofile_ui(int operation_code, int success) {
        if (success == 1) {
            loadUserInformation();


        } else {
            MyToast.popmessage("Oops an error occured, we will rectify it shortly", getActivity());
        }
    }


}
