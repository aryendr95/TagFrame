package com.tagframe.tagframe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abhinav on 01/06/2016.
 */
public class Product implements Parcelable {

    @SerializedName("product_name")
    @Expose
    private String name;
    @SerializedName("product_image")
    @Expose
    private String image;

    public Product(String name, String image, String url, String id) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.id = id;
    }

    @SerializedName("product_url")
    @Expose

    private String url;
    @SerializedName("product_id")
    @Expose
    private String id;

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    @SerializedName("product_price")
    @Expose
    private String product_price;

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

    public Product() {
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
        dest.writeString(this.product_price);
    }

    protected Product(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.url = in.readString();
        this.id = in.readString();
        this.product_price = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
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
