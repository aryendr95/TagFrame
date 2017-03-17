package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.SingleEventModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.Models.User_Frames_model;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Karan on 12/24/2015.
 */
public class AppPrefs {
    Context mcontext;
    SharedPreferences prefs;


    public AppPrefs(Context context)
    {
        mcontext=context;
        prefs= mcontext.getSharedPreferences("prefs", Context.MODE_MULTI_PROCESS);

    }






    Gson gson = new Gson();

   public ArrayList<Event_Model> gettagstreamlist(String listname )
    {

        SharedPreferences.Editor editor=prefs.edit();

        Type type = new TypeToken<ArrayList<Event_Model>>(){}.getType();
        String lists=prefs.getString(listname,"");
        Gson gson=new Gson();
        ArrayList<Event_Model> schoolinfomodels;
        if(!lists.equals("")) {
            schoolinfomodels = gson.fromJson(lists, type);
        }
        else
        {
            schoolinfomodels=new ArrayList<Event_Model>();
        }
        return  schoolinfomodels;
    }


    public void puttagstreamlist( ArrayList<Event_Model> arrayList)
    {

        SharedPreferences.Editor editor=prefs.edit();
        String jsonCars = gson.toJson(arrayList);
        editor.putString("tagstream",jsonCars);
        editor.commit();
    }

    public void putString(String key,String value)
    {
        SharedPreferences.Editor editor=prefs.edit();

        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key)
    {
      AppPrefs appPrefs=new AppPrefs(mcontext);
      User user=appPrefs.getUser();
      switch (key)
      {
        case Utility.user_id:
          return user.getUser_id();
        case Utility.user_name:
          return user.getUsername();
        case Utility.user_email:
          return user.getEmail();
        case Utility.user_pic:
          return user.getProfile_image();
        case Utility.user_descrip:
          return user.getDescription();
        case Utility.user_realname:
          return user.getRealname();
        case Utility.total_events:
          return user.getNumber_of_event();
        case Utility.user_frame:
          return user.getNumber_of_frame();
        case Utility.unread_notifications:
          return user.getUnreadnotifications();
        case Utility.number_of_followers:
          return user.getNumber_of_followers();
        case Utility.number_of_following:
          return user.getNumber_of_following();
      }
        return prefs.getString(key,"");
    }

    public int getInt(String key)
    {
        return prefs.getInt(key,0);
    }
    public void putInt(String key,int value)
    {
        SharedPreferences.Editor editor=prefs.edit();

        editor.putInt(key,value);
        editor.commit();
    }


    public ArrayList<SingleEventModel> getsingleeventlist( )
    {

        SharedPreferences.Editor editor=prefs.edit();

        Type type = new TypeToken<ArrayList<SingleEventModel>>(){}.getType();
        String lists=prefs.getString("singleevent","");
        Gson gson=new Gson();
        ArrayList<SingleEventModel> schoolinfomodels;
        if(!lists.equals("")) {
            schoolinfomodels = gson.fromJson(lists, type);
        }
        else
        {
            schoolinfomodels=new ArrayList<SingleEventModel>();
        }
        return  schoolinfomodels;
    }


    public void putsingleeventlist( ArrayList<SingleEventModel> arrayList)
    {

        SharedPreferences.Editor editor=prefs.edit();
        String jsonCars = gson.toJson(arrayList);
        editor.putString("singleevent", jsonCars);
        editor.commit();
    }

    public ArrayList<Event_Model> getuser_event( )
    {

        SharedPreferences.Editor editor=prefs.edit();

        Type type = new TypeToken<ArrayList<Event_Model>>(){}.getType();
        String lists=prefs.getString("userevent","");
        Gson gson=new Gson();
        ArrayList<Event_Model> schoolinfomodels;
        if(!lists.equals("")) {
            schoolinfomodels = gson.fromJson(lists, type);
        }
        else
        {
            schoolinfomodels=new ArrayList<Event_Model>();
        }
        return  schoolinfomodels;
    }


