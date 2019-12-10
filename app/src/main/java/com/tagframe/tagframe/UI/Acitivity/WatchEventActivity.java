package com.tagframe.tagframe.UI.Acitivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.TaggedUserAdapter;
import com.tagframe.tagframe.Adapters.WatchEventListAdapter;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.FollowModel;
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
import com.veer.exvidplayer.VideoPlayer.ExVpListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tagframe.tagframe.UI.Acitivity.MakeNewEvent.Flag_Get_Tagged_User;
import static com.tagframe.tagframe.Utils.Utility.base_url;

public class WatchEventActivity extends AppCompatActivity implements Broadcastresults.Receiver {
    //controls
    private RelativeLayout root;
    private ImageButton ivNext, ivPrev, ivForword, ivRev, ivPlayPause, ivSetting;
    private CustomSeekBar mProgress;
    private TextView tvCurrent, tvTotal;
    private ExVpListener exVpControls;
    private FrameLayout frameLayout;
    TaggedUserAdapter taggedUserAdapter;
    //views
    private ScrollView bottomLayout;

    private ImageView ivTagUsers, ivAddFrame, ivPlayback, img_frame_to_show, img_play_video, img_done,
            img_like;
    private RecyclerView rvEventList;
    private LinearLayout ll_like, ll_share, llcomment, mLayout;
    private TextView mtxt_directive, tvEventTittle, tvStats, tvBuffering, tvUsername;
    private ProgressBar mPbar_events;
    private RelativeLayout.LayoutParams params;
    private RelativeLayout ll_dimer,mlayout;
    //vars
    private Point p = new Point();

