package com.tagframe.tagframe.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.WebServiceHandler;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by abhinav on 19/04/2016.
 */
public class IntentServiceOperations extends IntentService implements WebServiceHandler.UploadCallbacks {


    private static final String TAG = "IntentServiceOperations";
    ResultReceiver receiver = null;
    int progr = 0;

    private static final int MY_NOTIFICATION_ID = 1;
    NotificationManager notificationManager;
    Notification myNotification;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param //name Used to name the worker thread, important only for debugging.
     */
    public IntentServiceOperations() {
        super(IntentServiceOperations.class.getName());
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        receiver = intent.getParcelableExtra("receiver");
        int operation = intent.getIntExtra("operation", 0);
        if (operation == Utility.operation_remove_follower) {
            String touser_id = intent.getStringExtra("to_userid");
            String fromuser_id = intent.getStringExtra("from_userid");
            String type = intent.getStringExtra("type");

            removefollow_unfollow(touser_id, fromuser_id, type, operation);
        } else if (operation == Utility.operation_unfollow) {
            String touser_id = intent.getStringExtra("to_userid");
            String fromuser_id = intent.getStringExtra("from_userid");
            String type = intent.getStringExtra("type");
            removefollow_unfollow(touser_id, fromuser_id, type, operation);
        } else if (operation == Utility.operation_follow) {
            String touser_id = intent.getStringExtra("to_userid");
            String fromuser_id = intent.getStringExtra("from_userid");
            String type = intent.getStringExtra("type");
            follow(touser_id, fromuser_id, operation);
        } else if (operation == Utility.operation_post_event) {

            String video_url = intent.getStringExtra("video_url");
            ArrayList<FrameList_Model> frameList_models = intent.getParcelableArrayListExtra("frame_list");
            String tittle = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            // String access_type=intent.getStringExtra("access_type");
            int duration = intent.getIntExtra("duration", 0);
            String user_id = intent.getStringExtra("user_id");
            String tagged_usr_id = intent.getStringExtra("tagged_user_id");

            post_event(video_url, frameList_models, tittle, description, duration, user_id, tagged_usr_id);

        } else if (operation == Utility.operation_follow_profile) {
            String touser_id = intent.getStringExtra("to_userid");
            String fromuser_id = intent.getStringExtra("from_userid");
            String type = intent.getStringExtra("type");
            follow(touser_id, fromuser_id, operation);

        } else if (operation == Utility.operation_unfollow_profile) {
            String touser_id = intent.getStringExtra("to_userid");
            String fromuser_id = intent.getStringExtra("from_userid");
            String type = intent.getStringExtra("type");
            removefollow_unfollow(touser_id, fromuser_id, type, operation);
        } else if (operation == Utility.operation_like) {
            String user_id = intent.getStringExtra("user_id");
            String event_id = intent.getStringExtra("event_id");

            like_event(user_id, event_id, operation);
        } else if (operation == Utility.operation_unlike) {
            String user_id = intent.getStringExtra("user_id");
            String event_id = intent.getStringExtra("event_id");

            unlike_event(user_id, event_id, operation);
        } else if (operation == Utility.operation_post_internet_event) {
            String user_id = intent.getStringExtra("user_id");
            String event_id = intent.getStringExtra("event_id");
            ArrayList<FrameList_Model> frameList_models = intent.getParcelableArrayListExtra("frame_list");

            if (frameList_models.size() > 0 && !event_id.isEmpty()) {
                for (int i = 0; i < frameList_models.size(); i++) {

                    //send locally added frames or edited frames(resynced or added product)
                    FrameList_Model fm = frameList_models.get(i);
                    if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {
                        send_frames(user_id, event_id, fm.getFrametype(), fm.getName(), fm.getStarttime(), fm.getEndtime(), fm.getImagepath(), fm.getProduct_id(),fm.isAProductFrame());
                    } else if (fm.isEdited()) {
                        send_edited_frames(event_id, fm.getFrame_id(), fm.getFrametype(), fm.getName(), fm.getStarttime(), fm.getEndtime(), fm.getImagepath(), fm.getProduct_id());
                    }
                }

            }

        } else if (operation == Utility.operation_comment) {
            String user_id = intent.getStringExtra("user_id");
            String event_id = intent.getStringExtra("video_id");
            String parent_id = intent.getStringExtra("parent_id");
            String comment = intent.getStringExtra("comment");

            comment(user_id, event_id, parent_id, comment, operation);
        }


    }

    private void send_edited_frames(String video_id, String frame_id, int frametype, String name, int starttime, int endtime, String imagepath, String product_id) {

        String status = "";


        sendNotification("Uploading Frame:" + name, "uploading..");


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.edit_frame);

            webServiceHandler.addFormField("video_id", video_id);
            webServiceHandler.addFormField("title", name);
            webServiceHandler.addFormField("frame_id", frame_id);
            webServiceHandler.addFormField("product_id", product_id);
            webServiceHandler.addFormField("start_time", starttime + "");
            webServiceHandler.addFormField("end_time", endtime + "");


