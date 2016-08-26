package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Brajendr on 8/25/2016.
 */
public class EventSuccessUploadResponseModel {

    @SerializedName("status")
    @Expose
    private String status;

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("event_id")

    @Expose
    private String event_id;
}
