package com.tagframe.tagframe.MyMediaPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tagframe.tagframe.UI.Acitivity.MakeNewEvent;
import com.tagframe.tagframe.Utils.Utility;

import java.io.IOException;



/**
 * Created by interview on 16/07/15.
 */
public class Player implements IPlayer,SurfaceHolder.Callback {

    private static final String TAG = Player.class.getSimpleName();
    private static boolean USE_BUFFERING_WORKAROUND = true;
    private static int ON_INFO_BUFFERING_START = 801;
    private static int ON_INFO_BUFFERING_END = 3;


    private MediaPlayer mPlayer;
    private SurfaceView mSurfaceView;
    private boolean mIsPrepared;
    private IPlayerListener mListener;
    private DisplayUtils mDisplayUtils;
    private int mStatusBarHeight;
    private Activity mActivity;
    private Handler mHandler=new Handler();
    private boolean IsVideoResized;


    public Player(SurfaceView view, Activity activity) {
        mSurfaceView = view;
        mActivity = activity;
        mDisplayUtils = new DisplayUtils();
        mPlayer = new MediaPlayer();

        mPlayer.setOnInfoListener(mOnInfo);
        mPlayer.setOnErrorListener(mOnError);
        mPlayer.setOnBufferingUpdateListener(mOnBuffering);
        mPlayer.setOnCompletionListener(mOnCompletion);
        mPlayer.setOnPreparedListener(mOnPrepared);
        mPlayer.setOnVideoSizeChangedListener(mOnVideoSize);


        mSurfaceView.getHolder().addCallback(this);
        // Initially 16:9
        resizeSurface(activity.getWindowManager().getDefaultDisplay());
    }



    @Override
    public void setSource(Context context, String url) {

        try {
            if (mPlayer != null) {
                if (mIsPrepared) {
                    reset();
                }
                if (USE_BUFFERING_WORKAROUND) {
                    mListener.onBufferingStarted();
                }
                mPlayer.setDataSource(context, Uri.parse(url));
                mPlayer.prepareAsync();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        mIsPrepared = false;

        // Call reset later, sometimes we call this from certain places and we dont want to fully block the call.
        // This way we post it to the looper and it will be executed in the next loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    mPlayer.reset();
                }
            }
        });

    }

    @Override
    public void release() {
        mIsPrepared = false;
        if (mPlayer != null) {
            if(mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.setDisplay(null);
            mPlayer.release();
            mSurfaceView.getHolder().removeCallback(this);
        }
        mPlayer = null;
    }

    private MediaPlayer.OnInfoListener mOnInfo = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            if (USE_BUFFERING_WORKAROUND && mListener != null) {
                if (what == ON_INFO_BUFFERING_START) {
                    mListener.onBufferingStarted();
                } else if (what == ON_INFO_BUFFERING_END) {
                    mListener.onBufferingFinished();
                }
                else if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                {
                    mListener.onRendereingstarted();
                }
            }
            Log.e("what",what+extra+"");

            return false;
        }
    };

    private MediaPlayer.OnErrorListener mOnError = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (mListener != null) {
                mListener.onError("Error, what: " + what + " extra: " + extra);
            }

            return false;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBuffering = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // NOOP
            //Log.e("dasd",percent+"");
        }
    };

    private MediaPlayer.OnPreparedListener mOnPrepared = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(final MediaPlayer mp) {
            mIsPrepared = true;
            if(mIsPrepared&&IsVideoResized)
            mp.start();
            else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mIsPrepared&&IsVideoResized)
                            mSurfaceView.setVisibility(View.VISIBLE);
                            mp.start();
                    }
                },500);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mListener != null) {
                mListener.onCompletion();
            }

        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSize = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            double proportion = (double)width / (double)height;
            Log.d(TAG, "onVideoSize: w"+ width + " h: " + height + " proportion: " + proportion);
            resizeSurface(mActivity.getWindowManager().getDefaultDisplay());
            IsVideoResized=true;
        }
    };




    public void resizeSurface(Display display) {

        Point point = mDisplayUtils.getFinalVideoSize(getRealDisplaySize(display), getVideoSize(mPlayer));

        ViewGroup.LayoutParams params =  mSurfaceView.getLayoutParams();
        params.width = point.x;
        params.height = point.y;

        mSurfaceView.requestLayout();
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mPlayer;
    }




    private PointF getRealDisplaySize(Display display) {

        PointF result = new PointF();
        Point size = new Point();
        display.getSize(size);
        result.x = size.x;
        if(mActivity instanceof MakeNewEvent) {
            result.y = size.y - getStatusBarHeight();
        }
        else
        {
            result.y=result.x;
        }
        return result;
    }

    private PointF getVideoSize(MediaPlayer mediaPlayer) {
        PointF result = new PointF();
        if (mediaPlayer != null) {
            result.x = mediaPlayer.getVideoWidth();
            result.y = mediaPlayer.getVideoHeight();
        }
        return result;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mActivity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void setListener(IPlayerListener listener) {
        mListener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "Surface created");
        if (mPlayer != null) {
            mPlayer.setDisplay(holder);
            if (mIsPrepared) {
                mPlayer.start();
            }
        } else {
            Log.e(TAG, "FATAL player is null!");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surface destroyed");
        if (mPlayer!=null) {
            mPlayer.setDisplay(null);
        } else {
            Log.e(TAG, "FATAL player is null!");
        }
    }



}
