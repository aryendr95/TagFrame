package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KaranVeer on 02-09-2016.
 */
public class EventDetailResponseModel {

    @SerializedName("status")
    @Expose
    private String status;

    public Event_Model getEvent_model() {
        return event_model;
    }

    public void setEvent_model(Event_Model event_model) {
        this.event_model = event_model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("media")

    @Expose
    private Event_Model event_model;

}
