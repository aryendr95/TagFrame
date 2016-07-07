package com.tagframe.tagframe.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tagframe.tagframe.Adapters.CommentAdapter;
import com.tagframe.tagframe.Models.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Brajendr on 7/7/2016.
 */
public class LoadComment extends AsyncTask<String,String,String> {

    private ProgressBar progressBar;
    private ListView listView;
    private String video_id;
    private Dialog dialog;
    private Context context;


    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    private ArrayList<Comment> commentArrayList=new ArrayList<>();;
    private String status="";

    public LoadComment(ProgressBar progressBar,ListView listView,String video_id,Dialog d,Context context)
    {
        this.dialog=d;
        this.listView=listView;
        this.progressBar=progressBar;
        this.video_id=video_id;
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected String doInBackground(String... params) {

        try
        {
            WebServiceHandler webServiceHandler=new WebServiceHandler(Constants.URL_COMMENT_LIST);
            webServiceHandler.addFormField("video_id",video_id);
            JSONObject resultobject=new JSONObject(webServiceHandler.finish());

            Log.e("Comment Result",resultobject.toString());

            status=resultobject.getString("status");
            if(status.equals("success"))
            {

                JSONArray comments=resultobject.getJSONArray("comments");
                if(comments.length()>0)
                {
                    for(int i=0;i<comments.length();i++)
                    {
                        JSONObject commentobject=comments.getJSONObject(i);
                        Comment comment=new Comment();

                        comment.setComment(commentobject.getString("comment"));
                        comment.setUsername(commentobject.getString("username"));
                        comment.setProfile_image(commentobject.getString("profile_image"));
                        comment.setCreated_on(commentobject.getString("created_on"));
                        comment.setParent_id(commentobject.getString("id"));
                        comment.setVideo_id(video_id);
                        JSONArray replyjsonArray;
                        try {
                             replyjsonArray = commentobject.getJSONArray("replycomments");
                        }
                        catch (JSONException e)
                        {
                            replyjsonArray=new JSONArray();
                        }

                        ArrayList<Comment.ReplyComment> replyCommentsarralylist=new ArrayList<>();
                        if(replyjsonArray.length()>0)
                        {

                            Comment.ReplyComment replyComment=new Comment.ReplyComment();

                            JSONObject replyobject=replyjsonArray.getJSONObject(0);

                            replyComment.setComment(replyobject.getString("comment"));
                            replyComment.setUsername(replyobject.getString("username"));
                            replyComment.setProfile_image(replyobject.getString("profile_image"));
                            replyComment.setCreated_on(replyobject.getString("created_on"));

                            replyCommentsarralylist.add(replyComment);


                        }
                        comment.setReplyCommentArrayList(replyCommentsarralylist);
                        commentArrayList.add(comment);



                    }
                }
            }

        }
        catch (IOException e)
        {
        Log.e("Das",e.getMessage());
        }
        catch(JSONException E)
        {
            Log.e("Das",E.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(dialog.isShowing())
        {
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(new CommentAdapter(context,commentArrayList));


        }
    }
}
