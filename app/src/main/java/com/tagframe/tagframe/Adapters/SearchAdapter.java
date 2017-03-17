package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.Models.TaggedUserModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.UI.Acitivity.SearchUserActivity;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

/**
 * Created by abhinav on 11/04/2016.
 */
public class SearchAdapter extends BaseAdapter {

    private ArrayList<FollowModel> followModelArrayList;
    private Context ctx;
    private LayoutInflater inflater;
    private int user_type;
    private AppPrefs userinfo;
    public Broadcastresults mReceiver;
    private int onClick_operation;

    public SearchAdapter(Context ctx, ArrayList<FollowModel> followModelArrayList, int type, int operation) {

        this.ctx = ctx;
        this.followModelArrayList = followModelArrayList;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user_type = type;
        this.onClick_operation = operation;
        userinfo = new AppPrefs(ctx);
    }

    @Override
    public int getCount() {
        return followModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return followModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_list_item_followlist, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();


        }
        FollowModel followModel = followModelArrayList.get(position);
        mViewHolder.tvusername.setText(followModel.getUser_name());

        mViewHolder.tvrealname.setText(followModel.getEmail());
        try {
            Picasso.with(ctx).load(followModel.getImage()).into(mViewHolder.user_pic);
        } catch (Exception e) {
            mViewHolder.user_pic.setImageResource(R.drawable.pro_image);
        }

        //adjust according to type
        adjust(mViewHolder, position, user_type, followModel);

