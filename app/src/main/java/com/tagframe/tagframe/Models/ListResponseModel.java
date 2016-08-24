package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Brajendr on 8/16/2016.
 */
public class ListResponseModel {

    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<Event_Model> getTagStreamArrayList() {
        return tagStreamArrayList;
    }

    public void setTagStreamArrayList(ArrayList<Event_Model> tagStreamArrayList) {
        this.tagStreamArrayList = tagStreamArrayList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("records")

    @Expose
    private ArrayList<Event_Model> tagStreamArrayList;

    public ListResponseModel(String status, ArrayList<Event_Model> tagStreamArrayList) {
        this.status = status;
        this.tagStreamArrayList = tagStreamArrayList;
    }
}
