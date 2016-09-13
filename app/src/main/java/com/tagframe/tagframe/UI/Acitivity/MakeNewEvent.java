package com.tagframe.tagframe.UI.Acitivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.models.Image;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.FrameListAdapter;
import com.tagframe.tagframe.Adapters.TaggedUserAdapter;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.SingleEventModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.MyMediaPlayer.IPlayerListener;
import com.tagframe.tagframe.MyMediaPlayer.Player;
import com.tagframe.tagframe.R;
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


/**
 * Created by Brajendr on 7/13/2016.
 */
public class MakeNewEvent extends Activity implements SeekBar.OnSeekBarChangeListener, Broadcastresults.Receiver {

    //Widgets
    private Player mPlayer;
    private MediaPlayer mediaPlayer;

    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    private ProgressBar pbar_mediaplayer;
    private ImageButton imageButton_play,imageButton_screen_resolution;
    private CustomSeekBar seekbar;
    private TextView txt_tagged_user, label_seekbar_currentduration, label_seekbar_totalduration, label_tittle, label_description, post_event, save_event, txt_tutorial_msg;
    private TextView txt_percent;
    private Button btn_tut_got_it;
    private LinearLayout ll_add_frame , mlayout,btn_add_frame;
    private RelativeLayout  ll_seekbar_frame_container, ll_tutorial;
    private AnimatingRelativeLayout ll_top_bar,ll_bottom_bar,ll_container_frames;
    private RelativeLayout.LayoutParams params;
    private Point p = new Point();
    private HorizontalListView framelist;
    private ImageView img_frame_to_show, img_play_video;


    //Utility
    private final static String TAG = MakeNewEvent.class.getSimpleName();
    private final static String CURRENT_DURATION = "CURRENT_DURATION";
    private final static String TOTAL_DURATION = "TOTAL_DURATION";
    private final static String FRAMELIST = "FRAMELIST";
    public static int Flag_select_video = 4;
    public static int Flag_pick_photo = 5;
    //public static int Flag_select_photo = 6;
    public static int Flag_product_list_result = 6;
    public static int Flag_Get_Tagged_User = 7;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private Handler mControlsVisibilityHandler = new Handler();

    private String vidAddress = "";
    private String tittle, description, type, event_id;
    private long totalduration;
    private String s_current_duration;

    private Broadcastresults mReceiver;
    private AppPrefs user_data;
    private Calendar cal;
    private int count = 0, event_type, counter_tut = 0, limit = 0;
    private Uri selectedImageUri;
    boolean delete_event = false;
    Uri uriImage;

    //this provides information for configration change
    //set this to true while retriving the data from the savedinstancestate
    //then check on bufferingfinished listener if flag is set
    //perform the mediaplayer seek to operation
    private boolean isfromsavedinstance = false, isControlVisible = true;
    private long sav_instance_current_duration = 0;


    private ArrayList<FrameList_Model> framedata_map;
    private ArrayList<TaggedUserModel> list_taggerd_user;
    private boolean isTutvisible = false;

    //lifecycles methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_event);

        //getting the intents
        vidAddress = getIntent().getStringExtra("data_url");


        event_type = getIntent().getIntExtra("eventtype", 0);


        if (event_type == Utility.eventtype_saved) {
            framedata_map = getIntent().getParcelableArrayListExtra("framelist");
            list_taggerd_user = getIntent().getParcelableArrayListExtra("tagged_user_id");
            tittle = "Title:" + getIntent().getStringExtra("tittle");
            description = "Description:" + getIntent().getStringExtra("des");

        } else if (event_type == Utility.eventtype_local) {
            framedata_map = new ArrayList<>();
            tittle = "Title:";
            description = "Description:";
            list_taggerd_user = new ArrayList<>();

        } else if (event_type == Utility.eventtype_internet) {
            framedata_map = getIntent().getParcelableArrayListExtra("framelist");
            list_taggerd_user = getIntent().getParcelableArrayListExtra("tagged_user_id");
            tittle = "Title:" + getIntent().getStringExtra("tittle");
            description = "Description:" + getIntent().getStringExtra("des");
            event_id = getIntent().getStringExtra("eventid");
        }


