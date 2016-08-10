package com.tagframe.tagframe.MyMediaPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.SurfaceView;



/**
 * Created by interview on 16/07/15.
 */
public interface IPlayer {

    public void setSource(Context context, String url);

    public void reset();

    public void release();

    public void setListener(IPlayerListener listener);

    public void resizeSurface(Display display);

    public MediaPlayer getMediaPlayer();


}

