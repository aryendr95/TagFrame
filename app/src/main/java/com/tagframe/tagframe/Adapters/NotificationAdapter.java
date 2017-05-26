package com.tagframe.tagframe.Adapters;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tagframe.tagframe.Models.Comment;
import com.tagframe.tagframe.Models.EventDetailResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.NotificationModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.Models.ResponseModels.RmFrameDetails;
import com.tagframe.tagframe.Models.ResponsePojo;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.UI.Acitivity.WatchEventActivity;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.ImageLoader;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brajendr on 8/26/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationModel> ListModels;
    private Context context;
    private ProgressDialog dialog;
    private int next_rec = 0;
    private boolean isAdded = false;
    private boolean areCommentsLoaded = false;

    public NotificationAdapter(List<NotificationModel> ListModels, Context context) {
        this.ListModels = ListModels;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_notifications, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final NotificationModel ListModel = ListModels.get(position);


        holder.txt_username.setText(ListModel.getUser_name());
        holder.txt_action_type.setText(ListModel.getAction_type());
        holder.txt_sub_action_name.setText(ListModel.getSub_action_name());
        holder.txt_date.setText(ListModel.getCreated_on());
        ImageLoader.loadImageOnline(context,ListModel.getProfile_pic(),holder.img_user,holder.pbar,R.drawable.pro_image);


        final AppPrefs appPrefs = new AppPrefs(context);
        final int user_type = Utility.getUser_Type(ListModel.getUser_id(), appPrefs.getString(Utility.user_id));
        if(ListModel.getSub_action_id().equals(appPrefs.getString(Utility.user_id)))
        {
            holder.txt_sub_action_name.setText("you");
        }

        holder.layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListModel.setIs_read("yes");
                notifyDataSetChanged();
                final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                apiInterface.markAsRead(ListModel.getId()).enqueue(new Callback<ResponsePojo>() {
                    @Override public void onResponse(Call<ResponsePojo> call,
                        Response<ResponsePojo> response) {
                        if(response.body().getStatus().equals(Utility.success_response))
                        {
                            User user=appPrefs.getUser();
                            Integer unread=Integer.parseInt(user.getUnreadnotifications());
                            if(unread<=0)
                            {
                                unread=0;
                            }
                            else
                            {
                                unread=unread-1;
                            }
                            user.setUnreadnotifications(String.valueOf((unread)));
                            appPrefs.putUser(user);

                            switch (ListModel.getSub_action_type())
                            {
                                case Utility.notification_op_watch_event:
                                    loadEvent(apiInterface,ListModel.getSub_action_id());
                                    break;
                                case Utility.notification_op_watch_frame:
                                    loadFrame(apiInterface,ListModel.getSub_action_id());
                                    break;
                                case Utility.notification_op_watch_comment:
                                    loadComment(apiInterface,ListModel.getSub_action_id());
                                    break;
                                case Utility.notification_op_watch_product:
                                    loadProduct(apiInterface,ListModel.getSub_action_id());
                                    break;
                                case Utility.notification_op_watch_profile:
                                    ((Modules) context).setprofile(ListModel.getSub_action_id(), user_type);
                                    break;
                                default: 
                                    break;
                            }
                        }
                    }

                    @Override public void onFailure(Call<ResponsePojo> call, Throwable t) {

                    }
                });
            }
        });


        //click listners on profile photo and nam
        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Modules) context).setprofile(ListModel.getUser_id(), user_type);
            }
        });

        holder.txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Modules) context).setprofile(ListModel.getUser_id(), user_type);
            }
        });

        if(ListModel.getIs_read().equalsIgnoreCase("no"))
        {
            holder.layout_back.setBackgroundColor(Color.parseColor("#e5e5e5"));
        }
        else
        {
            holder.layout_back.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    private void loadComment(ApiInterface apiInterface, final String sub_action_id) {
        AppPrefs appPrefs=new AppPrefs(context);
        User user=appPrefs.getUser();
        showCommentDialog(context,sub_action_id,user);
    }

    private void loadFrame(ApiInterface apiInterface, String sub_action_id) {
        AppPrefs appPrefs=new AppPrefs(context);
        showProgress("Loading Frame..");
        apiInterface.getFrameDetails(sub_action_id,appPrefs.getUser().getUser_id()).enqueue(new Callback<RmFrameDetails>() {
            @Override
            public void onResponse(Call<RmFrameDetails> call, Response<RmFrameDetails> response) {
                hideProgress();
                if(response.body().getStatus().equals("success"))
                {
                    User_Frames_model frame=response.body().getFrameList_model();
                    ImageAdapter.showFrame(context,frame);
                }

            }

            @Override public void onFailure(Call<RmFrameDetails> call, Throwable t) {
                hideProgress();
            }
        });
    }

    private void loadProduct(ApiInterface apiInterface, String sub_action_id) {
        showProgress("Loading Product");
        apiInterface.getProductDetails(sub_action_id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                hideProgress();
                Product product=(Product)response.body();
                ProductAdapter.showProductDialog(context,product);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                hideProgress();
                PopMessage.makeshorttoast(context,"Error loading product");
            }
        });
    }

    private void loadEvent(ApiInterface apiInterface, final String id) {
        showProgress("Loading Event");


        apiInterface.getEventDetails(id).enqueue(new Callback<EventDetailResponseModel>() {
            @Override
            public void onResponse(Call<EventDetailResponseModel> call, Response<EventDetailResponseModel> response) {
                dialog.dismiss();
                if (response.body().getStatus().equals(Utility.success_response)) {

                    Log.e("event",new Gson().toJson(response));
                    Event_Model event_model = response.body().getEvent_model();
                    Intent intent = new Intent(context, WatchEventActivity.class);
                    intent.putExtra("data_url", event_model.getDataurl());
                    intent.putExtra("tittle", event_model.getTitle());
                    intent.putExtra("from", "notification");
                    intent.putExtra("description", "");
                    intent.putParcelableArrayListExtra("framelist", event_model.getFrameList_modelArrayList());
                    intent.putExtra("eventtype", Utility.eventtype_internet);
                    intent.putExtra("eventid", id);
                    intent.putExtra("tagged_user_id",event_model.getTaggedUserModelArrayList());

                    intent.putExtra("name",event_model.getName());
                    //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
                    intent.putExtra("likes", event_model.getNumber_of_likes());
                    intent.putExtra("frames", event_model.getFrameList_modelArrayList().size());
                    intent.putExtra("comments", event_model.getNum_of_comments());
                    intent.putExtra("data_url", event_model.getDataurl());
                    intent.putExtra("tittle", event_model.getTitle());
                    intent.putExtra("from", "tagstream");
                    intent.putExtra("user_id", event_model.getUser_id());
                    intent.putExtra("description", event_model.getDescription());
                    intent.putParcelableArrayListExtra("framelist", event_model.getFrameList_modelArrayList());
                    intent.putExtra("eventtype", Utility.eventtype_internet);
                    intent.putExtra("eventid", event_model.getEvent_id());
                    intent.putExtra("sharelink", event_model.getSharelink());
                    intent.putExtra("likevideo", event_model.getLike_video());
                    intent.putExtra("tagged_user_id", event_model.getTaggedUserModelArrayList());

                    context.startActivity(intent);

                } else {
                    PopMessage.makeshorttoast(context, "Error, try after some time");
                }
            }

            @Override
            public void onFailure(Call<EventDetailResponseModel> call, Throwable t) {
                PopMessage.makeshorttoast(context, "Error loading, try after some time");
                hideProgress();
            }
        });
    }

    private void hideProgress() {
        dialog.dismiss();
    }

    public void showProgress(String message)
    {
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return ListModels.size();
    }


    public void showCommentDialog(final Context ctx, final String video, final User user) {

        next_rec = 0;
        isAdded = true;

        final Dialog dialog = new Dialog(ctx, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.setCancelable(true);

        final RecyclerView listview_comment = (RecyclerView) dialog.findViewById(R.id.list_comment);
        listview_comment.setNestedScrollingEnabled(false);
        final EditText editext_comment = (EditText) dialog.findViewById(R.id.ed_dialog_comment);
        final LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.mlayout_dialog_comment);
        ImageButton img_send_comment = (ImageButton) dialog.findViewById(R.id.img_dialog_send_comment);
        final ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.pbar_comment);

        final TextView m_txt_footer = (TextView) dialog.findViewById(R.id.txt_footer);

        //set Adapter to commentList
        final ArrayList<Comment> commentArrayList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        listview_comment.setLayoutManager(mLayoutManager);
        listview_comment.setItemAnimator(new DefaultItemAnimator());
        listview_comment.setAdapter(new CommentsRecyclerViewAdapter(commentArrayList, ctx));
        //load comment task

        m_txt_footer.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                loadComments(video, String.valueOf(next_rec), progressbar, m_txt_footer, listview_comment, commentArrayList);
                                            }
                                        }

        );

        loadComments(video, String.valueOf(next_rec), progressbar, m_txt_footer, listview_comment, commentArrayList);
        //cancelling dialog
        dialog.findViewById(R.id.img_comment_dialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdded = false;
                dialog.cancel();
            }
        });


        //send the comment
        img_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editext_comment.getText().toString().isEmpty()) {


                    if (areCommentsLoaded) {
                        // My AsyncTask is done and onPostExecute was called

                        Broadcastresults mReceiver = ((Modules) ctx).register_reviever();

                        Intent intent = new Intent(ctx, IntentServiceOperations.class);
                        intent.putExtra("operation", Utility.operation_comment);
                        intent.putExtra("user_id", user.getUser_id());
                        intent.putExtra("video_id", video);
                        intent.putExtra("parent_id", "0");
                        intent.putExtra("comment", editext_comment.getText().toString());

                        intent.putExtra("receiver", mReceiver);
                        ctx.startService(intent);


                        Comment comment = new Comment();
                        comment.setVideo_id(video);
                        comment.setParent_id("-1");
                        comment.setComment(editext_comment.getText().toString());
                        comment.setUsername(user.getUsername());
                        comment.setProfile_image(user.getProfile_image());
                        comment.setReplyCommentArrayList(new ArrayList<Comment.ReplyComment>());
                        //adding the comment to the first postion of list
                        //find a workaround since too much time taken to  move the n-1 number of elements
                        //commentArrayList.add(0,comment);

                        commentArrayList.add(comment);

                        listview_comment.getAdapter().notifyDataSetChanged();
                        //fast scroll to last item in list
                        //listview_comment.smoothScrollToPosition(listview_comment.getAdapter().getCount()-1);


                        //hiding keyboard and making the edittext empty
                        editext_comment.setText("");
                        InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    }
                } else {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    PopMessage.makesimplesnack(layout, "Please enter a comment");
                }
            }
        });


        dialog.show();


    }


    public void loadComments(String video_id, String next_records, final ProgressBar progressBar, final TextView textView, final RecyclerView recyclerView, final ArrayList<Comment> commentArrayList) {
        if (Networkstate.haveNetworkConnection(context)) {
            AppPrefs appPrefs=new AppPrefs(context);
            areCommentsLoaded = false;
            progressBar.setVisibility(View.VISIBLE);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.getCommentList(video_id, next_records,appPrefs.getUser().getUser_id()).enqueue(new Callback<CommentsResponseModel>() {
                @Override
                public void onResponse(Call<CommentsResponseModel> call, Response<CommentsResponseModel> response) {
                    if (response.body().getStatus().equals(Utility.success_response)) {
                        progressBar.setVisibility(View.GONE);
                        if (isAdded) {
                            if (response.body().getCommentArrayList().size() > 0) {
                                commentArrayList.addAll(response.body().getCommentArrayList());
                                recyclerView.getAdapter().notifyDataSetChanged();

                                if (response.body().getCommentArrayList().size() == 3) {
                                    next_rec = next_rec + 3;
                                    textView.setText("Load More Comments..");
                                } else {
                                    textView.setOnClickListener(null);
                                    textView.setText("No More Comments..");
                                }

                            } else {
                                textView.setText("No Comments to load..");
                            }

                            areCommentsLoaded = true;
                        } else {
                            textView.setText("Error..");
                            PopMessage.makeshorttoast(context, "Error+ " + response.body().getStatus());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommentsResponseModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    textView.setText("Error..");
                    Log.e("call failed", t.getMessage());
                }
            });
        } else {
            textView.setText(Utility.message_no_internet);
            PopMessage.makeshorttoast(context, Utility.message_no_internet);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView img_user;
        TextView txt_username, txt_action_type, txt_sub_action_name, txt_date;
        RelativeLayout layout_back;
        ProgressBar pbar;

        public MyViewHolder(View view) {
            super(view);
            layout_back = (RelativeLayout) view.findViewById(R.id.notification_background);

            img_user = (ImageView) view.findViewById(R.id.notification_pro_pic);

            txt_username = (TextView) view.findViewById(R.id.notification_user_name);
            txt_date = (TextView) view.findViewById(R.id.notification_date);
            txt_sub_action_name = (TextView) view.findViewById(R.id.notification_sub_action_name);
            txt_action_type = (TextView) view.findViewById(R.id.notification_action_type);

            pbar=(ProgressBar)view.findViewById(R.id.pbar_notImage);
        }
    }
}

