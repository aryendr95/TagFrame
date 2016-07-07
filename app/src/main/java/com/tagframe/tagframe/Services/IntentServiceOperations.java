package com.tagframe.tagframe.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.WebServiceHandler;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by abhinav on 19/04/2016.
 */
public class IntentServiceOperations extends IntentService{




    private static final String TAG = "IntentServiceOperations";
     ResultReceiver receiver=null;
    int progr=0;

    private static final int MY_NOTIFICATION_ID=1;
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
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {




        receiver = intent.getParcelableExtra("receiver");
        int operation = intent.getIntExtra("operation", 0);
        if(operation== Constants.operation_remove_follower)
        {
            String touser_id=intent.getStringExtra("to_userid");
            String fromuser_id=intent.getStringExtra("from_userid");
            String type=intent.getStringExtra("type");

            removefollow_unfollow(touser_id, fromuser_id, type, operation);
        }
        else if(operation== Constants.operation_unfollow)
        {
            String touser_id=intent.getStringExtra("to_userid");
            String fromuser_id=intent.getStringExtra("from_userid");
            String type=intent.getStringExtra("type");
            removefollow_unfollow(touser_id, fromuser_id, type, operation);
        }
        else if(operation== Constants.operation_follow)
        {
            String touser_id=intent.getStringExtra("to_userid");
            String fromuser_id=intent.getStringExtra("from_userid");
            String type=intent.getStringExtra("type");
            follow(touser_id, fromuser_id, operation);
        }
        else if(operation==Constants.operation_post_event)
        {

            String video_url=intent.getStringExtra("video_url");
            ArrayList<FrameList_Model> frameList_models=intent.getParcelableArrayListExtra("frame_list");
            String tittle=intent.getStringExtra("title");
            String description=intent.getStringExtra("description");
           // String access_type=intent.getStringExtra("access_type");
            int duration=intent.getIntExtra("duration",0);
            String user_id=intent.getStringExtra("user_id");

            post_event(video_url,frameList_models,tittle,description,duration,user_id);

        }
        else if(operation==Constants.operation_follow_profile)
        {
            String touser_id=intent.getStringExtra("to_userid");
            String fromuser_id=intent.getStringExtra("from_userid");
            String type=intent.getStringExtra("type");
            follow(touser_id, fromuser_id, operation);

        }
        else if(operation==Constants.operation_unfollow_profile)
        {
            String touser_id=intent.getStringExtra("to_userid");
            String fromuser_id=intent.getStringExtra("from_userid");
            String type=intent.getStringExtra("type");
            removefollow_unfollow(touser_id, fromuser_id, type, operation);
        }

        else if(operation==Constants.operation_like)
        {
            String user_id=intent.getStringExtra("user_id");
            String event_id=intent.getStringExtra("event_id");

            like_event(user_id, event_id, operation);
        }

        else if(operation==Constants.operation_unlike)
        {
            String user_id=intent.getStringExtra("user_id");
            String event_id=intent.getStringExtra("event_id");

            unlike_event(user_id, event_id, operation);
        }
        else if(operation==Constants.operation_post_internet_event)
        {
            String user_id=intent.getStringExtra("user_id");
            String event_id=intent.getStringExtra("event_id");
            ArrayList<FrameList_Model> frameList_models=intent.getParcelableArrayListExtra("frame_list");

            if(frameList_models.size()>0&&!event_id.isEmpty())
            {
                for(int i=0;i<frameList_models.size();i++) {
                    FrameList_Model fm = frameList_models.get(i);
                    if (fm.getFrame_resource_type().equals(Constants.frame_resource_type_local)) {
                        send_frames(user_id, event_id, fm.getFrametype(), fm.getName(), fm.getStarttime(), fm.getEndtime(), fm.getImagepath());
                    }
                }

            }

        }


        else if(operation==Constants.operation_comment)
        {
            String user_id=intent.getStringExtra("user_id");
            String event_id=intent.getStringExtra("video_id");
            String parent_id=intent.getStringExtra("parent_id");
            String comment=intent.getStringExtra("comment");

            comment(user_id,event_id,parent_id,comment,operation);
        }


    }

    private void comment(String user_id, String event_id, String parent_id, String comment, int operation) {

        String status="";

        Log.e("ok",user_id+" "+event_id+" "+parent_id+" "+comment);


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.URL_COMMENT);
            webServiceHandler.addFormField("user_id",user_id);
            webServiceHandler.addFormField("video_id", event_id);
            webServiceHandler.addFormField("parent_id", parent_id);
            webServiceHandler.addFormField("comment", comment);

            JSONObject jsonObject=new JSONObject(webServiceHandler.finish());

