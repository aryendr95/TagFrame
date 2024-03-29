package com.tagframe.tagframe.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Fragments.Profile;
import com.tagframe.tagframe.UI.Fragments.ProfileOld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * Created by abhinav on 04/04/2016.
 */
public class Utility {
    //SharedPreference
    public static final String user_id = "user_id";
    public static final String user_name = "username";
    public static final String user_pic = "profile_image";
    public static final String user_descrip = "description";
    public static final String user_realname = "first_name";
    public static final String user_password = "password";
    public static final String user_email = "email";
    public static final String total_events = "total_events";
    public static final String total_frames = "total_frames";
    public static final String number_of_following = "number_of_following";
    public static final String number_of_followers = "number_of_followers";
    public static final String unread_notifications = "unread_notifications";
    //shp tuts
    public static final String isTutShownForEvent = "isTutShownEvent";
    public static final String isTutShownForFrame = "isTutShownFrame";
    public static final int tutShown = 1;
    public static final int tutNotShown = 0;

    public static final String loginstatuskey = "log_key";
    public static final String loginstatusvalue = "success";

    //webservices
     //public static final String base_url = "http://tagframe.com/webservice/";
    public static final String base_url = "http://thinksmartapp.com/TagFrame/webservice/";
    //URL
    public static final String URL_COMMENT_LIST = base_url + "comment_list";
    public static final String URL_COMMENT = base_url + "post_comment";
    public static final String login = base_url + "login";
    public static final String signup = base_url + "signup";
    public static final String forgot_password = base_url + "forgot_password";

    public static final String tagstreams_url = base_url + "tagstreams";

    public static final String users_events_url = base_url + "user_events";
    public static final String users_details = base_url + "profile";

    public static final String users_timeline = base_url + "timeline";
    public static final String follow = base_url + "follow";
    public static final String unfollow = base_url + "unfollow";
    public static final String followers = base_url + "followers";
    public static final String following = base_url + "following";
    public static final String search = base_url + "search";

    public static final String update_identiy = base_url + "update_identity";
    public static final String search_user = base_url + "search_user";

    public static final String create_frame = base_url + "create_frame";

    public static final String edit_frame = base_url + "edit_frame";

    public static final String user_frame = base_url + "user_frames";
    public static final String upload_video = base_url + "upload_video";

    public static final String event = base_url + "event";
    public static final String upload_profile_photo = base_url + "upload_profile_photo";

    public static final String search_product = base_url + "search_product";

    public static final String change_password = base_url + "changepassword";

    public static final String like_video = base_url + "like_video";
    public static final String unlike_video = base_url + "unlike_video";

    public static final int user_type_following = 0;
    public static final int user_type_followers = 1;
    public static final int user_type_self = 99;

    public static final int frametype_image = 110;
    public static final int frametype_video = 111;

    public static final int eventtype_local = 1001;
    public static final int eventtype_saved = 1002;
    public static final int eventtype_internet = 1003;

    //intent service operations

    public static final int operation_remove_follower = 101;
    public static final int operation_unfollow = 102;
    public static final int operation_follow = 103;
    public static final int operation_post_event = 104;
    public static final int operation_post_internet_event = 1045;
    public static final int operation_unfollow_profile = 105;
    public static final int operation_follow_profile = 106;
    public static final int operation_like = 107;
    public static final int operation_unlike = 108;

    public static final int operation_load_user_events = 109;

    public static final int operation_comment = 110;

    public static final int operation_onclicked_tagged_user = 121;
    public static final int operation_onclicked_direct_endorse = 122;
    public static final int operation_onclicked_direct_follow = 123;
    public static final String ERROR_MESSAGE = "Sorry, error at this time";

    //public static final int operation_unlike=108;
    //public static final int operation_unlike=108;

    public static int framepostion = 110;

    public static final String frame_resource_type_internet = "internet";
    public static final String frame_resource_type_local = "local";

    public static final int PAGE_SIZE = 10;

    public static final String success_response = "success";
    public static final String message_no_internet = "No internet Connection";

    public static String media_type_video = "video";
    public static String media_type_image = "image";

    public static String test = "var";
    public static boolean isResumeFromActivityResult = false;
    public static int SCREEEN_ORIENTATION_INTERNET = 0;