    private ArrayList<Event_Model> event_modelArrayList = new ArrayList<>();
    private ArrayList<String> video_urls = new ArrayList<>();
    private ArrayList<String> video_type = new ArrayList<>();
    private String vidAddress, tittle, event_id, userName, user_id, shareLink, likevideo;
    private int likes, frames, comments;
    private ArrayList<FrameList_Model> framedata_map;
    private ArrayList<TaggedUserModel> list_tagged_user=new ArrayList<>();
    private ArrayList<TaggedUserModel> arrayListTag=new ArrayList<>();
    private ArrayList<FollowModel> followModelArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView list;
    private Broadcastresults mReceiver;
    private boolean shouldLoad = false;
    private int next_records = 0, currentListPosition = 0;
    private int frameSize;
    private TaggedUserModel taggedUserModel;
    private ProgressBar pbarLoadFrame;
    private boolean isFrameShowing = false;
    private Handler handler = new Handler();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_SETTINGS
    };
    private int currentDuration = 0;
    private AppPrefs userinfo;
    String tagurl = base_url+"/tagged_event";
    String taguserdb= base_url+"/get_tagged_users?event_id=";

    public List<TaggedUserModel> listWithUniqueValues1=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_watch_event);
        getIntentData(getIntent());
        init();
        setDatatoView();
        userinfo = new AppPrefs(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpPlayer();
        if (mProgress != null && currentDuration != mProgress.getMax()) {
            addTrackAndPlay(getIntent());
            if (exVpControls != null) exVpControls.seekToProgress(currentDuration);
            handler.postDelayed(runnable, 100);
        }
    }

    @Override
    protected void onPause() {
        currentDuration = mProgress.getProgress();
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
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
            @Override
            public void onClick(View view) {
                exVpControls.forward();
            }
        });
        ivRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exVpControls.reverse();
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exVpControls.nextTrack();
            }
        });
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exVpControls.previousTrack();
            }
        });
        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            @Override
            public void onClick(View view) {
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
        frameLayout = findViewById(R.id.parent);
        root = findViewById(R.id.root);
        ll_dimer = findViewById(R.id.dimer_layout);
        ivRev = findViewById(R.id.btn_rev);
        ivForword = findViewById(R.id.btn_fwd);
        ivNext = findViewById(R.id.btn_next);
        ivPrev = findViewById(R.id.btn_prev);
        ivPlayPause = findViewById(R.id.btn_pause);
        ivSetting = findViewById(R.id.btn_settings);
        mProgress = findViewById(R.id.seekbar);
        tvCurrent = findViewById(R.id.txt_currentTime);
        tvTotal = findViewById(R.id.txt_totalDuration);
        bottomLayout = findViewById(R.id.bottomLayout);
        mLayout = findViewById(R.id.activity_simple_player);

        mPbar_events = findViewById(R.id.pbar_events);

        ll_like = findViewById(R.id.lllike);
        ll_share = findViewById(R.id.llshare);
        llcomment = findViewById(R.id.llcomment);

        ivAddFrame = findViewById(R.id.add_frame);
        ivTagUsers = findViewById(R.id.taguser_event);
        img_done = findViewById(R.id.donewatching);
        img_like = findViewById(R.id.imglike);
        taggedUserAdapter= new TaggedUserAdapter(list_tagged_user,this);

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

        mtxt_directive = findViewById(R.id.txt_like_directive);

        ivAddFrame.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
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
            @Override
            public void onClick(View v) {
                //show_tagged_user();

//                list_tagged_user.clear();
                gettagedusers(event_id);
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvUsername = findViewById(R.id.txt_more_from);
        tvEventTittle = findViewById(R.id.txt_event_tittle);
        tvStats = findViewById(R.id.txt_stats);

        img_frame_to_show = findViewById(R.id.img_frame_to_show);
        img_play_video = findViewById(R.id.img_play_video);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 0;
        img_frame_to_show.setLayoutParams(params);
        img_play_video.setLayoutParams(params);
        setUpEventList();
        img_frame_to_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = img_frame_to_show.getTag().toString();
                Integer pos = Integer.parseInt(TAG);

                show_synced_frame(pos);
            }
        });

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                exVpControls.seekToProgress(seekBar.getProgress());
            }
        });

    }
    private void gettagedusers(String event_id) {
        listWithUniqueValues1.clear();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tagged_users);

        TextView txt_message = dialog.findViewById(R.id.txt_no_message_tagged_user);
      //  TextView tvCancel= (TextView)dialog.findViewById(R.id.tv_cancel);

        list = dialog.findViewById(R.id.list_tagged_users);
        LinearLayoutManager llayoutManager = new LinearLayoutManager(this);
        // RecyclerView.LayoutManager llayoutManager = new LinearLayoutManager(this);

        final TaggedUserAdapter taggedUserAdapter=new TaggedUserAdapter(list_tagged_user, this);
        list.setLayoutManager(llayoutManager);

        if (list_tagged_user.size() > 0) {
            txt_message.setVisibility(View.GONE);
        }
        // list_tagged_user.clear();
          list.setAdapter(taggedUserAdapter);

        // list_tagged_user.clear();
        StringRequest str= new StringRequest(Request.Method.GET, taguserdb+event_id, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object =new JSONObject(response);
                    JSONArray array= object.getJSONArray("taggedUsers");
                    for (int i=0;i<array.length();i++)
                    {
                        // list_tagged_user.clear();9
                        JSONObject object1= (JSONObject)array.get(i);
                        String name= object1.getString("name");
                        String userid= object1.getString("user_id");
                        String profilepic = object1.getString("profile_pic");
                        taggedUserModel= new TaggedUserModel(name, userid, profilepic);
                        // list_tagged_user.clear();
                        //  list_tagged_user.trimToSize();
                        //  HashSet<TaggedUserModel> setWithUniqueValues = new HashSet<>(list_tagged_user);
                        // listWithUniqueValues1= new ArrayList<>();


                        listWithUniqueValues1.add(taggedUserModel);
                        list.setAdapter(taggedUserAdapter);
                        taggedUserAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppSingleton.getInstance().addToRequestQueue(str);

        list.setAdapter(taggedUserAdapter);

        TextView txt = dialog.findViewById(R.id.txt_tag_add);
        txt.setVisibility(View.VISIBLE);


        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WatchEventActivity.this, SearchUserActivity.class);
                intent.putExtra("operation", Utility.operation_onclicked_tagged_user);
                startActivityForResult(intent, Flag_Get_Tagged_User);
                dialog.dismiss();

            }
        });



