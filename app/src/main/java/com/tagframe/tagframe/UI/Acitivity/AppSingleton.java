package com.tagframe.tagframe.UI.Acitivity;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tagframe.tagframe.Utils.ImageLoader;

public class AppSingleton extends Application {
    public static String android_id;
    public static final String TAG = AppSingleton.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppSingleton mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(android_id, android_id);
        mInstance = this;
    }

    public static synchronized AppSingleton getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}