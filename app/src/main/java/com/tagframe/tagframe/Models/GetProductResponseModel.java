package com.tagframe.tagframe.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Brajendr on 8/10/2016.
 */
public class GetProductResponseModel {

    public GetProductResponseModel(List<Product> productList, String status) {
        this.productList = productList;
        this.status = status;
    }

    @SerializedName("records")

    @Expose
    private List<Product> productList;

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
