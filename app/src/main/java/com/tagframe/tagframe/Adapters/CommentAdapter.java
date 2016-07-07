package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

/**
 * Created by Brajendr on 7/7/2016.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Comment> commentArrayList;
    private LayoutInflater inflater;
    listops user_data;

    public CommentAdapter(Context context, ArrayList<Comment> commentArrayList)
    {
        this.context=context;
        this.commentArrayList=commentArrayList;
        inflater=(LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        user_data=new listops(context);
    }
    @Override
    public int getCount() {
        return commentArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_list_comments, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Comment comment=commentArrayList.get(position);

        //adjusttheview
        if(comment.getReplyCommentArrayList().size()>0)
        {
            mViewHolder.lr_container.setVisibility(View.VISIBLE);
            mViewHolder.lr_sender.setVisibility(View.GONE);
            mViewHolder.tv_rply_button.setText("1 Reply");

            Comment.ReplyComment replyComment=comment.getReplyCommentArrayList().get(0);

            mViewHolder.tv_rplycomment.setText(replyComment.getComment());
            mViewHolder.tv_rplyusername.setText(replyComment.getUsername());
            try {
                Picasso.with(context).load(replyComment.getProfile_image()).into(mViewHolder.img_rplyuserpic);
            }
            catch (Exception e)
            {
                mViewHolder.img_rplyuserpic.setImageResource(R.drawable.pro_image);
            }
        }
        else
        {
            mViewHolder.lr_container.setVisibility(View.GONE);
            mViewHolder.lr_sender.setVisibility(View.VISIBLE);
            mViewHolder.tv_rply_button.setText("0 Reply");
            if(comment.getParent_id().equals("-1"))
            {
                mViewHolder.lr_sender.setVisibility(View.GONE);
            }
        }

        mViewHolder.tv_comment.setText(comment.getComment());
        mViewHolder.tv_username.setText(comment.getUsername());
        try {
            Picasso.with(context).load(comment.getProfile_image()).into(mViewHolder.img_userpic);
        }
        catch (Exception e)
        {
            mViewHolder.img_userpic.setImageResource(R.drawable.pro_image);
        }



        mViewHolder.img_send_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!mViewHolder.ed_comment.getText().toString().isEmpty())
                {
                    Broadcastresults mReceiver=((Modules)context).register_reviever();

                    Intent intent=new Intent(context, IntentServiceOperations.class);
                    intent.putExtra("operation", Constants.operation_comment);
                    intent.putExtra("user_id",user_data.getString(Constants.user_id));
                    intent.putExtra("video_id", comment.getVideo_id());
                    intent.putExtra("parent_id", comment.getParent_id());
                    intent.putExtra("comment", mViewHolder.ed_comment.getText().toString());

                    intent.putExtra("receiver", mReceiver);
                    context.startService(intent);

                    Comment.ReplyComment replyComment=new Comment.ReplyComment();
                    replyComment.setComment(mViewHolder.ed_comment.getText().toString());



                    replyComment.setUsername(user_data.getString(Constants.user_name));
                    replyComment.setProfile_image(user_data.getString(Constants.user_pic));

                    ArrayList<Comment.ReplyComment> arrayList=new ArrayList<Comment.ReplyComment>();
                    arrayList.add(replyComment);

                    comment.setReplyCommentArrayList(arrayList);

                    notifyDataSetChanged();
                }
                else
                {
                    PopMessage.makesimplesnack(mViewHolder.ll_layout,"Please enter a comment");
                }

            }
        });


        return convertView;
    }

    private class MyViewHolder {

        private CircularImageView img_userpic,img_rplyuserpic;
        private TextView tv_username,tv_rplyusername,tv_comment,tv_rplycomment,tv_rply_button;
        private EditText ed_comment;
        private ImageView img_send_imageview;
        private RelativeLayout lr_container,lr_sender;
        private LinearLayout ll_layout;

        public MyViewHolder(View item) {

            img_userpic=(CircularImageView)item.findViewById(R.id.img_commentlist_pic);
            img_rplyuserpic=(CircularImageView)item.findViewById(R.id.img_commentlist_rplypic);

            tv_username=(TextView)item.findViewById(R.id.txt_commentlist_username);
            tv_rplyusername=(TextView)item.findViewById(R.id.txt_commentlist_rplyusername);
            tv_comment=(TextView)item.findViewById(R.id.txt_commentlist_comment);
            tv_rplycomment=(TextView)item.findViewById(R.id.txt_commentlist_rplycomment);
            tv_rply_button=(TextView)item.findViewById(R.id.txt_commentlist_rply);

            ed_comment=(EditText)item.findViewById(R.id.ed_commentlist_comment);
            img_send_imageview=(ImageView) item.findViewById(R.id.img_commentlist_send_comment);

            lr_container=(RelativeLayout)item.findViewById(R.id.layout_commentlist_rplycontainer);
            lr_sender=(RelativeLayout)item.findViewById(R.id.layout_commentlist_rplysender);
            ll_layout=(LinearLayout)item.findViewById(R.id.mlayout_list_comment);


        }
    }
}
