package com.tagframe.tagframe.Models;

import java.util.ArrayList;

/**
 * Created by Brajendr on 7/7/2016.
 */
public class Comment {
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public ArrayList<ReplyComment> getReplyCommentArrayList() {
        return replyCommentArrayList;
    }

    public void setReplyCommentArrayList(ArrayList<ReplyComment> replyCommentArrayList) {
        this.replyCommentArrayList = replyCommentArrayList;
    }

    private String comment;
    private String created_on;
    private String username;
    private String profile_image;

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    private String video_id;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    private String parent_id;
    private ArrayList<ReplyComment> replyCommentArrayList;

   public static class ReplyComment
   {
       private String comment;
       private String created_on;
       private String username;

       public String getComment() {
           return comment;
       }

       public void setComment(String comment) {
           this.comment = comment;
       }

       public String getCreated_on() {
           return created_on;
       }

       public void setCreated_on(String created_on) {
           this.created_on = created_on;
       }

       public String getUsername() {
           return username;
       }

       public void setUsername(String username) {
           this.username = username;
       }

       public String getProfile_image() {
           return profile_image;
       }

       public void setProfile_image(String profile_image) {
           this.profile_image = profile_image;
       }

       private String profile_image;
   }

}