            status=jsonObject.getString("status");

        }
        catch (Exception e)
        {

            status="error";
        }
        if(status.equals("success"))
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id",user_id);
            receiver.send(1, bundle);
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(0,bundle);
        }
    }

    private void unlike_event(String user_id, String event_id, int operation) {

        String status="";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.unlike_video);
            webServiceHandler.addFormField("user_id",user_id);
            webServiceHandler.addFormField("video_id", event_id);

            JSONObject jsonObject=new JSONObject(webServiceHandler.finish());

            status=jsonObject.getString("status");

        }
        catch (Exception e)
        {

            status="error";
        }
        if(status.equals("success"))
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id",user_id);
            receiver.send(1, bundle);
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation", operation);
            bundle.putString("user_id", user_id);
            receiver.send(0,bundle);
        }


    }

    private void like_event(String user_id, String event_id, int operation) {

        String status="";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.like_video);
            webServiceHandler.addFormField("user_id",user_id);
            webServiceHandler.addFormField("video_id", event_id);
            String res=webServiceHandler.finish();
            Log.e("das", res);
            JSONObject jsonObject=new JSONObject(res);


            status=jsonObject.getString("status");

        }
        catch (Exception e)
        {

            Log.e("das", e.getMessage());
            status="error";
        }
        if(status.equals("success"))
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id",user_id);
            receiver.send(1, bundle);
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id", user_id);
            receiver.send(0,bundle);
        }
    }



    private void post_event(String video_url,ArrayList<FrameList_Model> frameList_models,String tittle,String descrip,int duration,String userid)
    {
        String status="",event_id="";

        sendNotification("Uploading Event:" + tittle, "uploading..");



        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.upload_video);
            webServiceHandler.addFormField("user_id",userid);
            webServiceHandler.addFormField("title",tittle);
            webServiceHandler.addFormField("description",descrip);

            webServiceHandler.addFormField("duration", duration/1000+"");
            webServiceHandler.addFormField("event_type","VIDEO");
            File file=new File(video_url);
            webServiceHandler.addFilePart("media_file", file,MY_NOTIFICATION_ID,getApplicationContext());
            String res=webServiceHandler.finish();
            status=res;
            JSONObject wr=new JSONObject(res);
            JSONObject upload=wr.getJSONObject("upload");

            status=upload.getString("status");
            event_id=upload.getString("event_id");

        }
        catch (Exception e)
        {

            status="error+"+e;
        }
        Log.e("csf",status);
        if(status.equals("success"))
        {

            sendNotification("Uploading Event:" + tittle, "Success");
            if(frameList_models.size()>0&&!event_id.isEmpty())
            {
                    for(int i=0;i<frameList_models.size();i++) {
                        FrameList_Model fm = frameList_models.get(i);
                        if (fm.getFrame_resource_type().equals(Constants.frame_resource_type_local)) {
                            send_frames(userid, event_id, fm.getFrametype(), fm.getName(), fm.getStarttime(), fm.getEndtime(), fm.getImagepath());
                        }
                    }

                sendNotification("Uploading Event:"+tittle,"Event uploaded Succesfull");
            }
        }
        else
        {
            sendNotification("Uploading Event:" + tittle, "Oops an error occured");
        }
    }

    private void send_frames(String userid,String event_id,int type,String name,int s_time,int e_time,String imagepath) {

        String status = "";

        sendNotification("Uploading Frame:" + name, "uploading..");



        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.create_frame);
            webServiceHandler.addFormField("user_id",userid);
            webServiceHandler.addFormField("title",name);
            webServiceHandler.addFormField("video_id", event_id);


            webServiceHandler.addFormField("start_time",s_time/1000+"");
            webServiceHandler.addFormField("end_time",e_time/1000+"");
            if(type==Constants.frametype_image) {
                webServiceHandler.addFormField("media_type","IMAGE" );
                Log.e("dsa","dsa");
            }
            else
            {
                webServiceHandler.addFormField("media_type","VIDEO" );
                Log.e("dsa", "vdsa");
            }

            File file=new File(imagepath);
            webServiceHandler.addFilePart("media_file",file,MY_NOTIFICATION_ID,getApplicationContext());
            String res=webServiceHandler.finish();
            status=res;
            JSONObject wr=new JSONObject(res);


            status=wr.getString("status");


        }
        catch (Exception e)
        {

            status="error+"+e;
        }

        if(status.equals("success"))
        {

            sendNotification("Uploading Frame:" + name, "Success");

        }
        else
        {
            sendNotification("Uploading Frame:"+name,"Oops an error occured");
        }

    }

    private void removefollow_unfollow(String touser_id, String fromuser_id, String type,int operation) {

        String status="";


        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.unfollow);
            webServiceHandler.addFormField("user_id",fromuser_id);
            webServiceHandler.addFormField("to_user_id", touser_id);
            webServiceHandler.addFormField("type", type);
            JSONObject jsonObject=new JSONObject(webServiceHandler.finish());
            JSONObject follow=jsonObject.getJSONObject("unfollow");
            status=follow.getString("status");
            Log.e("fsa",follow.toString());
        }
        catch (Exception e)
        {

            status="error";
        }
        if(status.equals("success"))
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id",fromuser_id);
            receiver.send(1, bundle);
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id", fromuser_id);
            receiver.send(0, bundle);
        }
    }
    private void follow(String touser_id, String fromuser_id,int operation) {

        String status="";



        try {
            WebServiceHandler webServiceHandler = new WebServiceHandler(Constants.follow);
            webServiceHandler.addFormField("user_id",fromuser_id);
            webServiceHandler.addFormField("to_user_id", touser_id);

            JSONObject jsonObject=new JSONObject(webServiceHandler.finish());
            JSONObject follow=jsonObject.getJSONObject("follow");
            status=follow.getString("status");

        }
        catch (Exception e)
        {

            status="error";
        }
        if(status.equals("success"))
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id",fromuser_id);
            receiver.send(1, bundle);
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putInt("operation",operation);
            bundle.putString("user_id", fromuser_id);
            receiver.send(0,bundle);
        }
    }

    private void sendNotification(String tittle,String msg) {
        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(tittle)
                .setContentText(msg)
                .setTicker(tittle)
                .setProgress(100,progr,false)
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
                .setContentText("")
                .setTicker("")
                .setProgress(100,progr,false)
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


    public  void publishprocess(int progress) {
        sendNotification(progress);
    }
}
