package com.tagframe.tagframe.UI.Acitivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Utils.PopMessage;

import java.util.ArrayList;

public class AndroidCustomGalleryActivity extends Activity {


    private int count, limit;
    private Bitmap[] thumbnails;
    private ArrayList<String> selectImages = new ArrayList<String>();
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private ImageAdapter imageAdapter;


    private RelativeLayout mlayout;
    private Button btn_select;
    private TextView mtxt_menu;
    private ImageView mimg_menu_back;
    private ProgressBar pbar;
    private GridView imagegrid;

    private int selectcount=0;


    loadimage loadimage;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media__chooser);

        loadimage = new loadimage();
        loadimage.execute();

        limit = getIntent().getIntExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_LIMIT, 5);

        mlayout = (RelativeLayout) findViewById(R.id.mlayout_media_choose);
        pbar = (ProgressBar) findViewById(R.id.pbar_media_choseer);
        imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        mtxt_menu = (TextView) findViewById(R.id.media_action_text);
        mimg_menu_back = (ImageView) findViewById(R.id.media_action_back);


        mimg_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(901);
                if (!loadimage.isCancelled()) {
                    loadimage.cancel(true);
                }
                finish();
            }
        });

        mtxt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(901);
                if (!loadimage.isCancelled()) {
                    loadimage.cancel(true);
                }
                finish();
            }
        });

        final Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                final int len = thumbnailsselection.length;

                selectImages = new ArrayList<String>();
                int cnt = 0;

                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages.add(arrPath[i]);


                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one item",
                            Toast.LENGTH_LONG).show();
                } else {

                    if (cnt > limit) {
                        PopMessage.makesimplesnack(mlayout, "You can't select more than " + limit + " videos");
                    } else {
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("videopaths", selectImages);
                        setResult(901, intent);
                        if (!loadimage.isCancelled()) {
                            loadimage.cancel(true);
                        }
                        finish();
                    }

                }
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.mlayout = (RelativeLayout) convertView.findViewById(R.id.layout_image_Sel);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageview.setId(position);



            holder.imageview.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub



                    int id = v.getId();
                    if (thumbnailsselection[id]) {
                        holder.mlayout.setVisibility(View.GONE);
                        selectcount--;
                        mtxt_menu.setText(""+selectcount+" selected");
                        thumbnailsselection[id] = false;
                    } else {
                        holder.mlayout.setVisibility(View.VISIBLE);
                        selectcount++;
                        mtxt_menu.setText(""+selectcount+" selected");
                        thumbnailsselection[id] = true;

                    }

                }
            });

            holder.imageview.setImageBitmap(thumbnails[position]);


            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        RelativeLayout mlayout;
        int id;
    }


    public class loadimage extends AsyncTask<String, String, String> {

        Cursor imagecursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            final String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
            final String orderBy = MediaStore.Video.Media._ID;
            imagecursor = managedQuery(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy);
            int image_column_index = imagecursor.getColumnIndex(MediaStore.Video.Media._ID);
            count = imagecursor.getCount();
            thumbnails = new Bitmap[count];
            arrPath = new String[count];
            thumbnailsselection = new boolean[count];
            for (int i = 0; i < count; i++) {
                imagecursor.moveToPosition(i);
                int id = imagecursor.getInt(image_column_index);
                int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DATA);

                thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(
                        getApplicationContext().getContentResolver(), id,
                        MediaStore.Video.Thumbnails.MINI_KIND, null);

                arrPath[i] = imagecursor.getString(dataColumnIndex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbar.setVisibility(View.GONE);
            imageAdapter = new ImageAdapter();
            imagegrid.setAdapter(imageAdapter);
            imagecursor.close();
        }
    }
}



