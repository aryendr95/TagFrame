package com.tagframe.tagframe.Retrofit;

import com.tagframe.tagframe.Adapters.CommentsResponseModel;
import com.tagframe.tagframe.Models.EndorseListResponseModel;
import com.tagframe.tagframe.Models.EventDetailResponseModel;
import com.tagframe.tagframe.Models.EventSuccessUploadResponseModel;
import com.tagframe.tagframe.Models.GetProductResponseModel;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.NotificationResponseModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.Models.ProfileResponseModel;
import com.tagframe.tagframe.Models.ResponseModels.RmAuthentication;
import com.tagframe.tagframe.Models.ResponseModels.RmFrameDetails;
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
  @GET("search_product/{page_number}") Call<GetProductResponseModel> getAllProducts(
      @Query("page_number") String page_number);

  //search product
  @GET("search_product/{product_title}") Call<GetProductResponseModel> searchProduct(
      @Query("product_title") String product_tittle, @Query("page_number") String page_number);

  //tagstream
  @GET("tagstreams/{user_id}") Call<ListResponseModel> getTagStream(
      @Query("user_id") String user_id);

  //tagstream
  @GET("tagstreams/{user_id}/{next_records}") Call<ListResponseModel> getTagStreamPaginated(
      @Query("user_id") String user_id, @Query("next_records") String next_records);

  //user_events
  @GET("user_events/{user_id}/{next_records}") Call<ListResponseModel> getUserEvents(
      @Query("user_id") String user_id, @Query("next_records") String next_records);

  //user_frames
  @GET("user_frames/{user_id}/{next_records}") Call<UserFrameResponseModel> getUserFrames(
      @Query("user_id") String user_id, @Query("next_records") String next_records);

  @GET("followers/{user_id}/{next_records}") Call<SearchUserResponseModel> getUserFollowers(
      @Query("user_id") String user_id, @Query("next_records") String next_records);

  @GET("following/{user_id}/{next_records}") Call<SearchUserResponseModel> getUserFollowing(
      @Query("user_id") String user_id, @Query("next_records") String next_records);

  //search
  @GET("search/{user_id}/{keyword}") Call<SearchUserResponseModel> searchUser(
      @Query("user_id") String user_id, @Query("keyword") String keyword);

  //direct Endorse
  @GET("direct_endorse/{from_user_id}/{to_user_id}/{product_id}/{message}")
  Call<ResponsePojo> directEndorse(@Query("from_user_id") String user_id,
      @Query("to_user_id") String to_user_id, @Query("product_id") String product_id,
      @Query("message") String message);

  @GET("direct_endorse_list/{from_user_id}") Call<EndorseListResponseModel> getDirectEndorseList(
      @Query("from_user_id") String user_id);

  @Multipart
  @POST("upload_video/{user_id}/{title}/{description}/{duration}/{event_type}/media_file")
  Call<EventSuccessUploadResponseModel> postEvent(@Part("user_id") RequestBody user_id,
      @Part("title") RequestBody title, @Part("description") RequestBody description,
      @Part("duration") RequestBody duration, @Part("event_type") RequestBody event_type,
      @Part("media_file\"; filename=\"media_file\" ") ProgressRequestBody media_file);

  @Multipart
  @POST("upload_video/{user_id}/{title}/{description}/{duration}/{event_type}/media_file")
  Call<EventSuccessUploadResponseModel> postEventRe(@Part("user_id") RequestBody user_id,
      @Part("title") RequestBody title, @Part("description") RequestBody description,
      @Part("duration") RequestBody duration, @Part("event_type") RequestBody event_type,
      @Part("media_file\"; filename=\"media_file\" ") RequestBody media_file);

  //timeline
  @GET("timeline/{user_id}/{next_records}") Call<ListResponseModel> getUserTimeLines(
      @Query("user_id") String user_id, @Query("next_records") String next_records);

  //  Notifications

  @GET("get_notification/{user_id}/{next_records}")
  Call<NotificationResponseModel> getNotifications(@Query("user_id") String user_id,
      @Query("next_records") String next_records);

  //profile_information
  @GET("profile/{user_id}/{logged_user_id}") Call<ProfileResponseModel> getProfileInfo(
      @Query("user_id") String user_id, @Query("logged_user_id") String logged_user_id);

  //event_details
  @GET("event_details/{event_id}") Call<EventDetailResponseModel> getEventDetails(
      @Query("event_id") String event_id);

  //load Comments
  @GET("comment_list/{video_id}/{next_records}") Call<CommentsResponseModel> getCommentList(
      @Query("video_id") String video_id, @Query("next_records") String next_records);

  //get product details
  @GET("product_details/{product_id}") Call<Product> getProductDetails(
      @Query("product_id") String product_id);

  @GET("logout/{user_id}/{device_id}") Call<ResponsePojo> logout(@Query("user_id") String user_id,
      @Query("device_id") String devide_id);

  @GET("update_token/{user_id}/{device_id}/{reg_token}") Call<ResponsePojo> update_Token(
      @Query("user_id") String user_id, @Query("device_id") String devide_id,
      @Query("reg_token") String token);

  @GET("remove_user/{user_id}") Call<ResponsePojo> removeUser(@Query("user_id") String user_id);

  @GET("read_notification/{notification_id}") Call<ResponsePojo> markAsRead(
      @Query("notification_id") String notification_id);

  @POST("login/{username}/{password}/{device_id}/{reg_token}") Call<RmAuthentication> login(
      @Query("username") String username, @Query("password") String password,
      @Query("device_id") String device_id, @Query("reg_token") String reg_token);


  @POST("login/{username}/{password}") Call<RmAuthentication> login(
      @Query("username") String usename, @Query("password") String password);

  @POST("signup/{username}/{password}/{device_id}/{email}/{first_name}/{reg_token}")
  Call<RmAuthentication> signUp(@Query("username") String username, @Query("password") String password,
      @Query("device_id") String device_id, @Query("email") String email,
      @Query("first_name") String first_name, @Query("reg_token") String reg_token);

  @POST("frame_details/{frame_id}")
  Call<RmFrameDetails> getFrameDetails(@Query("frame_id") String frame_id);

  //event_details
  @GET("comment_details/{comment_id}") Call<EventDetailResponseModel> getCommentDetails(
      @Query("comment_id") String event_id);

  @POST("delete_event/{event_id}")
  Call<ResponsePojo> delete_event(@Query("event_id") String event_id);

  @POST("delete_frame/{frame_id}/{user_id}")
  Call<ResponsePojo> delete_frame(@Query("frame_id") String event_id,
      @Query("user_id") String user_id);
}
