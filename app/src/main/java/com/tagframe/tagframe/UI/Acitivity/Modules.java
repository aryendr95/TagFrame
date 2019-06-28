package com.tagframe.tagframe.UI.Acitivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.Menulistadapter;
import com.tagframe.tagframe.Adapters.ProductRecyclerAdapter;
import com.tagframe.tagframe.Adapters.SearchAdapter;
import com.tagframe.tagframe.Application.TagFrame;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.Models.ProfileResponseModel;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.UI.Fragments.Follow;
import com.tagframe.tagframe.UI.Fragments.MarketPlaceFragment;
import com.tagframe.tagframe.UI.Fragments.Notifications;
import com.tagframe.tagframe.UI.Fragments.Profile;
//import com.tagframe.tagframe.UI.Fragments.ProfileCollapsing;
import com.tagframe.tagframe.UI.Fragments.ProfileOld;
import com.tagframe.tagframe.UI.Fragments.TagStream;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Edittext;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.PopMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Modules extends FragmentActivity implements Broadcastresults.Receiver {
    //Bottom bar
    private LinearLayout mlevent, mltagstream, mlnotification, mlmarket, mlprofile, bottombar, topbar;
    RelativeLayout mLayout;
    private TextView mtxt_no_of_notifications, mtxttagstream, mtxtevent, mtxtnotification, mtxtmarket, mtxtprofile, mtxt_no_of_events, mtxt_no_of_followers, mtxt_no_of_following;
    private ImageView mimgtagstream, mimgevent, mimgnotification, mimgmarket, mimgprofile;
    //Framelayout
    private FrameLayout frameLayout;
    //topbar
    private ImageView mod_menu, mod_search;
    private EditText mod_search_text;
    //Sliding PANEL
    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    CircleImageView mod_pro_image;
    TextView mod_usrname, mod_email;
    private AppPrefs userinfo;
    private ImageView r_layout;
    private int PICK_VIDEO = 1, TAKE_VIDEO = 2;
    public Broadcastresults mReceiver;
    private EditText ed_search;
    private ImageView img_search;
    private String[] requiredStoragePermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private String[] requiredCameraPermissions = {Manifest.permission.CAMERA};
    private final int Request_Storage_permissions = 1;
    private final int Request_Camera_permissions = 2;

    private ListView recyclerView;
    private List<FollowModel> contactList;
    private ContactsAdapter mAdapter;
    private SearchView searchView;
    Edittext edittext;
    SearchAdapter searchAdapter;
    ProductRecyclerAdapter productAdapter;
    private List<Product> productList;
    private static final String URL = "http://thinksmartapp.com/TagFrame/webservice/search?user_id=";
    public static final String productURL = "http://thinksmartapp.com/TagFrame/webservice/search_product";
    public static final String profileURL = "http://thinksmartapp.com/TagFrame/webservice/profile?user_id=";
    RecyclerView produtsview;
    public static String strfollower, strfollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        recyclerView = findViewById(R.id.lv_users);
        produtsview = findViewById(R.id.frg_market_grid_product);
        edittext = (Edittext) findViewById(R.id.mod_search_text);
        contactList = new ArrayList<>();
        productList = new ArrayList<>();
        userinfo = new AppPrefs(this);
        searchAdapter = new SearchAdapter(Modules.this, contactList, 2, Utility.operation_onclicked_direct_follow);

//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        produtsview.setLayoutManager(mLayoutManager);
//       // recyclerView.setItemAnimator(new DefaultItemAnimator());
//        productAdapter = new ProductRecyclerAdapter(Modules.this, (ArrayList<Product>) productList);
//        produtsview.setAdapter(productAdapter);
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        produtsview.setLayoutManager(staggeredGridLayoutManager);
        produtsview.setAdapter(productAdapter);

        recyclerView.setAdapter(searchAdapter);
        init();
        fetchContacts(Utility.getUserId(this));
        loadUserInformation(Utility.getUserId(this));
        getProducts();

        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadUserInformation(Utility.getUserId(Modules.this));
                // fetchContacts("117");

//                if(edittext.getText().toString().length()==0 || edittext.getText().toString().isEmpty()){
//                    recyclerView.setVisibility(View.GONE);
//                }
//                else {
//                    searchAdapter.getFilter().filter(charSequence.toString());
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // fetchContacts("117");
                if (checkCurrentFragment() instanceof MarketPlaceFragment) {
                    if (charSequence.toString().length() == 0) {
                        getProducts();
                        produtsview.setVisibility(View.GONE);
                    } else {
                        productAdapter.getFilter().filter(charSequence.toString());
                        produtsview.setVisibility(View.VISIBLE);
                    }
//                    if (charSequence.toString().length()>=3 && charSequence.toString().length() <=4) {
//                    MarketPlaceFragment marketPlaceFragment = new MarketPlaceFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("keyword", charSequence.toString());
//                    marketPlaceFragment.setArguments(bundle);
//                    hideKeyboard(Modules.this);
//                    //   ed_search.setText("");
//                    changefragment(marketPlaceFragment, "MarketPlace");
//                }
                } else {
                    if (charSequence.toString().length() == 0) {
                        fetchContacts(Utility.getUserId(Modules.this));
                        loadUserInformation(Utility.getUserId(Modules.this));
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        searchAdapter.getFilter().filter(charSequence.toString());
                        loadUserInformation(Utility.getUserId(Modules.this));
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // fetchContacts("117");

//                if(edittext.getText().toString().length()==0 ){
//                    recyclerView.setVisibility(View.GONE);
//                }
//                else {
//                    searchAdapter.getFilter().filter(editable.toString());
//                    recyclerView.setVisibility(View.VISIBLE);
//                }

            }
        });

        //  LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));

        Intent intent = getIntent();
        if (intent.hasExtra("name") && intent.hasExtra("user_id") && intent.hasExtra("pofile_pic")) {
            // Toast.makeText(this, "has extra", Toast.LENGTH_SHORT).show();
            String tname = intent.getStringExtra("name");
            String tuser_id = intent.getStringExtra("user_id");
            String tprofile_pic = intent.getStringExtra("pofile_pic");
            Fragment frr = Utility.getProfileFragment();
            Bundle tbundle = new Bundle();
            tbundle.putString("user_id", tuser_id);
            tbundle.putString("name", tname);
            tbundle.putString("profile_pic", tprofile_pic);
            frr.setArguments(tbundle);
            if (tname != null) {
                changefragment(frr, "Profile");
            } else {
                //Toast.makeText(this,  "no" + tname, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getProducts() {
        productList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, productURL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("records");
                        for (int i = 0; i <= jsonArray.length(); i++) {
                            JSONObject res = (JSONObject) jsonArray.get(i);
                            Product product = new Product();
                            product.setId(res.getString("product_id"));
                            product.setImage(res.getString("product_image"));
                            product.setName(res.getString("product_name"));
                            product.setProduct_price("product_price");
                            productList.add(product);
                            productAdapter = new ProductRecyclerAdapter(Modules.this, (ArrayList<Product>) productList);
                            // searchAdapter= new SearchAdapter(Modules.this,contactList);
                            produtsview.setAdapter(productAdapter);
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

        };
        AppSingleton.getInstance().addToRequestQueue(stringRequest);
    }

    private void fetchContacts(String userId) {
        contactList.clear();
        StringRequest request = new StringRequest(Request.Method.GET, URL + userId, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("userinfo");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject res = jsonArray.getJSONObject(i);
                            // if(res.getString("username").contains(params[0])){
                            FollowModel followModel = new FollowModel();
                            followModel.setUserid(res.getString("to_user_id"));
                            followModel.setImage(res.getString("image"));
                            followModel.setUser_name(res.getString("username"));
                            followModel.setFrom_user_id(userinfo.getString(Utility.user_id));
                            followModel.setIs_followed(res.getString("followed"));
                            // recyclerView.setAdapter(searchAdapter);
                            contactList.add(followModel);
                            recyclerView.setAdapter(new SearchAdapter(Modules.this, contactList, 2, Utility.operation_onclicked_direct_follow));
                            // searchAdapter= new SearchAdapter(Modules.this,contactList);
                            recyclerView.setAdapter(searchAdapter);
                            searchAdapter.notifyDataSetChanged();

                            //  }

                        }
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
        AppSingleton.getInstance().addToRequestQueue(request);
    }

    private void init() {

        mLayout = (RelativeLayout) findViewById(R.id.mLayout_module);
        mltagstream = (LinearLayout) findViewById(R.id.mod_tagstream);
        mltagstream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.white));
                mimgtagstream.setImageResource(R.drawable.tagstream_hover);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
                mimgmarket.setImageResource(R.drawable.market);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);

                /* mltagstream.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlmarket.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                Fragment f = getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
                if (f instanceof TagStream) {
                    ((TagStream) f).scrolltofirst();
                } else {
                    TagStream fr = new TagStream();
                    changefragment(fr, "TagStraem");
                }
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

                mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
                mimgmarket.setImageResource(R.drawable.market);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);

              /* mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlmarket.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                Notifications fr = new Notifications();
                changefragment(fr, "Notification");
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

                mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
                mimgmarket.setImageResource(R.drawable.market);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);

                mtxtevent.setTextColor(getResources().getColor(R.color.white));

                mimgevent.setImageResource(R.drawable.event_ho);

                /*mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlmarket.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/
                //CreateEvent createEvent=new CreateEvent();
                //changefragment(createEvent);

                generate_media_chooser(mlevent);
            }
        });

        mlmarket = (LinearLayout) findViewById(R.id.mod_market);
        mlmarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtmarket.setTextColor(getResources().getColor(R.color.white));
                mimgmarket.setImageResource(R.drawable.market_hover);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);
                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);

               /* mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlmarket.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.white));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                MarketPlaceFragment marketPlaceFragment = new MarketPlaceFragment();
                changefragment(marketPlaceFragment, "MarketPlace");
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

                mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
                mimgmarket.setImageResource(R.drawable.market);

                mtxtprofile.setTextColor(getResources().getColor(R.color.white));
                mimgprofile.setImageResource(R.drawable.user_hover);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);


               /* mltagstream.setBackgroundColor(getResources().getColor(R.color.white));
                mlnotification.setBackgroundColor(getResources().getColor(R.color.white));
                mlmarket.setBackgroundColor(getResources().getColor(R.color.white));
                mlprofile.setBackgroundColor(getResources().getColor(R.color.light_gray));
                mlevent.setBackgroundColor(getResources().getColor(R.color.white));*/

                changefragment(Utility.getProfileFragment(), "Profile");
            }
        });
        //imageview and textview

        mtxttagstream = (TextView) findViewById(R.id.mod_text_tagstream);
        mimgtagstream = (ImageView) findViewById(R.id.mod_img_tagsteam);
        mtxtnotification = (TextView) findViewById(R.id.mod_text_notification);
        mtxt_no_of_notifications = (TextView) findViewById(R.id.txt_mod_number_notifications);
        mimgnotification = (ImageView) findViewById(R.id.mod_img_notification);

        if (!userinfo.getString(Utility.unread_notifications).equals("0")) {
            mtxt_no_of_notifications.setText(userinfo.getString(Utility.unread_notifications));
        } else {
            mtxt_no_of_notifications.setVisibility(View.GONE);
        }

        mtxtevent = (TextView) findViewById(R.id.mod_text_event);
        mimgevent = (ImageView) findViewById(R.id.mod_img_event);

        mtxtmarket = (TextView) findViewById(R.id.mod_text_market);
        mimgmarket = (ImageView) findViewById(R.id.mod_img_market);

        mtxtprofile = (TextView) findViewById(R.id.mod_text_profile);
        mimgprofile = (ImageView) findViewById(R.id.mod_img_profile);

        mtxt_no_of_events = (TextView) findViewById(R.id.txt_mod_number_of_events);
        mtxt_no_of_followers = (TextView) findViewById(R.id.txt_mod_number_of_followers);
        mtxt_no_of_following = (TextView) findViewById(R.id.txt_mod_number_of_followings);

        mtxt_no_of_events.setText(userinfo.getString(Utility.total_events));
        //  AppPrefs.putString(Utility.total_events, userinfo.getString(Utility.total_events));
