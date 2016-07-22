package com.tagframe.tagframe.MyMediaPlayer;

/**
 * Created by interview on 17/07/15.
 */
public interface IPlayerListener {

    public void onError(String message);
    public void onBufferingStarted();
    public void onBufferingFinished();
    public void onRendereingstarted();

}
