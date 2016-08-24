package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Brajendr on 8/24/2016.
 */
public class TaggedUserModel implements Parcelable {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaggedUserModel(String name, String user_id, String profile_pic) {
        this.name = name;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("profile_pic")
    @Expose
    private String profile_pic;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.user_id);
        dest.writeString(this.profile_pic);
    }

    protected TaggedUserModel(Parcel in) {
        this.name = in.readString();
        this.user_id = in.readString();
        this.profile_pic = in.readString();
    }

    public static final Parcelable.Creator<TaggedUserModel> CREATOR = new Parcelable.Creator<TaggedUserModel>() {
        @Override
        public TaggedUserModel createFromParcel(Parcel source) {
            return new TaggedUserModel(source);
        }

        @Override
        public TaggedUserModel[] newArray(int size) {
            return new TaggedUserModel[size];
        }
    };
}
