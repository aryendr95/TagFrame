package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by abhinav on _13/04/2016.
 */
public class User_Frames_model implements Parcelable {

    public boolean is_product_frame() {
        return is_product_frame;
    }

    public void setIs_product_frame(boolean is_product_frame) {
        this.is_product_frame = is_product_frame;
    }

    @SerializedName("image_url")
    @Expose

    private String frame_image_url;

    @SerializedName("event_user_id")
    @Expose

    private String event_user_id;

    public String getEvent_user_id() {
        return event_user_id;
    }

    public void setEvent_user_id(String event_user_id) {
        this.event_user_id = event_user_id;
    }

    public String getProduct_image_url() {
        return product_image_url;
    }

    public void setProduct_image_url(String product_image_url) {
        this.product_image_url = product_image_url;
    }

    @SerializedName("product_image_url")
    @Expose

    private String product_image_url;

    @SerializedName("is_product_frame")
    private boolean is_product_frame = false;

    @SerializedName("product_url")
    @Expose
    private String product_url;


    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public String getFrame_image_url() {
        return frame_image_url;
    }

    public void setFrame_image_url(String frame_image_url) {
        this.frame_image_url = frame_image_url;
    }

    @Expose
    @SerializedName("frame_id")
    private String frame_id;
    @Expose
    @SerializedName("video_id")
    private String video_id;
    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("product_id")
    private String product_id;
    @Expose
    @SerializedName("media_type")
    private String media_type;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("thumbnail_url")
    private String thumbnail_url;
    @Expose
    @SerializedName("data_url")
    private String data_url;
    @Expose
    @SerializedName("number_of_frames")
    private String number_of_frames;
    @Expose
    @SerializedName("created_on")
    private String created_on;

    @Expose
    @SerializedName("num_of_comments")
    private String number_of_comments;

    @Expose
    @SerializedName("user_id")
    private String user_id;

    @Expose
    @SerializedName("website_video_url")
    private String share_link;

    @Expose
    @SerializedName("count_like")
    private String number_of_likes;

    public String getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    public String getNumber_of_comments() {
        return number_of_comments;
    }

    public void setNumber_of_comments(String number_of_comments) {
        this.number_of_comments = number_of_comments;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShare_link() {
        return share_link;
    }

    public void setShare_link(String share_link) {
        this.share_link = share_link;
    }

    public String getNumber_of_likes() {
        return number_of_likes;
    }

    public void setNumber_of_likes(String number_of_likes) {
        this.number_of_likes = number_of_likes;
    }

    @Expose

    @SerializedName("is_liked")
    private String is_liked;


    @Expose
    @SerializedName("frames")
    private ArrayList<FrameList_Model> frameList_modelArrayList;
    @SerializedName("tagged_user_id")

    @Expose
    private ArrayList<TaggedUserModel> taggedUserModelArrayList;

    public ArrayList<TaggedUserModel> getTaggedUserModelArrayList() {
        return taggedUserModelArrayList;
    }

    public void setTaggedUserModelArrayList(ArrayList<TaggedUserModel> taggedUserModelArrayList) {
        this.taggedUserModelArrayList = taggedUserModelArrayList;
    }

    public ArrayList<FrameList_Model> getFrameList_modelArrayList() {
        return frameList_modelArrayList;
    }

    public void setFrameList_modelArrayList(ArrayList<FrameList_Model> frameList_modelArrayList) {
        this.frameList_modelArrayList = frameList_modelArrayList;
    }

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getData_url() {
        return data_url;
    }

    public void setData_url(String data_url) {
        this.data_url = data_url;
    }

    public String getNumber_of_frames() {
        return number_of_frames;
    }

    public void setNumber_of_frames(String number_of_frames) {
        this.number_of_frames = number_of_frames;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.frame_image_url);
        dest.writeString(this.event_user_id);
        dest.writeString(this.product_image_url);
        dest.writeByte(this.is_product_frame ? (byte) 1 : (byte) 0);
        dest.writeString(this.product_url);
        dest.writeString(this.frame_id);
        dest.writeString(this.video_id);
        dest.writeString(this.title);
        dest.writeString(this.product_id);
        dest.writeString(this.media_type);
        dest.writeString(this.name);
        dest.writeString(this.thumbnail_url);
        dest.writeString(this.data_url);
        dest.writeString(this.number_of_frames);
        dest.writeString(this.created_on);
        dest.writeString(this.number_of_comments);
        dest.writeString(this.user_id);
        dest.writeString(this.share_link);
        dest.writeString(this.number_of_likes);
        dest.writeString(this.is_liked);
        dest.writeTypedList(this.frameList_modelArrayList);
        dest.writeTypedList(this.taggedUserModelArrayList);
    }

    public User_Frames_model() {
    }

    protected User_Frames_model(Parcel in) {
        this.frame_image_url = in.readString();
        this.event_user_id = in.readString();
        this.product_image_url = in.readString();
        this.is_product_frame = in.readByte() != 0;
        this.product_url = in.readString();
        this.frame_id = in.readString();
        this.video_id = in.readString();
        this.title = in.readString();
        this.product_id = in.readString();
        this.media_type = in.readString();
        this.name = in.readString();
        this.thumbnail_url = in.readString();
        this.data_url = in.readString();
        this.number_of_frames = in.readString();
        this.created_on = in.readString();
        this.number_of_comments = in.readString();
        this.user_id = in.readString();
        this.share_link = in.readString();
        this.number_of_likes = in.readString();
        this.is_liked = in.readString();
        this.frameList_modelArrayList = in.createTypedArrayList(FrameList_Model.CREATOR);
        this.taggedUserModelArrayList = in.createTypedArrayList(TaggedUserModel.CREATOR);
    }

    public static final Parcelable.Creator<User_Frames_model> CREATOR =
            new Parcelable.Creator<User_Frames_model>() {
                @Override
                public User_Frames_model createFromParcel(Parcel source) {
                    return new User_Frames_model(source);
                }

                @Override
                public User_Frames_model[] newArray(int size) {
                    return new User_Frames_model[size];
                }
            };
}
