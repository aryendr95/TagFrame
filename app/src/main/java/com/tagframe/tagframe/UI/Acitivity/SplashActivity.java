package com.tagframe.tagframe.UI.Acitivity;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tagframe.tagframe.Models.Event_Model;
import com.tagframe.tagframe.Models.ListResponseModel;
import com.tagframe.tagframe.Models.ResponseModels.RmAuthentication;
import com.tagframe.tagframe.Models.User;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Retrofit.ApiClient;
import com.tagframe.tagframe.Retrofit.ApiInterface;
import com.tagframe.tagframe.UI.Fragments.Splash;
import com.tagframe.tagframe.Utils.AppPrefs;
import com.tagframe.tagframe.Utils.Networkstate;
import com.tagframe.tagframe.Utils.PopMessage;
import com.tagframe.tagframe.Utils.Utility;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
  com.tagframe.tagframe.Utils.AppPrefs AppPrefs;
  private ProgressBar progressBar;
  private TextView txt;
  private String android_id;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_splash);
    android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    progressBar = (ProgressBar) findViewById(R.id.splashbar);

    AppPrefs = new AppPrefs(this);
    txt = (TextView) findViewById(R.id.txtsplashmessage);

    if (getIntent().getStringExtra("load").equalsIgnoreCase("tagstream")) {
      txt.setText("Loading...");
      loadTagStream();
    } else {
      loginUser();
    }
  }

  public void loginUser() {
    final AppPrefs appPrefs = new AppPrefs(this);
    final User user = appPrefs.getUser();
    TimeZone tz = TimeZone.getDefault();
    Utility.getApiCaller()
        .login(user.getUsername(), user.getPassword(),tz.getID())
        .enqueue(new Callback<RmAuthentication>() {
          @Override
          public void onResponse(Call<RmAuthentication> call, Response<RmAuthentication> response) {
            if (!isFinishing()) {
              try {
                if (response.body().getStatus().equals("success")) {
                  User user1 = response.body().getUser();
                  user1.setPassword(appPrefs.getString(Utility.user_password));
                  user1.setLoggedin(true);
                  appPrefs.putUser(user1);
                  txt.setText("Loading..");
                  loadTagStream();
                } else {
                  PopMessage.makeshorttoast(SplashActivity.this, response.body().getStatus());
                  appPrefs.putUser(new User());
                  Intent intent = new Intent(SplashActivity.this, Authentication.class);
                  startActivity(intent);
                  finish();
                }
              }
              catch (NullPointerException e)
              {
                PopMessage.makeshorttoast(SplashActivity.this, "Could not connect at this time");
                finish();
              }
            }
          }

          @Override public void onFailure(Call<RmAuthentication> call, Throwable t) {
            if (!isFinishing()) {
              PopMessage.makeshorttoast(SplashActivity.this, Utility.ERROR_MESSAGE);
              finish();
            }
          }
        });
  }

  public void loadTagStream() {
    if (Networkstate.haveNetworkConnection(this)) {
      ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
      Call<ListResponseModel> call = apiInterface.getTagStream(AppPrefs.getString(Utility.user_id));
      call.enqueue(new Callback<ListResponseModel>() {
        @Override
        public void onResponse(Call<ListResponseModel> call, Response<ListResponseModel> response) {
          if (!isFinishing()) {
            try {
              Log.e("ds",new Gson().toJson(response));
              if (response.body().getStatus().equals("success")) {
                ArrayList<Event_Model> tagStream_models = response.body().getTagStreamArrayList();
                AppPrefs.puttagstreamlist(tagStream_models);

                Intent intent = new Intent(SplashActivity.this, Modules.class);
                startActivity(intent);
                finish();
              } else {
                PopMessage.makeshorttoast(SplashActivity.this, response.body().getStatus());
              }
            } catch (Exception e) {
              PopMessage.makeshorttoast(SplashActivity.this,
                  "There is some error, try after some time..");
              finish();
            }
          }
        }

        @Override public void onFailure(Call<ListResponseModel> call, Throwable t) {
          Log.e("dsa","dsa"+t.getMessage());
          if(t instanceof TimeoutException)
          {
            Log.e("dsa","timeout,what the fuck ");
          }
        }
      });
    } else {
      PopMessage.makeshorttoast(SplashActivity.this, "No Internet Connection");
      finish();
    }
  }
}
