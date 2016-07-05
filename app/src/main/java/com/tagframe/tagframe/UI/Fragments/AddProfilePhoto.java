package com.tagframe.tagframe.UI.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.tagframe.tagframe.Utils.Constants;
import com.tagframe.tagframe.Utils.MyToast;
import com.tagframe.tagframe.Utils.WebServiceHandler;
import com.tagframe.tagframe.Utils.listops;

import org.json.JSONObject;

import java.io.File;

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
    String picturePath="";

    listops listops;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview=inflater.inflate(R.layout.add_profile_photo,container,false);

        listops=new listops(getActivity());

        descrip=(EditText)mview.findViewById(R.id.add_description);

        skip=(TextView)mview.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), Modules.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        pro_image=(CircularImageView)mview.findViewById(R.id.addpic);
        pro_image.setImageResource(R.drawable.pro_image);

        changeimage=(ImageView)mview.findViewById(R.id.chanepic);
        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        mbuttonsumbit=(Button)mview.findViewById(R.id.Save);
        mbuttonsumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String des=descrip.getText().toString();
                if(!des.isEmpty()&&!picturePath.isEmpty()) {
                    new saveprofilephoto().execute(des);
                }
                else
                {
                    MyToast.popmessage("Please provide a image or enter description",getActivity());
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
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


            pro_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    class saveprofilephoto extends AsyncTask<String,String,String>
    {
        String status="";
        WebServiceHandler webServiceHandler;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
           dialog=new ProgressDialog(getActivity());
            dialog.setMessage("Saving..");
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                webServiceHandler=new WebServiceHandler(Constants.upload_profile_photo);
                webServiceHandler.addFormField("user_id",listops.getString(Constants.user_id));
                if(!picturePath.isEmpty()) {
                    File file = new File(picturePath);
                    webServiceHandler.addFilePart("profile_photo",file,3,getActivity());
                }
                webServiceHandler.addFormField("description", params[0]);
                JSONObject jsonObject=new JSONObject(webServiceHandler.finish());
                status=jsonObject.getString("success");
            }
            catch (Exception e)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(status.equals("success"))
            {
                Intent intent=new Intent(getActivity(), Modules.class);
                startActivity(intent);
                getActivity().finish();
            }

        }
    }
}
