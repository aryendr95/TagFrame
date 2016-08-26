package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by abhinav on 08/04/2016.
 */
public class ImageAdapter extends BaseAdapter
{

    Context ctx;
    ArrayList<User_Frames_model> tagStream_models;
    LayoutInflater inflater;

    public ImageAdapter(Context ctx,ArrayList<User_Frames_model> tagStream_models)
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
            convertView = inflater.inflate(R.layout.layout_item_fgridview_frame, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final User_Frames_model tagStream=tagStream_models.get(position);


        Picasso.with(ctx).load(tagStream.getThumbnail_url()).into(mViewHolder.ivframeimage);

        mViewHolder.ivframeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window window = dialog.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_frame_display_profile);

                // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
                ImageView frameimage = (ImageView) dialog.findViewById(R.id.framelist_image);
                ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);

                //TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
                final TextView tittle = (TextView) dialog.findViewById(R.id.framelist_name);

                RelativeLayout relativeLayout=(RelativeLayout)dialog.findViewById(R.id.rl_see_event);
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx, MakeNewEvent.class);
                        intent.putExtra("data_url", tagStream.getData_url());
                        intent.putExtra("tittle", tagStream.getTitle());
                        intent.putExtra("from", "profile");
                        intent.putExtra("description","");
                        intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                        intent.putExtra("eventtype", Constants.eventtype_internet);
                        intent.putExtra("eventid", tagStream.getVideo_id());
                        intent.putExtra("tagged_user_id",tagStream.getTaggedUserModelArrayList());


                        ctx.startActivity(intent);
                    }
                });


                frameimage.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(tagStream.getThumbnail_url()).into(frameimage);

                tittle.setText(tagStream.getTitle());
               // duration.setText(Constants.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Constants.milliSecondsToTimer(frameList_model.getEndtime()));

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();


                    }
                });

                dialog.show();
            }
        });

        return convertView;
    }

    private class MyViewHolder {

        ImageView ivframeimage;


        public MyViewHolder(View item) {

            ivframeimage=(ImageView)item.findViewById(R.id.grid_iimag_item);



        }
    }
}
