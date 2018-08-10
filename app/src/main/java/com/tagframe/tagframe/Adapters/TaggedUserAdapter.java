package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.EndorseListModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.UI.Acitivity.WatchEventActivity;

import java.util.List;

/**
 * Created by Brajendr on 8/24/2016.
 */
public class TaggedUserAdapter extends RecyclerView.Adapter<TaggedUserAdapter.MyViewHolder> {

    private List<TaggedUserModel> ListModels;
    private Context context;

    public TaggedUserAdapter(List<TaggedUserModel> ListModels, Context context) {
        this.ListModels = ListModels;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_tagged_users, parent, false);

        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final TaggedUserModel endorseListModel = ListModels.get(position);

        holder.txt_username.setText(endorseListModel.getName());

        holder.txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Modules.class);
                intent.putExtra("user_id",endorseListModel.getUser_id());
                intent.putExtra("name", endorseListModel.getName());
                intent.putExtra("pofile_pic",endorseListModel.getProfile_pic());
                context.startActivity(intent);
            }
        });

        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Modules.class);
                intent.putExtra("user_id",endorseListModel.getUser_id());
                intent.putExtra("name", endorseListModel.getName());
                intent.putExtra("pofile_pic",endorseListModel.getProfile_pic());
                context.startActivity(intent);
            }
        });


        try {
            Picasso.with(context).load(endorseListModel.getProfile_pic()).into(holder.img_user);
        } catch (Exception e) {
            holder.img_user.setImageResource(R.drawable.pro_image);
        }

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListModels.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ListModels.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_remove;
        public CircularImageView img_user;
        public TextView txt_username;

        public MyViewHolder(View view) {
            super(view);
            img_remove = (ImageView) view.findViewById(R.id.tagged_remove);

            img_user = (CircularImageView) view.findViewById(R.id.tagged_pro_pic);

            txt_username = (TextView) view.findViewById(R.id.tagged_username);

        }
    }
}
