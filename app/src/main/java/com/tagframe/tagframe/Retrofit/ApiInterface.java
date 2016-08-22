package com.tagframe.tagframe.Retrofit;

import com.tagframe.tagframe.Models.GetProductResponseModel;
import com.tagframe.tagframe.Models.ListResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Brajendr on 8/10/2016.
 */
public interface ApiInterface {

    @GET("search_product/{page_number}")
    Call<GetProductResponseModel> getAllProducts(@Query("page_number") String page_number);

    @GET("search_product/{product_title}")
    Call<GetProductResponseModel> searchProduct(@Query("product_title") String product_tittle,@Query("page_number") String page_number);

    @GET("tagstreams/{user_id}")
    Call<ListResponseModel> getTagStream(@Query("user_id") String user_id);

    @GET("tagstreams/{user_id}/{next_records}")
    Call<ListResponseModel> getTagStreamPaginated(@Query("user_id") String user_id, @Query("next_records")String next_records);

    @GET("user_events/{user_id}/{next_records}")
    Call<ListResponseModel> getUserEvents(@Query("user_id") String user_id, @Query("next_records")String next_records);

}
