package com.tagframe.tagframe.PushNotification;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tagframe.tagframe.Application.TagFrame;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.AppSingleton;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Utility;
import android.provider.Settings.Secure;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("token",refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        saveToken(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    private void saveToken(String token) {
        // Add custom implementation, as needed.
        AppPrefs appPrefs = new AppPrefs(this);
        appPrefs.putString(Utility.shp_user_Token, token);
        //update the registration token if user is logged in
        if (appPrefs.getString(Utility.loginstatuskey).equals(Utility.loginstatusvalue)) {
             String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
            ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
            apiInterface.update_Token(appPrefs.getString(Utility.user_id), AppSingleton.android_id,token).enqueue(new Callback<ResponsePojo>() {
                @Override
                public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {
                }

                @Override
                public void onFailure(Call<ResponsePojo> call, Throwable t) {
                }
            });
        }
    }
}