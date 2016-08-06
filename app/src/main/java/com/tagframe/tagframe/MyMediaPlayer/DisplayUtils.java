package com.tagframe.tagframe.MyMediaPlayer;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by gaspar on 18/07/15.
 */
public class DisplayUtils {

    private static final String TAG = DisplayUtils.class.getSimpleName();

    /**
     * Calculates the right videoSize keeping the original ratio.
     * @param displaySize Real display size, make sure you include the status bar inside.
     * @param videoSize Real video size, usually got from the mediaplayer component.
     * @return
     */
    public Point getFinalVideoSize(PointF displaySize, PointF videoSize) {
        Point result = new Point();

        double displayRatio = 0;
        double videoRatio = 0;

        if (displaySize.y > 0) {
            displayRatio = displaySize.x / displaySize.y;
        }
        if (videoSize.y > 0) {
            videoRatio = videoSize.x / videoSize.y;
        } else {
            // If we have no video size, we use this default ratio: 1.6
            videoRatio = 1.6;
        }

        Log.d(TAG, "display: " + displaySize.toString() + " displayRatio: " + displayRatio + " video: " + videoSize.toString() + " videoRatio: " + videoRatio);

        if (displayRatio >= videoRatio) {
            result.y = (int) displaySize.y;
            result.x = (int) (displaySize.y * videoRatio);
        } else {
            result.y = (int) (displaySize.x / videoRatio);
            result.x = (int) displaySize.x;
        }

        return result;
    }
}
