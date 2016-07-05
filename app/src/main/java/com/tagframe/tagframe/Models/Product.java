package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abhinav on 01/06/2016.
 */
public class Product implements Parcelable {
    private String name;
    private String image;
    private String url;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.url);
        dest.writeString(this.id);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.url = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
