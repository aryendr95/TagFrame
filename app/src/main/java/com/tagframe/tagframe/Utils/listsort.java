package com.tagframe.tagframe.Utils;

import com.tagframe.tagframe.Models.FrameList_Model;
import java.util.Comparator;

public class listsort implements Comparator<FrameList_Model> {

        @Override
        public int compare(FrameList_Model e1, FrameList_Model e2) {
            if (e1.getStarttime() < e2.getStarttime()) {
                return -1;
            } else {
                return 1;
            }
        }
    }