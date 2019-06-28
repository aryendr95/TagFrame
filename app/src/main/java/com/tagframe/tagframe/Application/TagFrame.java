package com.tagframe.tagframe.Application;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tagframe.tagframe.PushNotification.FirebaseIDService;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Utility;

/**
 * Created by Brajendr on 9/20/2016.
 */
public class TagFrame extends Application {

    public static String android_id;

    @Override
    public void onCreate() {
        super.onCreate();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.e(android_id, android_id);
    }
}
