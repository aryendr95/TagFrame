package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.FollowModel;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abhinav on 11/04/2016.
 */
public class FollowListAdapter extends BaseAdapter {
    ArrayList<FollowModel> followModelArrayList;
    Context ctx;
    LayoutInflater inflater;
    int user_type;
    AppPrefs userinfo;
    public Broadcastresults mReceiver;

    public FollowListAdapter(Context ctx, ArrayList<FollowModel> followModelArrayList, int type) {
        this.ctx = ctx;
        this.followModelArrayList = followModelArrayList;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user_type = type;
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

        mViewHolder.tvrealname.setText(followModel.getFirst_name());
        try {
            Picasso.with(ctx).load(followModel.getImage()).resize(50, 50).into(mViewHolder.user_pic);
        } catch (Exception e) {
            mViewHolder.user_pic.setImageResource(R.drawable.pro_image);
        }
        //adjust according to type
        adjust(mViewHolder, position, user_type, followModel);
        return convertView;
    }

    private void adjust(MyViewHolder viewHolder, final int position, final int type, final FollowModel followModel) {
        //condition checks if profile visited of which user(self, other)
        if (followModel.getFrom_user_id().equals(userinfo.getString(Utility.user_id))) {
            if ((type == 0))//following
            {
                viewHolder.removeuser.setVisibility(View.GONE);
                viewHolder.followbutton.setText("UnFollow");

            } else if (type == 1)//followers
            {
                viewHolder.removeuser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       /* mReceiver=((Modules)ctx).register_reviever();
                        Intent intent=new Intent(ctx, IntentServiceOperations.class);
                        intent.putExtra("operation",Utility.operation_remove_follower);
                        intent.putExtra("from_userid",followModel.getFrom_user_id());
                        intent.putExtra("to_userid",followModel.getUserid());
                        intent.putExtra("type","follower");
                        intent.putExtra("receiver", mReceiver);
                        ctx.startService(intent);

                        followModelArrayList.remove(position);
                        notifyDataSetChanged();*/
                        final Dialog dialog = new Dialog(ctx);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_remove_follower);

                        CircleImageView circularImageView = (CircleImageView) dialog.findViewById(R.id.dia_remove_image);
                        try {
                            Picasso.with(ctx).load(followModel.getImage()).into(circularImageView);
                        } catch (Exception e) {
                            circularImageView.setImageResource(R.drawable.pro_image);
                        }

                        TextView textView = (TextView) dialog.findViewById(R.id.dia_remove_text);
                        textView.setText(followModel.getUser_name());

                        TextView textView1 = (TextView) dialog.findViewById(R.id.dia_rem_caption);
                        textView1.setText("Are you sure you want to remove this follower?");

                        dialog.findViewById(R.id.dia_rem_yesbtn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                followModelArrayList.remove(position);
                                notifyDataSetChanged();
                                ((Modules) ctx).setprofileparameter(Utility.user_type_followers, followModelArrayList.size() + "");
                                mReceiver = ((Modules) ctx).register_reviever();

                                Intent intent = new Intent(ctx, IntentServiceOperations.class);
                                intent.putExtra("operation", Utility.operation_remove_follower);
                                intent.putExtra("from_userid", followModel.getFrom_user_id());
                                intent.putExtra("to_userid", followModel.getUserid());
                                intent.putExtra("type", "follower");
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
            } else if (type == 2) {
                viewHolder.followbutton.setVisibility(View.GONE);
                viewHolder.removeuser.setVisibility(View.GONE);
            }
        } else {
            Log.e("das", followModel.getFrom_user_id() + " " + userinfo.getString(Utility.user_id));
            viewHolder.removeuser.setVisibility(View.GONE);
            viewHolder.followbutton.setVisibility(View.GONE);
        }


        viewHolder.followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == Utility.user_type_following) {
                    final Dialog dialog = new Dialog(ctx);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_remove_follower);

                    CircleImageView circularImageView = (CircleImageView) dialog.findViewById(R.id.dia_remove_image);

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
                            ((Modules) ctx).setprofileparameter(Utility.user_type_following, (followModelArrayList.size()) + "");

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
                } else if (type == Utility.user_type_followers) {

                    final Dialog dialog = new Dialog(ctx);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_remove_follower);

                    CircleImageView circularImageView = (CircleImageView) dialog.findViewById(R.id.dia_remove_image);

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
                            ((Modules) ctx).setprofileparameter(Utility.user_type_following, (followModelArrayList.size()) + "");
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


            }
        });

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
    }

    public void create_dialog() {
    }

    private class MyViewHolder {
        TextView tvusername, tvrealname;
        CircleImageView user_pic;
        TextView followbutton;
        ImageView removeuser;

        public MyViewHolder(View item) {
            tvusername = (TextView) item.findViewById(R.id.follow_username);
            tvrealname = (TextView) item.findViewById(R.id.follow_realname);
            user_pic = (CircleImageView) item.findViewById(R.id.follow_pro_pic);
            followbutton = (TextView) item.findViewById(R.id.follow_button);
            removeuser = (ImageView) item.findViewById(R.id.follow_remove);
        }
    }
}
