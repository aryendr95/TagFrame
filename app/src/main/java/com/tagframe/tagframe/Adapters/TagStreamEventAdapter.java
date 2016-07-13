package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Comment;
import com.tagframe.tagframe.Models.TagStream_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.LoadComment;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.listops;

import java.util.ArrayList;

/**
 * Created by abhinav on 08/04/2016.
 */
public class TagStreamEventAdapter extends BaseAdapter
{

    Context ctx;
    ArrayList<TagStream_Model> tagStream_models;
    LayoutInflater inflater;
    listops user_data;

    public TagStreamEventAdapter(Context ctx,ArrayList<TagStream_Model> tagStream_models)
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

        final TagStream_Model tagStream=tagStream_models.get(position);

        mViewHolder.tvTitlle.setText(tagStream.getTitle());
        mViewHolder.tvname.setText(tagStream.getName());
        mViewHolder.tvcurrentduration.setText(tagStream.getCreated_at());
        if(tagStream.isIn_center())
        {
            mViewHolder.iveventimage.setVisibility(View.GONE);
            mViewHolder.iveventvideo.setVisibility(View.VISIBLE);

            mViewHolder.iveventvideo.setVideoURI(Uri.parse(tagStream.getDataurl()));
            mViewHolder.iveventvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0,0);
                }
            });
        }
        else
        {
            mViewHolder.iveventimage.setVisibility(View.VISIBLE);
            mViewHolder.iveventvideo.setVisibility(View.GONE);

            Picasso.with(ctx).load(tagStream.getThumbnail()).into(mViewHolder.iveventimage);
        }


        mViewHolder.tvlike.setText(tagStream.getNumber_of_likes());

        Picasso.with(ctx).load(tagStream.getThumbnail()).into(mViewHolder.iveventimage);
        try {
            Picasso.with(ctx).load(tagStream.getProfile_picture()).into(mViewHolder.ivpropic);
        }
        catch (Exception e)
        {
            mViewHolder.ivpropic.setImageResource(R.drawable.pro_image);
        }




        mViewHolder.iveventimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, MakeNewEvent.class);
                intent.putExtra("data_url", tagStream.getDataurl());
                intent.putExtra("tittle", tagStream.getTitle());
                intent.putExtra("from", "tagstream");
                intent.putExtra("description",tagStream.getDescription());
                intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                intent.putExtra("eventtype", Constants.eventtype_internet);
                intent.putExtra("eventid", tagStream.getEvent_id());


                ctx.startActivity(intent);
            }
        });

        mViewHolder.iveventvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, MakeNewEvent.class);
                intent.putExtra("data_url", tagStream.getDataurl());
                intent.putExtra("tittle", tagStream.getTitle());
                intent.putExtra("from", "tagstream");
                intent.putExtra("description",tagStream.getDescription());
                intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                intent.putExtra("eventtype", Constants.eventtype_internet);
                intent.putExtra("eventid", tagStream.getEvent_id());


                ctx.startActivity(intent);
            }
        });

        mViewHolder.ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(tagStream.getSharelink(), ctx);
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


        //comments
        mViewHolder.llcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog(ctx,tagStream.getEvent_id(),user_data.getString(Constants.user_id));
            }
        });


        mViewHolder.tvframetext.setText(tagStream.getFrameList_modelArrayList().size()+"/"+"5"+" "+"Frames");

        return convertView;
    }

    private  void showCommentDialog(final Context ctx, final String video, final String user_id) {

        final Dialog dialog=new Dialog(ctx,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.setCancelable(true);

        final RecyclerView listview_comment=(RecyclerView) dialog.findViewById(R.id.list_comment);
        final EditText editext_comment=(EditText)dialog.findViewById(R.id.ed_dialog_comment);
        final LinearLayout layout=(LinearLayout)dialog.findViewById(R.id.mlayout_dialog_comment);
        ImageButton img_send_comment=(ImageButton) dialog.findViewById(R.id.img_dialog_send_comment);
        ProgressBar progressbar=(ProgressBar)dialog.findViewById(R.id.pbar_comment);

        //load comment task
        final LoadComment loadComment=new LoadComment(progressbar,listview_comment,video,dialog,ctx);
        loadComment.execute();

        //cancel load dialog task
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                loadComment.cancel(true);
            }
        });

        //cancelling dialog
        dialog.findViewById(R.id.img_comment_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        //send the comment
        img_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editext_comment.getText().toString().isEmpty())
                {


                    if(loadComment.getStatus() == AsyncTask.Status.FINISHED){
                        // My AsyncTask is done and onPostExecute was called

                        Broadcastresults mReceiver=((Modules)ctx).register_reviever();

                        Intent intent=new Intent(ctx, IntentServiceOperations.class);
                        intent.putExtra("operation", Constants.operation_comment);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("video_id",video);
                        intent.putExtra("parent_id","0");
                        intent.putExtra("comment",editext_comment.getText().toString());

                        intent.putExtra("receiver", mReceiver);
                        ctx.startService(intent);

                        ArrayList<Comment> commentArrayList=loadComment.getCommentArrayList();

                        Comment comment =new Comment();
                        comment.setVideo_id(video);
                        comment.setParent_id("-1");
                        comment.setComment(editext_comment.getText().toString());
                        comment.setUsername(user_data.getString(Constants.user_name));
                        comment.setProfile_image(user_data.getString(Constants.user_pic));
                        comment.setReplyCommentArrayList(new ArrayList<Comment.ReplyComment>());
                        //adding the comment to the first postion of list
                        //find a workaround since too much time taken to  move the n-1 number of elements
                        //commentArrayList.add(0,comment);

                        commentArrayList.add(comment);

                        listview_comment.getAdapter().notifyDataSetChanged();
                        //fast scroll to last item in list
                        //listview_comment.smoothScrollToPosition(listview_comment.getAdapter().getCount()-1);


                        //hiding keyboard and making the edittext empty
                        editext_comment.setText("");
                        InputMethodManager imm = (InputMethodManager) v.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);



                    }
                    else
                    {
                        InputMethodManager imm = (InputMethodManager) v.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        PopMessage.makesimplesnack(layout,"Comments are loading..");
                    }
                }
                else
                {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    PopMessage.makesimplesnack(layout,"Please enter a comment");
                }
            }
        });


        dialog.show();



    }


    private class MyViewHolder {
        TextView tvTitlle, tvname,tvcurrentduration,tvlike,tvlike_direct,tvframetext;
        ImageView iveventimage,ivlike;
        VideoView iveventvideo;
        LinearLayout ll_like,ll_share,llcomment,ll_frame;
        CircularImageView ivpropic;

        public MyViewHolder(View item) {
            tvTitlle = (TextView) item.findViewById(R.id.list_event_tittle);
            tvname = (TextView) item.findViewById(R.id.list_user_name);
            tvcurrentduration = (TextView) item.findViewById(R.id.list_user_duration);
            iveventimage=(ImageView)item.findViewById(R.id.list_event_image);

            ll_like=(LinearLayout)item.findViewById(R.id.lllike);
            ll_share=(LinearLayout)item.findViewById(R.id.llshare);
            llcomment=(LinearLayout)item.findViewById(R.id.llcomment);
            ll_frame=(LinearLayout)item.findViewById(R.id.lladd_frame);

            tvlike_direct=(TextView)item.findViewById(R.id.txt_like_directive);
            ivpropic=(CircularImageView)item.findViewById(R.id.list_pro_image);

            tvlike=(TextView)item.findViewById(R.id.txt_likes);
            ivlike=(ImageView)item.findViewById(R.id.imglike);
            tvframetext=(TextView)item.findViewById(R.id.txt_number_of_frames);

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
