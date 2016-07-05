package com.tagframe.tagframe.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tagframe.tagframe.R;


/**
 * Created by Karan on 12/30/2015.
 */
public class MyToast {



    public static  void  popmessage(String mytext,Context mcontext)
    {
        View layout;
        if(mcontext instanceof Activity) {
            LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
             layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) ((Activity) mcontext).findViewById(R.id.custom_toast_layout_id));
        }
        else
        {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.custom_toast,
                    null);
        }
        // set a message
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(mytext);


        // Toast...
        Toast toast = new Toast(mcontext);
       // toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
