package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Brajendr on 8/26/2016.
 */
public class NotificationResponseModel {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("unread_notifications")
    @Expose
    private String unread_notifications;

    public String getUnread_notifications() {
        return unread_notifications;
    }

    public void setUnread_notifications(String unread_notifications) {
        this.unread_notifications = unread_notifications;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<NotificationModel> getNotificationModelArrayList() {
        return notificationModelArrayList;
    }

    public void setNotificationModelArrayList(ArrayList<NotificationModel> notificationModelArrayList) {
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @SerializedName("notificationData")
    @Expose
    private ArrayList<NotificationModel> notificationModelArrayList;


}
