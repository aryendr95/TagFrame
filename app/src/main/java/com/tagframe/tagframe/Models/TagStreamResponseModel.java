package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tagframe.tagframe.UI.Fragments.TagStream;

import java.util.ArrayList;

/**
 * Created by Brajendr on 8/16/2016.
 */
public class TagStreamResponseModel {

    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<TagStream_Model> getTagStreamArrayList() {
        return tagStreamArrayList;
    }

    public void setTagStreamArrayList(ArrayList<TagStream_Model> tagStreamArrayList) {
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
    private ArrayList<TagStream_Model> tagStreamArrayList;

    public TagStreamResponseModel(String status, ArrayList<TagStream_Model> tagStreamArrayList) {
        this.status = status;
        this.tagStreamArrayList = tagStreamArrayList;
    }
}
