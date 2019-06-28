package com.tagframe.tagframe.Adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyandroidanimations.library.PuffOutAnimation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Brajendr on 10/4/2016.
 */
public class FrameListRecyclerAdapter extends RecyclerView.Adapter<FrameListRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<FrameList_Model> frameList_modelArrayList;

    public FrameListRecyclerAdapter(Context context, ArrayList<FrameList_Model> frameList_modelArrayList) {
        this.context = context;
        this.frameList_modelArrayList = frameList_modelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_frame, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder mViewHolder, final int position) {
        final FrameList_Model frame = frameList_modelArrayList.get(position);
        mViewHolder.videoIndicator.setVisibility(View.GONE);
        if (frame.getEndtime() != 0) {
            mViewHolder.wholeLayout.setBackgroundColor(Color.GREEN);
        } else {
            mViewHolder.wholeLayout.setBackgroundColor(Color.YELLOW);
        }

        final AppPrefs appPrefs = new AppPrefs(context);
        Log.e("gone", frame.getUser_id() + MakeNewEvent.user_id);
        if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
            if (appPrefs.getString(Utility.user_id).equals(frame.getUser_id()) || appPrefs.getString(Utility.user_id).equals(MakeNewEvent.user_id)) {
                mViewHolder.ivDelete.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.ivDelete.setVisibility(View.GONE);
            }
        }

        if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
            mViewHolder.ivDelete.setVisibility(View.VISIBLE);
        }

        String time = Utility.milliSecondsToTimer(frame.getStarttime()) + "-" + Utility.milliSecondsToTimer(frame.getEndtime());
        mViewHolder.tvTime.setText(time);
        mViewHolder.tvName.setText(frame.getName());

        if (frame.getFrametype() == Utility.frametype_image) {
            if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
                try {
                    Bitmap thumb = BitmapHelper.decodeFile(context, new File(frame.getImagepath()));
                    thumb = Utility.getResizedBitmap(thumb, 150, 200);
                    mViewHolder.ivFrameImage.setImageBitmap(thumb);
                } catch (Exception e) {
                    Picasso.with(context).load(frame.getFrame_image_url()).into(mViewHolder.ivFrameImage);
                }
            } else {
                Picasso.with(context).load(frame.getFrame_image_url()).into(mViewHolder.ivFrameImage,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                try {
                                    Picasso.with(context).load(frame.getImagepath()).into(mViewHolder.ivFrameImage);
                                } catch (Exception e) {

                                }
                            }
                        });
            }
        } else {
            if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
                try {
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(frame.getImagepath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    thumb = Utility.getResizedBitmap(thumb, 150, 200);
                    mViewHolder.ivFrameImage.setImageBitmap(thumb);
                    mViewHolder.videoIndicator.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Picasso.with(context).load(frame.getImagepath()).into(mViewHolder.ivFrameImage);
                }
            } else {
                Picasso.with(context).load(frame.getImagepath()).into(mViewHolder.ivFrameImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        try {
                            Picasso.with(context).load(frame.getFrame_image_url()).into(mViewHolder.ivFrameImage);
                        } catch (Exception e) {
                        }
                    }
                });
            }

        }
        mViewHolder.ivFrameImage.setTag("Frame" + position);

        mViewHolder.ivFrameImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
                    if (appPrefs.getString(Utility.user_id).equals(frame.getUser_id()) || appPrefs.getString(Utility.user_id).equals(MakeNewEvent.user_id)) {
                        resyncFrame(v, position, mViewHolder);
                    } else {
                        PopMessage.makeshorttoast(context, "You can not edit this frame");
                    }
                }
                if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
                    resyncFrame(v, position, mViewHolder);
                }
                return true;

            }

        });

        mViewHolder.ivFrameImage.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(final View v, DragEvent event) {


                long startTime = System.currentTimeMillis();
                int call_once = 0;
                switch (event.getAction()) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        // Log.e("TAG", "Action is DragEvent.ACTION_DRAG_STARTED");

                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        //Log.e("TAG", "Action is DragEvent.ACTION_DRAG_enteR");

                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        /* long stopTime = System.currentTimeMillis();
                        long elapsedTime = stopTime - startTime;
                        Log.e("Dragexited", elapsedTime + "");*/

                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        // Log.e("TAG", "Action is DragEvent.ACTION_DRAG_lo");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        //Log.e("TAG", "Action is DragEvent.ACTION_DRAG_emded");
                        if (context instanceof MakeNewEvent) {

                            ((MakeNewEvent) context).resync_frame(position);
                        }
                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        // Do nothing
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context instanceof MakeNewEvent) {
                    ((MakeNewEvent) context).deleteframe(frame.getFrame_id(), position, frame.getFrame_resource_type());
                }
            }
        });

        mViewHolder.ivFrameImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!MakeNewEvent.delete_event) {
                    if (context instanceof MakeNewEvent) {

                        ((MakeNewEvent) context).show_synced_frame(position);
                    }
                }
            }
        });

    }

    private void resyncFrame(View v, int position, MyViewHolder mViewHolder) {
        if (context instanceof MakeNewEvent) {
            ((MakeNewEvent) context).pause_mediaplayer();
        }

        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(mViewHolder.ivFrameImage);
        v.startDrag(dragData, myShadow, null, 0);

        MakeNewEvent.RESYC_FRAME_POSITION = position;
    }

    @Override
    public int getItemCount() {
        return frameList_modelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout wholeLayout, videoIndicator;
        private ImageView ivFrameImage, ivDelete;
        private TextView tvTime, tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            wholeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_list_frame);
            videoIndicator = (RelativeLayout) itemView.findViewById(R.id.rl_video_indicator);
            ivFrameImage = (ImageView) itemView.findViewById(R.id.framelist_image);
            ivDelete = (ImageView) itemView.findViewById(R.id.framelist_delete);
            tvName = (TextView) itemView.findViewById(R.id.framelist_name);
            tvTime = (TextView) itemView.findViewById(R.id.framelist_time);
        }
    }
}
