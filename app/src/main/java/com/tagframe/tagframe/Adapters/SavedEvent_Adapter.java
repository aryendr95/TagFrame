package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;


import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.SingleEventModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.SavedEvents;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.AppPrefs;

import com.veer.multiselect.Util.LoadBitmap;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by abhinav on 08/04/2016.
 */
public class SavedEvent_Adapter extends BaseAdapter {
    Context ctx;
    ArrayList<SingleEventModel> tagStream_models;
    LayoutInflater inflater;
    AppPrefs userdata;


    public SavedEvent_Adapter(Context ctx, ArrayList<SingleEventModel> tagStream_models) {
        this.ctx = ctx;
        this.tagStream_models = tagStream_models;
        userdata = new AppPrefs(ctx);
        inflater = (LayoutInflater) ctx
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
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_saved_events, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final SingleEventModel tagStream = tagStream_models.get(position);

        mViewHolder.tvTitlle.setText(tagStream.getTittle());
        //  mViewHolder.tvname.setText(tagStream.getName());
        mViewHolder.tvcurrentduration.setText(tagStream.getTime());

        mViewHolder.iveventimage.setVisibility(View.VISIBLE);
        // mViewHolder.iveventvideo.setVisibility(View.GONE);

       /* Picasso.with(ctx).load(tagStream.getThumbnail()).into(mViewHolder.iveventimage);
        Glide
                .with( ctx )
                .load( Uri.fromFile( new File( tagStream.getVidaddress()) ) )
                .into(mViewHolder.iveventimage);*/


       /* Bitmap thumb = ThumbnailUtils.createVideoThumbnail(tagStream.getVidaddress(),
                MediaStore.Images.Thumbnails.MICRO_KIND);

        //getting screen width
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p=new Point();
        display.getSize(p);
        int width=p.x;


        thumb=getResizedBitmap(thumb,width,200 );




        mViewHolder.iveventimage.setImageBitmap(thumb);*/

        LoadBitmap.loadBitmap(tagStream.getVidaddress(), mViewHolder.iveventimage);

        mViewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate_pop_up_add_frame(mViewHolder.menu, ctx, position);
            }
        });

        return convertView;
    }

    private class MyViewHolder {
        TextView tvTitlle, tvname, tvcurrentduration;
        ImageView iveventimage, menu;
        VideoView iveventvideo;

        public MyViewHolder(View item) {
            tvTitlle = (TextView) item.findViewById(R.id.list_event_tittle);
            //tvname = (TextView) item.findViewById(R.id.list_user_name);
            tvcurrentduration = (TextView) item.findViewById(R.id.list_user_duration);
            iveventimage = (ImageView) item.findViewById(R.id.list_event_image);
            menu = (ImageView) item.findViewById(R.id.img_menu_save_event);

            //iveventvideo=(VideoView)item.findViewById(R.id.list_event_video);

        }
    }

    public void generate_pop_up_add_frame(View v, final Context ctx, int pos) {

        final int p = pos;
        final Context c = ctx;
        PopupMenu popup = new PopupMenu(ctx, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_saved_events, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save_edit:

                        SingleEventModel singleEventModel = tagStream_models.get(p);

                        Intent intent = new Intent(c, MakeNewEvent.class);
                        intent.putExtra("data_url", singleEventModel.getVidaddress());
                        intent.putExtra("tittle", singleEventModel.getTittle());
                        intent.putExtra("des", singleEventModel.getDescription());
                        intent.putExtra("type", singleEventModel.getType());
                        intent.putParcelableArrayListExtra("framelist", singleEventModel.getFrameList_modelArrayList());
                        intent.putExtra("eventtype", Utility.eventtype_saved);
                        intent.putExtra("tagged_user_id", singleEventModel.getTaggedUserModelArrayList());

                        ctx.startActivity(intent);


                        break;

                    case R.id.save_post:
                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.

                        SingleEventModel sm = tagStream_models.get(p);
                        method_post_event(ctx, sm.getVidaddress(), sm.getFrameList_modelArrayList(), sm.getTittle(), sm.getDescription(), sm.getDuration(), userdata.getString(Utility.user_id),sm.getTaggedUserModelArrayList());
                        break;

                    case R.id.save_delete:
                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.
                        tagStream_models.remove(p);
                        userdata.putsingleeventlist(tagStream_models);
                        notifyDataSetChanged();


                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public void method_post_event(Context ctx, String vidAddress, ArrayList<FrameList_Model> framedata_map, String tittle, String description, int durat, String userid, ArrayList<TaggedUserModel> taggedUserModelArrayList) {

        MyToast.popmessage("Posting...", ctx);
        Intent intent = new Intent(ctx, IntentServiceOperations.class);
        intent.putExtra("video_url", vidAddress);
        intent.putParcelableArrayListExtra("frame_list", framedata_map);
        intent.putExtra("operation", Utility.operation_post_event);
        intent.putExtra("title", tittle);
        intent.putExtra("description", description);
        //intent.putExtra("access_type", type);
        intent.putExtra("duration", durat);
        intent.putParcelableArrayListExtra("tagul",taggedUserModelArrayList);
        Broadcastresults mreciver = ((SavedEvents) ctx).register_reviever();
        intent.putExtra("receiver", mreciver);
        intent.putExtra("user_id", userid);

        ctx.startService(intent);


    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}