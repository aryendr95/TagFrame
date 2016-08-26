package com.tagframe.tagframe.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by abhinav on 03/05/2016.
 */
public class SingleEventModel implements Parcelable {

    private String tittle;
    private String vidaddress;
    private String time;
    private Uri event_uri;
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Uri getEvent_uri() {
        return event_uri;
    }

    public void setEvent_uri(Uri event_uri) {
        this.event_uri = event_uri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVidaddress() {
        return vidaddress;
    }

    public void setVidaddress(String vidaddress) {
        this.vidaddress = vidaddress;
    }


    private String type;
    private String description;
    private ArrayList<FrameList_Model> frameList_modelArrayList;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<FrameList_Model> getFrameList_modelArrayList() {
        return frameList_modelArrayList;
    }

    public void setFrameList_modelArrayList(ArrayList<FrameList_Model> frameList_modelArrayList) {
        this.frameList_modelArrayList = frameList_modelArrayList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public ArrayList<TaggedUserModel> getTaggedUserModelArrayList() {
        return taggedUserModelArrayList;
    }

    public void setTaggedUserModelArrayList(ArrayList<TaggedUserModel> taggedUserModelArrayList) {
        this.taggedUserModelArrayList = taggedUserModelArrayList;
    }

    private ArrayList<TaggedUserModel> taggedUserModelArrayList;


    public SingleEventModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tittle);
        dest.writeString(this.vidaddress);
        dest.writeString(this.time);
        dest.writeParcelable(this.event_uri, flags);
        dest.writeInt(this.duration);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeTypedList(this.frameList_modelArrayList);
        dest.writeTypedList(this.taggedUserModelArrayList);
    }

    protected SingleEventModel(Parcel in) {
        this.tittle = in.readString();
        this.vidaddress = in.readString();
        this.time = in.readString();
        this.event_uri = in.readParcelable(Uri.class.getClassLoader());
        this.duration = in.readInt();
        this.type = in.readString();
        this.description = in.readString();
        this.frameList_modelArrayList = in.createTypedArrayList(FrameList_Model.CREATOR);
        this.taggedUserModelArrayList = in.createTypedArrayList(TaggedUserModel.CREATOR);
    }

    public static final Creator<SingleEventModel> CREATOR = new Creator<SingleEventModel>() {
        @Override
        public SingleEventModel createFromParcel(Parcel source) {
            return new SingleEventModel(source);
        }

        @Override
        public SingleEventModel[] newArray(int size) {
            return new SingleEventModel[size];
        }
    };
}