//        mtxt_no_of_following.setText(strfollowing);
//        mtxt_no_of_followers.setText(strfollower);
//        mtxt_no_of_following.setText(userinfo.getString(Utility.number_of_following));
//        mtxt_no_of_followers.setText(userinfo.getString(Utility.number_of_followers));

        frameLayout = (FrameLayout) findViewById(R.id.mod_frame_layout);
        Fragment myf = new TagStream();

        //checking if tagstream list has value else show the follow fragment
        if (userinfo.gettagstreamlist("tagstream").size() == 0) {
            myf = new Follow();
            mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
            mimgtagstream.setImageResource(R.drawable.tagstream);

            mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
            mimgnotification.setImageResource(R.drawable.nofication);

            mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
            mimgmarket.setImageResource(R.drawable.market);

            mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
            mimgprofile.setImageResource(R.drawable.user);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("name").equals("Edit Profile")) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.light_gray));
                mimgnotification.setImageResource(R.drawable.nofication);

                mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
                mimgmarket.setImageResource(R.drawable.followers);

                mtxtprofile.setTextColor(getResources().getColor(R.color.white));
                mimgprofile.setImageResource(R.drawable.user_hover);

                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);
                myf = Utility.getProfileFragment();

            } else if (getIntent().getStringExtra(Utility.NAVIGATE_TO).equals(Utility.NAVIGATE_TO_NOTIFICATION)) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.light_gray));
                mimgtagstream.setImageResource(R.drawable.tagstream);

                mtxtnotification.setTextColor(getResources().getColor(R.color.white));
                mimgnotification.setImageResource(R.drawable.nofication_hover);

                mtxtmarket.setTextColor(getResources().getColor(R.color.light_gray));
                mimgmarket.setImageResource(R.drawable.market);

                mtxtprofile.setTextColor(getResources().getColor(R.color.light_gray));
                mimgprofile.setImageResource(R.drawable.user);
                mtxtevent.setTextColor(getResources().getColor(R.color.light_gray));
                mimgevent.setImageResource(R.drawable.event);
                myf = new Notifications();
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

        r_layout = (ImageView) findViewById(R.id.img);

        String[] MenuTitles = new String[]{
                "Account", "My Endorsements", "Viewer Privacy", "Terms of Service", "Logout"
        };

        ArrayList<com.tagframe.tagframe.Models.Menu> list = populatelist();
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
                            Intent endorseIntent = new Intent(Modules.this, MyEndorsement.class);
                            startActivity(endorseIntent);
                            finish();
                            break;
                        case 2:
                            Intent intent1 = new Intent(Modules.this, Menu_Action.class);
                            intent1.putExtra("name", "Viewer Privacy");
                            intent1.putExtra("layout", position);
                            startActivity(intent1);
                            finish();
                            break;
                        case 3:
                            Intent intent11 = new Intent(Modules.this, Menu_Action.class);
                            intent11.putExtra("name", "Terms of Service");
                            intent11.putExtra("layout", position);
                            startActivity(intent11);
                            finish();
                            break;
                        case 4:
                            mSlidingPanel.closePane();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    final Dialog dialog = new Dialog(Modules.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(false);
                                    dialog.setContentView(R.layout.dialog_logout);
                                    final LinearLayout controls = (LinearLayout) dialog.findViewById(R.id.layout_logout_controls);
                                    final LinearLayout progress = (LinearLayout) dialog.findViewById(R.id.layout_logging_out);

                                    dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final AppPrefs listops = new AppPrefs(Modules.this);
                                            controls.setVisibility(View.GONE);
                                            progress.setVisibility(View.VISIBLE);

                                            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                            apiInterface.logout(listops.getString(Utility.user_id), AppSingleton.android_id)
                                                    .enqueue(new Callback<ResponsePojo>() {
                                                        @Override
                                                        public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {

                                                            if (response.body().getStatus().equals(Utility.success_response)) {
                                                                Utility.flushuserinfo(Modules.this);

                                                                Intent intent2 = new Intent(Modules.this, Authentication.class);
                                                                startActivity(intent2);
                                                                finish();
                                                                dialog.dismiss();
                                                            } else {
                                                                PopMessage.makeshorttoast(Modules.this, "Error Logging out..");
                                                                dialog.dismiss();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponsePojo> call, Throwable t) {
                                                            PopMessage.makeshorttoast(Modules.this, "Error Logging out..");
                                                            dialog.dismiss();
                                                        }
                                                    });
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

        //dispatching the touch top framelayout
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

        mod_pro_image = (CircleImageView) findViewById(R.id.mod_profile_image);
        try {
            Picasso.with(this).load(userinfo.getString(Utility.user_pic)).error(R.drawable.pro_image).into(mod_pro_image);
            Picasso.with(this).load(userinfo.getString(Utility.user_pic)).error(R.color.colorPrimaryDark).into(r_layout);
        } catch (Exception e) {
            mod_pro_image.setImageResource(R.drawable.pro_image);
            r_layout.setImageResource(R.drawable.pro_image);
        }


        mod_usrname = (TextView) findViewById(R.id.mod_usrname);
        mod_usrname.setText(userinfo.getString(Utility.user_name));

        mod_email = (TextView) findViewById(R.id.mod_email);
        mod_email.setText(userinfo.getString(Utility.user_email));

        bottombar = (LinearLayout) findViewById(R.id.bottom_bar);
        topbar = (LinearLayout) findViewById(R.id.topbar);

        ed_search = (EditText) findViewById(R.id.mod_search_text);
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!ed_search.getText().toString().isEmpty()) {
                        Search(ed_search.getText().toString());
                        return true;
                    } else {
                        PopMessage.makesimplesnack(mLayout, "Please provide a search term");
                    }
                }
                return false;
            }
        });

