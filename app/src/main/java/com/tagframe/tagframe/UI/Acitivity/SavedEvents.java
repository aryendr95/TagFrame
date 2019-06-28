package com.tagframe.tagframe.UI.Acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tagframe.tagframe.Adapters.SavedEvent_Adapter;
import com.tagframe.tagframe.R;
import com.tagframe.tagframe.Services.Broadcastresults;
import com.tagframe.tagframe.Utils.AppPrefs;

public class SavedEvents extends AppCompatActivity implements Broadcastresults.Receiver {


    ListView listView;
    AppPrefs user_data;

    TextView txtview_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        user_data = new AppPrefs(this);

        listView = (ListView) findViewById(R.id.list_saved_events);

        ImageView back = (ImageView) findViewById(R.id.save_event_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedEvents.this, Modules.class);
                startActivity(intent);
                finish();
            }
        });

        TextView mtxt_menu = (TextView) findViewById(R.id.save_action_text);

        mtxt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedEvents.this, Modules.class);
                startActivity(intent);
                finish();
            }
        });

        txtview_msg = (TextView) findViewById(R.id._txt_saveevent_msg);

        if (user_data.getsingleeventlist().size() > 0) {
            listView.setAdapter(new SavedEvent_Adapter(this, user_data.getsingleeventlist()));
        } else {
            txtview_msg.setVisibility(View.VISIBLE);
        }

    }

    public Broadcastresults register_reviever() {
        Broadcastresults mReceiver = new Broadcastresults(new Handler());

        mReceiver.setReceiver(this);

        return mReceiver;

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SavedEvents.this, Modules.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }
}
