package com.tagframe.tagframe.Adapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tagframe.tagframe.Models.Comment;

import java.util.ArrayList;

/**
 * Created by Brajendr on 9/6/2016.
 */
public class CommentsResponseModel {
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    public void setCommentArrayList(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    @SerializedName("comments")
    @Expose
    private ArrayList<Comment> commentArrayList;

}
