package com.tagframe.tagframe.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.tagframe.tagframe.Utils.AppPrefs;

import java.util.ArrayList;

/**
 * Created by Brajendr on 7/7/2016.
 */
public class NestedCommentsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Comment.ReplyComment> commentArrayList;
    private LayoutInflater inflater;
    AppPrefs user_data;

    public NestedCommentsAdapter(Context context, ArrayList<Comment.ReplyComment> commentArrayList)
    {
        this.context=context;
        this.commentArrayList=commentArrayList;
        inflater=(LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        user_data=new AppPrefs(context);
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
            convertView = inflater.inflate(R.layout.layout_list_nested_comments, parent, false);
            mViewHolder = new MyViewHolder();

            mViewHolder.img_userpic=(CircularImageView)convertView.findViewById(R.id.img_commentlist_rplypic);
            //img_rplyuserpic=(CircularImageView)item.findViewById(R.id.img_commentlist_rplypic);

            //tv_username=(TextView)item.findViewById(R.id.txt_commentlist_username);
            mViewHolder.tv_username=(TextView)convertView.findViewById(R.id.txt_commentlist_rplyusername);
            //tv_comment=(TextView)item.findViewById(R.id.txt_commentlist_comment);
            mViewHolder.tv_comment=(TextView)convertView.findViewById(R.id.txt_commentlist_rplycomment);

            mViewHolder.tv_date=(TextView)convertView.findViewById(R.id.txt_nestedcommentlist_date);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        if (commentArrayList.size() > 0) {

            final Comment.ReplyComment comment = commentArrayList.get(position);


            mViewHolder.tv_comment.setText(comment.getComment());
            mViewHolder.tv_username.setText(comment.getUsername());
            mViewHolder.tv_date.setText(comment.getCreated_on());

            try {
                Picasso.with(context).load(comment.getProfile_image()).into(mViewHolder.img_userpic);
            } catch (Exception e) {
                mViewHolder.img_userpic.setImageResource(R.drawable.pro_image);

            }



        }
        return convertView;
    }

    private class MyViewHolder {

        private CircularImageView img_userpic,img_rplyuserpic;
        private TextView tv_username,tv_comment,tv_date;
        private EditText ed_comment;
        private ImageButton img_send_imageview;
        private RelativeLayout lr_container,lr_sender;
        private LinearLayout ll_layout;
        private ListView list_nested_comments;

        public MyViewHolder() {





        }
    }
}


