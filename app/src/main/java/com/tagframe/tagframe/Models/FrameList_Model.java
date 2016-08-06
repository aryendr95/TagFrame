package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by abhinav on 27/04/2016.
 */
public class FrameList_Model implements Parcelable {

    private String imagepath;
    private int starttime;
    private int endtime;
    private String name;
    private boolean is_product_attached;
    private String product_path;
    private String product_id;
    private String product_url;

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



    public boolean is_product_attached() {
        return is_product_attached;
    }

    public void setIs_product_attached(boolean is_product_attached) {
        this.is_product_attached = is_product_attached;
    }

    public String getProduct_path() {
        return product_path;
    }

    public void setProduct_path(String product_path) {
        this.product_path = product_path;
    }

    private String frame_resource_type;
    public String getFrame_data_url() {
        return frame_data_url;
    }

    public void setFrame_data_url(String frame_data_url) {
        this.frame_data_url = frame_data_url;
    }

    private String frame_data_url;

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

    private int frametype;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagepath);
        dest.writeInt(this.starttime);
        dest.writeInt(this.endtime);
        dest.writeString(this.name);
        dest.writeByte(this.is_product_attached ? (byte) 1 : (byte) 0);
        dest.writeString(this.product_path);
        dest.writeString(this.product_id);
        dest.writeString(this.product_url);
        dest.writeString(this.frame_resource_type);
        dest.writeString(this.frame_data_url);
        dest.writeInt(this.frametype);
    }

    protected FrameList_Model(Parcel in) {
        this.imagepath = in.readString();
        this.starttime = in.readInt();
        this.endtime = in.readInt();
        this.name = in.readString();
        this.is_product_attached = in.readByte() != 0;
        this.product_path = in.readString();
        this.product_id = in.readString();
        this.product_url = in.readString();
        this.frame_resource_type = in.readString();
        this.frame_data_url = in.readString();
        this.frametype = in.readInt();
    }

    public static final Creator<FrameList_Model> CREATOR = new Creator<FrameList_Model>() {
        @Override
        public FrameList_Model createFromParcel(Parcel source) {
            return new FrameList_Model(source);
        }

        @Override
        public FrameList_Model[] newArray(int size) {
            return new FrameList_Model[size];
        }
    };
}
