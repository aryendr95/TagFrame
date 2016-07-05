package com.tagframe.tagframe.UI.Acitivity;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.Menulistadapter;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.UI.Fragments.CreateEvent;
import com.tagframe.tagframe.UI.Fragments.Follow;
import com.tagframe.tagframe.UI.Fragments.Notifications;
import com.tagframe.tagframe.UI.Fragments.Profile;
import com.tagframe.tagframe.UI.Fragments.TagStream;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.ViewAnimationUtils;
import com.tagframe.tagframe.Utils.listops;

import java.io.File;
import java.util.ArrayList;

public class Modules extends FragmentActivity implements Broadcastresults.Receiver {

    //Bottom bar
    private LinearLayout mlevent, mltagstream, mlnotification, mlfollow, mlprofile,bottombar,topbar;
    private TextView mtxttagstream,mtxtevent, mtxtnotification, mtxtfollow, mtxtprofile;
    private ImageView mimgtagstream,mimgevent, mimgnotification, mimgfollow, mimgprofile;

    //Framelayout
    private FrameLayout frameLayout;

    //topbar
    private ImageView mod_menu, mod_search;
    private EditText mod_search_text;

    //Sliding PANEL
    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;

    CircularImageView mod_pro_image;
    TextView mod_usrname, mod_email;


    private listops userinfo;

    private ImageView r_layout;


    private int PICK_VIDEO=1,TAKE_VIDEO=2;

    public Broadcastresults mReceiver;

    private EditText ed_search;
    private ImageView img_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        userinfo=new listops(this);
        init();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    private void init() {

        mltagstream = (LinearLayout) findViewById(R.id.mod_tagstream);
        mltagstream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mtxttagstream.setTextColor(getResources().getColor(R.color.white));
                mimgtagstream.setImageResource(R.drawable.tagstream_hover);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtfollow.setTextColor(getResources().getColor(R.color.light_gray));
                mimgfollow.setImageResource(R.drawable.followers);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);


