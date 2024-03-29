package com.tagframe.tagframe.UI.Acitivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;

import com.tagframe.tagframe.Adapters.TagPagerAdapter;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Fragments.Login;
import com.tagframe.tagframe.UI.Fragments.SignUp;
import com.tagframe.tagframe.UI.Fragments.Splash;
import com.tagframe.tagframe.Utils.*;

import java.util.ArrayList;

/**
 * Created by Karanveer on 04/04/2016.
 * <p>
 * this class handles all the authentications like login and sign up and also the splash each having their own respective fragment
 */
public class Authentication extends FragmentActivity {
    private ViewPager mpager;
    private SlidingTabLayout mpagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        //recognise views

        mpager = (ViewPager) findViewById(R.id.pager_authentication);
        mpagerTabStrip = (SlidingTabLayout) findViewById(R.id.pagertab);

        //
        mpagerTabStrip.setDistributeEvenly(true);
        mpagerTabStrip.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        // mpagerTabStrip.setCustomTabView(R.layout.layout_customized_tab, R.id.txttab);

        setadapter();
    }

    public void setadapter() {
        //setting up adapter for viewpager
        ArrayList<Fragment> fraglist = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        AppPrefs appPrefs = new AppPrefs(this);
        User user = appPrefs.getUser();
        if (user.isLoggedin()) {//if logged out
            Intent intent = new Intent(this, SplashActivity.class);
            intent.putExtra("load", "login");
            startActivity(intent);
            finish();

        } else {
            fraglist.add(new Login());
            fraglist.add(new SignUp());

            names.add("Login");
            names.add("Sign Up");
        }

        TagPagerAdapter tagPagerAdapter = new TagPagerAdapter(getSupportFragmentManager(), fraglist, names);

        mpager.setAdapter(tagPagerAdapter);

        mpagerTabStrip.setViewPager(mpager);
    }

    public void setadapteraddprofile(Fragment FR) {
        //setting up adapter for viewpager
        ArrayList<Fragment> fraglist = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        fraglist.add(FR);
        names.add("");

        TagPagerAdapter tagPagerAdapter = new TagPagerAdapter(getSupportFragmentManager(), fraglist, names);
        mpager.setAdapter(tagPagerAdapter);
        mpagerTabStrip.setViewPager(mpager);
    }
}
