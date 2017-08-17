package com.tagframe.tagframe.MyMediaPlayer;

//By extending a TextureView, I get no black screens in the beginning or end. This is if you want to avoid using ZOrderOnTop(true).

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import java.io.IOException;

public class MyVideoView extends TextureView implements TextureView.SurfaceTextureListener {
  private MediaPlayer mMediaPlayer;
  private Uri mSource;
  private MediaPlayer.OnCompletionListener mCompletionListener;
  private MediaPlayer.OnPreparedListener mPreparedListner;
  private boolean isLooping = false;

  public MyVideoView(Context context) {
    this(context, null, 0);
  }

  public MyVideoView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setSurfaceTextureListener(this);
  }

  public void setSource(Uri source) {
    mSource = source;
  }

  public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
    mCompletionListener = listener;
  }

  public void OnPrepareListener(MediaPlayer.OnPreparedListener listner) {
    mPreparedListner = listner;
  }

  public void setLooping(boolean looping) {
    isLooping = looping;
  }

  @Override protected void onDetachedFromWindow() {
    // release resources on detach
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    super.onDetachedFromWindow();
  }

  /*
   * TextureView.SurfaceTextureListener
   */
  @Override public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,
      int height) {
    Surface surface = new Surface(surfaceTexture);
    try {
      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setOnCompletionListener(mCompletionListener);
      //mMediaPlayer.setOnBufferingUpdateListener(this);
      //mMediaPlayer.setOnErrorListener(this);
      mMediaPlayer.setLooping(isLooping);
      mMediaPlayer.setDataSource(getContext(), mSource);
      mMediaPlayer.setSurface(surface);
      mMediaPlayer.prepare();
      mMediaPlayer.setOnPreparedListener(mPreparedListner);
      mMediaPlayer.start();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      mMediaPlayer.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
  }

  @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    surface.release();
    return true;
  }

  @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
  }

  public long getDuration() {
   return mMediaPlayer.getDuration();
  }

  public long getCurrentPosition() {
  return mMediaPlayer.getCurrentPosition();
  }

  public void seekTo(int current) {
  mMediaPlayer.seekTo(current);
  }

  public boolean isPlaying() {
    return mMediaPlayer.isPlaying();
  }

  public void pause() {
    mMediaPlayer.pause();
  }

  public void start() {
    mMediaPlayer.pause();
  }
}