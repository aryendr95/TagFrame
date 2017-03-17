package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.Constants;
import java.util.ArrayList;

/**
 * Created by Brajendr on 1/20/2017.
 */

public class UserFrameAdapter extends RecyclerView.Adapter<UserFrameAdapter.MyViewHolder> {
  private Context ctx;
  private ArrayList<User_Frames_model> frames_models;

  public UserFrameAdapter(Context ctx, ArrayList<User_Frames_model> frames_models) {
    this.ctx = ctx;
    this.frames_models = frames_models;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.layout_item_fgridview_frame, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder mViewHolder, int position) {

    final User_Frames_model frame = frames_models.get(position);

    Picasso.with(ctx).load(frame.getThumbnail_url()).into(mViewHolder.ivframeimage);
    if (frame.getMedia_type().equals(String.valueOf(Constants.frametype_image))) {
      mViewHolder.ivPLayVideo.setVisibility(View.GONE);
    } else {
      mViewHolder.ivPLayVideo.setVisibility(View.VISIBLE);
    }

    mViewHolder.ivframeimage.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ImageAdapter.showFrame(ctx, frame);
      }
    });

  }

  @Override public int getItemCount() {
    return frames_models.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView ivframeimage, ivPLayVideo;

    public MyViewHolder(View item) {
      super(item);

      ivframeimage = (ImageView) item.findViewById(R.id.grid_iimag_item);
      ivPLayVideo = (ImageView) item.findViewById(R.id.img_play);
    }
  }
}
