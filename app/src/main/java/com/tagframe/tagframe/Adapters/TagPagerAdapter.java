package com.tagframe.tagframe.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by karanveer on 04/04/2016.
 *
 * this class defines the which fragements(login and sign up) to be used in the pagertab used in authentiction class
 */

public class TagPagerAdapter extends FragmentPagerAdapter {


    ArrayList<Fragment> pagerfragmentslist;
    ArrayList<String> names;

    public TagPagerAdapter(FragmentManager fm,ArrayList<Fragment> pagerfragmentslist,ArrayList<String> names) {
        super(fm);
        this.pagerfragmentslist=pagerfragmentslist;
        this.names=names;
        if (fm.getFragments() != null) {
            fm.getFragments().clear();
        }

    }

    @Override
    public Fragment getItem(int position) {
        return pagerfragmentslist.get(position);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return pagerfragmentslist.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return names.get(position);
    }
}
