package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Brajendr on 8/26/2016.
 */
public class NotificationModel implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private String user_id;

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    @SerializedName("is_read")

    @Expose
    private String is_read;

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

    public String getSub_action_message() {
        return sub_action_message;
    }

    public void setSub_action_message(String sub_action_message) {
        this.sub_action_message = sub_action_message;
    }

    @SerializedName("sub_action_name")

    @Expose
    private String sub_action_name;

    @SerializedName("sub_action_message")
    @Expose
    private String sub_action_message;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user_id);
        dest.writeString(this.is_read);
        dest.writeString(this.id);
        dest.writeString(this.created_on);
        dest.writeString(this.action_type);
        dest.writeString(this.user_name);
        dest.writeString(this.profile_pic);
        dest.writeString(this.sub_action_type);
        dest.writeString(this.sub_action_id);
        dest.writeString(this.sub_action_name);
        dest.writeString(this.sub_action_message);
    }

    public NotificationModel() {
    }

    protected NotificationModel(Parcel in) {
        this.user_id = in.readString();
        this.is_read = in.readString();
        this.id = in.readString();
        this.created_on = in.readString();
        this.action_type = in.readString();
        this.user_name = in.readString();
        this.profile_pic = in.readString();
        this.sub_action_type = in.readString();
        this.sub_action_id = in.readString();
        this.sub_action_name = in.readString();
        this.sub_action_message = in.readString();
    }

    public static final Parcelable.Creator<NotificationModel> CREATOR =
            new Parcelable.Creator<NotificationModel>() {
                @Override
                public NotificationModel createFromParcel(Parcel source) {
                    return new NotificationModel(source);
                }

                @Override
                public NotificationModel[] newArray(int size) {
                    return new NotificationModel[size];
                }
            };
}
