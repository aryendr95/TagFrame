package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Brajendr on 8/16/2016.
 */
public class User implements Parcelable {


    private boolean isLoggedin=false;

    public boolean isLoggedin() {
        return isLoggedin;
    }

    public void setLoggedin(boolean loggedin) {
        isLoggedin = loggedin;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @SerializedName("blocked")
    @Expose

    private boolean blocked;

    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("username")
    @Expose
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SerializedName("password")
    @Expose

    private String password;
    @SerializedName("first_name")
    @Expose
    private String realname;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("profile_image")
    @Expose
    private String profile_image;
    @SerializedName("number_of_event")
    @Expose
    private String number_of_event;

    public String getNumber_of_timeline() {
        return number_of_timeline;
    }

    public void setNumber_of_timeline(String number_of_timeline) {
        this.number_of_timeline = number_of_timeline;
    }

    @SerializedName("number_of_frame")
    @Expose
    private String number_of_frame;
    @SerializedName("number_of_following")
    @Expose
    private String number_of_following;
    @SerializedName("number_of_timeline")
    @Expose
    private String number_of_timeline;
    @SerializedName("number_of_followers")
    @Expose
    private String number_of_followers;
    @SerializedName("followed")
    @Expose
    private String followed;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("unread_notifications")
    @Expose
    private String unreadnotifications;

    public String getUnreadnotifications() {
        return unreadnotifications;
    }

    public void setUnreadnotifications(String unreadnotifications) {
        this.unreadnotifications = unreadnotifications;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getNumber_of_event() {
        return number_of_event;
    }

    public void setNumber_of_event(String number_of_event) {
        this.number_of_event = number_of_event;
    }

    public String getNumber_of_frame() {
        return number_of_frame;
    }

    public void setNumber_of_frame(String number_of_frame) {
        this.number_of_frame = number_of_frame;
    }

    public String getNumber_of_following() {
        return number_of_following;
    }

    public void setNumber_of_following(String number_of_following) {
        this.number_of_following = number_of_following;
    }

    public String getNumber_of_followers() {
        return number_of_followers;
    }

    public void setNumber_of_followers(String number_of_followers) {
        this.number_of_followers = number_of_followers;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isLoggedin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.blocked ? (byte) 1 : (byte) 0);
        dest.writeString(this.user_id);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.realname);
        dest.writeString(this.description);
        dest.writeString(this.profile_image);
        dest.writeString(this.number_of_event);
        dest.writeString(this.number_of_frame);
        dest.writeString(this.number_of_following);
        dest.writeString(this.number_of_timeline);
        dest.writeString(this.number_of_followers);
        dest.writeString(this.followed);
        dest.writeString(this.email);
        dest.writeString(this.unreadnotifications);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.isLoggedin = in.readByte() != 0;
        this.blocked = in.readByte() != 0;
        this.user_id = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.realname = in.readString();
        this.description = in.readString();
        this.profile_image = in.readString();
        this.number_of_event = in.readString();
        this.number_of_frame = in.readString();
        this.number_of_following = in.readString();
        this.number_of_timeline = in.readString();
        this.number_of_followers = in.readString();
        this.followed = in.readString();
        this.email = in.readString();
        this.unreadnotifications = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override public User[] newArray(int size) {
            return new User[size];
        }
    };
}
