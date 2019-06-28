package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.EndorseListModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Brajendr on 8/23/2016.
 */
public class EndorseListAdapter extends RecyclerView.Adapter<EndorseListAdapter.MyViewHolder> {

    private List<EndorseListModel> endorseListModels;
    private Context context;

    public EndorseListAdapter(List<EndorseListModel> endorseListModels, Context context) {
        this.endorseListModels = endorseListModels;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_direct_endorse, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EndorseListModel endorseListModel = endorseListModels.get(position);
        holder.txt_date.setText(endorseListModel.getDate());
        holder.txt_username.setText(endorseListModel.getUser_name());
        holder.txt_productname.setText(endorseListModel.getProduct_name());

        try {
            Picasso.with(context).load(endorseListModel.getProduct_image()).into(holder.img_product);
        } catch (Exception e) {
            holder.img_product.setImageResource(R.drawable.product_placeholder);
        }

        try {
            Picasso.with(context).load(endorseListModel.getUser_image()).into(holder.img_user);
        } catch (Exception e) {
            holder.img_user.setImageResource(R.drawable.pro_image);
        }
    }

    @Override
    public int getItemCount() {
        return endorseListModels.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_product;
        public CircleImageView img_user;
        public TextView txt_username, txt_productname, txt_date;

        public MyViewHolder(View view) {
            super(view);
            img_product = (ImageView) view.findViewById(R.id.list_endorse_img_product);

            img_user = (CircleImageView) view.findViewById(R.id.list_endorse_img_user);

            txt_username = (TextView) view.findViewById(R.id.list_endorse_user_name);
            txt_productname = (TextView) view.findViewById(R.id.list_endorse_product_name);
            txt_date = (TextView) view.findViewById(R.id.list_endorse_date);

        }
    }
}
