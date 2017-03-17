package com.tagframe.tagframe.Adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyandroidanimations.library.PuffOutAnimation;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by abhinav on 27/04/2016.
 */
public class FrameListAdapter extends BaseAdapter {

  private ArrayList<FrameList_Model> frames;
  private ArrayList<Integer> index;
  private Context ctx;
  private LayoutInflater inflater;
  private int event_type;

  public FrameListAdapter(Context ctx, ArrayList<FrameList_Model> frameList_models,
      int event_type1) {
    this.ctx = ctx;
    this.frames = frameList_models;

    this.index = index;
    inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    event_type = event_type1;
  }

  @Override public int getCount() {
    return frames.size();
  }

  @Override public Object getItem(int position) {
    return frames.get(position);
  }

  @Override public long getItemId(int position) {
    return 0;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {

    final MyViewHolder mViewHolder;

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.layout_list_frame, parent, false);
      mViewHolder = new MyViewHolder(convertView);
      convertView.setTag(mViewHolder);
    } else {
      mViewHolder = (MyViewHolder) convertView.getTag();
    }

    mViewHolder.relativeLayout.setVisibility(View.GONE);

    //make changes as per user is watching internt event
    final FrameList_Model frame = frames.get(position);
    final AppPrefs appPrefs = new AppPrefs(ctx);

    Log.e("user_id", MakeNewEvent.user_id);

    if ((frame.getFrame_resource_type().equals(Utility.frame_resource_type_internet))) {
      if(!(appPrefs.getUser().getUser_id().equals(frame.getUser_id()))
          || !(appPrefs.getUser().getUser_id().equals(MakeNewEvent.user_id)) )
      {

        mViewHolder.ivdelete.setVisibility(View.GONE);
      }
    }

    mViewHolder.tvtime.setText(
        Utility.milliSecondsToTimer(frame.getStarttime()) + "-" + Utility.milliSecondsToTimer(
            frame.getEndtime()));
    mViewHolder.tvname.setText(frame.getName());

    if (frame.getFrametype() == Utility.frametype_image) {

      if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
        try {
          Bitmap thumb = BitmapHelper.decodeFile(ctx, new File(frame.getImagepath()));
          thumb = Utility.getResizedBitmap(thumb, 150, 200);
          mViewHolder.iveventimage.setImageBitmap(thumb);
        } catch (Exception e) {
          Picasso.with(ctx).load(frame.getImagepath()).into(mViewHolder.iveventimage);
        }
      } else {
        Picasso.with(ctx).load(frame.getImagepath()).into(mViewHolder.iveventimage);
      }
    } else {

      if (frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(frame.getImagepath(),
            MediaStore.Images.Thumbnails.MINI_KIND);

        thumb = Utility.getResizedBitmap(thumb, 150, 200);

        mViewHolder.iveventimage.setImageBitmap(thumb);
        mViewHolder.relativeLayout.setVisibility(View.VISIBLE);
      } else {
        Picasso.with(ctx).load(frame.getImagepath()).into(mViewHolder.iveventimage);
      }
    }
    mViewHolder.iveventimage.setTag("Frame" + position);

    mViewHolder.iveventimage.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {

        if ((frame.getFrame_resource_type().equals(Utility.frame_resource_type_internet)
            && (appPrefs.getString(Utility.user_id).equals(frame.getUser_id())))
            || frame.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
          if (ctx instanceof MakeNewEvent) {
            ((MakeNewEvent) ctx).pause_mediaplayer();
          }

          ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
          String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };

          ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
          View.DragShadowBuilder myShadow = new View.DragShadowBuilder(mViewHolder.iveventimage);

          v.startDrag(dragData, myShadow, null, 0);

          MakeNewEvent.RESYC_FRAME_POSITION = position;
        } else {
          PopMessage.makeshorttoast(ctx, "You can not edit this frame");
        }
        return true;
      }
    });

    mViewHolder.iveventimage.setOnDragListener(new View.OnDragListener() {
      @Override public boolean onDrag(final View v, DragEvent event) {

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
            if (ctx instanceof MakeNewEvent) {

              ((MakeNewEvent) ctx).resync_frame(position);
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

    mViewHolder.ivdelete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (ctx instanceof MakeNewEvent) {
          new PuffOutAnimation(mViewHolder.iveventimage).animate();
          ((MakeNewEvent) ctx).deleteframe(position);
        }
      }
    });

    return convertView;
  }

  private class MyViewHolder {
    TextView tvtime, tvname, tvaddproduct;
    ImageView iveventimage, ivdelete;
    RelativeLayout relativeLayout, contaimerlayout;

    public MyViewHolder(View item) {
      tvtime = (TextView) item.findViewById(R.id.framelist_time);
      tvname = (TextView) item.findViewById(R.id.framelist_name);

      iveventimage = (ImageView) item.findViewById(R.id.framelist_image);
      ivdelete = (ImageView) item.findViewById(R.id.framelist_delete);
      relativeLayout = (RelativeLayout) item.findViewById(R.id.rl_video_indicator);
      contaimerlayout = (RelativeLayout) item.findViewById(R.id.rl_list_frame);
    }
  }

  private Bitmap decodeFile(File f) {
    try {
      // Decode image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(new FileInputStream(f), null, o);

      // The new size we want to scale to
      final int REQUIRED_SIZE = 50;

      // Find the correct scale value. It should be the power of 2.
      int scale = 1;
      while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
        scale *= 2;
      }

      // Decode with inSampleSize
      BitmapFactory.Options o2 = new BitmapFactory.Options();
      o2.inSampleSize = scale;
      o2.inPurgeable = true;
      return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    } catch (FileNotFoundException e) {
    }
    return null;
  }
}
