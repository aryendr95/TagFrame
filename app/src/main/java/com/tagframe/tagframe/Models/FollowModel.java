package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abhinav on 11/04/2016.
 */
public class FollowModel implements Parcelable {

    @SerializedName("to_user_id")
    @Expose
    private String userid;


    @SerializedName("is_following")
    @Expose
    private boolean isFollowing;

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("username")
    @Expose
    private String user_name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("image")
    @Expose
    private String image;

    private String number;
    @SerializedName("user_id")
    @Expose
    private String from_user_id;
    @SerializedName("followed")
    @Expose
    private String is_followed;

    public String getIs_followed() {
        return is_followed;
    }

    public void setIs_followed(String is_followed) {
        this.is_followed = is_followed;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String real_name) {
        this.user_name = real_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userid);
        dest.writeByte(this.isFollowing ? (byte) 1 : (byte) 0);
        dest.writeString(this.first_name);
        dest.writeString(this.user_name);
        dest.writeString(this.email);
        dest.writeString(this.image);
        dest.writeString(this.number);
        dest.writeString(this.from_user_id);
        dest.writeString(this.is_followed);
    }

    public FollowModel() {
    }

    protected FollowModel(Parcel in) {
        this.userid = in.readString();
        this.isFollowing = in.readByte() != 0;
        this.first_name = in.readString();
        this.user_name = in.readString();
        this.email = in.readString();
        this.image = in.readString();
        this.number = in.readString();
        this.from_user_id = in.readString();
        this.is_followed = in.readString();
    }

    public static final Parcelable.Creator<FollowModel> CREATOR =
            new Parcelable.Creator<FollowModel>() {
                @Override
                public FollowModel createFromParcel(Parcel source) {
                    return new FollowModel(source);
                }

                @Override
                public FollowModel[] newArray(int size) {
                    return new FollowModel[size];
                }
            };
}
