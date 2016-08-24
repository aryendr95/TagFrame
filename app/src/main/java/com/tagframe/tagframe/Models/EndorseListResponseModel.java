package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Brajendr on 8/23/2016.
 */
public class EndorseListResponseModel {

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<EndorseListModel> getEndorseListModels() {
        return endorseListModels;
    }

    public void setEndorseListModels(ArrayList<EndorseListModel> endorseListModels) {
        this.endorseListModels = endorseListModels;
    }

    @SerializedName("endorseData")
    @Expose
    private ArrayList<EndorseListModel> endorseListModels;
}
