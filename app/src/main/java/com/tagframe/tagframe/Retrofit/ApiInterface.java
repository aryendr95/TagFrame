package com.tagframe.tagframe.Retrofit;

import com.tagframe.tagframe.Models.EndorseListResponseModel;
import com.tagframe.tagframe.Models.EventSuccessUploadResponseModel;
import com.tagframe.tagframe.Models.GetProductResponseModel;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.NotificationResponseModel;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Models.SearchUserResponseModel;
import com.tagframe.tagframe.Models.UserFrameResponseModel;
import com.tagframe.tagframe.Utils.ProgressRequestBody;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Brajendr on 8/10/2016.
 */
public interface ApiInterface {

    //search product
    @GET("search_product/{page_number}")
    Call<GetProductResponseModel> getAllProducts(@Query("page_number") String page_number);

    //search product
    @GET("search_product/{product_title}")
    Call<GetProductResponseModel> searchProduct(@Query("product_title") String product_tittle, @Query("page_number") String page_number);

    //tagstream
    @GET("tagstreams/{user_id}")
    Call<ListResponseModel> getTagStream(@Query("user_id") String user_id);

    //tagstream
    @GET("tagstreams/{user_id}/{next_records}")
    Call<ListResponseModel> getTagStreamPaginated(@Query("user_id") String user_id, @Query("next_records") String next_records);

    //user_events
    @GET("user_events/{user_id}/{next_records}")
    Call<ListResponseModel> getUserEvents(@Query("user_id") String user_id, @Query("next_records") String next_records);

    //user_frames
    @GET("user_frames/{user_id}/{next_records}")
    Call<UserFrameResponseModel> getUserFrames(@Query("user_id") String user_id, @Query("next_records") String next_records);

    @GET("followers/{user_id}/{next_records}")
    Call<SearchUserResponseModel> getUserFollowers(@Query("user_id") String user_id, @Query("next_records") String next_records);

    @GET("following/{user_id}/{next_records}")
    Call<SearchUserResponseModel> getUserFollowing(@Query("user_id") String user_id, @Query("next_records") String next_records);

    //search
    @GET("search/{user_id}/{keyword}")
    Call<SearchUserResponseModel> searchUser(@Query("user_id") String user_id, @Query("keyword") String keyword);

    //direct Endorse
    @GET("direct_endorse/{from_user_id}/{to_user_id}/{product_id}/{message}")
    Call<ResponsePojo> directEndorse(@Query("from_user_id") String user_id, @Query("to_user_id") String to_user_id, @Query("product_id") String product_id, @Query("message") String message);


    @GET("direct_endorse_list/{from_user_id}")
    Call<EndorseListResponseModel> getDirectEndorseList(@Query("from_user_id") String user_id);

    @Multipart
    @POST("upload_video/{user_id}/{title}/{description}/{duration}/{event_type}/media_file")
    Call<EventSuccessUploadResponseModel> postEvent(@Part("user_id") RequestBody user_id,
                                                    @Part("title") RequestBody title,
                                                    @Part("description") RequestBody description,
                                                    @Part("duration") RequestBody duration,
                                                    @Part("event_type") RequestBody event_type,
                                                    @Part("media_file\"; filename=\"media_file\" ") ProgressRequestBody media_file);

    @Multipart
    @POST("upload_video/{user_id}/{title}/{description}/{duration}/{event_type}/media_file")
    Call<EventSuccessUploadResponseModel> postEventRe(@Part("user_id") RequestBody user_id,
                                                    @Part("title") RequestBody title,
                                                    @Part("description") RequestBody description,
                                                    @Part("duration") RequestBody duration,
                                                    @Part("event_type") RequestBody event_type,
                                                    @Part("media_file\"; filename=\"media_file\" ") RequestBody media_file);



    //timeline
    @GET("timeline/{user_id}/{next_records}")
    Call<ListResponseModel> getUserTimeLines(@Query("user_id") String user_id, @Query("next_records") String next_records);

    //  Notifications

    @GET("get_notification/{user_id}")
    Call<NotificationResponseModel> getNotifications(@Query("user_id") String user_id);


}