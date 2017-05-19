package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Comment;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.UI.Acitivity.WatchEventActivity;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.VideoPlayer.ExVidPlayerActivity;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 1/24/2017.
 */

public class TagStreamRecyclerAdapter
    extends RecyclerView.Adapter<TagStreamRecyclerAdapter.MyViewHolder> {
  private Context ctx;
  private ArrayList<Event_Model> tagStream_models;
  private LayoutInflater inflater;
  private AppPrefs user_data;
  private int next_rec = 0;
  private boolean isAdded = false;
  private boolean areCommentsLoaded = false;
  private int VIEWTYPE = 0, VIEWTYPE_FRAME = 1, VIEWTYPE_EVENT = 2;

  public TagStreamRecyclerAdapter(Context ctx, ArrayList<Event_Model> tagStream_models) {
    this.ctx = ctx;
    this.tagStream_models = tagStream_models;

    user_data = new AppPrefs(ctx);
    inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView;
    if (viewType == VIEWTYPE_EVENT) {

      itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.layout_list_tagstream, parent, false);
    } else {
      itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.layout_list_tagstream, parent, false);
    }
    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder mViewHolder, final int position) {
    final Event_Model tagStream = tagStream_models.get(position);
    //setting data
    mViewHolder.tvTitlle.setText(tagStream.getTitle());
    mViewHolder.tvname.setText(tagStream.getName());
    mViewHolder.tvcurrentduration.setText(tagStream.getCreated_at());
    if (tagStream.isIn_center()) {
      mViewHolder.iveventimage.setVisibility(View.GONE);
      mViewHolder.iveventvideo.setVisibility(View.VISIBLE);

      mViewHolder.iveventvideo.setVideoURI(Uri.parse(tagStream.getDataurl()));
      mViewHolder.iveventvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mp) {
          mp.setVolume(0, 0);
        }
      });
    } else {
      mViewHolder.iveventimage.setVisibility(View.VISIBLE);
      mViewHolder.iveventvideo.setVisibility(View.GONE);

      try {
        Picasso.with(ctx).load(tagStream.getThumbnail()).into(mViewHolder.iveventimage);
      } catch (Exception e) {
        mViewHolder.iveventimage.setImageDrawable(new ColorDrawable(Color.GRAY));
      }
    }

    mViewHolder.tvlike.setText(
        tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList()
            .size() + " Frames" + " and " + tagStream.getNum_of_comments() + " Comments");

    try {
      Picasso.with(ctx).load(tagStream.getProfile_picture()).into(mViewHolder.ivpropic);
    } catch (Exception e) {
      mViewHolder.ivpropic.setImageResource(R.drawable.pro_image);
    }
    if (tagStream.getAction_type().equals("frame")) {
      mViewHolder.iveventimage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          showFrame(ctx, tagStream);
        }
      });
      if (tagStream.getMedia_type()
          .equals(String.valueOf(com.tagframe.tagframe.Utils.Constants.frametype_image))) {
        mViewHolder.caption.setText("added new photo frame");
      } else {
        mViewHolder.caption.setText("added new video frame");
      }
    } else {

      mViewHolder.iveventimage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Intent intent = new Intent(ctx, WatchEventActivity.class);
          intent.putExtra("name", tagStream.getName());
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
          intent.putExtra("sharelink", tagStream.getSharelink());
          intent.putExtra("likevideo", tagStream.getLike_video());
          intent.putExtra("tagged_user_id", tagStream.getTaggedUserModelArrayList());
          ctx.startActivity(intent);
      /*Intent intent = new Intent(ctx, ExVidPlayerActivity.class);
        intent.putStringArrayListExtra("urls", getVideoUrls());
        intent.putStringArrayListExtra("types", getVideoTypes());
        intent.putExtra("currentIndex", position);
        ctx.startActivity(intent);*/
        }
      });

      mViewHolder.iveventvideo.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Intent intent = new Intent(ctx, MakeNewEvent.class);
          intent.putExtra("data_url", tagStream.getDataurl());
          intent.putExtra("tittle", tagStream.getTitle());
          intent.putExtra("from", "tagstream");
          intent.putExtra("description", tagStream.getDescription());
          intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
          intent.putExtra("eventtype", Utility.eventtype_internet);
          intent.putExtra("eventid", tagStream.getEvent_id());

          ctx.startActivity(intent);
        }
      });

      mViewHolder.ll_share.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          share(tagStream.getSharelink(), ctx);
        }
      });

      if (tagStream.getLike_video().equals("No")) {
        if (Utility.getScreenOrientation((Modules) ctx) == Configuration.ORIENTATION_PORTRAIT) {
          mViewHolder.tvlike_direct.setText("Like");
          mViewHolder.ivlike.setImageResource(R.drawable.like);
        } else {
          mViewHolder.ivlike.setImageResource(R.drawable.ic_thumb_up_white_24dp);
        }
      } else {
        if (Utility.getScreenOrientation((Modules) ctx) == Configuration.ORIENTATION_PORTRAIT) {
          mViewHolder.tvlike_direct.setText("UnLike");
          mViewHolder.ivlike.setImageResource(R.drawable.unlike);
        } else {
          mViewHolder.ivlike.setImageResource(R.drawable.ic_thumb_down_white_24dp);
        }
      }

      mViewHolder.ll_like.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          if (tagStream.getLike_video().equals("No")) {
            Broadcastresults mReceiver = ((Modules) ctx).register_reviever();

            Intent intent = new Intent(ctx, IntentServiceOperations.class);
            intent.putExtra("operation", Utility.operation_like);
            intent.putExtra("user_id", user_data.getString(Utility.user_id));
            intent.putExtra("event_id", tagStream.getEvent_id());
            intent.putExtra("receiver", mReceiver);
            ctx.startService(intent);
            tagStream.setNumber_of_likes(
                (Integer.parseInt(tagStream.getNumber_of_likes()) + 1) + "");
            tagStream.setLike_video("Yes");
            notifyDataSetChanged();
          } else {
            Broadcastresults mReceiver = ((Modules) ctx).register_reviever();

            Intent intent = new Intent(ctx, IntentServiceOperations.class);
            intent.putExtra("operation", Utility.operation_unlike);
            intent.putExtra("user_id", user_data.getString(Utility.user_id));
            intent.putExtra("event_id", tagStream.getEvent_id());
            intent.putExtra("receiver", mReceiver);
            tagStream.setNumber_of_likes(
                (Integer.parseInt(tagStream.getNumber_of_likes()) - 1) + "");
            tagStream.setLike_video("No");
            ctx.startService(intent);
            notifyDataSetChanged();
          }
        }
      });

      //comments
      mViewHolder.llcomment.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          showCommentDialog(ctx, tagStream.getEvent_id(), user_data.getString(Utility.user_id));
        }
      });
    }
    //events might be of different user

    AppPrefs appPrefs = new AppPrefs(ctx);
    final int user_type = getUser_Type(tagStream.getUser_id(), appPrefs.getString(Utility.user_id));

    //click listners on profile photo and name
    mViewHolder.ivpropic.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ((Modules) ctx).setprofile(tagStream.getUser_id(), user_type);
      }
    });

    mViewHolder.tvname.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ((Modules) ctx).setprofile(tagStream.getUser_id(), user_type);
      }
    });

    //frame list
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    mViewHolder.rcFrames.setLayoutManager(linearLayoutManager);
    ArrayList<FrameList_Model> myFramesList=tagStream.getMyFrameArrraYlist();
    mViewHolder.rcFrames.setAdapter(new TagStreamFrameRecyclerAdapter(ctx, myFramesList,"mine"));
    if(myFramesList.size()==0)
    {
      mViewHolder.tvFrames.setVisibility(View.GONE);
    }
    else
    {
      mViewHolder.tvFrames.setVisibility(View.VISIBLE);
    }

    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(ctx);
    linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
    mViewHolder.rcOtherFrame.setLayoutManager(linearLayoutManager1);
    ArrayList<FrameList_Model> otherFramesList=tagStream.getOtherFrameArrraYlist();
    mViewHolder.rcOtherFrame.setAdapter(new TagStreamFrameRecyclerAdapter(ctx, otherFramesList,"other"));


  }

  private ArrayList<FrameList_Model> getOtherFrameList(ArrayList<FrameList_Model> frameList_modelArrayList) {
      ArrayList<FrameList_Model> frameList = new ArrayList<>();
      for (FrameList_Model fm : frameList_modelArrayList) {
        if(!(fm.getUser_id().equals(Utility.getUserId(ctx))))
        {
          frameList.add(fm);
        }
      }
      return frameList;
  }

  private ArrayList<FrameList_Model> getMyFrameList(
      ArrayList<FrameList_Model> frameList_modelArrayList) {
    ArrayList<FrameList_Model> frameList = new ArrayList<>();
    for (FrameList_Model fm : frameList_modelArrayList) {
      if(fm.getUser_id().equals(Utility.getUserId(ctx)))
      {
        frameList.add(fm);
      }
    }
    return frameList;
  }

  public void share(String link, Context ctx) {
    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out this event at TagFrame:" + link);
    sendIntent.setType("text/plain");
    ctx.startActivity(sendIntent);
  }

  private ArrayList<String> getVideoTypes() {
    ArrayList<String> types = new ArrayList<>();
    for (Event_Model video : tagStream_models) {
      types.add(Constants.MEDIA_TYPE_HLS);
    }
    return types;
  }

  private ArrayList<String> getVideoUrls() {
    ArrayList<String> urls = new ArrayList<>();
    for (Event_Model video : tagStream_models) {
      urls.add(video.getDataurl());
    }
    return urls;
  }

  private int getUser_Type(String user_id, String saved_user_id) {
    if (user_id.equals(saved_user_id)) {
      return Utility.user_type_self;
    } else {
      return Utility.user_type_following;
    }
  }

  public void showCommentDialog(final Context ctx, final String video, final String user_id) {

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
    RecyclerView.LayoutManager mLayoutManager =
        new LinearLayoutManager(ctx.getApplicationContext());
    listview_comment.setLayoutManager(mLayoutManager);
    listview_comment.setItemAnimator(new DefaultItemAnimator());
    listview_comment.setAdapter(new CommentsRecyclerViewAdapter(commentArrayList, ctx));
    //load comment task

    m_txt_footer.setOnClickListener(new View.OnClickListener() {
                                      @Override public void onClick(View v) {
                                        loadComments(video, String.valueOf(next_rec), progressbar, m_txt_footer, listview_comment,
                                            commentArrayList);
                                      }
                                    }

    );

    loadComments(video, String.valueOf(next_rec), progressbar, m_txt_footer, listview_comment,
        commentArrayList);
    //cancelling dialog
    dialog.findViewById(R.id.img_comment_dialog_back)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            isAdded = false;
            dialog.cancel();
          }
        });

    //send the comment
    img_send_comment.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (!editext_comment.getText().toString().isEmpty()) {

          if (areCommentsLoaded) {
            // My AsyncTask is done and onPostExecute was called

            Broadcastresults mReceiver = ((Modules) ctx).register_reviever();

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
            InputMethodManager imm =
                (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
          }
        } else {
          InputMethodManager imm =
              (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
          PopMessage.makesimplesnack(layout, "Please enter a comment");
        }
      }
    });

    dialog.show();
  }

  public void loadComments(String video_id, String next_records, final ProgressBar progressBar,
      final TextView textView, final RecyclerView recyclerView,
      final ArrayList<Comment> commentArrayList) {
    if (Networkstate.haveNetworkConnection(ctx)) {

      areCommentsLoaded = false;
      progressBar.setVisibility(View.VISIBLE);
      ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
      apiInterface.getCommentList(video_id, next_records)
          .enqueue(new Callback<CommentsResponseModel>() {
            @Override public void onResponse(Call<CommentsResponseModel> call,
                Response<CommentsResponseModel> response) {
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
                  PopMessage.makeshorttoast(ctx, "Error+ " + response.body().getStatus());
                }
              }
            }

            @Override public void onFailure(Call<CommentsResponseModel> call, Throwable t) {
              progressBar.setVisibility(View.GONE);
              textView.setText("Error..");
              Log.e("call failed", t.getMessage());
            }
          });
    } else {
      textView.setText(Utility.message_no_internet);
      PopMessage.makeshorttoast(ctx, Utility.message_no_internet);
    }
  }

  @Override public int getItemCount() {
    return tagStream_models.size();
  }

  @Override public int getItemViewType(int position) {
    Event_Model event_model = tagStream_models.get(position);

    if (event_model.getAction_type().equals("frame")) {
      return VIEWTYPE_FRAME;
    } else {
      return VIEWTYPE_EVENT;
    }
  }

  public static void showFrame(final Context ctx, final Event_Model tagStream) {
    if (tagStream.getMedia_type()
        .equals(String.valueOf(com.tagframe.tagframe.Utils.Constants.frametype_image))) {
      final Dialog dialog = new Dialog(ctx);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      Window window = dialog.getWindow();
      window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      window.setGravity(Gravity.CENTER);
      dialog.setCancelable(false);
      dialog.setContentView(R.layout.dialog_frame_display_profile);

      // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
      ImageView frameimage = (ImageView) dialog.findViewById(R.id.framelist_image);
      ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);
      ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);

      if (!tagStream.getProduct_id().equals("0") && !tagStream.is_product_frame()) {
        product_image.setVisibility(View.VISIBLE);
        Picasso.with(ctx).load(tagStream.getProduct_image()).into(product_image);
      }

      //TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
      final TextView tittle = (TextView) dialog.findViewById(R.id.framelist_name);

      RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_see_event);
      relativeLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          Intent intent = new Intent(ctx, WatchEventActivity.class);
          intent.putExtra("name", tagStream.getName());
          //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
          intent.putExtra("likes", tagStream.getNumber_of_likes());
          intent.putExtra("frames", tagStream.getFrameList_modelArrayList().size());
          intent.putExtra("comments", tagStream.getNum_of_comments());
          intent.putExtra("data_url", tagStream.getDataurl());
          intent.putExtra("tittle", tagStream.getTitle());
          intent.putExtra("from", "tagstream");
          intent.putExtra("user_id", tagStream.getUser_id());
          intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
          intent.putExtra("eventtype", Utility.eventtype_internet);
          intent.putExtra("eventid", tagStream.getEvent_id());
          intent.putExtra("sharelink", tagStream.getSharelink());
          intent.putExtra("likevideo", tagStream.getLike_video());
          intent.putExtra("tagged_user_id", tagStream.getTaggedUserModelArrayList());
          ctx.startActivity(intent);
        }
      });

      frameimage.setVisibility(View.VISIBLE);
      Picasso.with(ctx).load(tagStream.getThumbnail()).into(frameimage);

      tittle.setText(tagStream.getTitle());
      // duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

      delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          dialog.dismiss();
        }
      });

      dialog.show();
    } else {
      showVideoDialog(ctx, tagStream);
    }
  }

  private static void showVideoDialog(final Context ctx, final Event_Model frameList_model) {
    final Dialog dialog = new Dialog(ctx);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(false);
    dialog.setContentView(R.layout.dialog_framlist_video_frame);

    WindowManager.LayoutParams a = dialog.getWindow().getAttributes();
    a.dimAmount = 0;
    dialog.getWindow().setAttributes(a);

    dialog.getWindow()
        .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

    final VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
    final RelativeLayout coverLayout = (RelativeLayout) dialog.findViewById(R.id.cover);
    final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.pbar_video_dialog);

    final ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);

    ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);
    if (!frameList_model.getProduct_id().equals("0") && !frameList_model.is_product_frame()) {
      product_image.setVisibility(View.VISIBLE);
      Picasso.with(ctx).load(frameList_model.getProduct_image_url()).into(product_image);
    }

    TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
    final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);

    dialog.findViewById(R.id.see_event).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(ctx, WatchEventActivity.class);
        intent.putExtra("name", frameList_model.getName());
        //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
        intent.putExtra("likes", frameList_model.getNumber_of_likes());
        intent.putExtra("frames", frameList_model.getFrameList_modelArrayList().size());
        intent.putExtra("comments", frameList_model.getNum_of_comments());
        intent.putExtra("data_url", frameList_model.getDataurl());
        intent.putExtra("tittle", frameList_model.getTitle());
        intent.putExtra("from", "tagstream");
        intent.putExtra("user_id", frameList_model.getUser_id());
        intent.putParcelableArrayListExtra("framelist",
            frameList_model.getFrameList_modelArrayList());
        intent.putExtra("eventtype", Utility.eventtype_internet);
        intent.putExtra("eventid", frameList_model.getEvent_id());
        intent.putExtra("sharelink", frameList_model.getSharelink());
        intent.putExtra("likevideo", frameList_model.getLike_video());
        intent.putExtra("tagged_user_id", frameList_model.getTaggedUserModelArrayList());
        ctx.startActivity(intent);

        dialog.dismiss();
      }
    });

    try {

      framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_media_url()));
      MediaController mediaController = new MediaController(ctx);
      framevideo.setMediaController(mediaController);
      mediaController.setAnchorView(framevideo);
      framevideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mp) {
          coverLayout.setVisibility(View.GONE);
          progressBar.setVisibility(View.GONE);
        }
      });
      framevideo.start();
    } catch (Exception e) {

    }

    tittle.setText(frameList_model.getName());
    //duration.setText(Utility.milliSecondsToTimer(frameList_model.getS) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));
    duration.setVisibility(View.GONE);
    //video controls
    final SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekbar_dialog);

    final TextView current = (TextView) dialog.findViewById(R.id.dialog_txttotalduration);
    final TextView total = (TextView) dialog.findViewById(R.id.dialog_txtcurrentduration);
    final ImageButton play_stop = (ImageButton) dialog.findViewById(R.id.dialog_btn_play_stop);

    final Handler myHandler = new Handler();

    final Runnable mUpdateDialogVideo = new Runnable() {
      @Override public void run() {
        long totalDurationn = 0;
        long currentDuration = 0;
        try {
          totalDurationn = framevideo.getDuration();
          currentDuration = framevideo.getCurrentPosition();
        } catch (Exception e) {
          totalDurationn = 0;
          currentDuration = 0;
        }

        current.setText("" + Utility.milliSecondsToTimer(totalDurationn));
        // Displaying time completed playing
        total.setText("" + Utility.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDurationn));

        seekBar.setProgress(progress);
        myHandler.postDelayed(this, 100);
      }
    };
    myHandler.postDelayed(mUpdateDialogVideo, 100);

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(mUpdateDialogVideo);
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(mUpdateDialogVideo);
        int total = framevideo.getDuration();
        int current = Utility.progressToTimer(seekBar.getProgress(), total);
        framevideo.seekTo(current);
        myHandler.postDelayed(mUpdateDialogVideo, 100);
      }
    });

    delete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.cancel();
      }
    });

    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override public void onCancel(DialogInterface dialog) {
        myHandler.removeCallbacks(mUpdateDialogVideo);
      }
    });

    play_stop.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (framevideo.isPlaying()) {
          play_stop.setImageResource(R.drawable.dialog_play);
          framevideo.pause();
        } else {
          play_stop.setImageResource(R.drawable.dialog_pause);
          framevideo.start();
        }
      }
    });

    dialog.show();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitlle, tvname, tvcurrentduration, tvlike, tvlike_direct, caption,tvFrames;
    ImageView iveventimage, ivlike;
    VideoView iveventvideo;
    LinearLayout ll_like, ll_share, llcomment;
    CircularImageView ivpropic;
    RecyclerView rcFrames,rcOtherFrame;

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
      tvFrames = (TextView) item.findViewById(R.id.tvYourFrames);
      ivpropic = (CircularImageView) item.findViewById(R.id.list_pro_image);

      tvlike = (TextView) item.findViewById(R.id.txt_likes);
      ivlike = (ImageView) item.findViewById(R.id.imglike);

      iveventvideo = (VideoView) item.findViewById(R.id.list_event_video);
      caption = (TextView) item.findViewById(R.id.caption);
      rcFrames = (RecyclerView) item.findViewById(R.id.rcFrameList);
      rcOtherFrame = (RecyclerView) item.findViewById(R.id.rcOtherFrameList);
    }
  }
}
