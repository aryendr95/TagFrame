package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Brajendr on 8/26/2016.
 */
public class NotificationModel {

    @SerializedName("user_id")
    @Expose
    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("created_on")
    @Expose
    private String created_on;

    @SerializedName("action_type")
    @Expose
    private String action_type;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getSub_action_type() {
        return sub_action_type;
    }

    public void setSub_action_type(String sub_action_type) {
        this.sub_action_type = sub_action_type;
    }

    public String getSub_action_id() {
        return sub_action_id;
    }

    public void setSub_action_id(String sub_action_id) {
        this.sub_action_id = sub_action_id;
    }

    public String getSub_action_name() {
        return sub_action_name;
    }

    public void setSub_action_name(String sub_action_name) {
        this.sub_action_name = sub_action_name;
    }

    @SerializedName("username")

    @Expose
    private String user_name;

    @SerializedName("profile_pic")
    @Expose
    private String profile_pic;

    @SerializedName("sub_action_type")
    @Expose
    private String sub_action_type;

    @SerializedName("sub_action_id")
    @Expose
    private String sub_action_id;

    @SerializedName("sub_action_name")
    @Expose
    private String sub_action_name;

}
