package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tagframe.tagframe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Brajendr on 7/27/2016.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final List<String> mFragmentNumberList = new ArrayList<>();
    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();

    private Context context;

    public ViewPagerAdapter(FragmentManager manager,Context context) {

        super(manager);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {


        if (fragmentHashMap.get(position) != null) {
            return fragmentHashMap.get(position);
        }
        fragmentHashMap.put(position, mFragmentList.get(position));
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    public void addFragment(Fragment fragment, Bundle bundle,String title,String numb)
    {

        fragment.setArguments(bundle);
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
        mFragmentNumberList.add(numb);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_profile_tabs, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView number=(TextView)view.findViewById(R.id.number);

        number.setText(this.mFragmentNumberList.get(position));
        title.setText(this.mFragmentTitleList.get(position));

        return view;
    }
}

