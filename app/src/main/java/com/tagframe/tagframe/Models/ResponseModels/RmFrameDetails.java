package com.tagframe.tagframe.Models.ResponseModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.Models.User_Frames_model;

/**
 * Created by Brajendr on 1/13/2017.
 */

public class RmFrameDetails {
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("framedata")
    @Expose
    private User_Frames_model frameList_model;

    public User_Frames_model getFrameList_model() {
        return frameList_model;
    }

    public void setFrameList_model(User_Frames_model frameList_model) {
        this.frameList_model = frameList_model;
    }
}
