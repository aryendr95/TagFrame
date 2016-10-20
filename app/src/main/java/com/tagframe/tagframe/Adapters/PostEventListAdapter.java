package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.Utility;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Brajendr on 10/17/2016.
 */
public class PostEventListAdapter extends RecyclerView.Adapter<PostEventListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<FrameList_Model> frameList_modelArrayList;

    public PostEventListAdapter(Context context, ArrayList<FrameList_Model> frameList_modelArrayList) {
        this.context = context;
        this.frameList_modelArrayList = frameList_modelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_frames_post_event, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FrameList_Model frameList_model = frameList_modelArrayList.get(position);
        Bitmap thumb = BitmapHelper.decodeFile(context, new File(frameList_model.getImagepath()));
        thumb = Utility.getResizedBitmap(thumb, 100, 100);
        holder.imageView.setImageBitmap(thumb);
        String time = Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime());
        holder.time.setText(time);
        if (frameList_model.getEndtime() != 0) {
            holder.status.setText("Synched");
            holder.status.setTextColor(Color.GREEN);
        } else {
            holder.status.setText("Not Synched");
            holder.status.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return frameList_modelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView time, status;
        private EditText tittle;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_frame);
            time = (TextView) itemView.findViewById(R.id.synctime);
            tittle = (EditText) itemView.findViewById(R.id.frame_tittle);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
