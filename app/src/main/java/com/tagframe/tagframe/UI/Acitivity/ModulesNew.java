package com.tagframe.tagframe.UI.Acitivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Adapters.Menulistadapter;
import com.tagframe.tagframe.Application.TagFrame;
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
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModulesNew extends AppCompatActivity implements Broadcastresults.Receiver {
  private RelativeLayout mLayout;
  private FrameLayout frameLayout;
  //left navigation bar
  private SlidingPaneLayout mSlidingPanel;
  private ListView mMenuList;
  private CircularImageView mod_pro_image;
  private TextView mod_usrname, mod_email;
  private ImageView r_layout;
  //app prefrences
  private AppPrefs userinfo;
  private int PICK_VIDEO = 1, TAKE_VIDEO = 2;

  private Broadcastresults mReceiver;

  private String[] requiredStoragePermissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
  private String[] requiredCameraPermissions = { Manifest.permission.CAMERA };

  private final int Request_Storage_permissions = 1;
  private final int Request_Camera_permissions = 2;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_module_new);
    userinfo = new AppPrefs(this);
    init();
    setUpInitialFragment();
  }

  private void setUpInitialFragment() {
    TagStream myf = new TagStream();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.add(R.id.mod_frame_layout, myf);
    transaction.commit();
  }

  private void init() {
    mLayout = (RelativeLayout) findViewById(R.id.mLayout_module);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    BottomNavigationView bottomNavigationView =
        (BottomNavigationView) findViewById(R.id.bottom_navigation);
    try {
      BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
      for (int i = 0; i < menuView.getChildCount(); i++) {
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
        itemView.setShiftingMode(false);
        if(i!=0)
        itemView.setChecked(false);
      }
    }
    catch (Exception e)
    {

    }
    bottomNavigationView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment f = checkCurrentFragment();
            switch (item.getItemId()) {
              case R.id.bottomBarItemtagstream:
                if (f instanceof TagStream) {
                  ((TagStream) f).scrolltofirst();
                } else {
                  changefragment(new TagStream());
                }
                break;
              case R.id.bottomBarItemnotifications:
                changefragment(new Notifications());
                break;
              case R.id.bottomBarItemevents:
                generate_media_chooser();
                break;
              case R.id.bottomBarItemmarketplace:
                changefragment(new MarketPlaceFragment());
                break;
              case R.id.bottomBarItemprofile:
                changefragment(new Profile());
            }
            return true;
          }
        });
    final ImageView mod_menu = (ImageView) findViewById(R.id.mod_menu);
    mod_menu.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mSlidingPanel.isOpen()) {
          mSlidingPanel.closePane();
        } else {
          mSlidingPanel.openPane();
        }
      }
    });
    mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
    mSlidingPanel.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

      @Override public void onPanelClosed(View arg0) {
        // TODO Auto-genxxerated method stub        getActionBar().setTitle(getString(R.string.app_name));
        mod_menu.animate().rotation(0);
      }

      @Override public void onPanelOpened(View arg0) {
        // TODO Auto-generated method stub

        mod_menu.animate().rotation(90);
      }

      @Override public void onPanelSlide(View arg0, float arg1) {
        // TODO Auto-generated method stub

      }
    });
    mSlidingPanel.setSliderFadeColor(Color.parseColor("#00FFFFFF"));
    mSlidingPanel.setShadowResource(R.drawable.slider_shadow);
    mMenuList = (ListView) findViewById(R.id.mod_MenuList);

    r_layout = (ImageView) findViewById(R.id.img);

    String[] MenuTitles = new String[] {
        "Account", "My Endorsements", "Viewer Privacy", "Terms of Service", "Logout"
    };

    ArrayList<com.tagframe.tagframe.Models.Menu> list = populatelist();
    mMenuList.setAdapter(new Menulistadapter(this, list));

    mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mSlidingPanel.isOpen()) {
          switch (position) {
            case 0:

              final Intent intent = new Intent(ModulesNew.this, Menu_Action.class);
              intent.putExtra("name", "Account");
              intent.putExtra("layout", position);
              startActivity(intent);
              finish();

              break;

            case 1:

              Intent endorseIntent = new Intent(ModulesNew.this, MyEndorsement.class);
              startActivity(endorseIntent);
              finish();
              break;
            case 2:

              Intent intent1 = new Intent(ModulesNew.this, Menu_Action.class);
              intent1.putExtra("name", "Viewer Privacy");
              intent1.putExtra("layout", position);
              startActivity(intent1);
              finish();
              break;
            case 3:

              Intent intent11 = new Intent(ModulesNew.this, Menu_Action.class);
              intent11.putExtra("name", "Terms of Service");
              intent11.putExtra("layout", position);
              startActivity(intent11);
              finish();
              break;
            case 4:
              mSlidingPanel.closePane();

              new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                  final Dialog dialog = new Dialog(ModulesNew.this);
                  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                  dialog.setCancelable(false);
                  dialog.setContentView(R.layout.dialog_logout);
                  final LinearLayout controls =
                      (LinearLayout) dialog.findViewById(R.id.layout_logout_controls);
                  final LinearLayout progress =
                      (LinearLayout) dialog.findViewById(R.id.layout_logging_out);

                  dialog.findViewById(R.id.yesbtn).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                      final AppPrefs listops = new AppPrefs(ModulesNew.this);

                      controls.setVisibility(View.GONE);
                      progress.setVisibility(View.VISIBLE);

                      ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                      apiInterface.logout(listops.getString(Utility.user_id), TagFrame.android_id)
                          .enqueue(new Callback<ResponsePojo>() {
                            @Override public void onResponse(Call<ResponsePojo> call,
                                Response<ResponsePojo> response) {

                              if (response.body().getStatus().equals(Utility.success_response)) {
                                Utility.flushuserinfo(ModulesNew.this);

                                Intent intent2 = new Intent(ModulesNew.this, Authentication.class);
                                startActivity(intent2);
                                finish();
                                dialog.dismiss();
                              } else {
                                PopMessage.makeshorttoast(ModulesNew.this, "Error Logging out..");
                                dialog.dismiss();
                              }
                            }

                            @Override public void onFailure(Call<ResponsePojo> call, Throwable t) {
                              PopMessage.makeshorttoast(ModulesNew.this, "Error Logging out..");
                              dialog.dismiss();
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
              }, 200);

              break;
          }
        }
      }
    });

    //dispatching the touch top framelayout
    mMenuList.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        if (!mSlidingPanel.isOpen()) {
          return frameLayout.dispatchTouchEvent(event);
        } else {
          return false;
        }
      }
    });

    mod_pro_image = (CircularImageView) findViewById(R.id.mod_profile_image);
    try {
      Picasso.with(this)
          .load(userinfo.getString(Utility.user_pic))
          .error(R.drawable.pro_image)
          .into(mod_pro_image);
      Picasso.with(this)
          .load(userinfo.getString(Utility.user_pic))
          .error(R.color.colorPrimaryDark)
          .into(r_layout);
    } catch (Exception e) {
      mod_pro_image.setImageResource(R.drawable.pro_image);
      r_layout.setImageResource(R.drawable.pro_image);
    }
    mod_usrname = (TextView) findViewById(R.id.mod_usrname);
    mod_usrname.setText(userinfo.getString(Utility.user_name));

    mod_email = (TextView) findViewById(R.id.mod_email);
    mod_email.setText(userinfo.getString(Utility.user_email));
  }

  private Fragment checkCurrentFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment mCurrentFragment = fragmentManager.findFragmentById(R.id.mod_frame_layout);
    return mCurrentFragment;
  }

  private ArrayList<com.tagframe.tagframe.Models.Menu> populatelist() {
    String[] MenuTitles =
        new String[] { "Account", "Viewer Privacy", "Terms of Service", "Logout" };
    ArrayList<com.tagframe.tagframe.Models.Menu> menuArrayList = new ArrayList<>();
    menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Account",
        R.drawable.ic_account_circle_grey_600_24dp));
    menuArrayList.add(new com.tagframe.tagframe.Models.Menu("My Endorsements",
        R.drawable.ic_present_to_all_grey_600_24dp));
    menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Viewer Privacy",
        R.drawable.ic_view_carousel_grey_600_24dp));
    menuArrayList.add(new com.tagframe.tagframe.Models.Menu("Terms of Service",
        R.drawable.ic_speaker_notes_grey_600_24dp));
    menuArrayList.add(
        new com.tagframe.tagframe.Models.Menu("Logout", R.drawable.ic_exit_to_app_grey_600_24dp));
    return menuArrayList;
  }

  public static void hideKeyboard(Activity activity) {
    InputMethodManager imm =
        (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    //Find the currently focused view, so we can grab the correct window token from it.
    View view = activity.getCurrentFocus();
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public void generate_media_chooser() {

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
      @Override public void onClick(View v) {
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
      @Override public void onClick(View v) {
        if (askforPermission(requiredCameraPermissions, Request_Camera_permissions)) {
          Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
          // start the Video Capture Intent
          startActivityForResult(intent1, TAKE_VIDEO);
        }
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.media_saved).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent2 = new Intent(ModulesNew.this, SavedEvents.class);
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

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
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

  public void setprofile(String user_id, int user_type) {
    Fragment fr = Utility.getProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putString("user_id", user_id);
    bundle.putInt("type", user_type);
    fr.setArguments(bundle);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } else {
      return null;
    }
  }

  private void changefragment(Fragment fr) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    transaction.replace(R.id.mod_frame_layout, fr);

    transaction.commit();
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
      Profile profile =
          (Profile) getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
      profile.setprofilestat(s, v);
    } else {
      // do something for phones running an SDK before lollipop
      ProfileOld profile =
          (ProfileOld) getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
      profile.setprofilestat(s, v);
    }
  }

  @Override public void onReceiveResult(int resultCode, Bundle resultData) {

    int operation = resultData.getInt("operation");
    String userid = "";
    switch (operation) {
      case Utility.operation_remove_follower:

        userid = resultData.getString("user_id");
        if (resultCode == 1) {

          MyToast.popmessage("Successfully Removed", this);
        } else {
          setprofile(userid, Utility.user_type_self);
          MyToast.popmessage("There was error removing this user", this);
        }
        break;
      case Utility.operation_unfollow:

        userid = resultData.getString("user_id");
        if (resultCode == 1) {
          setprofile(userid, Utility.user_type_followers);
          MyToast.popmessage("Successfully UnFollowed", this);
        } else {
          setprofile(userid, Utility.user_type_self);
          MyToast.popmessage("There was error unfollowing this user", this);
        }
        break;
      case Utility.operation_follow:

        userid = resultData.getString("user_id");
        if (resultCode == 1) {
          setprofile(userid, Utility.user_type_following);
          MyToast.popmessage("Successfully Followed", this);
        } else {
          setprofile(userid, Utility.user_type_self);
          MyToast.popmessage("There was error unfollowing this user", this);
        }
        break;

      case Utility.operation_follow_profile:

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
        if (f instanceof Profile)

        {
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
          PopMessage.makeshorttoast(ModulesNew.this, "Successful");
          Fragment f2 = getSupportFragmentManager().findFragmentById(R.id.mod_frame_layout);
          if (f2 instanceof Profile)
          // do something with f
          {
            ((Profile) f2).changeprofile_ui(operation, resultCode);
          }
        }
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == PICK_VIDEO) {
        Uri selectedImageUri = data.getData();

        // OI FILE Manager
        String filemanagerstring = selectedImageUri.getPath();

        // MEDIA GALLERY
        String selectedImagePath = GetPaths.getPath(ModulesNew.this, selectedImageUri);

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
        String selectedImagePath = GetPaths.getPath(ModulesNew.this, selectedImageUri);
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


  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_modules, menu);
    //Get the SearchView and set the searchable configuration
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.searchItem).getActionView();
    try {
      ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHint("Search Here");
      ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
      ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);
      ImageView searchCloseIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
      searchCloseIcon.setImageResource(R.drawable.ic_close_white_24dp);
      ImageView searchIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
      searchIcon.setImageResource(R.drawable.ic_search_white_24dp);
    }
    catch (NullPointerException e)
    {

    }
    // Assumes current activity is the searchable activity
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
    //        searchView.setSubmitButtonEnabled(true);

    SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered

        return true;
      }

      @Override public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
      }
    };
    searchView.setOnQueryTextListener(textChangeListener);
    return true;
  }

  private void search(String keyword) {

    if (checkCurrentFragment() instanceof MarketPlaceFragment) {
      MarketPlaceFragment marketPlaceFragment = new MarketPlaceFragment();
      Bundle bundle = new Bundle();
      bundle.putString("keyword", keyword);
      marketPlaceFragment.setArguments(bundle);
      hideKeyboard(ModulesNew.this);
      changefragment(marketPlaceFragment);
    } else {
      Follow follow = new Follow();
      Bundle bundle = new Bundle();
      bundle.putString("keyword", keyword);
      follow.setArguments(bundle);
      hideKeyboard(ModulesNew.this);
      changefragment(follow);
    }
  }



  public void hidekeyboard() {

    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
  }
}
