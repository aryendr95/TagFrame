package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Brajendr on 8/23/2016.
 */
public class EndorseListModel {

    @SerializedName("user_name")
    @Expose
    private String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("from_user_id")
    @Expose
    private String from_user_id;

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    @SerializedName("to_user_id")
    @Expose

    private String to_user_id;

    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("product_image")
    @Expose
    private String product_image;

    @SerializedName("user_image")
    @Expose
    private String user_image;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @SerializedName("date")
    @Expose
    private String date;
}