//        tvCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });

        dialog.findViewById(R.id.txt_tag_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list_tagged_user.clear();
                taguser();
                dialog.cancel();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                exVpControls.play();
            }
        });

        dialog.show();
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private String get_tagged_user_in_json() {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray tag_array = new JSONArray();

            // List<String> listWithDuplicates; // Your list containing duplicates
            HashSet<TaggedUserModel> setWithUniqueValues = new HashSet<>(list_tagged_user);

            HashSet<TaggedUserModel> setWithUniqueValues1 = new HashSet<>(listWithUniqueValues1);

            List<TaggedUserModel> listWithUniqueValues = new ArrayList<>(setWithUniqueValues);

            ArrayList<TaggedUserModel> listWithUnique = new ArrayList<>(setWithUniqueValues1);

            List <TaggedUserModel>newlist= intersection(listWithUniqueValues,listWithUnique);
            Log.d("newlistinter","newlisttag"+newlist);

            if(listWithUniqueValues.size()> listWithUnique.size()){
                for (int i = 0; i < newlist.size(); i++) {
//                if(list_tagged_user.size()>0)
//                    list_tagged_user.remove(i);
                    tag_array.put(newlist.get(i).getUser_id());
                }

                jsonObject.put("tag_array", tag_array);
                Log.e("json", jsonObject.toString());
                return jsonObject.toString();

//                }
//                else {
//                    tag_array.put(list_tagged_user.get(i).getUser_id());
//
//                }
                //list_tagged_user.remove(i);

            }
            else{
//                Toast.makeText(this, "Same users can not be tag again", Toast.LENGTH_SHORT).show();
                return "";

            }
//

        } catch (JSONException E) {
            E.printStackTrace();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T> List<TaggedUserModel> intersection(List<TaggedUserModel> list1, List<TaggedUserModel> list2) {
        List<T> list =  new ArrayList<T>();


        Set<String> ids = list2.stream().map(TaggedUserModel::getUser_id).collect(Collectors.toSet());
        List<TaggedUserModel> parentBooks = list1.stream().filter(book -> !ids.contains(book.getUser_id())).collect(Collectors.toList());
        System.out.println("parlis"+parentBooks.toString());

        return parentBooks;

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
        rvEventList = findViewById(R.id.list_event_users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvEventList.setLayoutManager(linearLayoutManager);
        rvEventList.setAdapter(new WatchEventListAdapter(this, event_modelArrayList));

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

        TextView txt_message = dialog.findViewById(R.id.txt_no_message_tagged_user);

        list = dialog.findViewById(R.id.list_tagged_users);
        LinearLayoutManager llayoutManager = new LinearLayoutManager(this);
        // RecyclerView.LayoutManager llayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(llayoutManager);

        if (list_tagged_user.size() > 0) {
            txt_message.setVisibility(View.GONE);
        }
        list.setAdapter(taggedUserAdapter);



//        list.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), list, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                //loadUserFollowing();
//                Toast.makeText(WatchEventActivity.this, "is selected", Toast.LENGTH_SHORT).show();
//               Intent loadmodule = new Intent(WatchEventActivity.this , Modules.class);
//               loadmodule.putExtra("frgToLoad" , ProfileOld.class);
//                User user = userinfo.getUser();
//
//                userinfo.putUser(user);
//
//               startActivity(loadmodule);
//
//
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));


        TextView txt = dialog.findViewById(R.id.txt_tag_add);
        txt.setVisibility(View.VISIBLE);


        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WatchEventActivity.this, SearchUserActivity.class);
                intent.putExtra("operation", Utility.operation_onclicked_tagged_user);
                startActivityForResult(intent, Flag_Get_Tagged_User);
                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.txt_tag_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taguser();
                dialog.cancel();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                exVpControls.play();
            }
        });

        dialog.show();
    }

    private void taguser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tagurl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("status");
                    if(msg.equals ("success")){
                        Toast.makeText (WatchEventActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent (WatchEventActivity.this, Modules.class);
                        startActivity (i);
                    }
                    else{
                        Toast.makeText (WatchEventActivity.this,"",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(WatchEventActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
         {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("event_id", event_id);
                params.put("tagged_user_id", get_tagged_user_in_json());
                return params;
            }
        };
        AppSingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void loadUserEvents() {
        if (Networkstate.haveNetworkConnection(this)) {
            mPbar_events.setVisibility(View.VISIBLE);
            ApiInterface retrofitService = ApiClient.getClient().create(ApiInterface.class);
            retrofitService.getUserEvents(user_id, String.valueOf(next_records))
                    .enqueue(new Callback<ListResponseModel>() {
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

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
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

            //VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
            final ImageView frameimage = dialog.findViewById(R.id.framelist_image);
            ImageView delete = dialog.findViewById(R.id.framelist_delete);
            ImageView product_image = dialog.findViewById(R.id.product_image);

            TextView duration = dialog.findViewById(R.id.framelist_time);
            final EditText tittle = dialog.findViewById(R.id.framelist_name);

            TextView tvaddproduct = dialog.findViewById(R.id.add_product);
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
                @Override
                public void onClick(View v) {
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

            final VideoView framevideo = dialog.findViewById(R.id.framelist_video);
            final RelativeLayout coverLayout = dialog.findViewById(R.id.cover);
            final ProgressBar progressBar = dialog.findViewById(R.id.pbar_video_dialog);

            ImageView delete = dialog.findViewById(R.id.framelist_delete);
            ImageView product_image = dialog.findViewById(R.id.product_image);

            TextView duration = dialog.findViewById(R.id.framelist_time);
            final EditText tittle = dialog.findViewById(R.id.framelist_name);

            TextView tvaddproduct = dialog.findViewById(R.id.add_product);
            tvaddproduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    @Override
                    public void onPrepared(MediaPlayer mp) {
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
                @Override
                public void onClick(View v) {
                    exVpControls.play();
                    dialog.cancel();
                }
            });
            img_frame_to_show.setVisibility(View.GONE);
            img_play_video.setVisibility(View.GONE);

            //video controls
            final SeekBar seekBar = dialog.findViewById(R.id.seekbar_dialog);

            final TextView current = dialog.findViewById(R.id.dialog_txttotalduration);
            final TextView total = dialog.findViewById(R.id.dialog_txtcurrentduration);
            final ImageButton play_stop = dialog.findViewById(R.id.dialog_btn_play_stop);

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
                    int progress = Utility.getProgressPercentage(currentDuration, totalDurationn);

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

    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private WatchEventActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final WatchEventActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //updateProgressBar();

        // if (requestCode == Flag_select_video && resultCode == RESULT_OK) {
        //  selectedImageUri = data.getData();

        // OI FILE Manager
        // String filemanagerstring = selectedImageUri.getPath();

        // MEDIA GALLERY
//            final String selectedImagePath = GetPaths.getPath(WatchEventActivity.this, selectedImageUri);
//            if (selectedImagePath != null) {
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        set_frames_to_container(selectedImagePath, Utility.frametype_video);
//                    }
//                }, 1000);
//            }
//        } else if (requestCode == 901 && data != null) {
//            ArrayList<String> mPath = data.getStringArrayListExtra(com.veer.multiselect.Util.Constants.GET_PATHS);
//
//            for (int z = 0; z < mPath.size(); z++) {
//
//                set_frames_to_container(mPath.get(z), Utility.frametype_video);
//            }
//            //Your Code
//        } else if (requestCode == 902 && resultCode == RESULT_OK) {
//            selectedImageUri = data.getData();
//            // OI FILE Manager
//            String filemanagerstring = selectedImageUri.getPath();
//            // MEDIA GALLERY
//            final String selectedImagePath = GetPaths.getPath(MakeNewEvent.this, selectedImageUri);
//            if (selectedImagePath != null) {
//                set_frames_to_container(selectedImagePath, Utility.frametype_video);
//
//            }
//        }

//    if (requestCode == 903 && resultCode == RESULT_OK) {
//      selectedImageUri = data.getData();
//      // OI FILE Manager
//      String filemanagerstring = selectedImageUri.getPath();
//      // MEDIA GALLERY
//      final String selectedImagePath = GetPaths.getPath(MakeNewEvent.this, selectedImageUri);
//      if (selectedImagePath != null) {
//        new Handler().postDelayed(new Runnable() {
//          @Override public void run() {
//            mediaPlayer.pause();
//            set_frames_to_container(selectedImagePath, Utility.frametype_image);
//          }
//        }, 1000);
//      }
//    }

//        if (requestCode == 903 && resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            img_frame_to_show.setImageBitmap(photo);
////            knop.setVisibility(Button.VISIBLE);
//
//            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//            Uri tempUri = getImageUri(getApplicationContext(), photo);
//
//            // CALL THIS METHOD TO GET THE ACTUAL PATH
//            File finalFile = new File(getRealPathFromURI(tempUri));
//
//            if (finalFile != null) {
//                set_frames_to_container(finalFile.toString(), Utility.frametype_image);
//
//            }
//
//
//            // System.out.println(mImageCaptureUri);
//
//        } else if (requestCode == Flag_pick_photo && resultCode == RESULT_OK) {
//            selectedImageUri = data.getData();
//
//            // OI FILE Manager
//            //                String filemanagerstring = selectedImageUri.getPath();
//
//            // MEDIA GALLERY
//            String selectedImagePath = GetPaths.getPath(MakeNewEvent.this, selectedImageUri);
//            if (selectedImagePath != null) {
//                set_frames_to_container(selectedImagePath, Utility.frametype_image);
//            }
//        } else if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
//            ArrayList<String> images = data.getStringArrayListExtra(com.veer.multiselect.Util.Constants.GET_PATHS);
//            for (int i = 0; i < images.size(); i++) {
//                set_frames_to_container(images.get(i), Utility.frametype_image);
//            }
//        } else if (requestCode == Flag_product_list_result && data != null) {
//
//            int position = Utility.framepostion;
//
//            //make change to the frame to which product has been added
//            FrameList_Model frameList_model = framedata_map.get(position);
//            frameList_model.setProduct_id(data.getStringExtra("product_id"));
//            frameList_model.setProduct_path(data.getStringExtra("product_image"));
//            frameList_model.setProduct_url(data.getStringExtra("product_url"));

        //show the frame
        //show_synced_frame(position);
//        } else if (requestCode == FLAG_ADD_PRODUCT && data != null) {
//
//            FrameList_Model frameList_model = new FrameList_Model();
//            frameList_model.setFrametype(Utility.frametype_image);
//            frameList_model.setName(data.getStringExtra("product_name"));
//
//            frameList_model.setFrame_resource_type(Utility.frame_resource_type_local);
//
//            frameList_model.setImagepath(data.getStringExtra("product_image"));
//            frameList_model.setFrame_image_url(data.getStringExtra("product_image"));
//            frameList_model.setProduct_id(data.getStringExtra("product_id"));
//            frameList_model.setProduct_path(data.getStringExtra("product_image"));
//            frameList_model.setProduct_url(data.getStringExtra("product_url"));
//
//            frameList_model.setStarttime((int) 0);
//            frameList_model.setEndtime((int) (0));
//            frameList_model.setAProductFrame(true);
//
//            if (addframe(frameList_model, (int) totalduration)) {
//                if (count == 0) {
//                    if (appPrefs.getInt(Utility.isTutShownForEvent) == Utility.tutNotShown) {
//                        start_tut("Synch a Frame by holding it and moving it up");
//                        count++;
//                        appPrefs.putInt(Utility.isTutShownForEvent, Utility.tutShown);
//                    }
//                }
//            } else {
//                MyToast.popmessage("Frame is already attached to this duration", this);
//            }
//            updateProgressBar();
//        } else

        if (requestCode == Flag_Get_Tagged_User && data != null) {
            TaggedUserModel taggedUserModel = data.getParcelableExtra("tagged_user");
            if (searchForDuplicate(taggedUserModel)) {
              //  PopMessage.makesimplesnack(mlayout, "User already added");
            } else {
                list_tagged_user.add(taggedUserModel);
                 show_tagged_user();
            }
            //  pbar_mediaplayer.setVisibility(View.GONE);
            // }
            //}


        }
    }

    private boolean searchForDuplicate(TaggedUserModel taggedUserModel) {
        // pbar_mediaplayer.setVisibility(View.VISIBLE);
        for (int i = 0; i < list_tagged_user.size(); i++) {
            if (taggedUserModel.getUser_id().equals(list_tagged_user.get(i).getUser_id()))
                // list_tagged_user.remove(i);
                return true;
        }
        return false;
    }
}