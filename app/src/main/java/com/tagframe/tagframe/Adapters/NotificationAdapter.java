package com.tagframe.tagframe.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.EventDetailResponseModel;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.NotificationModel;
import com.tagframe.tagframe.Models.Product;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;

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


        try {
            Picasso.with(context).load(ListModel.getProfile_pic()).into(holder.img_user);
        } catch (Exception e) {
            holder.img_user.setImageResource(R.drawable.pro_image);
        }

        AppPrefs appPrefs = new AppPrefs(context);
        final int user_type = Utility.getUser_Type(ListModel.getUser_id(), appPrefs.getString(Utility.user_id));

        holder.layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ListModel.getSub_action_type().equals(Utility.notification_op_watch_event)) {
                    //show event
                    showProgress();

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    apiInterface.getEventDetails(ListModel.getSub_action_id()).enqueue(new Callback<EventDetailResponseModel>() {
                        @Override
                        public void onResponse(Call<EventDetailResponseModel> call, Response<EventDetailResponseModel> response) {
                            dialog.dismiss();
                            if (response.body().getStatus().equals(Utility.success_response)) {
                                Event_Model event_model = response.body().getEvent_model();
                                        Intent intent = new Intent(context, MakeNewEvent.class);
                                intent.putExtra("data_url", event_model.getDataurl());
                                intent.putExtra("tittle", event_model.getTitle());
                                intent.putExtra("from", "notification");
                                intent.putExtra("description", "");
                                intent.putParcelableArrayListExtra("framelist", event_model.getFrameList_modelArrayList());
                                intent.putExtra("eventtype", Utility.eventtype_internet);
                                intent.putExtra("eventid", ListModel.getSub_action_id());
                                intent.putExtra("tagged_user_id",event_model.getTaggedUserModelArrayList());

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
                } else if (ListModel.getSub_action_type().equals(Utility.notification_op_watch_profile)) {
                    ((Modules) context).setprofile(ListModel.getSub_action_id(), user_type);
                }
                else if(ListModel.getSub_action_type().equals(Utility.notification_op_watch_profile))
                {
                    showProgress();

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    apiInterface.getProductDetails(ListModel.getSub_action_id()).enqueue(new Callback<Product>() {
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
    }

    private void hideProgress() {
        dialog.dismiss();
    }

    public void showProgress()
    {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading Event..");
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return ListModels.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        public ImageView img_user;
        public TextView txt_username, txt_action_type, txt_sub_action_name, txt_date;
        public RelativeLayout layout_back;

        public MyViewHolder(View view) {
            super(view);
            layout_back = (RelativeLayout) view.findViewById(R.id.notification_background);

            img_user = (ImageView) view.findViewById(R.id.notification_pro_pic);

            txt_username = (TextView) view.findViewById(R.id.notification_user_name);
            txt_date = (TextView) view.findViewById(R.id.notification_date);
            txt_sub_action_name = (TextView) view.findViewById(R.id.notification_sub_action_name);
            txt_action_type = (TextView) view.findViewById(R.id.notification_action_type);
        }
    }
}