//        ed_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                Search(editable.toString());
//
//            }
//        });

        img_search = (ImageView) findViewById(R.id.mod_search);
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                    Intent intent = getIntent();
//                    Intent i =new Intent(Modules.this, SearchActivity.class);
//                    i.putExtra("user_id", intent.getStringExtra("user_id"));
//                    startActivity(i);
//
                // Search(ed_search.getText().toString());
            }
        });
    }

    private void Search(String keyword) {
        //  if (keyword.length()>=3 && keyword.length() <=4) {
        if (checkCurrentFragment() instanceof MarketPlaceFragment) {
            MarketPlaceFragment marketPlaceFragment = new MarketPlaceFragment();
            Bundle bundle = new Bundle();
            bundle.putString("keyword", keyword);
            marketPlaceFragment.setArguments(bundle);
            hideKeyboard(Modules.this);
            //   ed_search.setText("");
            changefragment(marketPlaceFragment, "MarketPlace");
        } else {
//                Follow follow = new Follow();
//                Bundle bundle = new Bundle();
//                bundle.putString("keyword", keyword);
//                follow.setArguments(bundle);
//                hideKeyboard(Modules.this);
//             //   ed_search.setText("");
//                changefragment(follow, "Follow");
            //  }
        }
    }

    private Fragment checkCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment mCurrentFragment = fragmentManager.findFragmentById(R.id.mod_frame_layout);
        return mCurrentFragment;
    }

    private ArrayList<com.tagframe.tagframe.Models.Menu> populatelist() {
        String[] MenuTitles = new String[]{"Account", "Viewer Privacy", "Terms of Service", "Logout"};
        ArrayList<com.tagframe.tagframe.Models.Menu> menuArrayList = new ArrayList<>();
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Account",
                R.drawable.ic_account_circle_grey_600_24dp));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("My Endorsements",
                R.drawable.ic_present_to_all_grey_600_24dp));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Viewer Privacy",
                R.drawable.ic_view_carousel_grey_600_24dp));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Terms of Service",
                R.drawable.ic_speaker_notes_grey_600_24dp));
        menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Logout", R.drawable.ic_exit_to_app_grey_600_24dp));
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

    public void onlistscroll(int visibilty, View v) {
        if (visibilty == View.GONE) {
            bottombar.setVisibility(View.GONE);
            v.setVisibility(View.GONE);
        } else {
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
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_media_chooser);

        // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
        dialog.findViewById(R.id.media_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askforPermission(requiredStoragePermissions, Request_Storage_permissions)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.media_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (askforPermission(requiredCameraPermissions, Request_Camera_permissions)) {
                    Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // start the Video Capture Intent
                    startActivityForResult(intent1, TAKE_VIDEO);
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.media_saved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Modules.this, SavedEvents.class);
                // start the Video Capture Intent
                startActivity(intent2);
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean askforPermission(String[] requiredPermissoion, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(requiredPermissoion[0]) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                requestPermissions(requiredPermissoion, requestCode);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("requestCode", requestCode + "");
        switch (requestCode) {
            case Request_Storage_permissions: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
                } else {
                    Toast.makeText(this, "Permission required to use videos as event", Toast.LENGTH_LONG)
                            .show();
                }
            }
            break;
            case Request_Camera_permissions: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // start the Video Capture Intent
                    startActivityForResult(intent1, TAKE_VIDEO);
                } else {
                    Toast.makeText(this, "Permission required to use video as event", Toast.LENGTH_LONG)
                            .show();
                }
            }
            break;
        }
    }

    SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener() {
        @Override
        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub
            //  getActionBar().setTitle(getString(R.string.app_name));
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

    public void setprofile(String user_id, int user_type) {
        loadUserInformation(user_id);
//        Fragment fr = Utility.getProfileFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("user_id", user_id);
//        bundle.putInt("type", user_type);
//        fr.setArguments(bundle);
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//        transaction.replace(R.id.mod_frame_layout, fr);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
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

    private void changefragment(Fragment fr, String str) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.mod_frame_layout, fr, str);
        transaction.addToBackStack(null);
        transaction.commit();
        if (fr instanceof Notifications) {
            mtxt_no_of_notifications.setVisibility(View.GONE);
        } else {
            mtxt_no_of_notifications.setVisibility(View.VISIBLE);
            int number = 0;
            try {
                number = Integer.parseInt(userinfo.getString(Utility.unread_notifications));
            } catch (NumberFormatException e) {
                number = 0;
            }
            if (number < 0) {
                number = 0;
                User user = userinfo.getUser();
                user.setUnreadnotifications(String.valueOf((number)));
                userinfo.putUser(user);
                userinfo.putString(Utility.unread_notifications, String.valueOf(number));
            }

            if (!(number >= 0)) {
                mtxt_no_of_notifications.setText(String.valueOf(number));
            } else {
                mtxt_no_of_notifications.setVisibility(View.GONE);
            }
        }
    }

    public Broadcastresults register_reviever() {
        mReceiver = new Broadcastresults(new Handler());
        mReceiver.setReceiver(this);
        return mReceiver;
    }

    public void setprofileparameter(int s, String v) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Profile profile = (Profile) getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
            profile.setprofilestat(s, v);
        } else {
            // do something for phones running an SDK before lollipop
            ProfileOld profile = (ProfileOld) getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
            profile.setprofilestat(s, v);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        int operation = resultData.getInt("operation");
        String userid = "";
        switch (operation) {
            case Utility.operation_remove_follower:
                userid = resultData.getString("user_id");
                if (resultCode == 1) {
                    MyToast.popmessage("Successfully Removed", this);
                    setprofile(userid, Utility.user_type_self);
                    // loadUserInformation();
                    this.recreate();
                } else {
                    setprofile(userid, Utility.user_type_self);
                    // MyToast.popmessage("There was error removing this user", this);
                }
                break;
            case Utility.operation_unfollow:
                userid = resultData.getString("user_id");
                if (resultCode == 1) {
                    setprofile(userid, Utility.user_type_followers);
                    // loadUserInformation();
                    MyToast.popmessage("Successfully UnFollowed", this);
                    this.recreate();
                } else {
                    setprofile(userid, Utility.user_type_self);
                    //MyToast.popmessage("There was error unfollowing this user", this);
                }
                break;
            case Utility.operation_follow:
                userid = resultData.getString("user_id");
                if (resultCode == 1) {
                    setprofile(userid, Utility.user_type_following);
                    // loadUserInformation();
                    MyToast.popmessage("Successfully Followed", this);
                    this.recreate();
                } else {
                    setprofile(userid, Utility.user_type_self);
                    // MyToast.popmessage("There was error unfollowing this user", this);
                }
                break;
            case Utility.operation_follow_profile:
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
                if (f instanceof Profile) {
                    ((Profile) f).changeprofile_ui(operation, resultCode);
                } else if (f instanceof ProfileOld) {
                    ((ProfileOld) f).changeprofile_ui(operation, resultCode);
                }
                break;
            case Utility.operation_unfollow_profile:
                Fragment f1 = getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
                if (f1 instanceof Profile)
                // do something with f
                {
                    ((Profile) f1).changeprofile_ui(operation, resultCode);
                } else if (f1 instanceof ProfileOld) {
                    ((ProfileOld) f1).changeprofile_ui(operation, resultCode);
                }
                break;
            case Utility.operation_unlike:
                if (resultCode == 1) {
                    MyToast.popmessage("Post Unliked", this);
                } else {
                    MyToast.popmessage("Error ", this);
                }
                break;
            case Utility.operation_like:
                if (resultCode == 1) {
                    MyToast.popmessage("Post liked", this);
                } else {
                    MyToast.popmessage("Error", this);
                }
                break;
            case Utility.operation_comment:
                if (resultCode == 1) {
                    PopMessage.makesimplesnack(mSlidingPanel, "Comment Successful");
                } else {
                    PopMessage.makesimplesnack(mSlidingPanel, "Handle Failure");
                }
                break;
            case Constants.operation_block:
                if (resultCode == 1) {
                    PopMessage.makeshorttoast(Modules.this, "Successful");
                    Fragment f2 = getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
                    if (f2 instanceof Profile)
                    // do something with f
                    {
                        ((Profile) f2).changeprofile_ui(operation, resultCode);
                    }
                }
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
                String selectedImagePath = GetPaths.getPath(Modules.this, selectedImageUri);

                if (selectedImagePath != null) {
                    Intent intent = new Intent(this, MakeNewEvent.class);
                    intent.putExtra("data_url", selectedImagePath);
                    //intent.putExtra("tittle",tittle);
                    //intent.putExtra("des",des);
                    //intent.putExtra("type",type);
                    intent.putExtra("eventtype", Utility.eventtype_local);
                    startActivity(intent);
                }
            } else if (requestCode == TAKE_VIDEO) {
                Uri selectedImageUri = data.getData();
                // OI FILE Manager
                //String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = GetPaths.getPath(Modules.this, selectedImageUri);
                if (selectedImagePath != null) {
                    Intent intent = new Intent(this, MakeNewEvent.class);
                    intent.putExtra("data_url", selectedImagePath);
                    intent.putExtra("eventtype", Utility.eventtype_local);
                    // intent.putExtra("tittle",tittle);
                    // intent.putExtra("des",des);
                    //intent.putExtra("type",type);
                    startActivity(intent);
                }
            }
        }
    }

    public void hidekeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        edittext.setText("");
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
            Fragment visibleFragment = checkCurrentFragment();
            Log.d("currentFragment", "" + visibleFragment);
            TagStream myFragment = (TagStream) getSupportFragmentManager().findFragmentByTag("TagStream");
            if (myFragment != null && myFragment.isVisible()) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.tagnamecolor));
                mimgtagstream.setColorFilter(getResources().getColor(R.color.colorAccent));
                // add your code here
            }

            Notifications notFrament = (Notifications) getSupportFragmentManager().findFragmentByTag("Notification");
            if (notFrament != null && notFrament.isVisible()) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.tagnamecolor));
                mimgtagstream.setColorFilter(getResources().getColor(R.color.colorAccent));
                // add your code here
            }

            MarketPlaceFragment marketFragment = (MarketPlaceFragment) getSupportFragmentManager().findFragmentByTag("MarketPlace");
            if (marketFragment != null && marketFragment.isVisible()) {
                produtsview.setVisibility(View.GONE);
                mtxttagstream.setTextColor(getResources().getColor(R.color.tagnamecolor));
                mimgtagstream.setColorFilter(getResources().getColor(R.color.colorAccent));
                // add your code here
            }

            Profile profile = (Profile) getSupportFragmentManager().findFragmentByTag("Profile");
            if (profile != null && profile.isVisible()) {
                mtxttagstream.setTextColor(getResources().getColor(R.color.tagnamecolor));
                mimgtagstream.setColorFilter(getResources().getColor(R.color.colorAccent));

                // add your code here
            }
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure,  You want to exit?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //super.onBackPressed();
        }
    }

