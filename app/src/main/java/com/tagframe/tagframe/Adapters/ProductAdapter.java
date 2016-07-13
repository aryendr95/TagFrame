package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;

import java.util.ArrayList;

/**
 * Created by abhinav on 01/06/2016.
 */
public class ProductAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<Product> tagStream_models;
    LayoutInflater inflater;

    public ProductAdapter(Context ctx,ArrayList<Product> tagStream_models)
    {
        this.ctx=ctx;
        this.tagStream_models=tagStream_models;
        inflater=(LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tagStream_models.size();
    }

    @Override
    public Object getItem(int position) {
        return tagStream_models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_grid_product, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Product tagStream=tagStream_models.get(position);


        Picasso.with(ctx).load(tagStream.getImage()).into(mViewHolder.ivimage);
        mViewHolder.textView.setText(tagStream.getName());



        return convertView;
    }

    private class MyViewHolder {

        ImageView ivimage;
        TextView textView;


        public MyViewHolder(View item) {

            ivimage=(ImageView)item.findViewById(R.id.product_image);
            textView=(TextView)item.findViewById(R.id.product_name);



        }
    }
}
