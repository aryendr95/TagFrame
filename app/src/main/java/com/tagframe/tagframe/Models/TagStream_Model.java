package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by abhinav on 08/04/2016.
 */
public class TagStream_Model implements Parcelable {

    private String name;
    private String profile_picture;
    private String created_at;
    private String thumbnail;
    private String dataurl;
    private String title;
    private String event_id;
    private String description;
    private String product_url;
    private String product_image;
    private String prduct_name;
    private String number_of_likes;
    private String sharelink;

    private String like_video;
    private ArrayList<FrameList_Model> frameList_modelArrayList;

    public String getLike_video() {
        return like_video;
    }

    public void setLike_video(String like_video) {
        this.like_video = like_video;
    }

    public ArrayList<FrameList_Model> getFrameList_modelArrayList() {
        return frameList_modelArrayList;
    }

    public void setFrameList_modelArrayList(ArrayList<FrameList_Model> frameList_modelArrayList) {
        this.frameList_modelArrayList = frameList_modelArrayList;
    }

    public String getNumber_of_likes() {
        return number_of_likes;
    }

    public void setNumber_of_likes(String number_of_likes) {
        this.number_of_likes = number_of_likes;
    }

    public String getSharelink() {
        return sharelink;
    }

    public void setSharelink(String sharelink) {
        this.sharelink = sharelink;
    }

    public String getName() {
        return name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDataurl() {
        return dataurl;
    }

    public void setDataurl(String dataurl) {
        this.dataurl = dataurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getPrduct_name() {
        return prduct_name;
    }

    public void setPrduct_name(String prduct_name) {
        this.prduct_name = prduct_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.profile_picture);
        dest.writeString(this.created_at);
        dest.writeString(this.thumbnail);
        dest.writeString(this.dataurl);
        dest.writeString(this.title);
        dest.writeString(this.event_id);
        dest.writeString(this.description);
        dest.writeString(this.product_url);
        dest.writeString(this.product_image);
        dest.writeString(this.prduct_name);
        dest.writeString(this.number_of_likes);
        dest.writeString(this.sharelink);
        dest.writeString(this.like_video);
        dest.writeTypedList(frameList_modelArrayList);
    }

    public TagStream_Model() {
    }

    protected TagStream_Model(Parcel in) {
        this.name = in.readString();
        this.profile_picture = in.readString();
        this.created_at = in.readString();
        this.thumbnail = in.readString();
        this.dataurl = in.readString();
        this.title = in.readString();
        this.event_id = in.readString();
        this.description = in.readString();
        this.product_url = in.readString();
        this.product_image = in.readString();
        this.prduct_name = in.readString();
        this.number_of_likes = in.readString();
        this.sharelink = in.readString();
        this.like_video = in.readString();
        this.frameList_modelArrayList = in.createTypedArrayList(FrameList_Model.CREATOR);
    }

    public static final Parcelable.Creator<TagStream_Model> CREATOR = new Parcelable.Creator<TagStream_Model>() {
        @Override
        public TagStream_Model createFromParcel(Parcel source) {
            return new TagStream_Model(source);
        }

        @Override
        public TagStream_Model[] newArray(int size) {
            return new TagStream_Model[size];
        }
    };
}
