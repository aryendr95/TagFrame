package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.Comment;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Services.IntentServiceOperations;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Edittext;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.listops;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brajendr on 7/7/2016.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Comment> commentArrayList;
    private LayoutInflater inflater;
    listops user_data;
    private MyViewHolder mViewHolder;


    public CommentAdapter(Context context, ArrayList<Comment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        user_data = new listops(context);
    }

    @Override
    public int getCount() {
        return commentArrayList.size();
    }

    @Override
    public Object getItem( int position) {
        return commentArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_list_comments, parent, false);
            mViewHolder = new MyViewHolder();

            mViewHolder.img_userpic = (CircularImageView) convertView.findViewById(R.id.img_commentlist_pic);
            mViewHolder.img_rplyuserpic = (CircularImageView) convertView.findViewById(R.id.img_commentlist_rplypic);

            mViewHolder.tv_username = (TextView) convertView.findViewById(R.id.txt_commentlist_username);
            // tv_rplyusername=(TextView)item.findViewById(R.id.txt_commentlist_rplyusername);
            mViewHolder.tv_comment = (TextView) convertView.findViewById(R.id.txt_commentlist_comment);
            // tv_rplycomment=(TextView)item.findViewById(R.id.txt_commentlist_rplycomment);
            mViewHolder.tv_date = (TextView) convertView.findViewById(R.id.txt_commentlist_date);

            mViewHolder.tv_view_allreply = (TextView) convertView.findViewById(R.id.txt_commentlist_viewallreply);

            mViewHolder.ed_comment = (EditText) convertView.findViewById(R.id.ed_commentlist_comment);
            mViewHolder.img_send_imageview = (ImageButton) convertView.findViewById(R.id.img_commentlist_send_comment);

            //lr_container=(RelativeLayout)item.findViewById(R.id.layout_commentlist_rplycontainer);
            mViewHolder.lr_sender = (RelativeLayout) convertView.findViewById(R.id.layout_commentlist_rplysender);
            mViewHolder.ll_layout = (LinearLayout) convertView.findViewById(R.id.mlayout_list_comment);

            mViewHolder.list_nested_comments = (ListView) convertView.findViewById(R.id.list_nested_comments);



            convertView.setTag(mViewHolder);
        } else {
           mViewHolder=(MyViewHolder)convertView.getTag();
        }


        final Comment comment = commentArrayList.get(position);




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

    //ensuring list has at a item

            mViewHolder.list_nested_comments.setAdapter(new NestedCommentsAdapter(context, comment.getReplyCommentArrayList()));
        Constants.setListViewHeightBasedOnChildren(mViewHolder.list_nested_comments);


            if(comment.getReplyCommentArrayList().size()<2)//the only reply done is alraeady visible
            {
                mViewHolder.tv_view_allreply.setVisibility(View.GONE);
            }
        else
            {
                mViewHolder.tv_view_allreply.setVisibility(View.GONE);
            }




        mViewHolder.tv_view_allreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.isViewallreply()) {
                    comment.setViewallreply(false);

                    mViewHolder.tv_view_allreply.setText("View all replies");
                    notifyDataSetChanged();
                } else {
                    comment.setViewallreply(true);

                    mViewHolder.tv_view_allreply.setText("Show less");
                    notifyDataSetChanged();
                }
            }
        });



        //mViewHolder.list_nested_comments.setAdapter(new NestedCommentsAdapter(context, comment.getReplyCommentArrayList()));




        mViewHolder.img_send_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!mViewHolder.ed_comment.getText().toString().isEmpty()) {
                    Broadcastresults mReceiver = ((Modules) context).register_reviever();

                    Intent intent = new Intent(context, IntentServiceOperations.class);
                    intent.putExtra("operation", Constants.operation_comment);
                    intent.putExtra("user_id", user_data.getString(Constants.user_id));
                    intent.putExtra("video_id", comment.getVideo_id());
                    intent.putExtra("parent_id", comment.getParent_id());
                    intent.putExtra("comment", mViewHolder.ed_comment.getText().toString());

                    intent.putExtra("receiver", mReceiver);
                    context.startService(intent);

                    comment.setViewallreply(true);

                    Comment.ReplyComment replyComment = new Comment.ReplyComment();
                    replyComment.setComment(mViewHolder.ed_comment.getText().toString());


                    replyComment.setUsername(user_data.getString(Constants.user_name));
                    replyComment.setProfile_image(user_data.getString(Constants.user_pic));


                    comment.getReplyCommentArrayList().add(replyComment);

                    notifyDataSetChanged();


                    //hiding the keyboard and making edittext empty after making comments

                    mViewHolder.ed_comment.setText("");
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    //making the view all reply text to show less since we made the list to show all
                    mViewHolder.tv_view_allreply.setText("Show less");


                } else {
                    PopMessage.makesimplesnack(mViewHolder.ll_layout, "Please enter a comment");
                }

            }
        });




        return convertView;
    }

    private class MyViewHolder {

        private CircularImageView img_userpic, img_rplyuserpic;
        private TextView tv_username, tv_comment, tv_date, tv_view_allreply;
        private EditText ed_comment;
        private ImageButton img_send_imageview;
        private RelativeLayout lr_container, lr_sender;
        private LinearLayout ll_layout;
        private ListView list_nested_comments;
        private int position;

        public MyViewHolder() {




        }
    }
}
