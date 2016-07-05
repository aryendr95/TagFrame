package com.tagframe.tagframe.Models;

import java.util.ArrayList;

/**
 * Created by abhinav on _13/04/2016.
 */
public class User_Frames_model {

    private String frame_id;
    private String video_id;
    private String title;
    private String product_id;
    private String media_type;
    private String name;
    private String thumbnail_url;
    private String data_url;
    private String number_of_frames;
    private String created_on;
    private ArrayList<FrameList_Model> frameList_modelArrayList;

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
}
