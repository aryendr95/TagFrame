package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.Productlist;

import java.util.ArrayList;

/**
 * Created by abhinav on 01/06/2016.
 */
public class ProductAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<Product> tagStream_models;
    private LayoutInflater inflater;
    private int posi;

    public ProductAdapter(Context ctx,ArrayList<Product> tagStream_models,int position)
    {
        this.ctx=ctx;
        this.tagStream_models=tagStream_models;
        this.posi=position;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_grid_product, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Product tagStream=tagStream_models.get(position);


        try {
            Picasso.with(ctx).load(tagStream.getImage()).into(mViewHolder.ivimage);
        }
        catch (Exception e)
        {

        }
        mViewHolder.textView.setText(tagStream.getName());

        mViewHolder.ivimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("running","code");
                if(ctx instanceof Productlist)
                {
                    Intent intent=new Intent();
                    intent.putExtra("product_id",tagStream.getId());
                    intent.putExtra("product_image",tagStream.getImage());
                    intent.putExtra("product_url",tagStream.getUrl());
                    intent.putExtra("frame_position",position);
                    ((Productlist)ctx).setResult(MakeNewEvent.Flag_product_list_result,intent);
                    ((Productlist)ctx).finish();

                    Log.i("running","code2");
                }
            }
        });



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
