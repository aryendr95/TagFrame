package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tagframe.tagframe.Models.Menu;
import com.tagframe.tagframe.R;

import java.util.ArrayList;

/**
 * Created by abhinav on 01/05/2016.
 */
public class Menulistadapter extends BaseAdapter {
    Context ctx;
    ArrayList<Menu>
            menulist;
    LayoutInflater inflater;

    public Menulistadapter(Context ctx, ArrayList<com.tagframe.tagframe.Models.Menu> menus) {
        this.ctx = ctx;
        this.menulist = menus;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return menulist.size();
    }

    @Override
    public Object getItem(int position) {
        return menulist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_menu_list, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }


        com.tagframe.tagframe.Models.Menu menu = menulist.get(position);
        mViewHolder.ivtxt.setText(menu.getTittle());
        mViewHolder.ivimg.setImageResource(menu.getIcon());

        return convertView;
    }

    private class MyViewHolder {

        ImageView ivimg;
        TextView ivtxt;


        public MyViewHolder(View item) {

            ivimg = (ImageView) item.findViewById(R.id.img);
            ivtxt = (TextView) item.findViewById(R.id.txt);


        }
    }
}


