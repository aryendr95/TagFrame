package com.tagframe.tagframe.Utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tagframe.tagframe.R;

/**
 * Created by Brajendr on 1/20/2017.
 */

public class ImageLoader {

  public static void loadImageOnline(Context context,String path,ImageView imageView,
      final ProgressBar progressBar)
  {
    if(path.isEmpty())
    {
      imageView.setImageResource(R.drawable.bg_no_image);
    }
    progressBar.setVisibility(View.VISIBLE);
    Picasso.with(context).load(path).fit().centerCrop().into(imageView, new Callback() {
      @Override public void onSuccess() {
        progressBar.setVisibility(View.GONE);
      }

      @Override public void onError() {
        progressBar.setVisibility(View.GONE);
      }
    });

  }

  public static void loadImageOnline(Context context,String path,ImageView imageView,
      final ProgressBar progressBar,int placeholder)
  {
    if(path.isEmpty())
    {
      imageView.setImageResource(placeholder);
      return;
    }
    progressBar.setVisibility(View.VISIBLE);
    Picasso.with(context).load(path).fit().centerCrop().into(imageView, new Callback() {
      @Override public void onSuccess() {
        progressBar.setVisibility(View.GONE);
      }

      @Override public void onError() {
        progressBar.setVisibility(View.GONE);
      }
    });

  }

  public static void loadImageOnline(Context context,String path,ImageView imageView)
  {
    if(path.isEmpty())
      return;
    Picasso.with(context).load(path).into(imageView);

  }
}