//    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String qty = intent.getStringExtra("follow");
//            Toast.makeText(Modules.this," "+qty ,Toast.LENGTH_SHORT).show();
//
//
//        }
//    };

    public void loadUserInformation(String user_id) {
        StringRequest request = new StringRequest(Request.Method.GET, profileURL + user_id, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONObject res = jsonObject.getJSONObject("userinfo");
//                        JSONArray jsonArray = jsonObject.getJSONArray("userinfo");
//                        for (int i = 0; i < jsonArray.length(); i++) {


                        String noOfFollower = res.getString("number_of_followers");
                        String noOfFollowings = res.getString("number_of_following");
                        String noOfEvents = res.getString("number_of_event");
                        mtxt_no_of_followers.setText(noOfFollower);
                        mtxt_no_of_following.setText(noOfFollowings);
                        mtxt_no_of_events.setText(noOfEvents);
                        // if(res.getString("username").contains(params[0])){
//                            FollowModel followModel = new FollowModel();
//                            followModel.setUserid(res.getString("to_user_id"));
//                            followModel.setImage(res.getString("image"));
//                            followModel.setUser_name(res.getString("username"));
//                            followModel.setFrom_user_id(userinfo.getString(Utility.user_id));
//                            followModel.setIs_followed(res.getString("followed"));
                        // recyclerView.setAdapter(searchAdapter);
//                            contactList.add(followModel);
//                            recyclerView.setAdapter(new SearchAdapter(Modules.this, contactList, 2, Utility.operation_onclicked_direct_follow));
//                            // searchAdapter= new SearchAdapter(Modules.this,contactList);
//                            recyclerView.setAdapter(searchAdapter);
//                            searchAdapter.notifyDataSetChanged();

                        //  }

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
        AppSingleton.getInstance().addToRequestQueue(request);
    }


//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        apiInterface.getProfileInfo("user_id", user_id).enqueue(new Callback<ProfileResponseModel>() {
//                    @Override
//                    public void onResponse(Call<ProfileResponseModel> call, Response<ProfileResponseModel> response) {
//                            //  pbarProImage.setVisibility(View.GONE);
//                            if (response.body().getStatus().equals(Utility.success_response)) {
//                                User user = response.body().getUsers();
//
//                                //setting tabs
//                                String eventsno= user.getNumber_of_event();
//                                String followersno= user.getNumber_of_followers();
//                                String followingno= user.getNumber_of_following();
//
//                                mtxt_no_of_events.setText(eventsno);
//                                //  AppPrefs.putString(Utility.total_events, userinfo.getString(Utility.total_events));
//                                mtxt_no_of_following.setText(followingno);
//                                mtxt_no_of_followers.setText(followersno);
////
////                                numbers = new ArrayList<String>();
////                                numbers.add(user.getNumber_of_timeline());
////                                numbers.add(user.getNumber_of_event());
////                                numbers.add(user.getNumber_of_frame());
////                                numbers.add(user.getNumber_of_following());
////                                numbers.add(user.getNumber_of_followers());
//
//                            } else {
//                                Log.e("ds", response.body().getStatus());
//                            }
//                        }
//
//                    @Override public void onFailure(Call<ProfileResponseModel> call, Throwable t) {
//                    }
//                });


}
