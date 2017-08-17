package com.tagframe.tagframe.UI.Acitivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.FrameListAdapter;
import com.tagframe.tagframe.Adapters.FrameListRecyclerAdapter;
import com.tagframe.tagframe.Adapters.PostEventListAdapter;
import com.tagframe.tagframe.Adapters.TaggedUserAdapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Models.SingleEventModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.MyMediaPlayer.IPlayerListener;
import com.tagframe.tagframe.MyMediaPlayer.Player;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.Utils.AnimatingRelativeLayout;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.CustomSeekBar;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.HorizontalListView;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.ProcessnaImages;
import com.tagframe.tagframe.Utils.SeekBarBackgroundDrawable;
import com.tagframe.tagframe.Utils.SeekBarProgressDrawable;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Validation;

import com.tagframe.tagframe.Utils.listsort;
import com.veer.multiselect.MultiSelectActivity;
import com.veer.multiselect.Util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 7/13/2016.
 */
public class MakeNewEvent extends Activity
    implements SeekBar.OnSeekBarChangeListener, Broadcastresults.Receiver {

  //Widgets
  private Player mPlayer;
  private MediaPlayer mediaPlayer;

  private SurfaceHolder vidHolder;
  private SurfaceView vidSurface;
  private ProgressBar pbar_mediaplayer;
  private ImageButton imageButton_play, imageButton_screen_resolution;
  private CustomSeekBar seekbar;
  private TextView label_seekbar_currentduration, label_seekbar_totalduration, label_tittle,
      label_description, txt_tutorial_msg;
  private TextView txt_percent;
  private Button btn_tut_got_it;
  private LinearLayout btn_add_frame, ll_time_container;
  private RelativeLayout ll_seekbar_frame_container, ll_tutorial, ll_dimer, mlayout,
      mSurfacecontainer;
  private AnimatingRelativeLayout ll_top_bar, ll_bottom_bar, ll_container_frames;
  private RelativeLayout.LayoutParams params;
  private Point p = new Point();

  private AppPrefs appPrefs;
  private RecyclerView framelist;
  private ImageView ll_title_edit, img_frame_to_show, img_play_video, post_event, save_event,
      ll_add_frame, txt_tagged_user;

  //Utility
  private final static String TAG = MakeNewEvent.class.getSimpleName();
  private final static String CURRENT_DURATION = "CURRENT_DURATION";
  private final static String TOTAL_DURATION = "TOTAL_DURATION", TAG_TITTLE = "TITTLE";
  private final static String FRAMELIST = "FRAMELIST", TAGGED_USER_LIST = "TAGGED_LIST";
  public static int Flag_select_video = 4;
  public static int Flag_pick_photo = 5;
  //public static int Flag_select_photo = 6;
  public static int Flag_product_list_result = 6;
  public static int Flag_Get_Tagged_User = 7;

  // Handler to update UI timer, progress bar etc,.
  private Handler mHandler = new Handler();

  private String vidAddress = "";
  public static String user_id = "0";
  private String tittle, description, type, event_id;
  private long totalduration;
  private String s_current_duration;

  private Broadcastresults mReceiver;
  private AppPrefs user_data;
  private Calendar cal;
  private int count = 0, event_type, counter_tut = 0, limit = 0;
  private Uri selectedImageUri;
  public static boolean delete_event = false;
  Uri uriImage;

  //this provides information for configration change
  //set this to true while retriving the data from the savedinstancestate
  //then check on bufferingfinished listener if flag is set
  //perform the mediaplayer seek to operation
  private boolean isfromsavedinstance = false, isControlVisible = true;
  private boolean isFromPause = false;
  private long sav_instance_current_duration = 0;

  private ArrayList<FrameList_Model> framedata_map;
  private ArrayList<TaggedUserModel> list_taggerd_user;
  private boolean isTutvisible = false;
  private int FLAG_ADD_PRODUCT = 910;
  public static int RESYC_FRAME_POSITION;
  private int mTimecontainerwidth;
  private Dialog mtutDialog;
  private boolean isLayoutSet = false;

  private static String[] PERMISSIONS = {
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.CAMERA
  };
  private View view;

  //lifecycles methods
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_make_new_event);
    appPrefs = new AppPrefs(this);
    //getting the intents
    vidAddress = getIntent().getStringExtra("data_url");

    event_type = getIntent().getIntExtra("eventtype", 0);

    if (event_type == Utility.eventtype_saved) {
      framedata_map = getIntent().getParcelableArrayListExtra("framelist");
      list_taggerd_user = getIntent().getParcelableArrayListExtra("tagged_user_id");
      tittle = "Title:" + getIntent().getStringExtra("tittle");
      description = "Description:" + getIntent().getStringExtra("des");
      user_id = appPrefs.getString(Utility.user_id);
    } else if (event_type == Utility.eventtype_local) {
      framedata_map = new ArrayList<>();
      tittle = "";
      description = "Description:";
      list_taggerd_user = new ArrayList<>();
      user_id = appPrefs.getString(Utility.user_id);
    } else if (event_type == Utility.eventtype_internet) {
      framedata_map = getIntent().getParcelableArrayListExtra("framelist");
      list_taggerd_user = getIntent().getParcelableArrayListExtra("tagged_user_id");
      tittle = "Title:" + getIntent().getStringExtra("tittle");
      description = "Description:" + getIntent().getStringExtra("des");
      event_id = getIntent().getStringExtra("eventid");
      user_id = getIntent().getStringExtra("user_id");
    }

    user_data = new AppPrefs(this);
    cal = Calendar.getInstance();

    //initialize views and mediaplayer
    init(savedInstanceState);
  }

  @Override protected void onResume() {
    super.onResume();
    mPlayer.setSource(this, vidAddress);
    mediaPlayer = mPlayer.getMediaPlayer();
  }

  @Override protected void onPause() {
    super.onPause();
    isFromPause = true;
    sav_instance_current_duration = mediaPlayer.getCurrentPosition();
    mPlayer.reset();
  }

  @Override protected void onDestroy() {
    mHandler.removeCallbacks(mUpdateTimeTask);
    mPlayer.release();
    super.onDestroy();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    mPlayer.resizeSurface(getWindowManager().getDefaultDisplay());
    mHandler.removeCallbacks(mUpdateTimeTask);

    super.onConfigurationChanged(newConfig);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {

    outState.putLong(CURRENT_DURATION, mediaPlayer.getCurrentPosition());
    outState.putLong(TOTAL_DURATION, mediaPlayer.getDuration());
    outState.putParcelableArrayList(FRAMELIST, framedata_map);
    outState.putParcelableArrayList(TAGGED_USER_LIST, list_taggerd_user);
    outState.putString(TAG_TITTLE, tittle);
    Utility.SCREEEN_ORIENTATION_INTERNET = Utility.getScreenOrientation(this);

    super.onSaveInstanceState(outState);
  }

  //*lifecycle methods

  @Override protected void onStart() {
    super.onStart();
    mSurfacecontainer = (RelativeLayout) findViewById(R.id.rl_event);
  }

  //utility methods
  private IPlayerListener mPlayerListener = new IPlayerListener() {
    @Override public void onError(String message) {

      mHandler.removeCallbacks(mUpdateTimeTask);
      pbar_mediaplayer.setVisibility(View.GONE);
      txt_percent.setVisibility(View.VISIBLE);
    }

    @Override public void onBufferingStarted() {

      txt_percent.setVisibility(View.VISIBLE);
      pbar_mediaplayer.setVisibility(View.VISIBLE);
      txt_percent.setText("Buffering...");
      seekbar.setVisibility(View.GONE);
    }

    @Override public void onBufferingFinished() {
      txt_percent.setVisibility(View.GONE);
      pbar_mediaplayer.setVisibility(View.GONE);
      seekbar.setVisibility(View.VISIBLE);
      //controlvisible

      if (isfromsavedinstance || isFromPause) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        isFromPause = false;
        //call this when mediaplayer is prepared or after the buffer has been finished
        mediaPlayer.seekTo((int) sav_instance_current_duration);
      }
      if (Utility.isResumeFromActivityResult) {
        mediaPlayer.pause();
        Utility.isResumeFromActivityResult = false;
      }
      updateProgressBar();
    }

    @Override public void onRendereingstarted() {

    }

    @Override public void onCompletion() {
      img_frame_to_show.setVisibility(View.GONE);
      isControlVisible = true;
      ll_bottom_bar.show();
      ll_top_bar.show();
    }
  };

  //This method initialiazes all the views
  private void init(Bundle savedstate) {

    //LAYOUTS
    ll_dimer = (RelativeLayout) findViewById(R.id.dimer_layout);
    ll_top_bar = (AnimatingRelativeLayout) findViewById(R.id.topbar);
    ll_bottom_bar = (AnimatingRelativeLayout) findViewById(R.id.ll_mp_tools);
    mlayout = (RelativeLayout) findViewById(R.id.mlayout_makenew_event);

    //VIDEO SURFUCE
    vidSurface = (SurfaceView) findViewById(R.id.surfaceviewnewevent);
    txt_percent = (TextView) findViewById(R.id.txt_percent);
    pbar_mediaplayer = (ProgressBar) findViewById(R.id.pbarmediaplayer);

    // Setup the player once.
    mPlayer = new Player(vidSurface, this);
    mPlayer.setListener(mPlayerListener);
    mediaPlayer = mPlayer.getMediaPlayer();

    //setupcontrols

    imageButton_play = (ImageButton) findViewById(R.id.btn_play_stop);
    label_seekbar_currentduration = (TextView) findViewById(R.id.txtcurrentduration);
    label_seekbar_totalduration = (TextView) findViewById(R.id.txttotalduration);
    label_tittle = (TextView) findViewById(R.id.txt_event_tittle);
    label_description = (TextView) findViewById(R.id.event_description);
    post_event = (ImageView) findViewById(R.id.postevent);
    txt_tagged_user = (ImageView) findViewById(R.id.taguser_event);
    label_tittle.setText(tittle);
    label_description.setText(description);
    ll_title_edit = (ImageView) findViewById(R.id.editTittle);
    ll_time_container = (LinearLayout) findViewById(R.id.ll_time);

    ll_title_edit.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        editevent_tittle();
      }
    });
    seekbar = (CustomSeekBar) findViewById(R.id.seekbar);
    seekbar.setOnSeekBarChangeListener(this);
    img_frame_to_show = (ImageView) findViewById(R.id.img_frame_to_show);
    img_play_video = (ImageView) findViewById(R.id.img_play_video);
    //setting the layout parameters of image
    ll_seekbar_frame_container = (RelativeLayout) findViewById(R.id.layout_frame_at_time_container);
    params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    params.leftMargin = 0;
    img_frame_to_show.setLayoutParams(params);
    img_play_video.setLayoutParams(params);
    getWindowManager().getDefaultDisplay().getSize(p);

    //setting the event on play/stop button

    imageButton_play.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mediaPlayer.isPlaying()) {
          mediaPlayer.pause();
          imageButton_play.setImageResource(android.R.drawable.ic_media_play);
        } else {
          mediaPlayer.start();
          imageButton_play.setImageResource(android.R.drawable.ic_media_pause);
        }
      }
    });

    ll_add_frame = (ImageView) findViewById(R.id.add_frame);

    ll_container_frames = (AnimatingRelativeLayout) findViewById(R.id.container_frames);

    ll_add_frame.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (framedata_map.size() == 5) {
          PopMessage.makesimplesnack(mlayout, "You can add only 5 frame to an event");
        } else {
          view = ll_add_frame;
          if (checkPermissions(PERMISSIONS, 2909)) {
            generate_pop_up_add_frame(ll_add_frame);
          }
          ;
        }
      }
    });

    framelist = (RecyclerView) findViewById(R.id.framelist);
       /* if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            framelist.setLayoutManager(linearLayoutManager);

        } else if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getGridColumns());
            framelist.setLayoutManager(gridLayoutManager);
        }*/
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    framelist.setLayoutManager(linearLayoutManager);
    framelist.setItemAnimator(new DefaultItemAnimator());

    framelist.setAdapter(new FrameListRecyclerAdapter(this, framedata_map));

    post_event.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (event_type == Utility.eventtype_internet) {

          method_post_internet_event();
        } else {
          method_post_event();
        }
      }
    });

    save_event = (ImageView) findViewById(R.id.saveevent);

    save_event.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (event_type == Utility.eventtype_internet) {

          if (mediaPlayer != null) {
            mediaPlayer.stop();
          }
          Intent intent = new Intent(MakeNewEvent.this, Modules.class);
          if (getIntent().getStringExtra("from").equals("profile")) {
            intent.putExtra("name", "Edit Profile");
          }

          startActivity(intent);

          finish();
        } else {
          method_save_event();
        }
      }
    });

    //tutorial screen

    ll_tutorial = (RelativeLayout) findViewById(R.id.layout_tutorials);

    txt_tutorial_msg = (TextView) findViewById(R.id.txt_tutorial_msg);

    btn_tut_got_it = (Button) findViewById(R.id.txt_tutorial_button);
    btn_tut_got_it.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        close_tut();
      }
    });

    //frame_to_show on click listener

    img_frame_to_show.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String TAG = img_frame_to_show.getTag().toString();
        Integer pos = Integer.parseInt(TAG);

        show_synced_frame(pos);
      }
    });

    if (event_type == Utility.eventtype_saved) {
      if (framedata_map.size() > 0) {
        ll_container_frames.setVisibility(View.VISIBLE);
      }
    } else if (event_type == Utility.eventtype_internet) {
      save_event.setImageResource(R.drawable.ic_done_white_24dp);
      ll_container_frames.setVisibility(View.GONE);
      if (!appPrefs.getUser().getUser_id().equals(MakeNewEvent.user_id)) {
        ll_title_edit.setVisibility(View.GONE);
        txt_tagged_user.setVisibility(View.GONE);
      }
    }

    //tagged user functionality
    txt_tagged_user.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        show_tagged_user();
      }
    });

    if (savedstate != null) {

      //resotring the mediaplayers state
      framedata_map = savedstate.getParcelableArrayList(FRAMELIST);
      list_taggerd_user = savedstate.getParcelableArrayList(TAGGED_USER_LIST);

      tittle = savedstate.getString(TAG_TITTLE);
      label_tittle.setText(tittle);
      sav_instance_current_duration = savedstate.getLong(CURRENT_DURATION);
      long total = savedstate.getLong(TOTAL_DURATION);

      ll_container_frames.setVisibility(View.VISIBLE);
      framelist.setAdapter(new FrameListRecyclerAdapter(this, framedata_map));

      label_seekbar_currentduration.setText(
          Utility.milliSecondsToTimer(sav_instance_current_duration));
      label_seekbar_totalduration.setText(Utility.milliSecondsToTimer(total));

      isfromsavedinstance = true;
    }
  }

  private int getGridColumns() {
    DisplayMetrics displaymetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    int height = displaymetrics.heightPixels;
    float dpWidth = displaymetrics.widthPixels / displaymetrics.density;
    return 3;
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

    if (list_taggerd_user.size() > 0) {
      txt_message.setVisibility(View.GONE);
    }
    list.setAdapter(new TaggedUserAdapter(list_taggerd_user, this));

    dialog.findViewById(R.id.txt_tag_add).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(MakeNewEvent.this, SearchUserActivity.class);
        intent.putExtra("operation", Utility.operation_onclicked_tagged_user);
        startActivityForResult(intent, Flag_Get_Tagged_User);
        dialog.dismiss();
      }
    });

    dialog.findViewById(R.id.txt_tag_done).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.cancel();
      }
    });

    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override public void onCancel(DialogInterface dialog) {
        mediaPlayer.start();
      }
    });

    dialog.show();
  }

  private void editevent_tittle() {
    mediaPlayer.pause();
    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_edit_event_tittle);

    // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);

    final TextView desxription = (TextView) dialog.findViewById(R.id.dia_framedescription);
    final TextView txt_tittle = (TextView) dialog.findViewById(R.id.dia_frametittle);

    txt_tittle.setText(tittle.replace("Title:", ""));
    desxription.setText(label_description.getText().toString().replace("Description:", ""));

    dialog.findViewById(R.id.dia_rem_yesbtn).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        if (txt_tittle.getText().toString().isEmpty() && desxription.getText()
            .toString()
            .isEmpty()) {
          MyToast.popmessage("Please add tittle or description", MakeNewEvent.this);
        } else {
          label_description.setText("Description:" + desxription.getText().toString());
          label_tittle.setText("Title:" + txt_tittle.getText().toString());
          tittle = "Title:" + txt_tittle.getText().toString();
          mediaPlayer.start();
          dialog.dismiss();
        }
      }
    });

    dialog.findViewById(R.id.dia_rem_nobtn).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mediaPlayer.start();
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  public void show_synced_frame(final int pos) {

    mediaPlayer.pause();
    if (Utility.getScreenOrientation(this) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
      ll_top_bar.setVisibility(View.GONE);
      ll_bottom_bar.setVisibility(View.GONE);
      ll_seekbar_frame_container.setVisibility(View.GONE);
    }
    final FrameList_Model frameList_model = framedata_map.get(pos);

    if (frameList_model.getFrametype() == Utility.frametype_image) {

      final Dialog dialog = new Dialog(this);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      Window window = dialog.getWindow();
      window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      window.setGravity(Gravity.CENTER);
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

            ll_top_bar.setVisibility(View.VISIBLE);
            ll_bottom_bar.setVisibility(View.VISIBLE);
            ll_seekbar_frame_container.setVisibility(View.VISIBLE);

            dialog.cancel();
          } else {
            addproduct(pos);
            ll_top_bar.setVisibility(View.VISIBLE);
            ll_bottom_bar.setVisibility(View.VISIBLE);
            ll_seekbar_frame_container.setVisibility(View.VISIBLE);
            dialog.cancel();
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
          Picasso.with(MakeNewEvent.this)
              .load(frameList_model.getProduct_path())
              .into(product_image);
        }
        tvaddproduct.setText("Buy Product");
      }
      if (frameList_model.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
        Picasso.with(MakeNewEvent.this).load(frameList_model.getFrame_image_url()).into(frameimage);
      } else {
        try {
          frameimage.setImageBitmap(
              BitmapHelper.decodeFile(MakeNewEvent.this, new File(frameList_model.getImagepath())));
        } catch (Exception e) {
          Picasso.with(MakeNewEvent.this)
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

          dialog.cancel();
        }
      });
      img_frame_to_show.setVisibility(View.GONE);
      img_play_video.setVisibility(View.GONE);
      dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override public void onCancel(DialogInterface dialog) {
          framedata_map.get(pos).setName(tittle.getText().toString());
          framelist.getAdapter().notifyDataSetChanged();
          mediaPlayer.start();
          ll_top_bar.setVisibility(View.VISIBLE);
          ll_bottom_bar.setVisibility(View.VISIBLE);
          ll_seekbar_frame_container.setVisibility(View.VISIBLE);
        }
      });
      dialog.show();
    } else {
      dimScreen();
      final Dialog dialog = new Dialog(this);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

            ll_top_bar.setVisibility(View.VISIBLE);
            ll_bottom_bar.setVisibility(View.VISIBLE);
            ll_seekbar_frame_container.setVisibility(View.VISIBLE);

            dialog.cancel();
          } else {
            addproduct(pos);
            ll_top_bar.setVisibility(View.VISIBLE);
            ll_bottom_bar.setVisibility(View.VISIBLE);
            ll_seekbar_frame_container.setVisibility(View.VISIBLE);
            dialog.cancel();
          }
        }
      });

      if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id()
          .equals("0")) {

        product_image.setVisibility(View.VISIBLE);
        Picasso.with(MakeNewEvent.this)
            .load(frameList_model.getProduct_path())
            .fit()
            .into(product_image);
        tvaddproduct.setText("Buy Product");
      }
      try {

        framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_image_url()));
        MediaController mediaController = new MediaController(MakeNewEvent.this);
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
          framedata_map.get(pos).setName(tittle.getText().toString());
          framelist.getAdapter().notifyDataSetChanged();
          mediaPlayer.start();
          ll_top_bar.setVisibility(View.VISIBLE);
          ll_bottom_bar.setVisibility(View.VISIBLE);
          ll_seekbar_frame_container.setVisibility(View.VISIBLE);
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

  public void close_tut() {
    isTutvisible = false;
    if (mediaPlayer != null) mediaPlayer.start();
  }

  public void start_tut(String msg) {

    isTutvisible = true;
    mediaPlayer = mPlayer.getMediaPlayer();
    mediaPlayer.pause();

    mtutDialog = new Dialog(this);
    mtutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    mtutDialog.getWindow()
        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    mtutDialog.setContentView(R.layout.dialog_tutorial);
    TextView message = (TextView) mtutDialog.findViewById(R.id.txt_tutorial_msg);
    message.setText(msg);
    mtutDialog.findViewById(R.id.txt_tutorial_button)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {

            mtutDialog.cancel();
          }
        });
    mtutDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override public void onCancel(DialogInterface dialog) {
        isTutvisible = false;
        mediaPlayer.start();
      }
    });
    mtutDialog.show();
  }

  public void dimScreen() {
    ll_dimer.setVisibility(View.VISIBLE);
  }

  public void brightenScreen() {
    ll_dimer.setVisibility(View.GONE);
  }

  public void method_save_event() {
    SingleEventModel singleEventModel = new SingleEventModel();
    singleEventModel.setTittle(tittle.replace("Title:", ""));
    // singleEventModel.setType(type);
    singleEventModel.setDescription(description.replace("Description", ""));
    singleEventModel.setFrameList_modelArrayList(framedata_map);
    singleEventModel.setVidaddress(vidAddress);
    singleEventModel.setDuration(mediaPlayer.getDuration());

    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
    String date = df.format(cal.getTime());

    singleEventModel.setTime(date);
    ArrayList<SingleEventModel> singleEventModelArrayList = user_data.getsingleeventlist();

    singleEventModelArrayList.add(singleEventModel);
    user_data.putsingleeventlist(singleEventModelArrayList);
    MyToast.popmessage("Event Saved", this);
  }

  public void method_post_event() {
    if (Validation.hasText(tittle)) {
      if (framedata_map.size() > 0) {
        if (Validation.hasFrameInformation(framedata_map)) {
          MyToast.popmessage("Posting...", MakeNewEvent.this);
          Intent intent = new Intent(MakeNewEvent.this, IntentServiceOperations.class);
          intent.putExtra("video_url", vidAddress);
          intent.putParcelableArrayListExtra("frame_list", framedata_map);
          intent.putExtra("operation", Utility.operation_post_event);
          intent.putExtra("title", label_tittle.getText().toString().replace("Title:", ""));
          intent.putExtra("tagged_user_id", get_tagged_user_in_json());
          intent.putExtra("description",
              label_description.getText().toString().replace("Description:", ""));
          intent.putExtra("duration", mediaPlayer.getDuration());
          intent.putExtra("receiver", register_reviever());
          intent.putExtra("user_id", user_data.getString(Utility.user_id));
          startService(intent);
          finish();
        } else {
          PopMessage.makesimplesnack(mlayout, "Please sync frames and add a title to them");
        }
      } else {
        MyToast.popmessage("Posting...", MakeNewEvent.this);
        Intent intent = new Intent(MakeNewEvent.this, IntentServiceOperations.class);
        intent.putExtra("video_url", vidAddress);
        intent.putParcelableArrayListExtra("frame_list", framedata_map);
        intent.putExtra("operation", Utility.operation_post_event);
        intent.putExtra("title", label_tittle.getText().toString().replace("Title:", ""));
        intent.putExtra("tagged_user_id", get_tagged_user_in_json());
        intent.putExtra("description",
            label_description.getText().toString().replace("Description:", ""));
        intent.putExtra("duration", mediaPlayer.getDuration());
        intent.putExtra("receiver", register_reviever());
        intent.putExtra("user_id", user_data.getString(Utility.user_id));
        startService(intent);
        finish();
      }
    } else {
      PopMessage.makesimplesnack(mlayout, "Provide a title to your event");
    }
  }

  private String get_tagged_user_in_json() {
    try {
      JSONObject jsonObject = new JSONObject();
      JSONArray tag_array = new JSONArray();
      for (int i = 0; i < list_taggerd_user.size(); i++) {
        tag_array.put(list_taggerd_user.get(i).getUser_id());
      }
      jsonObject.put("tag_array", tag_array);
      Log.e("json", jsonObject.toString());
      return jsonObject.toString();
    } catch (JSONException E) {
      return "";
    }
  }

  private void method_post_internet_event() {

    if (framedata_map.size() > 0) {
      if (Validation.hasFrameInformation(framedata_map)) {
        MyToast.popmessage("Posting Frames..", this);
        Intent intent = new Intent(this, IntentServiceOperations.class);

        intent.putParcelableArrayListExtra("frame_list", framedata_map);
        intent.putExtra("operation", Utility.operation_post_internet_event);

        intent.putExtra("user_id", user_data.getString(Utility.user_id));
        intent.putExtra("event_id", event_id);

        startService(intent);
        finish();
      } else {
        PopMessage.makesimplesnack(mlayout, "Please sync frames and add a title to them");
      }
    }
  }

  public void deleteframe(final String frameid, final int position, String frameType) {
    delete_event = true;
    if (frameType.equals(com.tagframe.tagframe.Utils.Constants.frame_resource_type_local)) {
      framedata_map.remove(position);
      framelist.getAdapter().notifyDataSetChanged();
      Collections.sort(framedata_map, new listsort());

      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          delete_event = false;
        }
      }, 2000);
    } else {

      final Dialog dialog = new Dialog(this);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setCancelable(false);
      dialog.setContentView(R.layout.dialog_delete_frame);
      final LinearLayout controls = (LinearLayout) dialog.findViewById(R.id.layout_logout_controls);
      final LinearLayout progress = (LinearLayout) dialog.findViewById(R.id.layout_logging_out);

      dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          controls.setVisibility(View.GONE);
          progress.setVisibility(View.VISIBLE);

          AppPrefs appPrefs = new AppPrefs(MakeNewEvent.this);
          ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
          apiInterface.delete_frame(frameid, appPrefs.getUser().getUser_id())
              .enqueue(new Callback<ResponsePojo>() {
                @Override
                public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                  dialog.dismiss();
                  if (response.body().getStatus().equalsIgnoreCase("success")) {
                    framedata_map.remove(position);
                    framelist.getAdapter().notifyDataSetChanged();
                    Collections.sort(framedata_map, new listsort());
                    delete_event = false;
                    PopMessage.makeshorttoast(MakeNewEvent.this, "Successfull");
                  } else {
                    PopMessage.makeshorttoast(MakeNewEvent.this, response.body().getStatus());
                  }
                }

                @Override public void onFailure(Call<ResponsePojo> call, Throwable t) {
                  dialog.dismiss();
                  PopMessage.makeshorttoast(MakeNewEvent.this, "Unable to delete event");
                }
              });
        }
      });
      dialog.findViewById(R.id.nobtn).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          dialog.dismiss();
        }
      });
      dialog.show();
    }
  }

  public Broadcastresults register_reviever() {
    mReceiver = new Broadcastresults(new Handler());

    mReceiver.setReceiver(this);

    return mReceiver;
  }

  public void updateProgressBar() {
    if (framedata_map.size() > 1) {
      Collections.sort(framedata_map, new listsort());
    }
    mHandler.postDelayed(mUpdateTimeTask, 100);
  }

  public boolean containsLocalFrame() {
    for (int i = 0; i < framedata_map.size(); i++) {
      if (framedata_map.get(i).getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Background Runnable thread
   */
  private Runnable mUpdateTimeTask = new Runnable() {
    public void run() {
      try {

        if (Utility.getScreenOrientation(MakeNewEvent.this)
            == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && !isLayoutSet) {

          Point size = new Point();
          getWindowManager().getDefaultDisplay().getSize(size);
          int height = size.y - Utility.getStatusBarHeight(MakeNewEvent.this);
          if ((vidSurface.getHeight() <= height - Utility.dpToPx(150))) {
            GridLayoutManager gridLayoutManager =
                new GridLayoutManager(MakeNewEvent.this, getGridColumns());
            framelist.setLayoutManager(gridLayoutManager);
            RelativeLayout.LayoutParams layoutParams1 =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mSurfacecontainer.setLayoutParams(layoutParams1);
            RelativeLayout.LayoutParams layoutParams2 =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            layoutParams2.addRule(RelativeLayout.BELOW, R.id.rl_event);
            ll_container_frames.show();
            ll_container_frames.setGravity(Gravity.TOP);
            ll_container_frames.setLayoutParams(layoutParams2);
          }

          isLayoutSet = true;
        }
        mediaPlayer = mPlayer.getMediaPlayer();
      } catch (NullPointerException e) {

      }
      long totalDurationn = 0;
      long currentDuration = 0;
      try {
        totalDurationn = mediaPlayer.getDuration();
        currentDuration = mediaPlayer.getCurrentPosition();
        Log.e("da", currentDuration + "   " + totalDurationn);
      } catch (Exception e) {
        totalDurationn = 0;
        currentDuration = 0;
      }

      if (framedata_map.size() == 0) {
        ll_container_frames.hide();
      }

      // Displaying Total Duration time
      label_seekbar_totalduration.setText("" + Utility.milliSecondsToTimer(totalDurationn));
      // Displaying time completed playing
      label_seekbar_currentduration.setText("" + Utility.milliSecondsToTimer(currentDuration));

      // Updating progress bar
      int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDurationn));

      seekbar.setProgress(progress);

      // Set the "topMargin" to the value you decided upon before

      // Set LayoutParams
      if (currentDuration != totalDurationn) {
        img_frame_to_show.setVisibility(View.INVISIBLE);
        img_play_video.setVisibility(View.INVISIBLE);
      }
      for (int i = 0; i < framedata_map.size(); i++) {
        FrameList_Model fm = framedata_map.get(i);
        if (fm.getStarttime() - 100 <= currentDuration
            && currentDuration <= fm.getEndtime() + 100
            && fm.getEndtime() != 0) {
          img_frame_to_show.setVisibility(View.VISIBLE);
          if (!isTutvisible) {
            isControlVisible = true;
            ll_bottom_bar.show();
            ll_top_bar.show();
            ll_container_frames.show();
          }
          if (fm.getFrametype() == Utility.frametype_image) {

            if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {

              try {
                Bitmap bitmap =
                    BitmapHelper.decodeFile(MakeNewEvent.this, new File(fm.getImagepath()));
                bitmap = Utility.getResizedBitmap(bitmap, 200, 200);
                img_frame_to_show.setImageBitmap(bitmap);
              } catch (Exception e) {
                Picasso.with(MakeNewEvent.this)
                    .load(fm.getImagepath())
                    .resize(200, 200)
                    .into(img_frame_to_show);
              }
            } else {
              Picasso.with(MakeNewEvent.this)
                  .load(fm.getFrame_image_url())
                  .resize(200, 200)
                  .into(img_frame_to_show);
            }
          } else {
            img_play_video.setVisibility(View.VISIBLE);
            if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
              Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fm.getImagepath(),
                  MediaStore.Images.Thumbnails.MINI_KIND);

              thumb = Utility.getResizedBitmap(thumb, 200, 200);
              img_frame_to_show.setImageBitmap(thumb);
            } else {
              Picasso.with(MakeNewEvent.this)
                  .load(fm.getImagepath())
                  .resize(200, 200)
                  .into(img_frame_to_show);
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
      if (totalDurationn != 0) {

        Utility.updateseekbar(seekbar, mediaPlayer.getDuration(), MakeNewEvent.this, framedata_map);
      }
      mHandler.postDelayed(this, 100);
    }
  };

  /**
   *
   * */
  public void show_frame_on_seekbar(final int progress) {
    int measure = (int) ((((float) progress * p.x) / 100) - (progress));
    if (p.x - measure < img_frame_to_show.getWidth()) {

      params.leftMargin = p.x - img_frame_to_show.getWidth() + 100;
    } else {

      params.leftMargin = measure;
    }
    img_frame_to_show.setLayoutParams(params);
    img_play_video.setLayoutParams(params);
  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

  }

  /**
   * When user starts moving the progress handler
   */
  @Override public void onStartTrackingTouch(SeekBar seekBar) {
    // remove message Handler from updating progress bar
    mHandler.removeCallbacks(mUpdateTimeTask);
  }

  public float dipToPixels(float dipValue) {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
  }

  /**
   * When user stops moving the progress hanlder
   */
  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    mHandler.removeCallbacks(mUpdateTimeTask);
    int totalDuration = mediaPlayer.getDuration();
    int currentPosition = Utility.progressToTimer(seekBar.getProgress(), totalDuration);

    // forward or backward to certain seconds
    mediaPlayer.seekTo(currentPosition);
    if (!mediaPlayer.isPlaying()) {
      imageButton_play.setImageResource(android.R.drawable.ic_media_play);
    }
    // update timer progress again
    updateProgressBar();
  }

  public void generate_pop_up_add_frame(View v) {
    PopupMenu popup = new PopupMenu(this, v);
    //Inflating the Popup using xml file
    popup.getMenuInflater().inflate(R.menu.menu_add_frame, popup.getMenu());

    //registering popup with OnMenuItemClickListener
    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

          case R.id.media_library:

            //remove the callbacks to timertask as mediaplayer is not in correct state to call the duration method on it
            mHandler.removeCallbacks(mUpdateTimeTask);

            Intent intent = new Intent(MakeNewEvent.this, MultiSelectActivity.class);
            intent.putExtra(com.veer.multiselect.Util.Constants.LIMIT, 5 - framedata_map.size());
            intent.putExtra(com.veer.multiselect.Util.Constants.SELECT_TYPE,
                com.veer.multiselect.Util.Constants.PATH_IMAGE);
            startActivityForResult(intent, 102);

            break;

          case R.id.add_frame_take_video:

            mHandler.removeCallbacks(mUpdateTimeTask);
            Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            // start the Video Capture Intent
            startActivityForResult(intent1, 902);

            break;

          case R.id.add_frame_take_photo:

            mHandler.removeCallbacks(mUpdateTimeTask);
            // create new Intentwith with Standard Intent action that can be
            // sent to have the camera application capture an video and return it.

            mediaPlayer.pause();
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            uriImage = ProcessnaImages.getOutputMediaFileUri(ProcessnaImages.MEDIA_TYPE_IMAGE,
                getApplicationContext());
            // Intent to pass a URI object containing the path and file name where you want to save the image. We'll get through the data parameter of the method onActivityResult().
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);

            // start the Video Capture Intent
            startActivityForResult(intent2, 903);
            break;

          case R.id.medial_library_video:
                       /* new VideoPicker.Builder(MakeNewEvent.this).mode(VideoPicker.Mode.GALLERY)
                                    .build();*/
            mHandler.removeCallbacks(mUpdateTimeTask);
            Intent intent3 = new Intent(MakeNewEvent.this, MultiSelectActivity.class);
            intent3.putExtra(com.veer.multiselect.Util.Constants.LIMIT, 5 - framedata_map.size());
            intent3.putExtra(com.veer.multiselect.Util.Constants.SELECT_TYPE, Constants.PATH_VIDEO);
            startActivityForResult(intent3, 901);

            break;

          case R.id.add_product:
            mHandler.removeCallbacks(mUpdateTimeTask);

            Intent addintent = new Intent(MakeNewEvent.this, Productlist.class);
            Utility.PRODUCT_LIST_FLAG = FLAG_ADD_PRODUCT;
            startActivityForResult(addintent, Utility.PRODUCT_LIST_FLAG);
        }
        return true;
      }
    });

    popup.show();//showing popup menu
  }

  public void set_frames_to_container(final String selectedimage, final int frametype) {

    FrameList_Model frameList_model = new FrameList_Model();
    frameList_model.setFrametype(frametype);
    frameList_model.setName("");

    frameList_model.setFrame_resource_type(Utility.frame_resource_type_local);

    frameList_model.setImagepath(selectedimage);
    if (frametype == Utility.frametype_video) frameList_model.setFrame_data_url(selectedimage);

    frameList_model.setStarttime((int) 0);
    frameList_model.setEndtime((int) (0));

    if (addframe(frameList_model, (int) totalduration)) {

      if (count == 0) {
        if (appPrefs.getInt(Utility.isTutShownForEvent) == Utility.tutNotShown) {
          start_tut("Synch a Frame by holding it and moving it up");
          count++;
          appPrefs.putInt(Utility.isTutShownForEvent, Utility.tutShown);
        }
      }
    } else {
      MyToast.popmessage("Frame is already attached to this duration", this);
    }
    updateProgressBar();
  }

  public boolean addframe(FrameList_Model frameList_model, int totalduration) {
    if (!isduplicate_frame(frameList_model)) {

      framedata_map.add(frameList_model);
      Utility.updateseekbar(seekbar, totalduration, this, framedata_map);

      if (framedata_map.size() > 1) {
        Collections.sort(framedata_map, new listsort());
      }

      framelist.setAdapter(new FrameListRecyclerAdapter(this, framedata_map));
      ll_container_frames.setVisibility(View.VISIBLE);
      return true;
    } else {
      return false;
    }
  }

  @Override public void onReceiveResult(int resultCode, Bundle resultData) {

  }

  private boolean isduplicate_frame(FrameList_Model frameList_model) {
    boolean isduplicate = false;
    int s_time = frameList_model.getStarttime();
    int e_time = frameList_model.getEndtime();
    Collections.sort(framedata_map, new listsort());
    if (e_time != 0) {

      for (int i = 0; i < framedata_map.size(); i++) {
        FrameList_Model fm = framedata_map.get(i);
        if ((fm.getStarttime() == s_time && fm.getStarttime() != 0) || fm.getEndtime() == e_time) {
          isduplicate = true;
        }
        if ((fm.getStarttime() <= s_time && s_time <= fm.getEndtime() && fm.getStarttime() != 0)
            || fm.getStarttime() <= e_time && e_time <= fm.getEndtime()) {
          isduplicate = true;
        }
      }
    }
    return isduplicate;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    updateProgressBar();

    if (requestCode == Flag_select_video && resultCode == RESULT_OK) {
      selectedImageUri = data.getData();

      // OI FILE Manager
      // String filemanagerstring = selectedImageUri.getPath();

      // MEDIA GALLERY
      final String selectedImagePath = GetPaths.getPath(MakeNewEvent.this, selectedImageUri);
      if (selectedImagePath != null) {

        new Handler().postDelayed(new Runnable() {
          @Override public void run() {

            set_frames_to_container(selectedImagePath, Utility.frametype_video);
          }
        }, 1000);
      }
    } else if (requestCode == 901 && data != null) {
      ArrayList<String> mPath = data.getStringArrayListExtra(Constants.GET_PATHS);

      for (int z = 0; z < mPath.size(); z++) {

        set_frames_to_container(mPath.get(z), Utility.frametype_video);
      }
      //Your Code
    } else if (requestCode == 902 && resultCode == RESULT_OK) {
      selectedImageUri = data.getData();

      // OI FILE Manager
      String filemanagerstring = selectedImageUri.getPath();

      // MEDIA GALLERY
      final String selectedImagePath = GetPaths.getPath(MakeNewEvent.this, selectedImageUri);
      if (selectedImagePath != null) {
        new Handler().postDelayed(new Runnable() {
          @Override public void run() {
            mediaPlayer.pause();
            set_frames_to_container(selectedImagePath, Utility.frametype_video);
          }
        }, 1000);
      }
    }

    if (requestCode == 903 && resultCode == RESULT_OK) {
      //                 selectedImageUri = data.getData();

      // OI FILE Manager
      //                String filemanagerstring = selectedImageUri.getPath();

      // MEDIA GALLERY
      String selectedImagePath = uriImage.getPath();
      if (selectedImagePath != null) {

        set_frames_to_container(selectedImagePath, Utility.frametype_image);
      }
    } else if (requestCode == Flag_pick_photo && resultCode == RESULT_OK) {
      selectedImageUri = data.getData();

      // OI FILE Manager
      //                String filemanagerstring = selectedImageUri.getPath();

      // MEDIA GALLERY
      String selectedImagePath = GetPaths.getPath(MakeNewEvent.this, selectedImageUri);
      if (selectedImagePath != null) {
        set_frames_to_container(selectedImagePath, Utility.frametype_image);
      }
    } else if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
      ArrayList<String> images = data.getStringArrayListExtra(Constants.GET_PATHS);
      for (int i = 0; i < images.size(); i++) {
        set_frames_to_container(images.get(i), Utility.frametype_image);
      }
    } else if (requestCode == Flag_product_list_result && data != null) {

      int position = Utility.framepostion;

      //make change to the frame to which product has been added
      FrameList_Model frameList_model = framedata_map.get(position);
      frameList_model.setProduct_id(data.getStringExtra("product_id"));
      frameList_model.setProduct_path(data.getStringExtra("product_image"));
      frameList_model.setProduct_url(data.getStringExtra("product_url"));

      //show the frame
      show_synced_frame(position);
    } else if (requestCode == FLAG_ADD_PRODUCT && data != null) {

      FrameList_Model frameList_model = new FrameList_Model();
      frameList_model.setFrametype(Utility.frametype_image);
      frameList_model.setName(data.getStringExtra("product_name"));

      frameList_model.setFrame_resource_type(Utility.frame_resource_type_local);

      frameList_model.setImagepath(data.getStringExtra("product_image"));
      frameList_model.setFrame_image_url(data.getStringExtra("product_image"));
      frameList_model.setProduct_id(data.getStringExtra("product_id"));
      frameList_model.setProduct_path(data.getStringExtra("product_image"));
      frameList_model.setProduct_url(data.getStringExtra("product_url"));

      frameList_model.setStarttime((int) 0);
      frameList_model.setEndtime((int) (0));
      frameList_model.setAProductFrame(true);

      if (addframe(frameList_model, (int) totalduration)) {

        if (count == 0) {
          if (appPrefs.getInt(Utility.isTutShownForEvent) == Utility.tutNotShown) {
            start_tut("Synch a Frame by holding it and moving it up");
            count++;
            appPrefs.putInt(Utility.isTutShownForEvent, Utility.tutShown);
          }
        }
      } else {
        MyToast.popmessage("Frame is already attached to this duration", this);
      }
      updateProgressBar();
    } else if (requestCode == Flag_Get_Tagged_User && data != null) {
      TaggedUserModel taggedUserModel = (TaggedUserModel) data.getParcelableExtra("tagged_user");
      if (searchForDuplicate(taggedUserModel)) {
        PopMessage.makesimplesnack(mlayout, "User already added");
      } else {
        list_taggerd_user.add(taggedUserModel);
        show_tagged_user();
      }
      pbar_mediaplayer.setVisibility(View.GONE);
    }
  }

  private boolean searchForDuplicate(TaggedUserModel taggedUserModel) {
    pbar_mediaplayer.setVisibility(View.VISIBLE);
    for (int i = 0; i < list_taggerd_user.size(); i++) {
      if (taggedUserModel.getUser_id().equals(list_taggerd_user.get(i).getUser_id())) return true;
    }
    return false;
  }

  public String getRealPathFromURI(Uri contentUri) {
    try {
      String[] proj = { MediaStore.Images.Media.DATA };
      Cursor cursor = managedQuery(contentUri, proj, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } catch (Exception e) {
      return contentUri.getPath();
    }
  }

  // UPDATED!
  public String getPath(Uri uri) {
    String[] projection = { MediaStore.Video.Media.DATA };
    Cursor cursor = managedQuery(uri, projection, null, null, null);
    if (cursor != null) {
      // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
      // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } else {
      return null;
    }
  }

  public void pause_mediaplayer() {
    mediaPlayer.pause();
  }

  //this method is used for resncing the frame
  public void resync_frame(int position) throws ConcurrentModificationException {
    if (position == RESYC_FRAME_POSITION) {
      FrameList_Model fm = framedata_map.get(position);
      int currentDuration = mediaPlayer.getCurrentPosition();
      int totalDuration = mediaPlayer.getDuration();
      if (currentDuration + 1000 < totalDuration) {
        int start_time, end_time;
        FrameList_Model frameList_model = new FrameList_Model();
        start_time = fm.getStarttime();
        end_time = fm.getEndtime();
        fm.setStarttime(0);
        fm.setEndtime(0);
        Collections.sort(framedata_map, new listsort());
        frameList_model.setStarttime(currentDuration);
        frameList_model.setEndtime(currentDuration + 2000);
        if (!isduplicate_frame(frameList_model)) {
          fm.setStarttime(currentDuration);
          fm.setEndtime(currentDuration + 2000);
          if (totalDuration - currentDuration < 2000) {
            fm.setEndtime(Math.abs(totalDuration));
          }

          {
            if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
              fm.setEdited(true);
            }
          }

          Collections.sort(framedata_map, new listsort());

          framelist.getAdapter().notifyDataSetChanged();
        } else {
          Collections.sort(framedata_map, new listsort());
          fm.setStarttime(start_time);
          fm.setEndtime(end_time);
          PopMessage.makesimplesnack(mlayout, "Frame Already Attached");
        }
        mediaPlayer.start();

        if (counter_tut == 0) {
          if (appPrefs.getInt(Utility.isTutShownForFrame) == Utility.tutNotShown) {
            start_tut("Tap on a Frame to add a Title to it");
            counter_tut++;
            appPrefs.putInt(Utility.isTutShownForFrame, Utility.tutShown);
          }
        }
      } else {
        MyToast.popmessage("Cannot Sync the frame at this duration", this);
      }
    }
  }

  public void addproduct(int pos) {
    //remove the callbacks to timertask as mediaplayer is not in correct state to call the duration method on it
    mHandler.removeCallbacks(mUpdateTimeTask);
    Utility.framepostion = pos;
    Utility.isResumeFromActivityResult = true;
    Intent intent = new Intent(this, Productlist.class);
    Utility.PRODUCT_LIST_FLAG = Flag_product_list_result;
    startActivityForResult(intent, Utility.PRODUCT_LIST_FLAG);
  }

  @Override public void onBackPressed() {
    if (event_type == Utility.eventtype_local) {
      final Dialog dialog = new Dialog(this);
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setContentView(R.layout.dialog_cancel_event);
      dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          method_save_event();
          mHandler.removeCallbacks(mUpdateTimeTask);
          go_to_modules();
          dialog.dismiss();
        }
      });
      dialog.findViewById(R.id.nobtn).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mHandler.removeCallbacks(mUpdateTimeTask);
          go_to_modules();
          dialog.dismiss();
        }
      });
      dialog.show();
    } else {
      mHandler.removeCallbacks(mUpdateTimeTask);
      super.onBackPressed();
    }
  }

  public boolean checkPermissions(String[] permission, int requestCode) {
    if (Build.VERSION.SDK_INT >= 23) {
      boolean isGranted = true;
      ArrayList<String> reqPermission = new ArrayList<>();
      for (int i = 0; i < permission.length; i++) {
        if (!(checkSelfPermission(permission[i]) == PackageManager.PERMISSION_GRANTED)) {
          isGranted = false;
          reqPermission.add(permission[i]);
        }
      }
      if (!isGranted) {
        String[] reqArray = new String[reqPermission.size()];
        reqArray = reqPermission.toArray(reqArray);
        requestPermissions(reqArray, requestCode);
      }
      return isGranted;
    } else { //permission is automatically granted on sdk<23 upon installation
      return true;
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case 2909: {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          generate_pop_up_add_frame(view);
        } else {
          //denied
          PopMessage.makeshorttoast(this, "We need this permission add frames to your event");
        }
        return;
      }
    }
  }

  private void go_to_modules() {

    Intent intent = new Intent(MakeNewEvent.this, Modules.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    startActivity(intent);
    finish();
  }

  public void showlog(String id) {
    Log.e("frames user id", id);
  }
}
