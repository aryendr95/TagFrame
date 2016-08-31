package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Comment;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brajendr on 7/12/2016.
 */
public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.MyViewHolder> {

    private List<Comment> commentList;
    private Context context;
    AppPrefs user_data;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView img_userpic, img_rplyuserpic;
        private TextView tv_username, tv_comment, tv_date, tv_view_allreply;
        private EditText ed_comment;
        private ImageButton img_send_imageview;
        private RelativeLayout lr_container, lr_sender;
        private LinearLayout ll_layout;
        private ListView list_nested_comments;
        private int position;

        public MyViewHolder(View convertView) {
            super(convertView);
            img_userpic = (CircularImageView) convertView.findViewById(R.id.img_commentlist_pic);
           //img_rplyuserpic = (CircularImageView) convertView.findViewById(R.id.img_commentlist_rplypic);

            tv_username = (TextView) convertView.findViewById(R.id.txt_commentlist_username);
            // tv_rplyusername=(TextView)item.findViewById(R.id.txt_commentlist_rplyusername);
           tv_comment = (TextView) convertView.findViewById(R.id.txt_commentlist_comment);
            // tv_rplycomment=(TextView)item.findViewById(R.id.txt_commentlist_rplycomment);
           tv_date = (TextView) convertView.findViewById(R.id.txt_commentlist_date);

           tv_view_allreply = (TextView) convertView.findViewById(R.id.txt_commentlist_viewallreply);

           ed_comment = (EditText) convertView.findViewById(R.id.ed_commentlist_comment);
            img_send_imageview = (ImageButton) convertView.findViewById(R.id.img_commentlist_send_comment);

            //lr_container=(RelativeLayout)item.findViewById(R.id.layout_commentlist_rplycontainer);
           lr_sender = (RelativeLayout) convertView.findViewById(R.id.layout_commentlist_rplysender);
           ll_layout = (LinearLayout) convertView.findViewById(R.id.mlayout_list_comment);

           list_nested_comments = (ListView) convertView.findViewById(R.id.list_nested_comments);
        }
    }


    public CommentsRecyclerViewAdapter(ArrayList<Comment> moviesList,Context context) {
        this.commentList = moviesList;
        this.context=context;
        user_data = new AppPrefs(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_comments, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder mViewHolder, int position) {


        Comment comment = commentList.get(position);




        mViewHolder.tv_comment.setText(comment.getComment());
        mViewHolder.tv_username.setText(comment.getUsername());
        mViewHolder.tv_date.setText(comment.getCreated_on());

        try {
            Picasso.with(context).load(comment.getProfile_image()).into(mViewHolder.img_userpic);
        } catch (Exception e) {
            mViewHolder.img_userpic.setImageResource(R.drawable.pro_image);
        }



        //condition to check whether the comment was added locally or it was added from server
        if(comment.getParent_id().equals("-1"))
        {
            mViewHolder.lr_sender.setVisibility(View.GONE);
        }

        mViewHolder.list_nested_comments.setAdapter(new NestedCommentsAdapter(context, comment.getReplyCommentArrayList()));
        //ensuring list has at a item
        if(comment.isViewallreply()) {

            Utility.setListViewHeightBasedOnChildren(mViewHolder.list_nested_comments);
            mViewHolder.tv_view_allreply.setText("Show less");
        }
        else
        {

            Utility.setListViewHeightBasedOnOneChildren(mViewHolder.list_nested_comments);
            mViewHolder.tv_view_allreply.setText("View all "+comment.getReplyCommentArrayList().size()+" replies");
        }


        if(comment.getReplyCommentArrayList().size()<2)//the only reply done is alraeady visible
        {
            mViewHolder.tv_view_allreply.setVisibility(View.GONE);
        }
        else
        {
            mViewHolder.tv_view_allreply.setVisibility(View.VISIBLE);
        }



        mViewHolder.tv_view_allreply.setTag(position);
        mViewHolder.tv_view_allreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos=Integer.parseInt(v.getTag().toString());
                Comment comment1=commentList.get(pos);

                if (comment1.isViewallreply()) {
                    comment1.setViewallreply(false);

                    mViewHolder.tv_view_allreply.setText("View all replies");
                    notifyDataSetChanged();
                } else {
                    comment1.setViewallreply(true);

                    mViewHolder.tv_view_allreply.setText("Show less");
                    notifyDataSetChanged();
                }
            }
        });



        //mViewHolder.list_nested_comments.setAdapter(new NestedCommentsAdapter(context, comment.getReplyCommentArrayList()));

        mViewHolder.img_send_imageview.setTag(position);


        mViewHolder.img_send_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos=Integer.parseInt(v.getTag().toString());
                Comment comment1=commentList.get(pos);
                String str_comment=mViewHolder.ed_comment.getText().toString();

                if (!str_comment.isEmpty()) {
                    Broadcastresults mReceiver = ((Modules) context).register_reviever();

                    Intent intent = new Intent(context, IntentServiceOperations.class);
                    intent.putExtra("operation", Utility.operation_comment);
                    intent.putExtra("user_id", user_data.getString(Utility.user_id));
                    intent.putExtra("video_id", comment1.getVideo_id());
                    intent.putExtra("parent_id", comment1.getParent_id());
                    intent.putExtra("comment", mViewHolder.ed_comment.getText().toString());

                    intent.putExtra("receiver", mReceiver);
                    context.startService(intent);

                    comment1.setViewallreply(true);

                    Comment.ReplyComment replyComment = new Comment.ReplyComment();
                    replyComment.setComment(mViewHolder.ed_comment.getText().toString());


                    replyComment.setUsername(user_data.getString(Utility.user_name));
                    replyComment.setProfile_image(user_data.getString(Utility.user_pic));


                    comment1.getReplyCommentArrayList().add(replyComment);

                    notifyDataSetChanged();


                    //hiding the keyboard and making edittext empty after making comments

                    mViewHolder.ed_comment.setText("");
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    //making the view all reply text to show less since we made the list to show all
                    mViewHolder.tv_view_allreply.setText("Show less");


                } else {
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mViewHolder.ed_comment.setText("working");
                    PopMessage.makesimplesnack(mViewHolder.ll_layout, "Please enter a comment");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
