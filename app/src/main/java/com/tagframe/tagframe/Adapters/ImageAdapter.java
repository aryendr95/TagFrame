package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.Models.User_Frames_model;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.WatchEventActivity;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Utility;

import java.util.ArrayList;

/**
 * Created by abhinav on 08/04/2016.
 */
public class ImageAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<User_Frames_model> tagStream_models;
    LayoutInflater inflater;

    public ImageAdapter(Context ctx, ArrayList<User_Frames_model> tagStream_models) {
        this.ctx = ctx;
        this.tagStream_models = tagStream_models;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tagStream_models.size();
    }

    @Override
    public Object getItem(int position) {
        return tagStream_models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_item_fgridview_frame, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final User_Frames_model tagStream = tagStream_models.get(position);

        Picasso.with(ctx).load(tagStream.getFrame_image_url()).into(mViewHolder.ivframeimage);
        if (tagStream.getMedia_type().equals(String.valueOf(Constants.frametype_image))) {
            mViewHolder.ivPLayVideo.setVisibility(View.GONE);
        } else {
            mViewHolder.ivPLayVideo.setVisibility(View.VISIBLE);
        }

        mViewHolder.ivframeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFrame(ctx, tagStream);
            }
        });

        return convertView;
    }

    public static void showFrame(final Context ctx, final User_Frames_model tagStream) {
        if (tagStream.getMedia_type().equals(String.valueOf(Constants.frametype_image))) {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_frame_display_profile);

            // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
            ImageView frameimage = (ImageView) dialog.findViewById(R.id.framelist_image);
            ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);
            ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);
            TextView buyProduct = (TextView) dialog.findViewById(R.id.buyProduct);

            if (!tagStream.getProduct_id().equals("0") && !tagStream.is_product_frame()) {
                product_image.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(tagStream.getProduct_image_url()).into(product_image);
                buyProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent =
                                new Intent(Intent.ACTION_VIEW, Uri.parse(tagStream.getProduct_url()));
                        ctx.startActivity(browserIntent);
                        dialog.dismiss();
                    }
                });
            } else {
                product_image.setVisibility(View.GONE);
                buyProduct.setVisibility(View.GONE);
            }

            TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
            final TextView tittle = (TextView) dialog.findViewById(R.id.framelist_name);
            duration.setVisibility(View.GONE);
            RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_see_event);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ctx, WatchEventActivity.class);
                    intent.putExtra("name", tagStream.getName());
                    //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
                    intent.putExtra("likes", tagStream.getNumber_of_likes());
                    intent.putExtra("frames", tagStream.getFrameList_modelArrayList().size());
                    intent.putExtra("comments", tagStream.getNumber_of_comments());
                    intent.putExtra("data_url", tagStream.getData_url());
                    intent.putExtra("tittle", tagStream.getTitle());
                    intent.putExtra("from", "tagstream");
                    intent.putExtra("user_id", tagStream.getUser_id());
                    intent.putParcelableArrayListExtra("framelist", tagStream.getFrameList_modelArrayList());
                    intent.putExtra("eventtype", Utility.eventtype_internet);
                    intent.putExtra("eventid", tagStream.getVideo_id());
                    intent.putExtra("sharelink", tagStream.getShare_link());
                    intent.putExtra("likevideo", tagStream.getIs_liked());
                    intent.putExtra("tagged_user_id", tagStream.getTaggedUserModelArrayList());
                    ctx.startActivity(intent);
                }
            });

            frameimage.setVisibility(View.VISIBLE);
            Picasso.with(ctx).load(tagStream.getFrame_image_url()).into(frameimage);

            tittle.setText(tagStream.getTitle());
            //duration.setText(Utility.milliSecondsToTimer(tagStream.get) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();
        } else {
            showVideoDialog(ctx, tagStream);
        }
    }

    private class MyViewHolder {

        ImageView ivframeimage, ivPLayVideo;

        public MyViewHolder(View item) {

            ivframeimage = (ImageView) item.findViewById(R.id.grid_iimag_item);
            ivPLayVideo = (ImageView) item.findViewById(R.id.img_play);
        }
    }

    private static void showVideoDialog(final Context ctx, final User_Frames_model frameList_model) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_framlist_video_frame);

        WindowManager.LayoutParams a = dialog.getWindow().getAttributes();
        a.dimAmount = 0;
        dialog.getWindow().setAttributes(a);

        dialog.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
        final RelativeLayout coverLayout = (RelativeLayout) dialog.findViewById(R.id.cover);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.pbar_video_dialog);

        final ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);

        ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);
        TextView buyProduct = (TextView) dialog.findViewById(R.id.buyProduct);
        if (!frameList_model.getProduct_id().equals("0") && !frameList_model.is_product_frame()) {
            product_image.setVisibility(View.VISIBLE);
            Picasso.with(ctx).load(frameList_model.getProduct_image_url()).into(product_image);
            buyProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse(frameList_model.getProduct_url()));
                    ctx.startActivity(browserIntent);
                    dialog.dismiss();
                }
            });
        } else {
            product_image.setVisibility(View.GONE);
            buyProduct.setVisibility(View.GONE);
        }

        TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
        final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);

        dialog.findViewById(R.id.see_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, WatchEventActivity.class);
                intent.putExtra("name", frameList_model.getName());
                //intent.putExtra("stats",tagStream.getNumber_of_likes() + " Likes" + ", " + tagStream.getFrameList_modelArrayList().size() + " Frames" + " and "+tagStream.getNum_of_comments() + " Comments");
                intent.putExtra("likes", frameList_model.getNumber_of_likes());
                intent.putExtra("frames", frameList_model.getFrameList_modelArrayList().size());
                intent.putExtra("comments", frameList_model.getNumber_of_comments());
                intent.putExtra("data_url", frameList_model.getData_url());
                intent.putExtra("tittle", frameList_model.getTitle());
                intent.putExtra("from", "tagstream");
                intent.putExtra("user_id", frameList_model.getUser_id());
                intent.putParcelableArrayListExtra("framelist",
                        frameList_model.getFrameList_modelArrayList());
                intent.putExtra("eventtype", Utility.eventtype_internet);
                intent.putExtra("eventid", frameList_model.getVideo_id());
                intent.putExtra("sharelink", frameList_model.getShare_link());
                intent.putExtra("likevideo", frameList_model.getIs_liked());
                intent.putExtra("tagged_user_id", frameList_model.getTaggedUserModelArrayList());
                ctx.startActivity(intent);

                dialog.dismiss();
            }
        });

        try {

            framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_image_url()));
            MediaController mediaController = new MediaController(ctx);
            framevideo.setMediaController(mediaController);
            mediaController.setAnchorView(framevideo);
            framevideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    coverLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            });
            framevideo.setZOrderOnTop(true);
            framevideo.start();
        } catch (Exception e) {

        }

        tittle.setText(frameList_model.getName());
        //duration.setText(Utility.milliSecondsToTimer(frameList_model.getS) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));
        duration.setVisibility(View.GONE);
        //video controls
        final SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekbar_dialog);

        final TextView current = (TextView) dialog.findViewById(R.id.dialog_txttotalduration);
        final TextView total = (TextView) dialog.findViewById(R.id.dialog_txtcurrentduration);
        final ImageButton play_stop = (ImageButton) dialog.findViewById(R.id.dialog_btn_play_stop);

        final Handler myHandler = new Handler();

        final Runnable mUpdateDialogVideo = new Runnable() {
            @Override
            public void run() {
                long totalDurationn = 0;
                long currentDuration = 0;
                try {
                    totalDurationn = framevideo.getDuration();
                    currentDuration = framevideo.getCurrentPosition();
                } catch (Exception e) {
                    totalDurationn = 0;
                    currentDuration = 0;
                }

                current.setText("" + Utility.milliSecondsToTimer(totalDurationn));
                // Displaying time completed playing
                total.setText("" + Utility.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDurationn));

                seekBar.setProgress(progress);
                myHandler.postDelayed(this, 100);
            }
        };
        myHandler.postDelayed(mUpdateDialogVideo, 100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(mUpdateDialogVideo);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(mUpdateDialogVideo);
                int total = framevideo.getDuration();
                int current = Utility.progressToTimer(seekBar.getProgress(), total);
                framevideo.seekTo(current);
                myHandler.postDelayed(mUpdateDialogVideo, 100);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                myHandler.removeCallbacks(mUpdateDialogVideo);
            }
        });

        play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (framevideo.isPlaying()) {
                    play_stop.setImageResource(R.drawable.dialog_play);
                    framevideo.pause();
                } else {
                    play_stop.setImageResource(R.drawable.dialog_pause);
                    framevideo.start();
                }
            }
        });

        dialog.show();
    }
}
