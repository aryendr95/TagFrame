package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Brajendr on 8/22/2016.
 */
public class UserFrameResponseModel {

    @Expose
    @SerializedName("status")
    String status="";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<User_Frames_model> getUser_frames_models() {
        return user_frames_models;
    }

    public void setUser_frames_models(ArrayList<User_Frames_model> user_frames_models) {
        this.user_frames_models = user_frames_models;
    }

    @Expose

    @SerializedName("framedata")
    ArrayList<User_Frames_model> user_frames_models;

}
