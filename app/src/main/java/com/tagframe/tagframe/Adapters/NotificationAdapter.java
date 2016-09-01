package com.tagframe.tagframe.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.NotificationModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Utility;

import java.util.List;

/**
 * Created by Brajendr on 8/26/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationModel> ListModels;
    private Context context;

    public NotificationAdapter(List<NotificationModel> ListModels, Context context) {
        this.ListModels = ListModels;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_notifications, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NotificationModel ListModel = ListModels.get(position);

        holder.txt_username.setText(ListModel.getUser_name());
        holder.txt_action_type.setText(ListModel.getAction_type());
        holder.txt_sub_action_name.setText(ListModel.getSub_action_name());
        holder.txt_date.setText(ListModel.getCreated_on());


        try {
            Picasso.with(context).load(ListModel.getProfile_pic()).into(holder.img_user);
        } catch (Exception e) {
            holder.img_user.setImageResource(R.drawable.pro_image);
        }

        AppPrefs appPrefs = new AppPrefs(context);
        final int user_type = Utility.getUser_Type(ListModel.getUser_id(), appPrefs.getString(Utility.user_id));

        holder.layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ListModel.getSub_action_type().equals(Utility.notification_op_watch_event))
                {

                }
                else if(ListModel.getSub_action_type().equals(Utility.notification_op_watch_profile))
                {
                    ((Modules) context).setprofile(ListModel.getSub_action_id(), user_type);
                }
            }
        });




        //click listners on profile photo and nam
        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Modules) context).setprofile(ListModel.getUser_id(), user_type);
            }
        });

        holder.txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Modules) context).setprofile(ListModel.getUser_id(), user_type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ListModels.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        public ImageView img_user;
        public TextView txt_username,txt_action_type,txt_sub_action_name,txt_date;
        public RelativeLayout layout_back;

        public MyViewHolder(View view) {
            super(view);
            layout_back=(RelativeLayout)view.findViewById(R.id.notification_background);

            img_user = (ImageView) view.findViewById(R.id.notification_pro_pic);

            txt_username = (TextView) view.findViewById(R.id.notification_user_name);
            txt_date = (TextView) view.findViewById(R.id.notification_date);
            txt_sub_action_name = (TextView) view.findViewById(R.id.notification_sub_action_name);
            txt_action_type = (TextView) view.findViewById(R.id.notification_action_type);
        }
    }
}
