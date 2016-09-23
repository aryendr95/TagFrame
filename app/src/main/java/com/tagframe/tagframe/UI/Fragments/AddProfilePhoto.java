package com.tagframe.tagframe.UI.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Acitivity.Modules;
import com.tagframe.tagframe.Utils.BitmapHelper;
import com.tagframe.tagframe.Utils.GetPaths;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.AppPrefs;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by abhinav on 18/04/2016.
 */
public class AddProfilePhoto extends Fragment {


    View mview;
    TextView skip;
    CircularImageView pro_image;
    ImageView changeimage;
    Button mbuttonsumbit;
    EditText descrip;

    private static int RESULT_LOAD_IMAGE = 1;
    String picturePath = "";

    AppPrefs AppPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.add_profile_photo, container, false);

        AppPrefs = new AppPrefs(getActivity());

        descrip = (EditText) mview.findViewById(R.id.add_description);

        skip = (TextView) mview.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Modules.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        pro_image = (CircularImageView) mview.findViewById(R.id.addpic);
        pro_image.setImageResource(R.drawable.pro_image);

        changeimage = (ImageView) mview.findViewById(R.id.chanepic);
        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        mbuttonsumbit = (Button) mview.findViewById(R.id.Save);
        mbuttonsumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String des = descrip.getText().toString();
                if (!des.isEmpty() && !picturePath.isEmpty()) {
                    new saveprofilephoto().execute(des);
                } else {
                    MyToast.popmessage("Please provide a image or enter description", getActivity());
                }
            }
        });

        return mview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {


            Uri selectedImage = data.getData();
            picturePath = GetPaths.getPath(getActivity(), selectedImage);
            pro_image.setImageBitmap(BitmapHelper.decodeFile(getActivity(),new File(picturePath)));

        }


    }

    class saveprofilephoto extends AsyncTask<String, String, String> {
        String status = "", profilephoto, description;
        WebServiceHandler webServiceHandler;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Saving..");
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                description = params[0];

                webServiceHandler = new WebServiceHandler(Utility.upload_profile_photo);
                webServiceHandler.addFormField("user_id", AppPrefs.getString(Utility.user_id));
                if (!picturePath.isEmpty()) {
                    File file = new File(picturePath);
                    if(file.length()/1000>512)
                    {
                        File fileCache = new File(getActivity().getCacheDir(), "temp.png");
                        Bitmap loadBitmap=BitmapHelper.decodeFile(getActivity(),file);
                        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                        loadBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                        byte[] bitmapData=byteArrayOutputStream.toByteArray();

                        FileOutputStream fileOutputStream=new FileOutputStream(fileCache);
                        fileOutputStream.write(bitmapData);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        file=fileCache;

                    }
                    webServiceHandler.addFilePart("profile_photo", file, 3, getActivity());
                }
                webServiceHandler.addFormField("description", params[0]);
                String result = webServiceHandler.finish();
                JSONObject jsonObject = new JSONObject(result);

                status = jsonObject.getString("status");
                profilephoto = jsonObject.getString("profile_photo");

            } catch (Exception e) {
                Log.e("get", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (isAdded()) {
                if (status.equals("success")) {
                    AppPrefs.putString(Utility.user_descrip, description);
                    AppPrefs.putString(Utility.user_pic, profilephoto);
                    Intent intent = new Intent(getActivity(), Modules.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    PopMessage.makeshorttoast(getActivity(), "Error, please try after some time" + status + profilephoto);
                }
            }

        }
    }
}
