package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.tagframe.tagframe.Utils.Constants;
import java.io.Serializable;

/**
 * Created by abhinav on 27/04/2016.
 */
public class FrameList_Model implements Parcelable {

    public String getFrame_image_url() {
        return frame_image_url;
    }

    public void setFrame_image_url(String frame_image_url) {
        this.frame_image_url = frame_image_url;
    }


    private boolean edited = false;

    public boolean isAProductFrame() {
        return isAProductFrame;
    }

    public void setAProductFrame(boolean AProductFrame) {
        isAProductFrame = AProductFrame;
    }

    private boolean isAProductFrame=false;

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @SerializedName("frame_id")
    @Expose
    private String frame_id;
    @SerializedName("user_id")
    @Expose
    private String user_id="77";

    @SerializedName("frame_thumbnail_url")
    @Expose
    private String imagepath;

    @SerializedName("frame_image_url")
    @Expose
    private String frame_image_url;

    @SerializedName("frame_start_time")
    @Expose
    private int starttime;
    @SerializedName("frame_end_time")
    @Expose
    private int endtime;
    @SerializedName("frame_title")
    @Expose
    private String name;

    @SerializedName("product_image")
    @Expose
    private String product_path;


    @SerializedName("product_id")
    @Expose
    private String product_id="";

    @SerializedName("user_name")
    @Expose
    private String user_name="";

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @SerializedName("frame_product_url")
    @Expose
    private String product_url;
    @SerializedName("frame_resource_type")
    @Expose
    private String frame_resource_type= Constants.frame_resource_type_local;
    @SerializedName("frame_data_url")
    @Expose
    private String frame_data_url;
    @SerializedName("frame_media_type")
    @Expose
    private int frametype;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }




    public String getProduct_path() {
        return product_path;
    }

    public void setProduct_path(String product_path) {
        this.product_path = product_path;
    }


    public String getFrame_data_url() {
        return frame_data_url;
    }

    public void setFrame_data_url(String frame_data_url) {
        this.frame_data_url = frame_data_url;
    }


    public String getFrame_resource_type() {
        return frame_resource_type;
    }

    public void setFrame_resource_type(String frame_resource_type) {
        this.frame_resource_type = frame_resource_type;
    }

    public int getStarttime() {
        return starttime;
    }

    public void setStarttime(int starttime) {
        this.starttime = starttime;
    }

    public int getEndtime() {
        return endtime;
    }

    public void setEndtime(int endtime) {
        this.endtime = endtime;
    }

    public int getFrametype() {
        return frametype;

    }

    public void setFrametype(int frametype) {
        this.frametype = frametype;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }


    public FrameList_Model() {
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.edited ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAProductFrame ? (byte) 1 : (byte) 0);
        dest.writeString(this.frame_id);
        dest.writeString(this.user_id);
        dest.writeString(this.imagepath);
        dest.writeString(this.frame_image_url);
        dest.writeInt(this.starttime);
        dest.writeInt(this.endtime);
        dest.writeString(this.name);
        dest.writeString(this.product_path);
        dest.writeString(this.product_id);
        dest.writeString(this.user_name);
        dest.writeString(this.product_url);
        dest.writeString(this.frame_resource_type);
        dest.writeString(this.frame_data_url);
        dest.writeInt(this.frametype);
    }

    protected FrameList_Model(Parcel in) {
        this.edited = in.readByte() != 0;
        this.isAProductFrame = in.readByte() != 0;
        this.frame_id = in.readString();
        this.user_id = in.readString();
        this.imagepath = in.readString();
        this.frame_image_url = in.readString();
        this.starttime = in.readInt();
        this.endtime = in.readInt();
        this.name = in.readString();
        this.product_path = in.readString();
        this.product_id = in.readString();
        this.user_name = in.readString();
        this.product_url = in.readString();
        this.frame_resource_type = in.readString();
        this.frame_data_url = in.readString();
        this.frametype = in.readInt();
    }

    public static final Creator<FrameList_Model> CREATOR = new Creator<FrameList_Model>() {
        @Override public FrameList_Model createFromParcel(Parcel source) {
            return new FrameList_Model(source);
        }

        @Override public FrameList_Model[] newArray(int size) {
            return new FrameList_Model[size];
        }
    };

    @Override public String toString() {
        return starttime+":"+endtime;
    }
}
