package com.tagframe.tagframe.UI.Acitivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.TagStreamEventAdapter;
import com.tagframe.tagframe.Adapters.TaggedUserAdapter;
import com.tagframe.tagframe.Adapters.WatchEventListAdapter;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.NotificationModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.MyMediaPlayer.IPlayerListener;
import com.tagframe.tagframe.MyMediaPlayer.Player;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.Utils.AnimatingRelativeLayout;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.CustomSeekBar;
import com.tagframe.tagframe.Utils.EndlessRecyclerViewScrollListener;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 10/7/2016.
 */
public class WatchEventActivity extends Activity implements SeekBar.OnSeekBarChangeListener, Broadcastresults.Receiver {
    private SurfaceView surfaceView;
    private TextView tvCurrentTime, tvTotalTime, tvEventTittle, tvStats, tvBuffering, tvUsername;
    private ImageView ivTagUsers, ivAddFrame, ivPlayback, img_frame_to_show, img_play_video, img_done, img_like;
    private CustomSeekBar mSeekBar;
    private ProgressBar mPbar, mPbar_events;
    private RecyclerView rvEventList;
    private RelativeLayout mSurfaceContainer, ll_dimer, mLayout;
    private AnimatingRelativeLayout mTopBar, mBottomBar;
    private TextView mTxt_footer, mtxt_directive;
    private ImageView img_footer;
    LinearLayoutManager linearLayoutManager;
    private Broadcastresults mReceiver;

    private String vidAddress, tittle, event_id, userName, stats, user_id, shareLink, likevideo;
    private int likes, frames, comments;
    private int currentListPosition = 0;
    private ArrayList<FrameList_Model> framedata_map;
    private ArrayList<TaggedUserModel> list_tagged_user;
    private Player mPlayer;
    private MediaPlayer mediaPlayer;
    private int sav_instance_current_duration;
    private Handler mHandler = new Handler();
    private Handler mControlsVisibilityHandler = new Handler();
    private boolean isfromsavedinstance;
    private int next_records = 0;
    private LinearLayout ll_like, ll_share, llcomment;
    private ArrayList<Event_Model> event_modelArrayList = new ArrayList<>();

    private Point p = new Point();
    private RelativeLayout.LayoutParams params;

    private final static String CURRENT_DURATION = "CURRENT_DURATION";
    private final static String TOTAL_DURATION = "TOTAL_DURATION", TAG_TITTLE = "TITTLE";
    private final static String FRAMELIST = "FRAMELIST", TAGGED_USER_LIST = "TAGGED_LIST";

