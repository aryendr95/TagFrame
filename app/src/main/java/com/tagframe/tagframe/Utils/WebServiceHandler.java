package com.tagframe.tagframe.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.IntentServiceOperations;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by abhinav on 04/04/2016.
 */
public class WebServiceHandler {

    private HttpURLConnection httpConn;
    private DataOutputStream request;
    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    int bytesRead, bytesAvailable, bufferSize;
    public static int upload_video_headers = 475;

    byte[] buffer;

    int maxBufferSize = 1 * 1024 * 1024;
    private int MY_NOTIFICATION_ID;
    NotificationManager notificationManager;
    Context context;
    Notification myNotification;

    private UploadCallbacks mlistener;


    public WebServiceHandler(String requestURL, long size, int header_size)
            throws IOException {


        // creates a unique boundary based on time stamp
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        //httpConn.setChunkedStreamingMode(1024);
        httpConn.setFixedLengthStreamingMode((int) (size + header_size));

        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "Kee");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        httpConn.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + this.boundary);

        request = new DataOutputStream(httpConn.getOutputStream());
    }

    public WebServiceHandler(String requestURL)
            throws IOException {

        // creates a unique boundary based on time stamp
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        httpConn.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + this.boundary);

        request = new DataOutputStream(httpConn.getOutputStream());
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws IOException {
        try {
            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + this.crlf);
            //request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
            request.writeBytes(this.crlf);
            request.writeBytes(value);
            request.writeBytes(this.crlf);
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }


    }

    public void addFilePart(String fieldName, File uploadFile, int notification_id, Context context, UploadCallbacks mlisten)
            throws IOException {
        try {
            long filesize = uploadFile.length();
            mlistener = mlisten;

            FileInputStream fileInputStream = new FileInputStream(uploadFile);

            String fileName = uploadFile.getName();
            Log.e("get_name", fileName.getBytes().length + "");
            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    fieldName + "\";filename=\"" +
                    fileName + "\"" + this.crlf);
            request.writeBytes(this.crlf);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];


            Handler handler = new Handler(Looper.getMainLooper());
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            long uploaded = 0;
            while (bytesRead > 0) {


                if (bytesRead < bufferSize)
                    uploaded = uploaded + bytesRead * 100 / filesize;
                else
                    uploaded = uploaded + bufferSize * 100 / filesize;


                handler.post(new ProgressUpdater(uploaded));

                request.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            request.writeBytes(this.crlf);

            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }

    }


    public void addFilePart(String fieldName, File uploadFile, int notification_id, Context context)
            throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(uploadFile);

            String fileName = uploadFile.getName();
            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    fieldName + "\";filename=\"" +
                    fileName + "\"" + this.crlf);
            request.writeBytes(this.crlf);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];


            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {


                request.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            request.writeBytes(this.crlf);

            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }

    }


    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */


    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response = "";
        try {


            //  request.writeBytes(this.crlf);
            request.writeBytes(this.twoHyphens + this.boundary +
                    this.twoHyphens + this.crlf);

            request.flush();
            request.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = new
                        BufferedInputStream(httpConn.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                response = stringBuilder.toString();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return response;
    }


    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);

        void onError();

        void onFinish();
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded) {
            mUploaded = uploaded;

        }

        @Override
        public void run() {
            mlistener.onProgressUpdate((int) (mUploaded));
        }
    }


}


