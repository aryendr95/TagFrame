package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.TimeLine_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.listops;

import java.util.ArrayList;

/**
 * Created by abhinav on 03/05/2016.
 */
public class Time_Line_Adapter extends BaseAdapter
{

    Context ctx;
    ArrayList<TimeLine_Model> tagStream_models;
    LayoutInflater inflater;
    listops user_data;

    public Time_Line_Adapter(Context ctx,ArrayList<TimeLine_Model> tagStream_models)
    {
        this.ctx=ctx;
        this.tagStream_models=tagStream_models;
        user_data=new listops(ctx);
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
            convertView = inflater.inflate(R.layout.layout_list_tagstream, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

       final TimeLine_Model tagStream=tagStream_models.get(position);

        mViewHolder.tvTitlle.setText(tagStream.getTittle());
        mViewHolder.tvname.setText(tagStream.getUser_name());
        mViewHolder.tvcurrentduration.setText(tagStream.getCreated_on());

        mViewHolder.iveventimage.setVisibility(View.VISIBLE);
        mViewHolder.iveventvideo.setVisibility(View.GONE);

        mViewHolder.tvlike.setText(tagStream.getNumber_of_likes());


        Picasso.with(ctx).load(tagStream.getThumbnail()).into(mViewHolder.iveventimage);
        try {
            Picasso.with(ctx).load(tagStream.getProfile_picture()).into(mViewHolder.ivpropic);
        }
        catch (Exception e)
        {
            mViewHolder.ivpropic.setImageResource(R.drawable.pro_image);
        }

        mViewHolder.ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(tagStream.getSharelink(),ctx);
            }
        });
        if(tagStream.getLike_video().equals("No"))
        {
            mViewHolder.tvlike_direct.setText("Like");
            mViewHolder.ivlike.setImageResource(R.drawable.like);
        }
        else
        {
            mViewHolder.tvlike_direct.setText("UnLike");
            mViewHolder.ivlike.setImageResource(R.drawable.unlike);
        }

        mViewHolder.ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(tagStream.getLike_video().equals("No"))
                {
                    Broadcastresults mReceiver=((Modules)ctx).register_reviever();

                    Intent intent=new Intent(ctx, IntentServiceOperations.class);
                    intent.putExtra("operation",Constants.operation_like);
                    intent.putExtra("user_id",user_data.getString(Constants.user_id));
                    intent.putExtra("event_id", tagStream.getEvent_id());
                    intent.putExtra("receiver", mReceiver);

                    ctx.startService(intent);
                    tagStream.setNumber_of_likes((Integer.parseInt(tagStream.getNumber_of_likes()) + 1) + "");
                    tagStream.setLike_video("Yes");
                    notifyDataSetChanged();

                }
                else
                {
                    Broadcastresults mReceiver=((Modules)ctx).register_reviever();

                    Intent intent=new Intent(ctx, IntentServiceOperations.class);
                    intent.putExtra("operation",Constants.operation_unlike);
                    intent.putExtra("user_id",user_data.getString(Constants.user_id));
                    intent.putExtra("event_id", tagStream.getEvent_id());
                    intent.putExtra("receiver", mReceiver);
                    tagStream.setNumber_of_likes((Integer.parseInt(tagStream.getNumber_of_likes()) - 1) + "");
                    tagStream.setLike_video("No");
                    ctx.startService(intent);
                    notifyDataSetChanged();
                }

            }
        });

        mViewHolder.iveventimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, MakeNewEvent.class);
                intent.putExtra("data_url", tagStream.getData_url());
                intent.putExtra("tittle", tagStream.getTittle());
                intent.putExtra("from", "tagstream");
                intent.putExtra("description",tagStream.getDescription());
                intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                intent.putExtra("eventtype", Constants.eventtype_internet);
                intent.putExtra("eventid", tagStream.getEvent_id());

                ctx.startActivity(intent);
            }
        });

        return convertView;
    }

    private class MyViewHolder {
        TextView tvTitlle, tvname,tvcurrentduration,tvlike,tvlike_direct;
        ImageView iveventimage,ivlike;
        VideoView iveventvideo;
        CircularImageView ivpropic;
        LinearLayout ll_like,ll_share;

        public MyViewHolder(View item) {
            tvTitlle = (TextView) item.findViewById(R.id.list_event_tittle);
            tvname = (TextView) item.findViewById(R.id.list_user_name);
            tvcurrentduration = (TextView) item.findViewById(R.id.list_user_duration);
            iveventimage=(ImageView)item.findViewById(R.id.list_event_image);


            ll_like=(LinearLayout)item.findViewById(R.id.lllike);
            ll_share=(LinearLayout)item.findViewById(R.id.llshare);

            tvlike=(TextView)item.findViewById(R.id.txt_likes);
            ivlike=(ImageView)item.findViewById(R.id.imglike);
            tvlike_direct=(TextView)item.findViewById(R.id.txt_like_directive);

            ivpropic=(CircularImageView)item.findViewById(R.id.list_pro_image);

            iveventvideo=(VideoView)item.findViewById(R.id.list_event_video);

        }
    }
    public void share(String link,Context ctx)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this event at TagFrame:"+link);
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }
}