    boolean shouldLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_event);
        getIntentData(getIntent());
        initView(savedInstanceState);
        setDatatoView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.setSource(this, vidAddress);
        mediaPlayer = mPlayer.getMediaPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sav_instance_current_duration = mediaPlayer.getCurrentPosition();
        mPlayer.reset();
    }

    @Override
    protected void onDestroy() {
        mPlayer.release();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
        mReceiver = null;
        super.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mPlayer.resizeSurface(getWindowManager().getDefaultDisplay());
        mHandler.removeCallbacks(mUpdateTimeTask);
        mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putLong(CURRENT_DURATION, mediaPlayer.getCurrentPosition());
        outState.putLong(TOTAL_DURATION, mediaPlayer.getDuration());
        outState.putParcelableArrayList(FRAMELIST, framedata_map);
        outState.putParcelableArrayList(TAGGED_USER_LIST, list_tagged_user);
        outState.putString(TAG_TITTLE, tittle);


        super.onSaveInstanceState(outState);
    }

    //*lifecycle methods

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void getIntentData(Intent intent) {

        vidAddress = intent.getStringExtra("data_url");
        framedata_map = intent.getParcelableArrayListExtra("framelist");
        list_tagged_user = intent.getParcelableArrayListExtra("tagged_user_id");
        tittle = intent.getStringExtra("tittle");
        event_id = intent.getStringExtra("eventid");
        userName = intent.getStringExtra("name");
        user_id = intent.getStringExtra("user_id");
        currentListPosition = intent.getIntExtra("position", 0);
        shareLink = intent.getStringExtra("sharelink");
        likevideo = intent.getStringExtra("likevideo");
        likes = Integer.parseInt(intent.getStringExtra("likes"));
        try {
            frames = Integer.parseInt(intent.getStringExtra("frames"));

        } catch (NumberFormatException e) {
            frames = 0;
        }
        comments = Integer.parseInt(intent.getStringExtra("comments"));
    }

    public void resetMediaPlayer() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
        surfaceView.setVisibility(View.GONE);
        mPlayer.reset();
        mPlayer.release();
        mPlayer = new Player(surfaceView, this);
        mPlayer.setListener(mPlayerListener);
        mPlayer.setSource(this, vidAddress);

    }

    ;

    public void pauseMediaPlayer() {
        mediaPlayer = mPlayer.getMediaPlayer();
        mediaPlayer.pause();
    }

    public void playMediaPlayer() {
        mediaPlayer = mPlayer.getMediaPlayer();
        mediaPlayer.start();
    }

    public int getCurrentListPosition()

    {
        return linearLayoutManager.findLastVisibleItemPosition();
    }


    private void initView(Bundle savedInstanceState) {
        getWindowManager().getDefaultDisplay().getSize(p);
        mSurfaceContainer = (RelativeLayout) findViewById(R.id.rl_surface_container);
        mSurfaceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlsVisibilty();
            }
        });
        ll_dimer = (RelativeLayout) findViewById(R.id.dimer_layout);
        surfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mLayout = (RelativeLayout) findViewById(R.id.mlayout_watchevent);
        tvCurrentTime = (TextView) findViewById(R.id.txttotalduration);
        tvTotalTime = (TextView) findViewById(R.id.txtcurrentduration);
        tvUsername = (TextView) findViewById(R.id.txt_more_from);
        tvEventTittle = (TextView) findViewById(R.id.txt_event_tittle);
        tvStats = (TextView) findViewById(R.id.txt_stats);
        tvBuffering = (TextView) findViewById(R.id.txt_percent);
        ivAddFrame = (ImageView) findViewById(R.id.add_frame);
        ivTagUsers = (ImageView) findViewById(R.id.taguser_event);
        ivTagUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_tagged_user();
            }
        });
        ivAddFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WatchEventActivity.this, MakeNewEvent.class);
                intent.putExtra("name", userName);
                intent.putExtra("stats", stats);
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
        ivPlayback = (ImageView) findViewById(R.id.btn_play_stop);
        img_frame_to_show = (ImageView) findViewById(R.id.img_frame_to_show);
        img_play_video = (ImageView) findViewById(R.id.img_play_video);
        mPbar = (ProgressBar) findViewById(R.id.pbarmediaplayer);
        mPbar_events = (ProgressBar) findViewById(R.id.pbar_events);
        mSeekBar = (CustomSeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(this);
        rvEventList = (RecyclerView) findViewById(R.id.list_event_users);
        linearLayoutManager = new LinearLayoutManager(this);
        rvEventList.setLayoutManager(linearLayoutManager);
        rvEventList.setAdapter(new WatchEventListAdapter(this, event_modelArrayList));
        mTopBar = (AnimatingRelativeLayout) findViewById(R.id.topbar);
        mBottomBar = (AnimatingRelativeLayout) findViewById(R.id.ll_mp_tools);
        mTxt_footer = (TextView) findViewById(R.id.txt_footer);
        mTxt_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserEvents();
            }
        });
        img_footer = (ImageView) findViewById(R.id.img_footer);
        img_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserEvents();
            }
        });
        img_done = (ImageView) findViewById(R.id.donewatching);
        img_like = (ImageView) findViewById(R.id.imglike);
        mtxt_directive = (TextView) findViewById(R.id.txt_like_directive);
        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Setup the player once.
        mPlayer = new Player(surfaceView, this);
        mPlayer.setListener(mPlayerListener);
        mediaPlayer = mPlayer.getMediaPlayer();

        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 0;
        img_frame_to_show.setLayoutParams(params);
        img_play_video.setLayoutParams(params);

        ll_like = (LinearLayout) findViewById(R.id.lllike);
        ll_share = (LinearLayout) findViewById(R.id.llshare);
        llcomment = (LinearLayout) findViewById(R.id.llcomment);
        ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeEvent();
            }
        });
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEvent();
            }
        });
        llcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentOnEvent();
            }
        });

        img_frame_to_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = img_frame_to_show.getTag().toString();
                Integer pos = Integer.parseInt(TAG);

                show_synced_frame(pos);
            }
        });

        ivPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ivPlayback.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mediaPlayer.start();
                    ivPlayback.setImageResource(android.R.drawable.ic_media_pause);

                }
            }
        });
        rvEventList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
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
        ((WatchEventListAdapter) rvEventList.getAdapter()).showCommentDialog(this, event_id, appPrefs.getString(Utility.user_id));
    }

    private void shareEvent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this event at TagFrame:" + shareLink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void setDatatoView() {
        try {
            tvEventTittle.setText(tittle);
            tvStats.setText(likes + " Likes" + ", " + frames + " Frames" + " and " + comments + " Comments");
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

    private IPlayerListener mPlayerListener = new IPlayerListener() {
        @Override
        public void onError(String message) {

            mHandler.removeCallbacks(mUpdateTimeTask);
            mPbar.setVisibility(View.GONE);
            tvBuffering.setVisibility(View.VISIBLE);
            tvBuffering.setText("An Error Occurred");
        }

        @Override
        public void onBufferingStarted() {

            tvBuffering.setVisibility(View.VISIBLE);
            mPbar.setVisibility(View.VISIBLE);
            tvBuffering.setText("Buffering...");
        }

        @Override
        public void onBufferingFinished() {
            tvBuffering.setVisibility(View.GONE);
            mPbar.setVisibility(View.GONE);

            if (isfromsavedinstance) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                //call this when mediaplayer is prepared or after the buffer has been finished
                mediaPlayer.seekTo((int) sav_instance_current_duration);
            }
            if (Utility.isResumeFromActivityResult) {
                mediaPlayer.pause();
                Utility.isResumeFromActivityResult = false;
            }
            mediaPlayer = mPlayer.getMediaPlayer();
            setControlsVisibilty();
            updateProgressBar();
        }

        @Override
        public void onRendereingstarted() {

        }

        @Override
        public void onCompletion() {
            mTopBar.show();
            mBottomBar.show();

        }
    };

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {


            try {
                mSurfaceContainer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, surfaceView.getHeight()));
                mediaPlayer = mPlayer.getMediaPlayer();
            } catch (NullPointerException e) {

            }
            long totalDurationn = 0;
            long currentDuration = 0;
            try {
                totalDurationn = mediaPlayer.getDuration();
                currentDuration = mediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                totalDurationn = 0;
                currentDuration = 0;
            }
            // Displaying Total Duration time
            tvCurrentTime.setText("" + Utility.milliSecondsToTimer(totalDurationn));
            // Displaying time completed playing
            tvTotalTime.setText("" + Utility.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDurationn));
            mSeekBar.setProgress(progress);


            // Set the "topMargin" to the value you decided upon before


            // Set LayoutParams

            img_frame_to_show.setVisibility(View.INVISIBLE);
            img_play_video.setVisibility(View.INVISIBLE);
            for (int i = 0; i < framedata_map.size(); i++) {
                FrameList_Model fm = framedata_map.get(i);
                if (fm.getStarttime() - 100 <= currentDuration && currentDuration <= fm.getEndtime() + 100 && fm.getEndtime() != 0) {
                    img_frame_to_show.setVisibility(View.VISIBLE);
                    if (!mTopBar.isVisible()) {
                        mBottomBar.show();
                        mTopBar.show();

                    }
                    if (fm.getFrametype() == Utility.frametype_image) {
                        Picasso.with(WatchEventActivity.this).load(fm.getImagepath()).resize(200, 200).into(img_frame_to_show);

                    } else {
                        img_play_video.setVisibility(View.VISIBLE);
                        if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fm.getImagepath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND);

                            thumb = Utility.getResizedBitmap(thumb, 200, 200);
                            img_frame_to_show.setImageBitmap(thumb);
                        } else {
                            Picasso.with(WatchEventActivity.this).load(fm.getImagepath()).resize(200, 200).into(img_frame_to_show);
                        }
                    }

                    img_frame_to_show.setTag(i + "");
                    int prog = (int) (Utility.getProgressPercentage(fm.getEndtime(), totalDurationn));
                    if (prog > 80) {
                        prog = 80;
                    }
                    show_frame_on_seekbar(prog - (int) (Utility.getProgressPercentage(2000, totalDurationn)));

                }
            }

            if (totalDurationn != 0)
                Utility.updateseekbar(mSeekBar, mediaPlayer.getDuration(), WatchEventActivity.this, framedata_map);


            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public void show_frame_on_seekbar(final int progress) {

        int measure = (int) ((((float) progress * p.x) / 100) - (progress));

        // When "measure" will become equal to "p.x"(at progress = 100),
        // the image will be outside the view when we set its "leftMargin".
        // But, the image will start disappearing before that.
        // When this situation comes, set the "leftMargin" to a maximum value
        // which is the screen width - ImageView' width
        if (p.x - measure < img_frame_to_show.getWidth()) {

            params.leftMargin = p.x - img_frame_to_show.getWidth();
        } else {
            params.leftMargin = measure;
        }
        img_frame_to_show.setLayoutParams(params);
        img_play_video.setLayoutParams(params);
    }

    public void show_synced_frame(final int pos) {

        mediaPlayer.pause();
        final FrameList_Model frameList_model = framedata_map.get(pos);

        if (frameList_model.getFrametype() == Utility.frametype_image) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
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
                @Override
                public void onClick(View v) {
                    if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id().equals("0")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(frameList_model.getProduct_url()));
                        startActivity(browserIntent);
                        dialog.dismiss();
                    }
                }
            });


            frameimage.setVisibility(View.VISIBLE);
            //if there is product
            if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id().equals("0")) {
                //checking if it is a product frame
                if (!frameList_model.isAProductFrame()) {
                    product_image.setVisibility(View.VISIBLE);
                    Picasso.with(WatchEventActivity.this).load(frameList_model.getProduct_path()).into(product_image);

                }
                tvaddproduct.setText("Buy Product");
            } else {
                tvaddproduct.setVisibility(View.GONE);
            }

            if (frameList_model.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
                Picasso.with(WatchEventActivity.this).load(frameList_model.getFrame_image_url()).into(frameimage);
            } else {
                try {
                    frameimage.setImageBitmap(BitmapHelper.decodeFile(WatchEventActivity.this, new File(frameList_model.getImagepath())));
                } catch (Exception e) {
                    Picasso.with(WatchEventActivity.this).load(frameList_model.getFrame_image_url()).into(frameimage);
                }
            }

            tittle.setText(frameList_model.getName());
            duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.start();
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


            final VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);

            ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);
            ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);

            TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
            final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);

            TextView tvaddproduct = (TextView) dialog.findViewById(R.id.add_product);
            tvaddproduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id().equals("0")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(frameList_model.getProduct_url()));
                        startActivity(browserIntent);

                        dialog.cancel();
                    } else {
                        dialog.cancel();
                    }
                }
            });


            if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id().equals("0")) {

                product_image.setVisibility(View.VISIBLE);
                Picasso.with(WatchEventActivity.this).load(frameList_model.getProduct_path()).fit().into(product_image);
                tvaddproduct.setText("Buy Product");

            } else {
                tvaddproduct.setVisibility(View.GONE);
            }
            try {

                framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_data_url()));
                MediaController mediaController = new MediaController(WatchEventActivity.this);
                framevideo.setMediaController(mediaController);
                mediaController.setAnchorView(framevideo);
                framevideo.start();
            } catch (Exception e) {

            }


            tittle.setText(frameList_model.getName());
            duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                @Override
                public void run() {
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
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    myHandler.removeCallbacks(mUpdateDialogVideo);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    myHandler.removeCallbacks(mUpdateDialogVideo);
                    int total = framevideo.getDuration();
                    int current = Utility.progressToTimer(seekBar.getProgress(), total);
                    framevideo.seekTo(current);
                    myHandler.postDelayed(mUpdateDialogVideo, 100);
                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    brightenScreen();
                    myHandler.removeCallbacks(mUpdateDialogVideo);

                }
            });

            play_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


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

    public void setControlsVisibilty() {
        mControlsVisibilityHandler.postDelayed(setVisibiltyTask, 4000);
    }

    private Runnable setVisibiltyTask = new Runnable() {
        @Override
        public void run() {
            if (mTopBar.isVisible()) {
                mTopBar.hide();
                mBottomBar.hide();
            }
            mControlsVisibilityHandler.postDelayed(setVisibiltyTask, 4000);
        }
    };

    private void toggleControlsVisibilty() {
        if (mTopBar.isVisible()) {
            mTopBar.hide();
            mBottomBar.hide();
        } else {
            mTopBar.show();
            mBottomBar.show();
        }
    }

    public void loadUserEvents() {
        if (Networkstate.haveNetworkConnection(this)) {
            mPbar_events.setVisibility(View.VISIBLE);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.getUserEvents(user_id, String.valueOf(next_records)).enqueue(new Callback<ListResponseModel>() {
                @Override
                public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {


                    mPbar_events.setVisibility(View.GONE);

                    try {
                        if (response.body().getStatus().equals("success")) {

                            event_modelArrayList.addAll(response.body().getTagStreamArrayList());
                            rvEventList.getAdapter().notifyDataSetChanged();
                            if (rvEventList.getAdapter().getItemCount() > currentListPosition) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        rvEventList.smoothScrollToPosition(currentListPosition);
                                    }
                                }, 1000);
                            }
                            if (response.body().getTagStreamArrayList().size() == Utility.PAGE_SIZE) {
                                next_records = next_records + Utility.PAGE_SIZE;

                                shouldLoad = true;

                            } else {
                                mTxt_footer.setOnClickListener(null);
                                shouldLoad = false;
                            }

                        } else {

                        }
                    } catch (Exception e) {
                        PopMessage.makesimplesnack(mLayout, "Error, Please try after some time...");
                    }

                }

                @Override
                public void onFailure(Call<ListResponseModel> call, Throwable t) {
                }
            });

        } else {
            PopMessage.makesimplesnack(mLayout, "No Internet Connection");
        }
    }

    private void show_tagged_user() {
        mediaPlayer.pause();

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
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mediaPlayer.start();
            }
        });


        dialog.show();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = Utility.progressToTimer(seekBar.getProgress(), totalDuration);


        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        int operation = resultData.getInt("operation");
        switch (operation) {

            case Utility.operation_unlike:

                if (resultCode == 1) {

                    PopMessage.makesimplesnack(mLayout, "Event unliked");
                    likes--;
                    tvStats.setText(likes + " Likes" + ", " + frames + " Frames" + " and " + comments + " Comments");

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
                    tvStats.setText(likes + " Likes" + ", " + frames + " Frames" + " and " + comments + " Comments");
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
                    tvStats.setText(likes + " Likes" + ", " + frames + " Frames" + " and " + comments + " Comments");
                } else {

                    PopMessage.makesimplesnack(mLayout, "Error commenting..");
                }


        }

    }

    public Broadcastresults register_reviever() {
        mReceiver = new Broadcastresults(new Handler());

        mReceiver.setReceiver(this);

        return mReceiver;

    }


}
