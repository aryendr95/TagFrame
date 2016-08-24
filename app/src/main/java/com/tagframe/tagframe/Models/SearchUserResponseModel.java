package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Brajendr on 8/23/2016.
 */
public class SearchUserResponseModel {
    @SerializedName("status")
    @Expose
    private String status;

    public SearchUserResponseModel(String status, ArrayList<FollowModel> arrayList_search_user_model) {
        this.status = status;
        this.arrayList_search_user_model = arrayList_search_user_model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<FollowModel> getArrayList_search_user_model() {
        return arrayList_search_user_model;
    }

    public void setArrayList_search_user_model(ArrayList<FollowModel> arrayList_search_user_model) {
        this.arrayList_search_user_model = arrayList_search_user_model;
    }

    @SerializedName("userinfo")
    @Expose
    private ArrayList<FollowModel> arrayList_search_user_model;
}
