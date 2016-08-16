package com.tagframe.tagframe.Models;

/**
 * Created by Brajendr on 8/16/2016.
 */
public class User {

    private String user_id;
    private String username;
    private String realname;
    private String description;
    private String profile_image;
    private String number_of_event;
    private String number_of_frame;
    private String number_of_following;
    private String number_of_followers;
    private String followed;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getNumber_of_event() {
        return number_of_event;
    }

    public void setNumber_of_event(String number_of_event) {
        this.number_of_event = number_of_event;
    }

    public String getNumber_of_frame() {
        return number_of_frame;
    }

    public void setNumber_of_frame(String number_of_frame) {
        this.number_of_frame = number_of_frame;
    }

    public String getNumber_of_following() {
        return number_of_following;
    }

    public void setNumber_of_following(String number_of_following) {
        this.number_of_following = number_of_following;
    }

    public String getNumber_of_followers() {
        return number_of_followers;
    }

    public void setNumber_of_followers(String number_of_followers) {
        this.number_of_followers = number_of_followers;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }
}