            String res = webServiceHandler.finish();
            status = res;
            JSONObject wr = new JSONObject(res);


            status = wr.getString("status");

            Log.e("edit_stats", status);


        } catch (Exception e) {

            status = "error+" + e;
        }

        if (status.equals("success")) {

            sendNotification("Uploading Frame:" + name, "Success");

        } else {
            sendNotification("Uploading Frame:" + name, "Oops an error occured");
        }

    }

    private void comment(String user_id, String event_id, String parent_id, String comment, int operation) {

        String status = "";

        Log.e("ok", user_id + " " + event_id + " " + parent_id + " " + comment);


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.URL_COMMENT);
            webServiceHandler.addFormField("user_id", user_id);
            webServiceHandler.addFormField("video_id", event_id);
            webServiceHandler.addFormField("parent_id", parent_id);
            webServiceHandler.addFormField("comment", comment);
            Log.e("u_id + v_id + p_id ",user_id+" "+event_id +" "+ parent_id);
            JSONObject jsonObject = new JSONObject(webServiceHandler.finish());

            status = jsonObject.getString("status");

        } catch (Exception e) {

            status = "error";
        }
        if (status.equals("success")) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(1, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(0, bundle);
        }
    }

    private void unlike_event(String user_id, String event_id, int operation) {

        String status = "";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.unlike_video);
            webServiceHandler.addFormField("user_id", user_id);
            webServiceHandler.addFormField("video_id", event_id);

            JSONObject jsonObject = new JSONObject(webServiceHandler.finish());

            status = jsonObject.getString("status");

        } catch (Exception e) {

            status = "error";
        }
        if (status.equals("success")) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(1, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(0, bundle);
        }


    }

    private void like_event(String user_id, String event_id, int operation) {

        String status = "";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.like_video);
            webServiceHandler.addFormField("user_id", user_id);
            webServiceHandler.addFormField("video_id", event_id);
            String res = webServiceHandler.finish();
            Log.e("das", res);
            JSONObject jsonObject = new JSONObject(res);


            status = jsonObject.getString("status");

        } catch (Exception e) {

            Log.e("das", e.getMessage());
            status = "error";
        }
        if (status.equals("success")) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(1, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(0, bundle);
        }
    }


    private void post_event(String video_url, ArrayList<FrameList_Model> frameList_models, String tittle, String descrip, int duration, String user_id, String tag) {
        String status = "", event_id = "";

        sendNotification("Uploading Event:" + tittle, "uploading..");
        Log.e("user_id",user_id);

       /* ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), userid);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), tittle);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), descrip);
        RequestBody durati = RequestBody.create(MediaType.parse("text/plain"), duration+"");
        RequestBody event_type = RequestBody.create(MediaType.parse("text/plain"), "VIDEO");
        ProgressRequestBody media_file= new ProgressRequestBody(new File(video_url),this,Utility.media_type_video);

        apiInterface.postEvent(user_id,title,description,durati,event_type,media_file).enqueue(new Callback<EventSuccessUploadResponseModel>() {
            @Override
            public void onResponse(Call<EventSuccessUploadResponseModel> call, Response<EventSuccessUploadResponseModel> response) {
                Log.i("status",response.body().getStatus()+" "+response.body().getEvent_id());
            }

            @Override
            public void onFailure(Call<EventSuccessUploadResponseModel> call, Throwable t) {
                Log.i("status",t.getMessage().toString());
            }
        });*/


        try {

            String dur = duration + "";
            String event_type = "VIDEO";
            File file = new File(video_url);

            long size = tag.getBytes().length + user_id.getBytes().length + tittle.getBytes().length + descrip.getBytes().length +
                    dur.getBytes().length + event_type.getBytes().length + file.length() + file.getName().getBytes().length;

            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.upload_video, size, WebServiceHandler.upload_video_headers);
            webServiceHandler.addFormField("user_id", user_id);
            webServiceHandler.addFormField("title", tittle);
            webServiceHandler.addFormField("description", descrip);
            webServiceHandler.addFormField("tagged_user_id", tag);
            webServiceHandler.addFormField("duration", dur);
            webServiceHandler.addFormField("event_type", event_type);

            webServiceHandler.addFilePart("media_file", file, MY_NOTIFICATION_ID, getApplicationContext(), this);
            String res = webServiceHandler.finish();
            Log.e("status",res);
            status = res;

            JSONObject upload = new JSONObject(res);

            status = upload.getString("status");
            event_id = upload.getString("event_id");

        } catch (Exception e) {

            status = "error+" + e.getMessage().toString() + status;
        }
        Log.e("csf", status);
        if (status.equals("success")) {

            sendNotification("Uploading Event:" + tittle, "Success");
            if (frameList_models.size() > 0 && !event_id.isEmpty()) {
                for (int i = 0; i < frameList_models.size(); i++) {
                    FrameList_Model fm = frameList_models.get(i);
                    if (fm.getFrame_resource_type().equals(Utility.frame_resource_type_local)) {

                        //check whether this is product frame or not, if it is treat it differently like send only the url of image not the file

                        send_frames(user_id, event_id, fm.getFrametype(), fm.getName(), fm.getStarttime(), fm.getEndtime(), fm.getImagepath(), fm.getProduct_id(), fm.isAProductFrame());


                    }
                }

                sendNotification("Uploading Event:" + tittle, "Event uploaded Succesful");
            }
        } else {
            sendNotification("Uploading Event:" + tittle, "Oops an error occured");
        }
    }

    private void send_frames(String userid, String event_id, int type, String name, int s_time, int e_time, String imagepath, String product_id, boolean isproductframe) {

        String status = "";

        sendNotification("Uploading Frame:" + name, "uploading..");
        String start_Time = s_time + "";
        String end_Time = e_time + "";
        String media_type = "IMAGE";
        String is_a_product_frame = isproductframe ? "yes" : "no";
        String product_image_url = imagepath;

        long size = 0;
        File file = null;
        if (!isproductframe) {


            file = new File(imagepath);
            size = userid.getBytes().length + name.getBytes().length +
                    event_id.getBytes().length +
                    product_id.getBytes().length +
                    start_Time.getBytes().length + end_Time.getBytes().length +
                    is_a_product_frame.getBytes().length +
                    media_type.getBytes().length +
                    file.getName().getBytes().length + file.length();

        } else {
            size = userid.getBytes().length + name.getBytes().length +
                    event_id.getBytes().length +
                    product_id.getBytes().length +
                    start_Time.getBytes().length + end_Time.getBytes().length +
                    is_a_product_frame.getBytes().length +
                    media_type.getBytes().length +
                    product_image_url.getBytes().length;
        }
        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.create_frame);
            webServiceHandler.addFormField("user_id", userid);
            webServiceHandler.addFormField("title", name);
            webServiceHandler.addFormField("video_id", event_id);
            webServiceHandler.addFormField("product_id", product_id);
            webServiceHandler.addFormField("start_time", start_Time);
            webServiceHandler.addFormField("is_product_frame", is_a_product_frame);
            webServiceHandler.addFormField("end_time", end_Time);



            if (type == Utility.frametype_image) {
                webServiceHandler.addFormField("media_type", "IMAGE");

            } else {
                webServiceHandler.addFormField("media_type", "VIDEO");

            }

            if (isproductframe) {
                webServiceHandler.addFormField("media_file",product_image_url);
            } else {
                webServiceHandler.addFilePart("media_file", file, MY_NOTIFICATION_ID, getApplicationContext());
            }
            String res = webServiceHandler.finish();
            status = res;
            Log.e("status",status);
            JSONObject wr = new JSONObject(res);


            status = wr.getString("status");


        } catch (Exception e) {

            status = "error+" + e;
        }


        if (status.equals("success")) {

            sendNotification("Uploading Frame:" + name, "Success");

        } else {
            sendNotification("Uploading Frame:" + name, "Oops an error occured");
        }

    }

    private void removefollow_unfollow(String touser_id, String fromuser_id, String type, int operation) {

        String status = "";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.unfollow);
            webServiceHandler.addFormField("user_id", fromuser_id);
            webServiceHandler.addFormField("to_user_id", touser_id);
            webServiceHandler.addFormField("type", type);
            JSONObject jsonObject = new JSONObject(webServiceHandler.finish());
            JSONObject follow = jsonObject.getJSONObject("unfollow");
            status = follow.getString("status");
            Log.e("fsa", follow.toString());
        } catch (Exception e) {

            status = "error";
        }
        if (status.equals("success")) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", fromuser_id);
            receiver.send(1, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", fromuser_id);
            receiver.send(0, bundle);
        }
    }

    private void follow(String touser_id, String fromuser_id, int operation) {

        String status = "";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Utility.follow);
            webServiceHandler.addFormField("user_id", fromuser_id);
            webServiceHandler.addFormField("to_user_id", touser_id);

            JSONObject jsonObject = new JSONObject(webServiceHandler.finish());
            JSONObject follow = jsonObject.getJSONObject("follow");
            status = follow.getString("status");

        } catch (Exception e) {

            status = "error";
        }
        if (status.equals("success")) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", fromuser_id);
            receiver.send(1, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", fromuser_id);
            receiver.send(0, bundle);
        }
    }

    private void sendNotification(String tittle, String msg) {
        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(tittle)
                .setContentText(msg)
                .setTicker(tittle)
                .setSmallIcon(R.drawable.noti)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);


    }

    private void sendNotification(int progr) {

        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Uploading")
                .setContentText(progr + "")
                .setTicker("")
                .setProgress(100, progr, false)
                .setSmallIcon(R.drawable.noti)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);


    }

    public void clearNotification() {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MY_NOTIFICATION_ID);
    }


    public void publishprocess(int progress) {
        sendNotification(progress);
    }

    @Override
    public void onProgressUpdate(int percentage) {
        sendNotification(percentage);
        Log.e("Progress", "Progress" + percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        sendNotification(100);
    }
}