    //notification onclick operations
    public static final String notification_op_watch_profile = "watch_profile";
    public static final String notification_op_watch_event = "watch_event";
    public static final String notification_op_watch_product = "watch_product";
    public static final String notification_op_watch_comment = "watch_comment";
    public static final String notification_op_watch_frame = "watch_frame";

    public static String NAVIGATE_TO = "name";
    public static String NAVIGATE_TO_NOTIFICATION = "notification";

    public static int PRODUCT_LIST_FLAG;

    public static String shp_user_Token = "reg_token";

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static void flushuserinfo(Context context) {
        AppPrefs userinfo = new AppPrefs(context);
        userinfo.puttagstreamlist(new ArrayList<Event_Model>());
        userinfo.putString(Utility.user_pic, "");
        userinfo.putString(Utility.user_name, "");
        userinfo.putString(Utility.user_realname, "");
        userinfo.putString(Utility.user_email, "");
        userinfo.putString(Utility.user_id, "");
        userinfo.putString(Utility.user_descrip, "");
        userinfo.putString(Utility.number_of_following, "");
        userinfo.putString(Utility.number_of_followers, "");
        userinfo.putString(Utility.total_frames, "");
        userinfo.putString(Utility.total_events, "");
        userinfo.putString(Utility.user_pic, "");
        userinfo.putUser(new User());
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static long stringtomillisecond(String dur) {

        long s = 0;
        try {
            int min = Integer.parseInt(dur.substring(0, dur.lastIndexOf(":")));
            int sec = Integer.parseInt(dur.substring(dur.lastIndexOf(":") + 1, dur.length()));

            int totalseconds = min * 60 + sec;

            return TimeUnit.SECONDS.toMillis(totalseconds);
        } catch (Exception e) {
            return s;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int desiredWidth =
                View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(
                        new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnOneChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) return;

        int desiredWidth =
                View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        if (listAdapter.getCount() > 0) {
            for (int i = 0; i < 1; i++) {
                view = listAdapter.getView(i, view, listView);
                if (i == 0) {
                    view.setLayoutParams(
                            new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));
                }

                view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += view.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static int getstatusBarHeight(android.content.res.Resources res) {
        return (int) (24 * res.getDisplayMetrics().density);
    }

    public static int getUser_Type(String user_id, String saved_user_id) {
        if (user_id.equals(saved_user_id)) {
            return Utility.user_type_self;
        } else {
            return Utility.user_type_following;
        }
    }

    public static Fragment getProfileFragment() {

        return new Profile();
    /*Fragment fragment = null;
    int currentapiVersion = Build.VERSION.SDK_INT;
    if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
      // Do something for lollipop and above versions
      fragment = new Profile();
    } else {
      // do something for phones running an SDK before lollipop
      fragment = new ProfileOld();
    }
    return fragment;*/
    }

    public static boolean isLollipop() {
        int currentapiVersion = Build.VERSION.SDK_INT;
        return (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void updateseekbar(SeekBar seekBar, int total, Context context,
                                     ArrayList<FrameList_Model> framedata_map) {

        // ColorDrawable backDrawable = new ColorDrawable(Color.WHITE);
        SeekBarBackgroundDrawable backgroundDrawable = new SeekBarBackgroundDrawable(context);
        // ColorDrawable progressDrawable = new ColorDrawable(Color.BLUE);
        //Custom seek bar progress drawable. Also allows you to modify appearance.
        SeekBarProgressDrawable clipProgressDrawable =
                new SeekBarProgressDrawable(context, framedata_map, total);
        Drawable[] drawables = new Drawable[]{backgroundDrawable, clipProgressDrawable};

        //Create layer drawables with android pre-defined ids
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.progress);

        //Set to seek bar
        seekBar.setProgressDrawable(layerDrawable);
    }

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
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static void toggleOrientation(Activity activity) {
        int orientation = getScreenOrientation(activity);
        if (orientation == 1)//portrait
        {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == 2)//landscape
        {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static int getScreenOrientation(Activity activity) {
        Display getOrient = activity.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static ApiInterface getApiCaller() {
        return ApiClient.getClient().create(ApiInterface.class);
    }

    public static String getUserId(Context context) {
        AppPrefs appPrefs = new AppPrefs(context);
        return appPrefs.getUser().getUser_id();
    }
}
