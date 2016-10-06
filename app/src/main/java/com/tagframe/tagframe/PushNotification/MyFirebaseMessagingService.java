package com.tagframe.tagframe.PushNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tagframe.tagframe.Application.TagFrame;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.Authentication;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.


        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            String user_id = jsonObject.getString("user_id");
            AppPrefs appPrefs=new AppPrefs(this);
            if(appPrefs.getString(Utility.user_id).equals(user_id))
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
            else
            {
                deleteToken(user_id, TagFrame.android_id);
            }
        }
        catch (Exception e)
        {

        }

    }

    private void deleteToken(String user_id, String android_id) {

        ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        apiInterface.logout(user_id,android_id).enqueue(new Callback<ResponsePojo>() {
            @Override
            public void onResponse(Call<ResponsePojo> call, Response<ResponsePojo> response) {


                if(response.body().getStatus().equals(Utility.success_response))
                {
                Log.e("successfully","sessioned ended");
                }
                else
                {

                }
            }

            @Override
            public void onFailure(Call<ResponsePojo> call, Throwable t) {


            }
        });
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String tittle) {

        Intent intent = new Intent(this, Modules.class);
        intent.putExtra(Utility.NAVIGATE_TO, Utility.NAVIGATE_TO_NOTIFICATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(tittle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}