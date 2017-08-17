package com.tagframe.tagframe.UI.Acitivity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.TaggedUserAdapter;
import com.tagframe.tagframe.Adapters.WatchEventListAdapter;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.CustomSeekBar;
import com.tagframe.tagframe.Utils.EndlessRecyclerViewScrollListener;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.listsort;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.VideoPlayer.ExSimpleVpFragment;
import com.veer.exvidplayer.VideoPlayer.ExVpCompleteFragment;
import com.veer.exvidplayer.VideoPlayer.ExVpFragment;
import com.veer.exvidplayer.VideoPlayer.ExVpListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchEventActivity extends AppCompatActivity implements Broadcastresults.Receiver {
  //controls
  private RelativeLayout root;
  private ImageButton ivNext, ivPrev, ivForword, ivRev, ivPlayPause, ivSetting;
  private CustomSeekBar mProgress;
  private TextView tvCurrent, tvTotal;
  private ExVpListener exVpControls;
  private FrameLayout frameLayout;
  //views
  private ScrollView bottomLayout;
  private ImageView ivTagUsers, ivAddFrame, ivPlayback, img_frame_to_show, img_play_video, img_done,
      img_like;
  private RecyclerView rvEventList;
  private LinearLayout ll_like, ll_share, llcomment, mLayout;
  private TextView mtxt_directive, tvEventTittle, tvStats, tvBuffering, tvUsername;
  private ProgressBar mPbar_events;
  private RelativeLayout.LayoutParams params;
  private RelativeLayout ll_dimer;
  //vars
  private Point p = new Point();

  private ArrayList<Event_Model> event_modelArrayList = new ArrayList<>();
  private ArrayList<String> video_urls = new ArrayList<>();
  private ArrayList<String> video_type = new ArrayList<>();
  private String vidAddress, tittle, event_id, userName, user_id, shareLink, likevideo;
  private int likes, frames, comments;
  private ArrayList<FrameList_Model> framedata_map;
  private ArrayList<TaggedUserModel> list_tagged_user;
  private Broadcastresults mReceiver;
  private boolean shouldLoad = false;
  private int next_records = 0, currentListPosition = 0;
  private int frameSize;
  private ProgressBar pbarLoadFrame;
  private boolean isFrameShowing = false;
  private Handler handler = new Handler();
  private static String[] PERMISSIONS_STORAGE = {
      Manifest.permission.WRITE_SETTINGS
  };
  private int currentDuration = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_watch_event);
    getIntentData(getIntent());
    init();
    setDatatoView();
  }

  @Override protected void onResume() {
    super.onResume();
    setUpPlayer();
    if (mProgress != null && currentDuration != mProgress.getMax()) {
      addTrackAndPlay(getIntent());
      if (exVpControls != null) exVpControls.seekToProgress(currentDuration);
      handler.postDelayed(runnable, 100);
    }
  }

  @Override protected void onPause() {
    currentDuration = mProgress.getProgress();
    handler.removeCallbacks(runnable);
    super.onPause();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    getWindowManager().getDefaultDisplay().getSize(p);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      bottomLayout.setVisibility(View.GONE);
    } else {
      bottomLayout.setVisibility(View.VISIBLE);
    }
  }

  public void getIntentData(Intent intent) {
    vidAddress = intent.getStringExtra("data_url");
    video_urls.add(intent.getStringExtra("data_url"));
    video_type.add(Constants.MEDIA_TYPE_HLS);
    framedata_map = intent.getParcelableArrayListExtra("framelist");
    if (framedata_map != null && framedata_map.size() > 0) {
      Collections.sort(framedata_map, new listsort());
    }
    list_tagged_user = intent.getParcelableArrayListExtra("tagged_user_id");
    tittle = intent.getStringExtra("tittle");
    event_id = intent.getStringExtra("eventid");
    userName = intent.getStringExtra("name");
    user_id = intent.getStringExtra("user_id");
    shareLink = intent.getStringExtra("sharelink");
    currentListPosition = intent.getIntExtra("position", 0);
    likevideo = intent.getStringExtra("likevideo");
    try {
      likes = Integer.parseInt(intent.getStringExtra("likes"));
    } catch (NumberFormatException e) {
      likes = 0;
    }
    try {
      frames = Integer.parseInt(intent.getStringExtra("frames"));
    } catch (NumberFormatException e) {
      frames = 0;
    }
    try {
      comments = Integer.parseInt(intent.getStringExtra("comments"));
    } catch (NumberFormatException e) {
      frames = 0;
    }
  }

  private void setUpControls() {
    ivForword.setVisibility(View.GONE);
    ivRev.setVisibility(View.GONE);
    ivForword.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        exVpControls.forward();
      }
    });
    ivRev.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        exVpControls.reverse();
      }
    });
    ivNext.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        exVpControls.nextTrack();
      }
    });
    ivPrev.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        exVpControls.previousTrack();
      }
    });
    ivPlayPause.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVpControls.isPlaying()) {
          ivPlayPause.setImageResource(R.drawable.ic_play_circle_filled_white_white_24dp);
          pauseMediaPlayer();
        } else {
          playMediaPlayer();
          ivPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
        }
      }
    });
    ivSetting.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        exVpControls.changeQuality(view);
      }
    });
    exVpControls.setControlLayout(root);
    exVpControls.setProgressBar(mProgress);
    exVpControls.setCurrentText(tvCurrent);
    exVpControls.setDurationText(tvTotal);
  }

  public void setDatatoView() {
    try {
      tvEventTittle.setText(tittle);
      tvStats.setText(likes
          + " Likes"
          + ", "
          + framedata_map.size()
          + " Frames"
          + " and "
          + comments
          + " Comments");
      tvUsername.setText("More from " + userName);
      if (likevideo.equals("No")) {
        img_like.setImageResource(R.drawable.like);
        mtxt_directive.setText("Like");
      } else {
        img_like.setImageResource(R.drawable.unlike);
        mtxt_directive.setText("Unlike");
      }
    } catch (NullPointerException e) {
      tvUsername.setText("More from this user");
    }
  }

  private void setUpPlayer() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    ExSimpleVpFragment exVpFragment = new ExSimpleVpFragment();
    exVpControls = exVpFragment.getExVpListener();
    setUpControls();
    Bundle bundle = new Bundle();
    bundle.putStringArrayList("urls", video_urls);
    bundle.putStringArrayList("type", video_type);
    bundle.putInt("currentIndex", currentListPosition);
    exVpFragment.setArguments(bundle);
    fragmentTransaction.replace(R.id.parent, exVpFragment);
    fragmentTransaction.commit();
    handler.postDelayed(runnable, 100);
  }

  private void init() {
    frameSize = (int) getResources().getDimension(R.dimen.frame_size);
    frameSize = 150;
    getWindowManager().getDefaultDisplay().getSize(p);
    frameLayout = (FrameLayout) findViewById(R.id.parent);
    root = (RelativeLayout) findViewById(R.id.root);
    ll_dimer = (RelativeLayout) findViewById(R.id.dimer_layout);
    ivRev = (ImageButton) findViewById(R.id.btn_rev);
    ivForword = (ImageButton) findViewById(R.id.btn_fwd);
    ivNext = (ImageButton) findViewById(R.id.btn_next);
    ivPrev = (ImageButton) findViewById(R.id.btn_prev);
    ivPlayPause = (ImageButton) findViewById(R.id.btn_pause);
    ivSetting = (ImageButton) findViewById(R.id.btn_settings);
    mProgress = (CustomSeekBar) findViewById(R.id.seekbar);
    tvCurrent = (TextView) findViewById(R.id.txt_currentTime);
    tvTotal = (TextView) findViewById(R.id.txt_totalDuration);
    bottomLayout = (ScrollView) findViewById(R.id.bottomLayout);
    mLayout = (LinearLayout) findViewById(R.id.activity_simple_player);

    mPbar_events = (ProgressBar) findViewById(R.id.pbar_events);

    ll_like = (LinearLayout) findViewById(R.id.lllike);
    ll_share = (LinearLayout) findViewById(R.id.llshare);
    llcomment = (LinearLayout) findViewById(R.id.llcomment);

    ivAddFrame = (ImageView) findViewById(R.id.add_frame);
    ivTagUsers = (ImageView) findViewById(R.id.taguser_event);
    img_done = (ImageView) findViewById(R.id.donewatching);
    img_like = (ImageView) findViewById(R.id.imglike);

    ll_like.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        likeEvent();
      }
    });
    ll_share.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        shareEvent();
      }
    });
    llcomment.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        commentOnEvent();
      }
    });

    mtxt_directive = (TextView) findViewById(R.id.txt_like_directive);

    ivAddFrame.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(WatchEventActivity.this, MakeNewEvent.class);
        intent.putExtra("name", userName);
        intent.putExtra("stats", "");
        intent.putExtra("data_url", vidAddress);
        intent.putExtra("tittle", tittle);
        intent.putExtra("from", "tagstream");
        intent.putExtra("user_id", user_id);
        intent.putExtra("description", "");
        intent.putParcelableArrayListExtra("framelist", framedata_map);
        intent.putExtra("eventtype", Utility.eventtype_internet);
        intent.putExtra("eventid", event_id);
        intent.putExtra("tagged_user_id", list_tagged_user);
        startActivity(intent);
      }
    });
    ivTagUsers.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        show_tagged_user();
      }
    });

    img_done.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    tvUsername = (TextView) findViewById(R.id.txt_more_from);
    tvEventTittle = (TextView) findViewById(R.id.txt_event_tittle);
    tvStats = (TextView) findViewById(R.id.txt_stats);

    img_frame_to_show = (ImageView) findViewById(R.id.img_frame_to_show);
    img_play_video = (ImageView) findViewById(R.id.img_play_video);
    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    params.leftMargin = 0;
    img_frame_to_show.setLayoutParams(params);
    img_play_video.setLayoutParams(params);
    setUpEventList();
    img_frame_to_show.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String TAG = img_frame_to_show.getTag().toString();
        Integer pos = Integer.parseInt(TAG);

        show_synced_frame(pos);
      }
    });

    mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        exVpControls.seekToProgress(seekBar.getProgress());
      }
    });

  }

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      if (exVpControls != null && exVpControls.isPlaying() && mProgress.getProgress() != 0) {
        if (mProgress.getProgress() != mProgress.getMax()) {
          showFrame(mProgress.getProgress());
        } else {
          img_play_video.setVisibility(View.INVISIBLE);
          img_frame_to_show.setVisibility(View.INVISIBLE);
        }

        Utility.updateseekbar(mProgress, mProgress.getMax(), WatchEventActivity.this,
            framedata_map);
      }
      handler.postDelayed(runnable, 100);
    }
  };

  private void showFrame(int progress) {
    img_play_video.setVisibility(View.INVISIBLE);
    img_frame_to_show.setVisibility(View.INVISIBLE);
    for (int i = 0; i < framedata_map.size(); i++) {
      final FrameList_Model fm = framedata_map.get(i);

      if (fm.getStarttime() - 100 <= progress
          && progress <= fm.getEndtime() + 100
          && fm.getEndtime() != 0) {

        img_frame_to_show.setVisibility(View.VISIBLE);
        if (fm.getFrametype() == Utility.frametype_image) {
          Picasso.with(WatchEventActivity.this)
              .load(fm.getFrame_image_url())
              .resize(frameSize, frameSize)
              .into(img_frame_to_show);
        } else {
          img_play_video.setVisibility(View.VISIBLE);
          img_play_video.setImageResource(R.drawable.playvideo);
          if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fm.getImagepath(),
                MediaStore.Images.Thumbnails.MINI_KIND);

            thumb = Utility.getResizedBitmap(thumb, frameSize, frameSize);
            img_frame_to_show.setImageBitmap(thumb);
          } else {

            Picasso.with(WatchEventActivity.this)
                .load(fm.getImagepath())
                .resize(frameSize, frameSize)
                .into(img_frame_to_show);
          }
        }

        img_frame_to_show.setTag(i + "");
        int totalDuration = mProgress.getMax();
        int prog = (fm.getStarttime() * 100) / totalDuration;
        //show_frame_on_seekbar(prog - (int) (Utility.getProgressPercentage(2000, totalDuration)));

        show_frame_on_seekbar(prog);
      }
    }
  }

  public void show_frame_on_seekbar(final int progress) {

    int measure = (int) ((((float) progress * p.x) / 100) - (progress));
    if (p.x - measure < Utility.dpToPx(150)) {

      params.leftMargin = p.x - Utility.dpToPx(100);
    } else {
      params.leftMargin = measure;
    }
    img_frame_to_show.setLayoutParams(params);
    img_play_video.setLayoutParams(params);
  }

  private void setUpEventList() {
    rvEventList = (RecyclerView) findViewById(R.id.list_event_users);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    rvEventList.setLayoutManager(linearLayoutManager);
    rvEventList.setAdapter(new WatchEventListAdapter(this, event_modelArrayList));

    rvEventList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
      @Override public void onLoadMore(int page, int totalItemsCount) {
        if (shouldLoad) {
          loadUserEvents();
          PopMessage.makesimplesnack(mLayout, "Loading more event..");
        } else {
          PopMessage.makesimplesnack(mLayout, "No more events to load..");
        }
      }
    });
    loadUserEvents();
  }

  public Broadcastresults register_reviever() {
    mReceiver = new Broadcastresults(new Handler());

    mReceiver.setReceiver(this);

    return mReceiver;
  }

  private void likeEvent() {
    mReceiver = register_reviever();
    AppPrefs appPrefs = new AppPrefs(this);
    if (likevideo.equals("No")) {

      img_like.setImageResource(R.drawable.unlike);
      mtxt_directive.setText("Unlike");
      likevideo = "Yes";
      Intent intent = new Intent(this, IntentServiceOperations.class);
      intent.putExtra("operation", Utility.operation_like);
      intent.putExtra("user_id", appPrefs.getString(Utility.user_id));
      intent.putExtra("event_id", event_id);
      intent.putExtra("receiver", mReceiver);
      startService(intent);
    } else {

      img_like.setImageResource(R.drawable.like);
      mtxt_directive.setText("Like");
      likevideo = "No";
      Intent intent = new Intent(this, IntentServiceOperations.class);
      intent.putExtra("operation", Utility.operation_unlike);
      intent.putExtra("user_id", appPrefs.getString(Utility.user_id));
      intent.putExtra("event_id", event_id);
      intent.putExtra("receiver", mReceiver);
      startService(intent);
    }
  }

  private void commentOnEvent() {
    AppPrefs appPrefs = new AppPrefs(this);
    ((WatchEventListAdapter) rvEventList.getAdapter()).showCommentDialog(this, event_id,
        appPrefs.getString(Utility.user_id));
  }

  private void shareEvent() {
    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out this event at TagFrame:" + shareLink);
    sendIntent.setType("text/plain");
    startActivity(sendIntent);
  }

  private void show_tagged_user() {
    exVpControls.stop();

    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_tagged_users);

    TextView txt_message = (TextView) dialog.findViewById(R.id.txt_no_message_tagged_user);

    RecyclerView list = (RecyclerView) dialog.findViewById(R.id.list_tagged_users);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    list.setLayoutManager(layoutManager);

    if (list_tagged_user.size() > 0) {
      txt_message.setVisibility(View.GONE);
    }
    list.setAdapter(new TaggedUserAdapter(list_tagged_user, this));

    TextView txt = (TextView) dialog.findViewById(R.id.txt_tag_add);
    txt.setVisibility(View.GONE);

    dialog.findViewById(R.id.txt_tag_done).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.cancel();
      }
    });

    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override public void onCancel(DialogInterface dialog) {
        exVpControls.play();
      }
    });

    dialog.show();
  }

  public void loadUserEvents() {
    if (Networkstate.haveNetworkConnection(this)) {
      mPbar_events.setVisibility(View.VISIBLE);
      ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
      retrofitService.getUserEvents(user_id, String.valueOf(next_records))
          .enqueue(new Callback<ListResponseModel>() {
            @Override public void onResponse(Call<ListResponseModel> call,
                Response<ListResponseModel> response) {

              mPbar_events.setVisibility(View.GONE);

              try {
                if (response.body().getStatus().equals("success")) {

                  event_modelArrayList.addAll(response.body().getTagStreamArrayList());
                  rvEventList.getAdapter().notifyDataSetChanged();
                  if (rvEventList.getAdapter().getItemCount() > currentListPosition) {
                    new Handler().postDelayed(new Runnable() {
                      @Override public void run() {

                        rvEventList.smoothScrollToPosition(currentListPosition);
                      }
                    }, 1000);
                  }
                  if (response.body().getTagStreamArrayList().size() == Utility.PAGE_SIZE) {
                    next_records = next_records + Utility.PAGE_SIZE;

                    shouldLoad = true;
                  } else {

                    shouldLoad = false;
                  }
                } else {

                }
              } catch (Exception e) {
                PopMessage.makesimplesnack(mLayout, "Error, Please try after some time...");
              }
            }

            @Override public void onFailure(Call<ListResponseModel> call, Throwable t) {
            }
          });
    } else {
      PopMessage.makesimplesnack(mLayout, "No Internet Connection");
    }
  }

  @Override public void onReceiveResult(int resultCode, Bundle resultData) {
    int operation = resultData.getInt("operation");
    switch (operation) {

      case Utility.operation_unlike:

        if (resultCode == 1) {

          PopMessage.makesimplesnack(mLayout, "Event unliked");
          likes--;
          tvStats.setText(likes
              + " Likes"
              + ", "
              + framedata_map.size()
              + " Frames"
              + " and "
              + comments
              + " Comments");
        } else {

          PopMessage.makesimplesnack(mLayout, "Error unliking event,reverting changes");
          img_like.setImageResource(R.drawable.unlike);
          mtxt_directive.setText("Unlike");
        }

        break;

      case Utility.operation_like:

        if (resultCode == 1) {

          PopMessage.makesimplesnack(mLayout, "Event Liked");
          likes++;
          tvStats.setText(likes
              + " Likes"
              + ", "
              + framedata_map.size()
              + " Frames"
              + " and "
              + comments
              + " Comments");
        } else {

          PopMessage.makesimplesnack(mLayout, "Error liking event,reverting changes");
          img_like.setImageResource(R.drawable.unlike);
          mtxt_directive.setText("Unlike");
        }

        break;

      case Utility.operation_comment:

        if (resultCode == 1) {

          PopMessage.makesimplesnack(mLayout, "Comment Successful");
          comments++;
          tvStats.setText(likes
              + " Likes"
              + ", "
              + framedata_map.size()
              + " Frames"
              + " and "
              + comments
              + " Comments");
        } else {

          PopMessage.makesimplesnack(mLayout, "Error commenting..");
        }
    }
  }

  public void show_synced_frame(final int pos) {

    exVpControls.stop();
    final FrameList_Model frameList_model = framedata_map.get(pos);

    if (frameList_model.getFrametype() == Utility.frametype_image) {

      final Dialog dialog = new Dialog(this);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      Window window = dialog.getWindow();
      window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      window.setGravity(Gravity.CENTER);
      dialog.setCancelable(false);
      dialog.setContentView(R.layout.dialog_frame_to_show);

      // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
      final ImageView frameimage = (ImageView) dialog.findViewById(R.id.framelist_image);
      ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);
      ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);

      TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
      final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);

      TextView tvaddproduct = (TextView) dialog.findViewById(R.id.add_product);
      tvaddproduct.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id()
              .equals("0")) {
            Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse(frameList_model.getProduct_url()));
            startActivity(browserIntent);
            dialog.dismiss();
          }
        }
      });

      frameimage.setVisibility(View.VISIBLE);
      //if there is product
      if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id()
          .equals("0")) {
        //checking if it is a product frame
        if (!frameList_model.isAProductFrame()) {
          product_image.setVisibility(View.VISIBLE);
          Picasso.with(WatchEventActivity.this)
              .load(frameList_model.getProduct_path())
              .into(product_image);
        }
        tvaddproduct.setText("Buy Product");
      } else {
        tvaddproduct.setVisibility(View.GONE);
      }

      if (frameList_model.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
        Picasso.with(WatchEventActivity.this)
            .load(frameList_model.getFrame_image_url())
            .into(frameimage);
      } else {
        try {
          frameimage.setImageBitmap(BitmapHelper.decodeFile(WatchEventActivity.this,
              new File(frameList_model.getImagepath())));
        } catch (Exception e) {
          Picasso.with(WatchEventActivity.this)
              .load(frameList_model.getFrame_image_url())
              .into(frameimage);
        }
      }

      tittle.setText(frameList_model.getName());
      duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime())
          + "-"
          + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

      delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          exVpControls.play();
          dialog.dismiss();
        }
      });
      img_frame_to_show.setVisibility(View.GONE);
      img_play_video.setVisibility(View.GONE);
      dialog.show();
    } else {
      dimScreen();
      final Dialog dialog = new Dialog(this);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setCancelable(false);
      dialog.setContentView(R.layout.dialog_show_video_frame);

      WindowManager.LayoutParams a = dialog.getWindow().getAttributes();
      a.dimAmount = 0;
      dialog.getWindow().setAttributes(a);

      dialog.getWindow()
          .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);

      final VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
      final RelativeLayout coverLayout = (RelativeLayout) dialog.findViewById(R.id.cover);
      final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.pbar_video_dialog);

      ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);
      ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);

      TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
      final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);

      TextView tvaddproduct = (TextView) dialog.findViewById(R.id.add_product);
      tvaddproduct.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id()
              .equals("0")) {
            Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse(frameList_model.getProduct_url()));
            startActivity(browserIntent);

            dialog.cancel();
          } else {
            dialog.cancel();
          }
        }
      });

      if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id()
          .equals("0")) {

        product_image.setVisibility(View.VISIBLE);
        Picasso.with(WatchEventActivity.this)
            .load(frameList_model.getProduct_path())
            .fit()
            .into(product_image);
        tvaddproduct.setText("Buy Product");
      } else {
        tvaddproduct.setVisibility(View.GONE);
      }
      try {

        framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_image_url()));
        MediaController mediaController = new MediaController(WatchEventActivity.this);
        framevideo.setMediaController(mediaController);
        mediaController.setAnchorView(framevideo);
        framevideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override public void onPrepared(MediaPlayer mp) {
            coverLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
          }
        });
        framevideo.setZOrderOnTop(true);
        framevideo.start();
      } catch (Exception e) {

      }

      tittle.setText(frameList_model.getName());
      duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime())
          + "-"
          + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

      delete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          exVpControls.play();
          dialog.cancel();
        }
      });
      img_frame_to_show.setVisibility(View.GONE);
      img_play_video.setVisibility(View.GONE);

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

      dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override public void onCancel(DialogInterface dialog) {
          brightenScreen();
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
  }

  public void dimScreen() {
    ll_dimer.setVisibility(View.VISIBLE);
  }

  public void brightenScreen() {
    ll_dimer.setVisibility(View.GONE);
  }

  public int getCurrentListPosition() {
    return 0;
  }

  public void pauseMediaPlayer() {
    exVpControls.stop();
  }

  public void playMediaPlayer() {
    exVpControls.play();
  }

  public void playSelected(Intent intent) {
    getIntentData(intent);
    setDatatoView();
    addTrackAndPlay(intent);
  }

  private void addTrackAndPlay(Intent intent) {
    video_urls.clear();
    video_type.clear();
    handler.removeCallbacks(runnable);
    video_urls.add(intent.getStringExtra("data_url"));
    video_type.add(Constants.MEDIA_TYPE_HLS);
    exVpControls.addTrack(intent.getStringExtra("data_url"), Constants.MEDIA_TYPE_HLS);
    exVpControls.setCurrent(video_urls.size() - 1);
    handler.postDelayed(runnable, 100);
    frameLayout.requestFocus();
  }

  @Override protected void onStop() {
    handler.removeCallbacks(runnable);
    super.onStop();
  }
}

