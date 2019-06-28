package com.tagframe.tagframe.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.FrameList_Model;
import com.tagframe.tagframe.MyMediaPlayer.MyVideoView;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.UI.Acitivity.WatchEventActivity;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.Utility;

import java.util.ArrayList;

/**
 * Created by Brajendr on 3/22/2017.
 */

public class TagStreamFrameRecyclerAdapter extends RecyclerView.Adapter<TagStreamFrameRecyclerAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<FrameList_Model> frameList_models;
    private String LIST_TYPE;

    public TagStreamFrameRecyclerAdapter(Context context, ArrayList<FrameList_Model> frameList_models,
                                         String mine) {
        this.context = context;
        this.frameList_models = frameList_models;
        this.LIST_TYPE = mine;
    }

    @Override
    public TagStreamFrameRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_tagstream_framelist, parent, false);
        return new TagStreamFrameRecyclerAdapter.MyViewHolder(itemView);

    }

    private int getUser_Type(String user_id, String saved_user_id) {
        if (user_id.equals(saved_user_id)) {
            return Utility.user_type_self;
        } else {
            return Utility.user_type_following;
        }
    }

    @Override
    public void onBindViewHolder(TagStreamFrameRecyclerAdapter.MyViewHolder holder, int position) {
        final FrameList_Model tagStream = frameList_models.get(position);
        if (tagStream.getFrametype() == com.tagframe.tagframe.Utils.Constants.frametype_image) {
            Picasso.with(context).load(tagStream.getFrame_image_url()).into(holder.ivframeimage);
        } else {
            Picasso.with(context).load(tagStream.getImagepath()).into(holder.ivframeimage);

        }
        if (tagStream.getFrametype() == Utility.frametype_image) {
            holder.ivPLayVideo.setVisibility(View.GONE);
        } else {
            holder.ivPLayVideo.setVisibility(View.VISIBLE);
        }
        if (LIST_TYPE.equals("mine")) {
            holder.userName.setVisibility(View.GONE);
        } else {
            AppPrefs appPrefs = new AppPrefs(context);
            final int user_type = getUser_Type(tagStream.getUser_id(), appPrefs.getString(Utility.user_id));
            holder.userName.setText(tagStream.getUser_name() + " added ");
            holder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Modules) context).setprofile(tagStream.getUser_id(), user_type);
                }
            });
        }
        holder.ivframeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFrame(context, tagStream);
            }
        });
    }

    public static void showFrame(final Context ctx, final FrameList_Model tagStream) {
        if (tagStream.getFrametype() == com.tagframe.tagframe.Utils.Constants.frametype_image) {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_frame_display_profile);

            // VideoView framevideo = (VideoView) dialog.findViewById(R.id.framelist_video);
            ImageView frameimage = (ImageView) dialog.findViewById(R.id.framelist_image);
            ImageView delete = (ImageView) dialog.findViewById(R.id.framelist_delete);
            ImageView product_image = (ImageView) dialog.findViewById(R.id.product_image);
            final TextView buyProduct = (TextView) dialog.findViewById(R.id.buyProduct);
            if (!tagStream.getProduct_id().equals("0")) {
                Picasso.with(ctx).load(tagStream.getProduct_path()).into(product_image);
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


            RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.rl_see_event);
            relativeLayout.setVisibility(View.GONE);
            frameimage.setVisibility(View.VISIBLE);
            Picasso.with(ctx).load(tagStream.getFrame_image_url()).into(frameimage);


            tittle.setFocusable(false);
            tittle.setText(tagStream.getName());
            duration.setText(Utility.milliSecondsToTimer(tagStream.getStarttime()) + "-" + Utility.milliSecondsToTimer(tagStream.getEndtime()));

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

    private static void showVideoDialog(final Context ctx, final FrameList_Model frameList_model) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
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
        if (!frameList_model.getProduct_id().equals("0") && !frameList_model.isAProductFrame()) {
            product_image.setVisibility(View.VISIBLE);
            Picasso.with(ctx).load(frameList_model.getProduct_path()).into(product_image);
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
        TextView relativeLayout = (TextView) dialog.findViewById(R.id.see_event);
        relativeLayout.setVisibility(View.GONE);
        TextView duration = (TextView) dialog.findViewById(R.id.framelist_time);
        final EditText tittle = (EditText) dialog.findViewById(R.id.framelist_name);
        tittle.setFocusable(false);


        try {

            framevideo.setVideoURI(Uri.parse(frameList_model.getFrame_image_url()));
            MediaController mediaController = new MediaController(ctx);
            mediaController.setAnchorView(framevideo);
            framevideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    coverLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            });
            framevideo.setZOrderOnTop(true);
            framevideo.start();
        } catch (Exception e) {

        }

        tittle.setText(frameList_model.getName());
        duration.setText(Utility.milliSecondsToTimer(frameList_model.getStarttime()) + "-" + Utility.milliSecondsToTimer(frameList_model.getEndtime()));
        //duration.setVisibility(View.GONE);
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
                int total = (int) framevideo.getDuration();
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

    @Override
    public int getItemCount() {
        return frameList_models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivframeimage, ivPLayVideo;
        private TextView userName;

        public MyViewHolder(View item) {
            super(item);
            ivframeimage = (ImageView) item.findViewById(R.id.grid_iimag_item);
            ivPLayVideo = (ImageView) item.findViewById(R.id.img_play);
            userName = (TextView) item.findViewById(R.id.userName);
        }
    }
}
