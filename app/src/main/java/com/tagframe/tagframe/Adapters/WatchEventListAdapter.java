package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.UI.Acitivity.WatchEventActivity;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 10/14/2016.
 */
public class WatchEventListAdapter extends RecyclerView.Adapter<WatchEventListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Event_Model> eventModels;
    private AppPrefs user_data;
    private int next_rec = 0;
    private boolean isAdded = false;
    private boolean areCommentsLoaded = false;

    public WatchEventListAdapter(Context context, ArrayList<Event_Model> event_models) {
        this.context = context;
        this.eventModels = event_models;
        user_data=new AppPrefs(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_events, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder mViewHolder, int position) {
        final Event_Model tagStream = eventModels.get(position);
        final Context ctx=context;

        mViewHolder.tvTitlle.setText(tagStream.getTitle());
        mViewHolder.tvname.setText(tagStream.getName());
        mViewHolder.tvcurrentduration.setText(tagStream.getCreated_at());
        if (tagStream.isIn_center()) {
            mViewHolder.iveventimage.setVisibility(View.GONE);
            mViewHolder.iveventvideo.setVisibility(View.VISIBLE);

            mViewHolder.iveventvideo.setVideoURI(Uri.parse(tagStream.getDataurl()));
            mViewHolder.iveventvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0, 0);
                }
            });
        } else {
            mViewHolder.iveventimage.setVisibility(View.VISIBLE);
            mViewHolder.iveventvideo.setVisibility(View.GONE);

            try {
                Picasso.with(context).load(tagStream.getThumbnail()).into(mViewHolder.iveventimage);
            } catch (Exception e) {
                mViewHolder.iveventimage.setImageDrawable(new ColorDrawable(Color.GRAY));
            }
        }


        mViewHolder.tvlike.setText(tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");


        try {
            Picasso.with(context).load(tagStream.getProfile_picture()).into(mViewHolder.ivpropic);
        } catch (Exception e) {
            mViewHolder.ivpropic.setImageResource(R.drawable.pro_image);
        }


        mViewHolder.iveventimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(context,WatchEventActivity.class);
                intent.putExtra("name",tagStream.getName());
                //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
                intent.putExtra("likes", tagStream.getNumber_of_likes());
                intent.putExtra("frames", tagStream.getFrameList_modelArrayList().size());
                intent.putExtra("comments", tagStream.getNum_of_comments());
                intent.putExtra("data_url", tagStream.getDataurl());
                intent.putExtra("tittle", tagStream.getTitle());
                intent.putExtra("from", "tagstream");
                intent.putExtra("user_id", tagStream.getUser_id());
                intent.putExtra("description", tagStream.getDescription());
                intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                intent.putExtra("eventtype", Utility.eventtype_internet);
                intent.putExtra("eventid", tagStream.getEvent_id());
                intent.putExtra("tagged_user_id", tagStream.getTaggedUserModelArrayList());
                intent.putExtra("position", ((WatchEventActivity)ctx).getCurrentListPosition());
                intent.putExtra("sharelink", tagStream.getSharelink());
                intent.putExtra("likevideo",tagStream.getLike_video());*/
                if(context instanceof WatchEventActivity)
                {
                    Intent intent = new Intent();
                    intent.putExtra("name",tagStream.getName());
                    //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
                    intent.putExtra("likes", tagStream.getNumber_of_likes());
                    intent.putExtra("frames", tagStream.getFrameList_modelArrayList().size());
                    intent.putExtra("comments", tagStream.getNum_of_comments());
                    intent.putExtra("data_url", tagStream.getDataurl());
                    intent.putExtra("tittle", tagStream.getTitle());
                    intent.putExtra("from", "tagstream");
                    intent.putExtra("user_id", tagStream.getUser_id());
                    intent.putExtra("description", tagStream.getDescription());
                    intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                    intent.putExtra("eventtype", Utility.eventtype_internet);
                    intent.putExtra("eventid", tagStream.getEvent_id());
                    intent.putExtra("tagged_user_id", tagStream.getTaggedUserModelArrayList());
                    intent.putExtra("position", ((WatchEventActivity)ctx).getCurrentListPosition());
                    intent.putExtra("sharelink", tagStream.getSharelink());
                    intent.putExtra("likevideo",tagStream.getLike_video());
                    ((WatchEventActivity)context).playSelected(intent);
                }
               // context.startActivity(intent);
               // ((WatchEventActivity)context).finish();
            }
        });


        mViewHolder.ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(tagStream.getSharelink(), context);
            }
        });

        if (tagStream.getLike_video().equals("No")) {
            mViewHolder.tvlike_direct.setText("Like");
            mViewHolder.ivlike.setImageResource(R.drawable.like);
        } else {
            mViewHolder.tvlike_direct.setText("UnLike");
            mViewHolder.ivlike.setImageResource(R.drawable.unlike);
        }

        mViewHolder.ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Broadcastresults mReceiver;
                if(ctx instanceof Modules)
                    mReceiver = ((Modules) ctx).register_reviever();
                else
                mReceiver=((WatchEventActivity)ctx).register_reviever();

                if (tagStream.getLike_video().equals("No")) {


                    Intent intent = new Intent(ctx, IntentServiceOperations.class);
                    intent.putExtra("operation", Utility.operation_like);
                    intent.putExtra("user_id", user_data.getString(Utility.user_id));
                    intent.putExtra("event_id", tagStream.getEvent_id());
                    intent.putExtra("receiver", mReceiver);
                    ctx.startService(intent);
                    tagStream.setNumber_of_likes((Integer.parseInt(tagStream.getNumber_of_likes()) + 1) + "");
                    tagStream.setLike_video("Yes");
                    notifyDataSetChanged();

                } else {


                    Intent intent = new Intent(ctx, IntentServiceOperations.class);
                    intent.putExtra("operation", Utility.operation_unlike);
                    intent.putExtra("user_id", user_data.getString(Utility.user_id));
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
                showCommentDialog(ctx, tagStream.getEvent_id(), user_data.getString(Utility.user_id));
            }
        });


        //events might be of different user

        AppPrefs appPrefs = new AppPrefs(ctx);
        final int user_type = getUser_Type(tagStream.getUser_id(), appPrefs.getString(Utility.user_id));


        //click listners on profile photo and name
        mViewHolder.ivpropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Modules) ctx).setprofile(tagStream.getUser_id(), user_type);
            }
        });

        mViewHolder.tvname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Modules) ctx).setprofile(tagStream.getUser_id(), user_type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitlle, tvname, tvcurrentduration, tvlike, tvlike_direct;
        ImageView iveventimage, ivlike;
        VideoView iveventvideo;
        LinearLayout ll_like, ll_share, llcomment;
        CircularImageView ivpropic;

        public MyViewHolder(View item) {
            super(item);
            tvTitlle = (TextView) item.findViewById(R.id.list_event_tittle);
            tvname = (TextView) item.findViewById(R.id.list_user_name);
            tvcurrentduration = (TextView) item.findViewById(R.id.list_user_duration);
            iveventimage = (ImageView) item.findViewById(R.id.list_event_image);

            ll_like = (LinearLayout) item.findViewById(R.id.lllike);
            ll_share = (LinearLayout) item.findViewById(R.id.llshare);
            llcomment = (LinearLayout) item.findViewById(R.id.llcomment);

            tvlike_direct = (TextView) item.findViewById(R.id.txt_like_directive);
            ivpropic = (CircularImageView) item.findViewById(R.id.list_pro_image);

            tvlike = (TextView) item.findViewById(R.id.txt_likes);
            ivlike = (ImageView) item.findViewById(R.id.imglike);

            iveventvideo = (VideoView) item.findViewById(R.id.list_event_video);


        }
    }

    private int getUser_Type(String user_id, String saved_user_id) {
        if (user_id.equals(saved_user_id))
            return Utility.user_type_self;
        else
            return Utility.user_type_following;
    }

    public void showCommentDialog(final Context ctx, final String video, final String user_id) {

        if(ctx instanceof WatchEventActivity)
        {
            ((WatchEventActivity)ctx).pauseMediaPlayer();
        }

        next_rec = 0;
        isAdded = true;

        final Dialog dialog = new Dialog(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.setCancelable(true);

        final RecyclerView listview_comment = (RecyclerView) dialog.findViewById(R.id.list_comment);
        listview_comment.setNestedScrollingEnabled(false);
        final EditText editext_comment = (EditText) dialog.findViewById(R.id.ed_dialog_comment);
        final LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.mlayout_dialog_comment);
        ImageButton img_send_comment = (ImageButton) dialog.findViewById(R.id.img_dialog_send_comment);
        final ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.pbar_comment);

        final TextView m_txt_footer = (TextView) dialog.findViewById(R.id.txt_footer);

        //set Adapter to commentList
        final ArrayList<Comment> commentArrayList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        listview_comment.setLayoutManager(mLayoutManager);
        listview_comment.setItemAnimator(new DefaultItemAnimator());
        listview_comment.setAdapter(new CommentsRecyclerViewAdapter(commentArrayList, ctx));
        //load comment task

        m_txt_footer.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                loadComments(video, String.valueOf(next_rec), progressbar, m_txt_footer, listview_comment, commentArrayList);
                                            }
                                        }

        );

        loadComments(video, String.valueOf(next_rec), progressbar, m_txt_footer, listview_comment, commentArrayList);
        //cancelling dialog
        dialog.findViewById(R.id.img_comment_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdded = false;
                dialog.cancel();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if(ctx instanceof WatchEventActivity)
                {
                    ((WatchEventActivity)ctx).playMediaPlayer();
                }

            }
        });

        //send the comment
        img_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editext_comment.getText().toString().isEmpty()) {


                    if (areCommentsLoaded) {
                        // My AsyncTask is done and onPostExecute was called

                        Broadcastresults mReceiver = ((WatchEventActivity) ctx).register_reviever();

                        Intent intent = new Intent(ctx, IntentServiceOperations.class);
                        intent.putExtra("operation", Utility.operation_comment);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("video_id", video);
                        intent.putExtra("parent_id", "0");
                        intent.putExtra("comment", editext_comment.getText().toString());

                        intent.putExtra("receiver", mReceiver);
                        ctx.startService(intent);


                        Comment comment = new Comment();
                        comment.setVideo_id(video);
                        comment.setParent_id("-1");
                        comment.setComment(editext_comment.getText().toString());
                        comment.setUsername(user_data.getString(Utility.user_name));
                        comment.setProfile_image(user_data.getString(Utility.user_pic));
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
                } else {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    PopMessage.makesimplesnack(layout, "Please enter a comment");
                }
            }
        });


        dialog.show();


    }


    public void loadComments(String video_id, String next_records, final ProgressBar progressBar, final TextView textView, final RecyclerView recyclerView, final ArrayList<Comment> commentArrayList) {
        if (Networkstate.haveNetworkConnection(context)) {
            AppPrefs appPrefs=new AppPrefs(context);
            areCommentsLoaded = false;
            progressBar.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.getCommentList(video_id, next_records,appPrefs.getUser().getUser_id()).enqueue(new Callback<CommentsResponseModel>() {
                @Override
                public void onResponse(Call<CommentsResponseModel> call, Response<CommentsResponseModel> response) {
                    if (response.body().getStatus().equals(Utility.success_response)) {
                        progressBar.setVisibility(View.GONE);
                        if (isAdded) {
                            if (response.body().getCommentArrayList().size() > 0) {
                                commentArrayList.addAll(response.body().getCommentArrayList());
                                recyclerView.getAdapter().notifyDataSetChanged();

                                if (response.body().getCommentArrayList().size() == 3) {
                                    next_rec = next_rec + 3;
                                    textView.setText("Load More Comments..");
                                } else {
                                    textView.setOnClickListener(null);
                                    textView.setText("No More Comments..");
                                }

                            } else {
                                textView.setText("No Comments to load..");
                            }

                            areCommentsLoaded = true;
                        } else {
                            textView.setText("Error..");
                            PopMessage.makeshorttoast(context, "Error+ " + response.body().getStatus());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommentsResponseModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("Error..");

                }
            });
        } else {
            textView.setText(Utility.message_no_internet);
            PopMessage.makeshorttoast(context, Utility.message_no_internet);
        }
    }

    public void share(String link, Context ctx) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this event at TagFrame:" + link);
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }


}
