package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tagframe.tagframe.Models.Event_Model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by abhinav on 04/04/2016.
 */
public class Constants {


    //URL
    public static final String URL_COMMENT_LIST = "http://workintelligent.com/TagFrame/webservice/comment_list";
    public static final String URL_COMMENT = "http://workintelligent.com/TagFrame/webservice/post_comment";

    //SP
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

    public static final String loginstatuskey = "log_key";
    public static final String loginstatusvalue = "success";


    //webservices

    public static final String base_url = "http://workintelligent.com/TagFrame/webservice/";
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


    //public static final int operation_unlike=108;
    //public static final int operation_unlike=108;

    public static int framepostion = 110;


    public static final String frame_resource_type_internet = "internet";
    public static final String frame_resource_type_local = "local";

    public static final int PAGE_SIZE = 10;

    public static final String success_response="success";
    public static final String message_no_internet="No internet Connection";


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
        userinfo.putString(Constants.user_pic, "");
        userinfo.putString(Constants.user_name, "");
        userinfo.putString(Constants.user_realname, "");
        userinfo.putString(Constants.user_email, "");
        userinfo.putString(Constants.user_id, "");
        userinfo.putString(Constants.user_descrip, "");
        userinfo.putString(Constants.number_of_following, "");
        userinfo.putString(Constants.number_of_followers, "");
        userinfo.putString(Constants.total_frames, "");
        userinfo.putString(Constants.total_events, "");
        userinfo.putString(Constants.user_pic, "");
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
     *
     * @param currentDuration
     * @param totalDuration
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
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));


        listView.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnOneChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        if (listAdapter.getCount() > 0) {
            for (int i = 0; i < 1; i++) {
                view = listAdapter.getView(i, view, listView);
                if (i == 0)
                    view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

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

}