        return convertView;
    }

    private void adjust(MyViewHolder viewHolder, final int position, final int type, final FollowModel followModel) {
        //condition checks if profile visited of which user(self, other)
        if (ctx instanceof Modules) {
            if (followModel.getFrom_user_id().equals(userinfo.getString(Utility.user_id))) {


                if (type == 2) {

                    if (followModel.getIs_followed().equals("No")) {
                        viewHolder.followbutton.setText("Follow");
                        viewHolder.followbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_remove_follower);

                                CircularImageView circularImageView = (CircularImageView) dialog.findViewById(R.id.dia_remove_image);

                                try {
                                    Picasso.with(ctx).load(followModel.getImage()).into(circularImageView);
                                } catch (Exception e) {
                                    circularImageView.setImageResource(R.drawable.pro_image);
                                }
                                TextView textView = (TextView) dialog.findViewById(R.id.dia_remove_text);
                                textView.setText(followModel.getUser_name());

                                TextView textView1 = (TextView) dialog.findViewById(R.id.dia_rem_caption);
                                textView1.setText("Are you sure you want to follow this person?");


                                dialog.findViewById(R.id.dia_rem_yesbtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        MyToast.popmessage("Following", ctx);

                                        mReceiver = ((Modules) ctx).register_reviever();

                                        Intent intent = new Intent(ctx, IntentServiceOperations.class);
                                        intent.putExtra("operation", Utility.operation_follow);
                                        intent.putExtra("from_userid", followModel.getFrom_user_id());
                                        intent.putExtra("to_userid", followModel.getUserid());
                                        intent.putExtra("type", "following");
                                        intent.putExtra("receiver", mReceiver);
                                        ctx.startService(intent);
                                        dialog.dismiss();


                                    }
                                });

                                dialog.findViewById(R.id.dia_rem_nobtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        });
                    } else {
                        viewHolder.followbutton.setText("UnFollow");
                        viewHolder.followbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_remove_follower);

                                CircularImageView circularImageView = (CircularImageView) dialog.findViewById(R.id.dia_remove_image);

                                try {
                                    Picasso.with(ctx).load(followModel.getImage()).into(circularImageView);
                                } catch (Exception e) {
                                    circularImageView.setImageResource(R.drawable.pro_image);
                                }
                                TextView textView = (TextView) dialog.findViewById(R.id.dia_remove_text);
                                textView.setText(followModel.getUser_name());

                                TextView textView1 = (TextView) dialog.findViewById(R.id.dia_rem_caption);
                                textView1.setText("Are you sure you want to unfollow this person?");


                                dialog.findViewById(R.id.dia_rem_yesbtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        followModelArrayList.remove(position);
                                        notifyDataSetChanged();
                                        MyToast.popmessage("UnFollowing", ctx);

                                        mReceiver = ((Modules) ctx).register_reviever();

                                        Intent intent = new Intent(ctx, IntentServiceOperations.class);
                                        intent.putExtra("operation", Utility.operation_unfollow);
                                        intent.putExtra("from_userid", followModel.getFrom_user_id());
                                        intent.putExtra("to_userid", followModel.getUserid());
                                        intent.putExtra("type", "following");
                                        intent.putExtra("receiver", mReceiver);
                                        ctx.startService(intent);
                                        dialog.dismiss();


                                    }
                                });

                                dialog.findViewById(R.id.dia_rem_nobtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            }
                        });
                    }
                    viewHolder.removeuser.setVisibility(View.GONE);
                }
            } else {
                Log.e("das", followModel.getFrom_user_id() + " " + userinfo.getString(Utility.user_id));
                viewHolder.removeuser.setVisibility(View.GONE);
                viewHolder.followbutton.setVisibility(View.GONE);
            }


            viewHolder.user_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Modules) ctx).setprofile(followModel.getUserid(), user_type);
                }
            });
            viewHolder.tvusername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Modules) ctx).setprofile(followModel.getUserid(), user_type);
                }
            });
        } else if (ctx instanceof SearchUserActivity) {
            viewHolder.removeuser.setVisibility(View.GONE);
            viewHolder.followbutton.setVisibility(View.GONE);
            viewHolder.user_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClick_operation == Utility.operation_onclicked_direct_endorse)
                        show_DirectEndorse_dialog(followModel);
                    else if (onClick_operation == Utility.operation_onclicked_tagged_user)
                    {
                        Log.e("getting called","Yee");
                        TaggedUserModel taggedUserModel=new TaggedUserModel(followModel.getUser_name()
                                ,followModel.getUserid(),followModel.getImage());
                        Intent intent=new Intent(ctx, MakeNewEvent.class);
                        intent.putExtra("tagged_user",taggedUserModel);
                        ((SearchUserActivity) ctx).setResult(MakeNewEvent.Flag_Get_Tagged_User,intent);
                        ((SearchUserActivity) ctx).finish();
                    }
                }
            });

            viewHolder.tvusername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClick_operation == Utility.operation_onclicked_direct_endorse)
                        show_DirectEndorse_dialog(followModel);
                    else if (onClick_operation == Utility.operation_onclicked_tagged_user)
                    {
                        TaggedUserModel taggedUserModel=new TaggedUserModel(followModel.getUser_name()
                                ,followModel.getUserid(),followModel.getImage());
                        Intent intent=new Intent(ctx, MakeNewEvent.class);
                        intent.putExtra("tagged_user",taggedUserModel);
                        ((SearchUserActivity) ctx).setResult(MakeNewEvent.Flag_Get_Tagged_User,intent);
                        ((SearchUserActivity) ctx).finish();
                    }
                }
            });
        }
    }


    public void show_DirectEndorse_dialog(final FollowModel followModel) {

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_direct_endorse);


        CircularImageView user_pic = (CircularImageView) dialog.findViewById(R.id.dia_direct_image);
        final TextView username = (TextView) dialog.findViewById(R.id.dia_direct_caption);
        final EditText message = (EditText) dialog.findViewById(R.id.dia_direct_message);

        username.setText(followModel.getUser_name());
        //always catch exception with piccasso, path may be null(Illegal state exception)
        //use a generalized exception
        try {
            Picasso.with(ctx).load(followModel.getImage()).into(user_pic);
        } catch (Exception e) {
            user_pic.setImageResource(R.drawable.pro_image);
        }

        dialog.findViewById(R.id.dia_direct_yesbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_message = message.getText().toString();
                if (!str_message.isEmpty()) {
                    //Direct Endorse
                    ((SearchUserActivity) ctx).directEndorse(followModel.getUserid(), str_message);
                    dialog.dismiss();

                }
            }
        });
        dialog.findViewById(R.id.dia_direct_nobtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    private class MyViewHolder {
        TextView tvusername, tvrealname;
        CircularImageView user_pic;
        TextView followbutton;
        ImageView removeuser;

        public MyViewHolder(View item) {
            tvusername = (TextView) item.findViewById(R.id.follow_username);
            tvrealname = (TextView) item.findViewById(R.id.follow_realname);
            user_pic = (CircularImageView) item.findViewById(R.id.follow_pro_pic);
            followbutton = (TextView) item.findViewById(R.id.follow_button);

            removeuser = (ImageView) item.findViewById(R.id.follow_remove);

        }
    }
}