    public void putusereventlist( ArrayList<Event_Model> arrayList)
    {

        SharedPreferences.Editor editor=prefs.edit();
        String jsonCars = gson.toJson(arrayList);
        editor.putString("userevent", jsonCars);
        editor.commit();
    }

    public ArrayList<User_Frames_model> getuser_frames( )
    {

        SharedPreferences.Editor editor=prefs.edit();

        Type type = new TypeToken<ArrayList<User_Frames_model>>(){}.getType();
        String lists=prefs.getString("userframes","");
        Gson gson=new Gson();
        ArrayList<User_Frames_model> schoolinfomodels;
        if(!lists.equals("")) {
            schoolinfomodels = gson.fromJson(lists, type);
        }
        else
        {
            schoolinfomodels=new ArrayList<User_Frames_model>();
        }
        return  schoolinfomodels;
    }


    public void putuserframelist( ArrayList<User_Frames_model> arrayList)
    {

        SharedPreferences.Editor editor=prefs.edit();
        String jsonCars = gson.toJson(arrayList);
        editor.putString("userframes", jsonCars);
        editor.commit();
    }


    public ArrayList<FollowModel> getuser_following( )
    {

        SharedPreferences.Editor editor=prefs.edit();

        Type type = new TypeToken<ArrayList<FollowModel>>(){}.getType();
        String lists=prefs.getString("following","");
        Gson gson=new Gson();
        ArrayList<FollowModel> schoolinfomodels;
        if(!lists.equals("")) {
            schoolinfomodels = gson.fromJson(lists, type);
        }
        else
        {
            schoolinfomodels=new ArrayList<FollowModel>();
        }
        return  schoolinfomodels;
    }


    public void putuserfollowing( ArrayList<FollowModel> arrayList)
    {

        SharedPreferences.Editor editor=prefs.edit();
        String jsonCars = gson.toJson(arrayList);
        editor.putString("following", jsonCars);
        editor.commit();
    }

    public ArrayList<FollowModel> getuser_followers( )
    {

        SharedPreferences.Editor editor=prefs.edit();

        Type type = new TypeToken<ArrayList<FollowModel>>(){}.getType();
        String lists=prefs.getString("followers","");
        Gson gson=new Gson();
        ArrayList<FollowModel> schoolinfomodels;
        if(!lists.equals("")) {
            schoolinfomodels = gson.fromJson(lists, type);
        }
        else
        {
            schoolinfomodels=new ArrayList<FollowModel>();
        }
        return  schoolinfomodels;
    }


    public void putuserfollowers( ArrayList<FollowModel> arrayList)
    {

        SharedPreferences.Editor editor=prefs.edit();
        String jsonCars = gson.toJson(arrayList);
        editor.putString("followers", jsonCars);

        editor.commit();
    }

    public void flushProfileInformation()
    {
        ArrayList<Event_Model> tagStream_modelArrayList=new ArrayList<>();
        ArrayList<FollowModel> followModelArrayLis=new ArrayList<>();
        ArrayList<User_Frames_model> framesArrayList=new ArrayList<>();

        putusereventlist(tagStream_modelArrayList);
        putuserframelist(framesArrayList);
        putuserfollowers(followModelArrayLis);
        putuserfollowing(followModelArrayLis);


    }


  public void putUser(User user) {

    Gson gson = new Gson();
    SharedPreferences.Editor editor = prefs.edit();
    String jsonValue = gson.toJson(user);
    editor.putString(Constants.SHP_USER_INFORMATION, jsonValue);
    editor.commit();
  }

  public User getUser() {
    Gson gson = new Gson();
    String json = prefs.getString(Constants.SHP_USER_INFORMATION, "");
    User obj = null;
    if (TextUtils.isEmpty(json)) {
      obj = new User();
    } else {
      obj = gson.fromJson(json, User.class);
    }
    return obj;
  }




}