        user_data = new AppPrefs(this);
        cal = Calendar.getInstance();

        //initialize views and mediaplayer
        init(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.setSource(this, vidAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
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

        super.onSaveInstanceState(outState);
    }

    //*lifecycle methods


    //utility methods

    private IPlayerListener mPlayerListener = new IPlayerListener() {
        @Override
        public void onError(String message) {

            mHandler.removeCallbacks(mUpdateTimeTask);
            pbar_mediaplayer.setVisibility(View.GONE);
            txt_percent.setVisibility(View.VISIBLE);
            txt_percent.setText("An Error Occurred");
        }

        @Override
        public void onBufferingStarted() {

            txt_percent.setVisibility(View.VISIBLE);
            pbar_mediaplayer.setVisibility(View.VISIBLE);
            txt_percent.setText("Buffering...");
        }

        @Override
        public void onBufferingFinished() {
            Log.e("onBuffering finisnhed", "d");
            txt_percent.setVisibility(View.GONE);
            pbar_mediaplayer.setVisibility(View.GONE);
            //controlvisible

            setControlsVisibilty();

            if (isfromsavedinstance) {
                mHandler.removeCallbacks(mUpdateTimeTask);

                //call this when mediaplayer is prepared or after the buffer has been finished
                mediaPlayer.seekTo((int) sav_instance_current_duration);


            }
            updateProgressBar();
        }

        @Override
        public void onRendereingstarted() {
            Log.e("Ds", "rendering statered");

        }

        @Override
        public void onCompletion() {

            mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
            isControlVisible = true;
            ll_bottom_bar.setVisibility(View.VISIBLE);
            ll_top_bar.setVisibility(View.VISIBLE);
        }
    };

    //This method initialiazes all the views
    private void init(Bundle savedstate) {


        //LAYOUTS
        ll_top_bar = (AnimatingRelativeLayout) findViewById(R.id.topbar);
        ll_bottom_bar = (AnimatingRelativeLayout) findViewById(R.id.ll_mp_tools);
        mlayout = (LinearLayout) findViewById(R.id.mlayout_makenew_event);
        mlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((framedata_map.size()==0))
                    toggleControlsVisibilty();
            }
        });

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
        imageButton_screen_resolution=(ImageButton) findViewById(R.id.btn_screen_resolution);
        label_seekbar_currentduration = (TextView) findViewById(R.id.txtcurrentduration);
        label_seekbar_totalduration = (TextView) findViewById(R.id.txttotalduration);
        label_tittle = (TextView) findViewById(R.id.event_tittle);
        label_description = (TextView) findViewById(R.id.event_description);
        post_event = (TextView) findViewById(R.id.postevent);
        txt_tagged_user = (TextView) findViewById(R.id.taguser_event);
        label_tittle.setText(tittle);
        label_description.setText(description);
        label_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editevent_tittle();
            }
        });
        seekbar = (CustomSeekBar) findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        img_frame_to_show = (ImageView) findViewById(R.id.img_frame_to_show);
        img_play_video = (ImageView) findViewById(R.id.img_play_video);
        //setting the layout parameters of image
        ll_seekbar_frame_container = (RelativeLayout) findViewById(R.id.layout_frame_at_time_container);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 0;
        img_frame_to_show.setLayoutParams(params);
        img_play_video.setLayoutParams(params);
        getWindowManager().getDefaultDisplay().getSize(p);


        //setting the event on play/stop button

        imageButton_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageButton_play.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.start();
                    imageButton_play.setImageResource(R.drawable.stop);

                }
            }
        });


        imageButton_screen_resolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScreenResolution();
            }
        });

        ll_add_frame = (LinearLayout) findViewById(R.id.ll_add_frame);
        btn_add_frame = (LinearLayout) findViewById(R.id.btn_add_frame);

        ll_container_frames = (AnimatingRelativeLayout) findViewById(R.id.container_frames);

        btn_add_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (framedata_map.size() == 5) {
                    PopMessage.makesimplesnack(mlayout, "You can add only 5 frame to an event");
                } else {
                    generate_pop_up_add_frame(ll_add_frame);
                }
            }
        });

        framelist = (HorizontalListView) findViewById(R.id.framelist);
        framelist.setAdapter(new FrameListAdapter(this, framedata_map, event_type));

        seteventon_framelist();

        post_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (event_type == Utility.eventtype_internet) {

                    method_post_internet_event();

                } else {
                    method_post_event();
                }
            }
        });

        save_event = (TextView) findViewById(R.id.saveevent);

        save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            @Override
            public void onClick(View v) {
                close_tut();
            }
        });


        //frame_to_show on click listener

        img_frame_to_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            save_event.setText("Done");
            if (framedata_map.size() > 0) {
                ll_container_frames.setVisibility(View.VISIBLE);
            }
        }

        //tagged user functionality
        txt_tagged_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_tagged_user();
            }
        });


        if (savedstate != null) {

            //resotring the mediaplayers state
            framedata_map = savedstate.getParcelableArrayList(FRAMELIST);
            sav_instance_current_duration = savedstate.getLong(CURRENT_DURATION);
            long total = savedstate.getLong(TOTAL_DURATION);
            if (framedata_map.size() > 0) {
                ll_container_frames.setVisibility(View.VISIBLE);
                framelist.setAdapter(new FrameListAdapter(this, framedata_map, Utility.eventtype_local));
            }
            label_seekbar_currentduration.setText(Utility.milliSecondsToTimer(sav_instance_current_duration));
            label_seekbar_totalduration.setText(Utility.milliSecondsToTimer(total));

            isfromsavedinstance = true;

        }

    }

    private void toggleScreenResolution() {
        int orientation=getScreenOrientation();
        if(orientation==1)//portrait
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(orientation==2)//landscape
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public int getScreenOrientation()
    {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    private void toggleControlsVisibilty() {
        if (!isControlVisible) {
            mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
            isControlVisible = true;
            ll_bottom_bar.show();
            ll_top_bar.show();
            if(framedata_map.size()>0)
            ll_container_frames.show();
            setControlsVisibilty();
        } else

        {
            mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
            isControlVisible = false;
            ll_bottom_bar.hide();
            ll_top_bar.hide();
            if(framedata_map.size()>0)
            ll_container_frames.hide();
            setControlsVisibilty();
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

        if (list_taggerd_user.size() > 0) {
            txt_message.setVisibility(View.GONE);
        }
        list.setAdapter(new TaggedUserAdapter(list_taggerd_user, this));

        dialog.findViewById(R.id.txt_tag_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeNewEvent.this, SearchUserActivity.class);
                intent.putExtra("operation", Utility.operation_onclicked_tagged_user);
                startActivityForResult(intent, Flag_Get_Tagged_User);
                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.txt_tag_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

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

    private void editevent_tittle() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_event_tittle);

        // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);


        final TextView desxription = (TextView) dialog.findViewById(R.id.dia_framedescription);
        final TextView tittle = (TextView) dialog.findViewById(R.id.dia_frametittle);


        tittle.setText(label_tittle.getText().toString().replace("Title:", ""));
        desxription.setText(label_description.getText().toString().replace("Description:", ""));

        dialog.findViewById(R.id.dia_rem_yesbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tittle.getText().toString().isEmpty() && desxription.getText().toString().isEmpty()) {
                    MyToast.popmessage("Please add tittle or description", MakeNewEvent.this);
                } else {
                    label_description.setText("Description:" + desxription.getText().toString());
                    label_tittle.setText("Title:" + tittle.getText().toString());
                    dialog.dismiss();
                }

            }
        });

        dialog.findViewById(R.id.dia_rem_nobtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void show_synced_frame(final int pos) {

        mediaPlayer.pause();
        ll_top_bar.setVisibility(View.GONE);
        ll_bottom_bar.setVisibility(View.GONE);
        ll_seekbar_frame_container.setVisibility(View.GONE);
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

            TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
            final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);

            TextView tvaddproduct = (TextView) dialog.findViewById(R.id.add_product);
            tvaddproduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id().equals("0")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(frameList_model.getProduct_url()));
                        startActivity(browserIntent);

                        ll_top_bar.setVisibility(View.VISIBLE);
                        ll_bottom_bar.setVisibility(View.VISIBLE);
                        ll_seekbar_frame_container.setVisibility(View.VISIBLE);

                        dialog.dismiss();
                    } else {
                        addproduct(pos);
                        ll_top_bar.setVisibility(View.VISIBLE);
                        ll_bottom_bar.setVisibility(View.VISIBLE);
                        ll_seekbar_frame_container.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                }
            });


            frameimage.setVisibility(View.VISIBLE);
            if (!frameList_model.getProduct_id().isEmpty() && !frameList_model.getProduct_id().equals("0")) {
                Picasso.with(MakeNewEvent.this).load(frameList_model.getProduct_path()).into(frameimage);
                tvaddproduct.setText("Buy Product");

            } else {
                if (frameList_model.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
                    Picasso.with(MakeNewEvent.this).load(frameList_model.getImagepath()).into(frameimage);
                } else {
                    frameimage.setImageBitmap(BitmapHelper.decodeFile(MakeNewEvent.this, new File(frameList_model.getImagepath())));

                }
            }
            tittle.setText(frameList_model.getName());
            duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    framedata_map.get(pos).setName(tittle.getText().toString());
                    ((BaseAdapter) framelist.getAdapter()).notifyDataSetChanged();
                    mediaPlayer.start();
                    ll_top_bar.setVisibility(View.VISIBLE);
                    ll_bottom_bar.setVisibility(View.VISIBLE);
                    ll_seekbar_frame_container.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });
            img_frame_to_show.setVisibility(View.GONE);
            img_play_video.setVisibility(View.GONE);
            dialog.show();

        } else {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_show_video_frame);

            VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
            //ImageView frameimage = (ImageView) dialog.findViewById(R.id.framelist_image);
            ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);

            TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
            final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);


            try {
                Log.e("ds", frameList_model.getFrame_data_url());
                framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_data_url()));
            } catch (Exception e) {
                Log.e("Dsa", e.getMessage() + frameList_model.getFrame_data_url());
            }
            framevideo.start();

            tittle.setText(frameList_model.getName());
            duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    framedata_map.get(pos).setName(tittle.getText().toString());
                    ((BaseAdapter) framelist.getAdapter()).notifyDataSetChanged();
                    mediaPlayer.start();
                    ll_top_bar.setVisibility(View.VISIBLE);
                    ll_bottom_bar.setVisibility(View.VISIBLE);
                    ll_seekbar_frame_container.setVisibility(View.VISIBLE);
                }
            });
            img_frame_to_show.setVisibility(View.GONE);
            img_play_video.setVisibility(View.GONE);
            dialog.show();
        }
    }

    public void close_tut() {
        isTutvisible = false;
        ll_top_bar.setVisibility(View.VISIBLE);
        ll_bottom_bar.setVisibility(View.VISIBLE);
        ll_tutorial.setVisibility(View.GONE);
        ll_seekbar_frame_container.setVisibility(View.VISIBLE);
        if (mediaPlayer != null)
            mediaPlayer.start();

    }

    public void start_tut(String msg) {

        isTutvisible = true;
        ll_top_bar.setVisibility(View.GONE);
        ll_bottom_bar.setVisibility(View.GONE);
        ll_tutorial.setVisibility(View.VISIBLE);
        txt_tutorial_msg.setText(msg);
        ll_seekbar_frame_container.setVisibility(View.GONE);
        if (mediaPlayer != null)
            mediaPlayer.pause();
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
        MyToast.popmessage("Posting...", this);
        Intent intent = new Intent(this, IntentServiceOperations.class);
        intent.putExtra("video_url", vidAddress);
        intent.putParcelableArrayListExtra("frame_list", framedata_map);
        intent.putExtra("operation", Utility.operation_post_event);
        intent.putExtra("title", label_tittle.getText().toString().replace("Title:", ""));
        intent.putExtra("tagged_user_id", get_tagged_user_in_json());
        intent.putExtra("description", label_description.getText().toString().replace("Description:", ""));
        //intent.putExtra("access_type", type);
        intent.putExtra("duration", mediaPlayer.getDuration());
        intent.putExtra("receiver", register_reviever());
        intent.putExtra("user_id", user_data.getString(Utility.user_id));

        startService(intent);
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

        MyToast.popmessage("Posting Frames..", this);
        Intent intent = new Intent(this, IntentServiceOperations.class);

        intent.putParcelableArrayListExtra("frame_list", framedata_map);
        intent.putExtra("operation", Utility.operation_post_internet_event);

        intent.putExtra("user_id", user_data.getString(Utility.user_id));
        intent.putExtra("event_id", event_id);

        startService(intent);
    }


    private void seteventon_framelist() {

        framelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //FrameList_Model frameList_model = (FrameList_Model) framelist.getAdapter().getItem(position);
                if (!delete_event)
                    show_synced_frame(position);


            }
        });
    }

    public void deleteframe(int position) {
        delete_event = true;
        framedata_map.remove(position);
        ((BaseAdapter) framelist.getAdapter()).notifyDataSetChanged();
        Collections.sort(framedata_map, new listsort());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                delete_event = false;
            }
        }, 2000);

    }

    public Broadcastresults register_reviever() {
        mReceiver = new Broadcastresults(new Handler());

        mReceiver.setReceiver(this);

        return mReceiver;

    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public void setControlsVisibilty() {
        mControlsVisibilityHandler.postDelayed(setVisibiltyTask, 4000);
    }

    private Runnable setVisibiltyTask = new Runnable() {
        @Override
        public void run() {
            if ((framedata_map.size()==0)) {
                if (isControlVisible) {
                    isControlVisible = false;
                    ll_top_bar.hide();
                    ll_bottom_bar.hide();
                    ll_container_frames.setVisibility(View.GONE);
                }
                mControlsVisibilityHandler.postDelayed(setVisibiltyTask, 4000);
            } else {
                mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
            }
        }
    };

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDurationn = 0;
            long currentDuration = 0;
            try {
                totalDurationn = mediaPlayer.getDuration();
                currentDuration = mediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                totalDurationn = 0;
                currentDuration = 0;
            }


            setControlsVisibilty();

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

            img_frame_to_show.setVisibility(View.INVISIBLE);
            img_play_video.setVisibility(View.INVISIBLE);
            for (int i = 0; i < framedata_map.size(); i++) {
                FrameList_Model fm = framedata_map.get(i);
                if (fm.getStarttime() - 100 <= currentDuration && currentDuration <= fm.getEndtime() + 100 && fm.getEndtime() != 0) {
                    img_frame_to_show.setVisibility(View.VISIBLE);
                    //set controls visibity
                    if (!isTutvisible) {
                        mControlsVisibilityHandler.removeCallbacks(setVisibiltyTask);
                        isControlVisible = true;
                        ll_bottom_bar.show();
                        ll_top_bar.show();
                        ll_container_frames.show();
                    }

                    //if a product is attached to the frame
                    if (!fm.getProduct_id().isEmpty() && !fm.getProduct_id().equals("0")) {
                        Picasso.with(MakeNewEvent.this).load(fm.getProduct_path()).into(img_frame_to_show);


                    } else {

                        if (fm.getFrametype() == Utility.frametype_image) {

                            if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {

                                Bitmap bitmap = BitmapHelper.decodeFile(MakeNewEvent.this, new File(fm.getImagepath()));
                                bitmap = getResizedBitmap(bitmap, 200, 200);
                                img_frame_to_show.setImageBitmap(bitmap);
                            } else {
                                Picasso.with(MakeNewEvent.this).load(fm.getImagepath()).resize(200, 200).into(img_frame_to_show);
                            }
                        } else {
                            img_play_video.setVisibility(View.VISIBLE);
                            if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
                                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fm.getImagepath(),
                                        MediaStore.Images.Thumbnails.MINI_KIND);

                                thumb = getResizedBitmap(thumb, 200, 200);

                                img_frame_to_show.setImageBitmap(thumb);
                            } else {
                                Picasso.with(MakeNewEvent.this).load(fm.getImagepath()).resize(200, 200).into(img_frame_to_show);
                            }

                        }
                    }
                    img_frame_to_show.setTag(i + "");
                    int prog = (int) (Utility.getProgressPercentage(fm.getEndtime(), totalDurationn));
                    if (prog > 80) {
                        prog = 80;
                    }


                    show_frame_on_seekbar(prog - (int) (Utility.getProgressPercentage(2000, totalDurationn)));

                }
                if (totalDurationn != 0)
                    updateseekbar(seekbar, mediaPlayer.getDuration());

            }


            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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

    /**
     *
     * */
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

        // Set the "topMargin" to the value you decided upon before


        // Set LayoutParams
        img_frame_to_show.setLayoutParams(params);
        img_play_video.setLayoutParams(params);
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


                        Intent inte = new Intent(MakeNewEvent.this, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
                        inte.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 5 - framedata_map.size());
                        startActivityForResult(inte, 102);

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

                        uriImage = ProcessnaImages.getOutputMediaFileUri(ProcessnaImages.MEDIA_TYPE_IMAGE, getApplicationContext());
                        // Intent to pass a URI object containing the path and file name where you want to save the image. We'll get through the data parameter of the method onActivityResult().
                        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);

                        // start the Video Capture Intent
                        startActivityForResult(intent2, 903);
                        break;


                    case R.id.medial_library_video:
                       /* new VideoPicker.Builder(MakeNewEvent.this).mode(VideoPicker.Mode.GALLERY)
                                    .build();*/
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        Intent intent12 = new Intent(MakeNewEvent.this, AndroidCustomGalleryActivity.class);
                        intent12.putExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 5 - framedata_map.size());
                        startActivityForResult(intent12, 901);

                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public void set_frames_to_container(final String selectedimage, final int frametype) {

        /*final int starttime;
        final int endtime;
        final Dialog dialog=new Dialog(this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_addframe);


        final ImageView frameimage = (ImageView) dialog.findViewById(R.id.dia_frameimage);
        VideoView framevideo=(VideoView) dialog.findViewById(R.id.dia_framevideo);

        final EditText ed_start_time=(EditText)dialog.findViewById(R.id.dia_frame_start_time);
        final EditText ed_end_time=(EditText)dialog.findViewById(R.id.dia_frame_end_time);
        final EditText ed_frame_tittle=(EditText)dialog.findViewById(R.id.dia_frametittle);

        final TextView txt_current=(TextView)dialog.findViewById(R.id.dia_txtcurrentduration);
        final TextView txt_total=(TextView)dialog.findViewById(R.id.dia_txttotalduration);
        final TextView txt_msg=(TextView)dialog.findViewById(R.id.dia_frame_msg);



        final SeekBar dia_seekbar=(SeekBar)dialog.findViewById(R.id.dia_seekbar_starttime);



        updateseekbar(dia_seekbar, (int) totalduration);

        Button yesbutton=(Button)dialog.findViewById(R.id.dia_rem_yesbtn);
        Button nobutton=(Button)dialog.findViewById(R.id.dia_rem_nobtn);

        if(frametype==Utility.frametype_image) {

            Picasso.with(this).load(new File(selectedimage)).into(frameimage);
        }
        else
        {
            frameimage.setVisibility(View.GONE);
            framevideo.setVisibility(View.VISIBLE);
            framevideo.setVideoURI(Uri.parse(selectedimage));
            framevideo.start();
        }

        txt_total.setText(label_seekbar_totalduration.getText().toString());
        txt_current.setText(s_current_duration);

        totalduration = Utility.stringtomillisecond(label_seekbar_totalduration.getText().toString());
        long current=Utility.stringtomillisecond(s_current_duration);
        int progress=(int)Utility.getProgressPercentage(current,totalduration);

        dia_seekbar.setProgress(progress);
        long current_time = Utility.progressToTimer(progress, (int) totalduration);


        ed_start_time.setText("" + Utility.milliSecondsToTimer(current_time));
        txt_current.setText("" + Utility.milliSecondsToTimer(current_time));


        ed_end_time.setText("" + Utility.milliSecondsToTimer(current_time + 2000));

        dia_seekbar.setMax(100);
        dia_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar6) {

                int progress = seekBar6.getProgress();
                totalduration = Utility.stringtomillisecond(label_seekbar_totalduration.getText().toString());
                long current_time = Utility.progressToTimer(progress, (int) totalduration);
                txt_total.setText("" + Utility.milliSecondsToTimer(current_time));

                ed_start_time.setText("" + Utility.milliSecondsToTimer(current_time));
                txt_current.setText("" + Utility.milliSecondsToTimer(current_time));


                ed_end_time.setText("" + Utility.milliSecondsToTimer(current_time + 2000));


            }
        });

        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!ed_frame_tittle.getText().toString().isEmpty()&&!ed_end_time.getText().toString().isEmpty())

                {


                    int progress = dia_seekbar.getProgress();
                    totalduration = Utility.stringtomillisecond(label_seekbar_totalduration.getText().toString());
                    long current_time = Utility.progressToTimer(progress, (int) totalduration);
                    long end_time=current_time+2000;
                    if(end_time<=totalduration) {

                        FrameList_Model frameList_model = new FrameList_Model();
                        frameList_model.setFrametype(frametype);
                        frameList_model.setName(ed_frame_tittle.getText().toString());

                        frameList_model.setImagepath(selectedimage);

                        frameList_model.setStarttime((int) current_time);
                        frameList_model.setEndtime((int) (current_time + 2000));

                        if (addframe(frameList_model, (int) totalduration)) {

                            if(count==0)
                                {
                                    start_tut("You can also resync frame by holding it and swiping it to the event");
                                    count++;
                                }
                            dialog.dismiss();


                        } else {
                            txt_msg.setText("Frame is already synced at this duration.");
                        }
                    }
                    else
                    {
                        txt_msg.setText("You can only attach a frame within the time-limit of frame");
                    }


                }
                else
                {
                    MyToast.popmessage("Please input all fields",MakeNewEvent.this);
                }
            }
        });
        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


        //ll_container_frames.setVisibility(View.VISIBLE);*/

        FrameList_Model frameList_model = new FrameList_Model();
        frameList_model.setFrametype(frametype);
        frameList_model.setName("");

        frameList_model.setFrame_resource_type(Utility.frame_resource_type_local);

        frameList_model.setImagepath(selectedimage);
        if (frametype == Utility.frametype_video)
            frameList_model.setFrame_data_url(selectedimage);

        frameList_model.setStarttime((int) 0);
        frameList_model.setEndtime((int) (0));

        if (addframe(frameList_model, (int) totalduration)) {

            if (count == 0) {
                start_tut("Synch a Frame by holding it and moving it up");
                count++;
            }


        } else {
            MyToast.popmessage("Frame is already attached to this duration", this);
        }
        updateProgressBar();

    }

    public boolean addframe(FrameList_Model frameList_model, int totalduration) {
        if (!isduplicate_frame(frameList_model)) {

            framedata_map.add(frameList_model);
            updateseekbar(seekbar, totalduration);

            displaylistdata();
            if (framedata_map.size() > 1) {
                Collections.sort(framedata_map, new listsort());
                displaylistdata();
            }

            framelist.setAdapter(new FrameListAdapter(MakeNewEvent.this, framedata_map, event_type));
            ll_container_frames.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public void displaylistdata() {
        for (int i = 0; i < framedata_map.size(); i++) {
            Log.e("frame:", framedata_map.get(i).getName());
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }


    class listsort implements Comparator<FrameList_Model> {

        @Override
        public int compare(FrameList_Model e1, FrameList_Model e2) {
            if (e1.getStarttime() < e2.getStarttime()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private boolean isduplicate_frame(FrameList_Model frameList_model) {

        boolean isduplicate = false;
        int s_time = frameList_model.getStarttime();
        int e_time = frameList_model.getEndtime();

        if (e_time != 0) {


            for (int i = 0; i < framedata_map.size(); i++) {
                FrameList_Model fm = framedata_map.get(i);
                if (fm.getStarttime() <= s_time && s_time <= fm.getEndtime()) {
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
                    @Override
                    public void run() {

                        set_frames_to_container(selectedImagePath, Utility.frametype_video);
                    }
                }, 1000);


            }
        } else if (requestCode == 901 && data != null) {
            ArrayList<String> mPath = data.getStringArrayListExtra("videopaths");

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
                    @Override
                    public void run() {
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
            ArrayList<Image> images = data.getParcelableArrayListExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
            for (int i = 0; i < images.size(); i++) {
                set_frames_to_container(images.get(i).path, Utility.frametype_image);
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
            if (taggedUserModel.getUser_id().equals(list_taggerd_user.get(i).getUser_id()))
                return true;
        }
        return false;
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
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
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void updateseekbar(SeekBar seekBar, int total) {

          /*  LayerDrawable localLayerDrawable = new LayerDrawable(new Drawable[] {new SeekBarBackgroundDrawable(this),new SeekBarProgressDrawable(new ColorDrawable(Color.WHITE),3,1,this)});
            localLayerDrawable.setId(0, android.R.id.background);
            localLayerDrawable.setId(1, android.R.id.progress);
            seekbar.setProgressDrawable(localLayerDrawable);*/


        // ColorDrawable backDrawable = new ColorDrawable(Color.WHITE);
        SeekBarBackgroundDrawable backgroundDrawable = new SeekBarBackgroundDrawable(this);
        // ColorDrawable progressDrawable = new ColorDrawable(Color.BLUE);
        //Custom seek bar progress drawable. Also allows you to modify appearance.
        SeekBarProgressDrawable clipProgressDrawable = new SeekBarProgressDrawable(this, framedata_map, total);
        Drawable[] drawables = new Drawable[]{backgroundDrawable, clipProgressDrawable};

        //Create layer drawables with android pre-defined ids
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.progress);

        //Set to seek bar
        seekBar.setProgressDrawable(layerDrawable);

    }

    public void pause_mediaplayer() {
        mediaPlayer.pause();
    }

    //this method is used for resncing the frame
    public void resync_frame(int position) {

        long startTime = System.currentTimeMillis();
        FrameList_Model fm = framedata_map.get(position);
        if (mediaPlayer.getCurrentPosition() + 2000 <= mediaPlayer.getDuration()) {

            FrameList_Model frameList_model = new FrameList_Model();
            frameList_model.setStarttime(mediaPlayer.getCurrentPosition());
            frameList_model.setEndtime(mediaPlayer.getCurrentPosition() + 2000);

            if (!isduplicate_frame(frameList_model)) {
                fm.setStarttime(mediaPlayer.getCurrentPosition());
                fm.setEndtime(mediaPlayer.getCurrentPosition() + 2000);

                if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_internet)) {
                    fm.setEdited(true);
                }
                Collections.sort(framedata_map, new listsort());
                ((BaseAdapter) framelist.getAdapter()).notifyDataSetChanged();
            } else {
                PopMessage.makesimplesnack(mlayout, "Frame Already Attached");
            }
            mediaPlayer.start();


            if (counter_tut == 0) {
                start_tut("Tap on a Frame to add a Title to it");
                counter_tut++;
            }

        } else {
            MyToast.popmessage("Cannot Sync the frame at this duration", this);
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        Log.e("resyc", elapsedTime + "");


    }


    public void addproduct(int pos) {
        //remove the callbacks to timertask as mediaplayer is not in correct state to call the duration method on it
        mHandler.removeCallbacks(mUpdateTimeTask);

        Utility.framepostion = pos;
        Intent intent = new Intent(this, Productlist.class);
        intent.putExtra("frame_position", pos);
        startActivityForResult(intent, Flag_product_list_result);
    }

    @Override
    public void onBackPressed() {
        if (event_type == Utility.eventtype_local) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_cancel_event);
            dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    method_save_event();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    go_to_modules();
                }
            });
            dialog.findViewById(R.id.nobtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    go_to_modules();
                }
            });
            dialog.show();
        } else {
            mHandler.removeCallbacks(mUpdateTimeTask);
            super.onBackPressed();
        }
    }

    private void go_to_modules() {
        Intent intent = new Intent(MakeNewEvent.this, Modules.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