               /* mltagstream.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlfollow.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/


                TagStream fr = new TagStream();
                changefragment(fr);


            }
        });


        mlnotification = (LinearLayout) findViewById(R.id.mod_notification);
        mlnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.white));
                mimgnotification.setImageResource(R.drawable.nofication_hover);

                mtxtfollow.setTextColor(getResources().getColor(R.color.light_gray));
                mimgfollow.setImageResource(R.drawable.followers);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);
                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);


              /* mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlfollow.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                Notifications fr = new Notifications();
                changefragment(fr);

            }
        });


       mlevent = (LinearLayout) findViewById(R.id.mod_event);
        mlevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtfollow.setTextColor(getResources().getColor(R.color.light_gray));
                mimgfollow.setImageResource(R.drawable.followers);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);

                mtxtevent.setTextColor(getResources().getColor(R.color.white));

                mimgevent.setImageResource(R.drawable.event_ho);
                /*mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlfollow.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                //CreateEvent createEvent=new CreateEvent();
                //changefragment(createEvent);
                generate_media_chooser(mlevent);

            }
        });


        mlfollow = (LinearLayout) findViewById(R.id.mod_follow);
        mlfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtfollow.setTextColor(getResources().getColor(R.color.white));
                mimgfollow.setImageResource(R.drawable.followers_hover);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);
                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);

               /* mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlfollow.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/


                Follow follow = new Follow();

                changefragment(follow);
            }
        });

        mlprofile = (LinearLayout) findViewById(R.id.mod_profile);
        mlprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtfollow.setTextColor(getResources().getColor(R.color.light_gray));
                mimgfollow.setImageResource(R.drawable.followers);

                mtxtprofile.setTextColor(getResources().getColor(R.color.white));
                mimgprofile.setImageResource(R.drawable.user_hover);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);


               /* mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlfollow.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                Profile fr = new Profile();
                changefragment(fr);

            }
        });


        //imageview and textview

        mtxttagstream = (TextView) findViewById(R.id.mod_text_tagstream);
        mimgtagstream = (ImageView) findViewById(R.id.mod_img_tagsteam);

        mtxtnotification = (TextView) findViewById(R.id.mod_text_notification);
        mimgnotification = (ImageView) findViewById(R.id.mod_img_notification);

        mtxtevent = (TextView) findViewById(R.id.mod_text_event);
        mimgevent = (ImageView) findViewById(R.id.mod_img_event);

        mtxtfollow = (TextView) findViewById(R.id.mod_text_follow);
        mimgfollow = (ImageView) findViewById(R.id.mod_img_follow);

        mtxtprofile = (TextView) findViewById(R.id.mod_text_profile);
        mimgprofile = (ImageView) findViewById(R.id.mod_img_profile);


        frameLayout = (FrameLayout) findViewById(R.id.mod_frame_layout);




        Fragment myf=new TagStream();
        //checking if tagstream list has value else show the follow fragment
        if(userinfo.gettagstreamlist("tagstream").size()==0) {
            myf = new Follow();
            mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
            mimgtagstream.setImageResource(R.drawable.tagstream);

            mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
            mimgnotification.setImageResource(R.drawable.nofication);

            mtxtfollow.setTextColor(getResources().getColor(R.color.white));
            mimgfollow.setImageResource(R.drawable.followers_hover);

            mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
            mimgprofile.setImageResource(R.drawable.user);

        }

        if(getIntent() != null && getIntent().getExtras() != null)

        {
            if (getIntent().getStringExtra("name").equals("Edit Profile")) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtfollow.setTextColor(getResources().getColor(R.color.light_gray));
                mimgfollow.setImageResource(R.drawable.followers);

                mtxtprofile.setTextColor(getResources().getColor(R.color.white));
                mimgprofile.setImageResource(R.drawable.user_hover);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);
                myf = new Profile();

            }
        }



        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mod_frame_layout, myf);
        transaction.commit();


        //TOPBAR AND SLDING PANEL

        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);

        mSlidingPanel.setSliderFadeColor(Color.parseColor("#00FFFFFF"));
        mSlidingPanel.setShadowResource(R.drawable.slider_shadow);
        mSlidingPanel.setPanelSlideListener(panelListener);
        mMenuList = (ListView) findViewById(R.id.mod_MenuList);

        r_layout=(ImageView)findViewById(R.id.img);



        String [] MenuTitles = new String[]{"Account","Viewer Privacy","Terms of Service","Logout"};

       ArrayList<com.tagframe.tagframe.Models.Menu> list= populatelist();
        mMenuList.setAdapter(new Menulistadapter(this, list));

        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mSlidingPanel.isOpen()) {
                    switch (position) {
                        case 0:

                            final Intent intent = new Intent(Modules.this, Menu_Action.class);
                            intent.putExtra("name", "Account");
                            intent.putExtra("layout", position);
                            startActivity(intent);
                            finish();

                            break;
                        case 1:

                            Intent intent1 = new Intent(Modules.this, Menu_Action.class);
                            intent1.putExtra("name", "Viewer Privacy");
                            intent1.putExtra("layout", position);
                            startActivity(intent1);
                            finish();
                            break;
                        case 2:

                            Intent intent11 = new Intent(Modules.this, Menu_Action.class);
                            intent11.putExtra("name", "Terms of Service");
                            intent11.putExtra("layout", position);
                            startActivity(intent11);
                            finish();
                            break;
                        case 3:
                            mSlidingPanel.closePane();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final Dialog dialog = new Dialog(Modules.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_logout);
                                    dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            listops listops = new listops(Modules.this);

                                            listops.putString(Constants.loginstatuskey, "");
                                            Constants.flushuserinfo(Modules.this);
                                            Intent intent2 = new Intent(Modules.this, Authentication.class);
                                            startActivity(intent2);
                                            finish();
                                        }
                                    });
                                    dialog.findViewById(R.id.nobtn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            }, 200);


                            break;
                    }
                }


            }

        });

        mMenuList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mSlidingPanel.isOpen()) {
                    return frameLayout.dispatchTouchEvent(event);
                } else {
                    return false;
                }
            }
        });


        mod_menu = (ImageView) findViewById(R.id.mod_menu);
        mod_search = (ImageView) findViewById(R.id.mod_search);

        mod_search_text = (EditText) findViewById(R.id.mod_search_text);

        mod_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlidingPanel.isOpen()) {
                    mSlidingPanel.closePane();
                } else {
                    mSlidingPanel.openPane();
                }
            }
        });

        mod_pro_image = (CircularImageView) findViewById(R.id.mod_profile_image);
        try {
            Picasso.with(this).load(userinfo.getString(Constants.user_pic)).error(R.drawable.pro_image).into(mod_pro_image);
            Picasso.with(this).load(userinfo.getString(Constants.user_pic)).error(R.color.colorPrimaryDark).into(r_layout);
        }
        catch (Exception e)
        {
            mod_pro_image.setImageResource(R.drawable.pro_image);
            r_layout.setImageResource(R.drawable.pro_image);
        }
        mod_usrname = (TextView) findViewById(R.id.mod_usrname);
        mod_usrname.setText(userinfo.getString(Constants.user_name));

        mod_email = (TextView) findViewById(R.id.mod_email);
        mod_email.setText(userinfo.getString(Constants.user_email));

        bottombar=(LinearLayout)findViewById(R.id.bottom_bar);
        topbar=(LinearLayout)findViewById(R.id.topbar);


        ed_search=(EditText)findViewById(R.id.mod_search_text);
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(!ed_search.getText().toString().isEmpty()) {
                        Follow follow = new Follow();
                        Bundle bundle = new Bundle();
                        bundle.putString("keyword", ed_search.getText().toString());
                        follow.setArguments(bundle);
                        hideKeyboard(Modules.this);

                        changefragment(follow);
                        return true;
                    }
                    else
                    {
                        MyToast.popmessage("Please provide a search term",Modules.this);
                    }
                }
                return false;
            }
        });

        img_search=(ImageView)findViewById(R.id.mod_search);
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ed_search.getText().toString().isEmpty()) {
                    Follow follow = new Follow();
                    Bundle bundle = new Bundle();
                    bundle.putString("keyword", ed_search.getText().toString());
                    follow.setArguments(bundle);
                    hideKeyboard(Modules.this);
                    changefragment(follow);

                }
                else
                {
                    MyToast.popmessage("Please provide a search term",Modules.this);
                }
            }
        });






    }

    private ArrayList<com.tagframe.tagframe.Models.Menu> populatelist() {
        String [] MenuTitles = new String[]{"Account","Viewer Privacy","Terms of Service","Logout"};
        ArrayList<com.tagframe.tagframe.Models.Menu> menuArrayList=new ArrayList<>();
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Account",R.drawable.account));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Viewer Privacy",R.drawable.privacy));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Terms of Service",R.drawable.terms_of_service));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Logout",R.drawable.logout));
        return menuArrayList;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onlistscroll(int visibilty,View v)
    {
        if(visibilty==View.GONE)
        {
            bottombar.setVisibility(View.GONE);
            v.setVisibility(View.GONE);


        }
        else
        {
            bottombar.setVisibility(View.VISIBLE);
            v.setVisibility(View.VISIBLE);
        }
       // topbar.setVisibility(visibilty);
       // bottombar.setVisibility(visibilty);


    }



    public void generate_media_chooser(View v) {

       /* PopupMenu popup = new PopupMenu(this, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.pop_menu_media_choseer, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.media_select_video:

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("video/*");

                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);

                        break;

                    case R.id.media_video:
                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.
                        Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);



                        // start the Video Capture Intent
                        startActivityForResult(intent1, TAKE_VIDEO);

                        break;

                    case R.id.saved_events:
                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.
                        Intent intent2 = new Intent(Modules.this,SavedEvents.class);



                        // start the Video Capture Intent
                        startActivity(intent2);
                        finish();

                        break;

                }
                return true;
            }
        });

        popup.show();*/  //showing popup menu

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_media_chooser);

        // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
        dialog.findViewById(R.id.media_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
            }
        });
        dialog.findViewById(R.id.media_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);



                // start the Video Capture Intent
                startActivityForResult(intent1, TAKE_VIDEO);

            }
        });
        dialog.findViewById(R.id.media_saved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Modules.this,SavedEvents.class);



                // start the Video Capture Intent
                startActivity(intent2);
                finish();
            }
        });


        dialog.show();
    }




    SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener() {

        @Override
        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub        getActionBar().setTitle(getString(R.string.app_name));
            mod_menu.animate().rotation(0);
        }

        @Override
        public void onPanelOpened(View arg0) {
            // TODO Auto-generated method stub

            mod_menu.animate().rotation(90);
        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {
            // TODO Auto-generated method stub

        }

    };




    public void setprofile(String user_id,int user_type)
    {
        Profile fr=new Profile();
        Bundle bundle=new Bundle();
        bundle.putString("user_id",user_id);
        bundle.putInt("type", user_type);
        fr.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        transaction.replace(R.id.mod_frame_layout, fr);

        transaction.commit();
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
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

    private void changefragment(Fragment fr) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        transaction.replace(R.id.mod_frame_layout, fr);

        transaction.commit();
    }

    public Broadcastresults register_reviever()
    {
        mReceiver = new Broadcastresults(new Handler());

        mReceiver.setReceiver(this);

        return mReceiver;

    }

    public void setprofileparameter(int s,String v)
    {
        Profile profile=(Profile)getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
        profile.setprofilestat(s, v);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

       int operation=resultData.getInt("operation");
        String userid="";
        switch (operation)
        {
            case Constants.operation_remove_follower:

                 userid=resultData.getString("user_id");
                if(resultCode==1)
                {

                    MyToast.popmessage("Successfully Removed",this);
                }
                else
                {
                    setprofile(userid,Constants.user_type_self);
                    MyToast.popmessage("There was error removing this user", this);
                }
                break;
            case Constants.operation_unfollow:

                userid=resultData.getString("user_id");
                if(resultCode==1)
                {

                    MyToast.popmessage("Successfully UnFollowed",this);
                }
                else
                {
                    setprofile(userid,Constants.user_type_self);
                    MyToast.popmessage("There was error unfollowing this user", this);
                }
                break;
            case Constants.operation_follow:

                userid=resultData.getString("user_id");
                if(resultCode==1)
                {

                    MyToast.popmessage("Successfully Followed",this);
                }
                else
                {
                    setprofile(userid,Constants.user_type_self);
                    MyToast.popmessage("There was error unfollowing this user", this);
                }
                break;

            case Constants.operation_follow_profile:

                Fragment f =getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
                if (f instanceof Profile)
                    // do something with f
                    ((Profile) f).changeprofile_ui(operation,resultCode);

                break;
            case Constants.operation_unfollow_profile:

                Fragment f1 =getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
                if (f1 instanceof Profile)
                    // do something with f
                    ((Profile) f1).changeprofile_ui(operation,resultCode);

                break;
            case Constants.operation_unlike:

                if(resultCode==1)
                {

                    MyToast.popmessage("Post Unliked",this);
                }
                else
                {

                    MyToast.popmessage("ss ", this);
                }


                break;

            case Constants.operation_like:

                if(resultCode==1)
                {

                    MyToast.popmessage("Post liked",this);
                }
                else
                {

                    MyToast.popmessage("ss ", this);
                }

                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = GetPaths.getPath(Modules.this,selectedImageUri);

                if (selectedImagePath != null) {

                    Intent intent = new Intent(this,
                            MakeNewEvent.class);

                    intent.putExtra("data_url", selectedImagePath);
                    //intent.putExtra("tittle",tittle);
                    //intent.putExtra("des",des);
                    //intent.putExtra("type",type);
                    intent.putExtra("eventtype", Constants.eventtype_local);
                    startActivity(intent);
                }
            }
            else if(requestCode==TAKE_VIDEO)
            {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                //String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = GetPaths.getPath(Modules.this, selectedImageUri);
                if (selectedImagePath != null) {

                    Intent intent = new Intent(this,
                            MakeNewEvent.class);
                    intent.putExtra("data_url", selectedImagePath);
                    intent.putExtra("eventtype", Constants.eventtype_local);
                   // intent.putExtra("tittle",tittle);
                   // intent.putExtra("des",des);
                    //intent.putExtra("type",type);
                    startActivity(intent);
                }
            }
        }



    }


}
