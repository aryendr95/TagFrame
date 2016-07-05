package com.tagframe.tagframe.UI.Acitivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Fragments.Account;
import com.tagframe.tagframe.UI.Fragments.TagStream;
import com.tagframe.tagframe.UI.Fragments.TermsofService;
import com.tagframe.tagframe.UI.Fragments.ViewerPrivacy;

/**
 * Created by abhinav on 06/04/2016.
 */
public class Menu_Action extends FragmentActivity {


    private ImageView mimg_menu_back;
    private TextView mtxt_menu;
    private FrameLayout layout;
    int which_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_actions);

        mtxt_menu=(TextView)findViewById(R.id.menu_action_text);
        mimg_menu_back=(ImageView)findViewById(R.id.menu_action_back);
        mimg_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu_Action.this,Modules.class);
                intent.putExtra("name",getIntent().getStringExtra("name"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        mtxt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu_Action.this,Modules.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        mtxt_menu.setText(getIntent().getStringExtra("name"));

        which_layout=getIntent().getIntExtra("layout",0);

        layout=(FrameLayout)findViewById(R.id.menu_action_frame);


        Fragment myf=new Account();
        switch (which_layout)
        {
              case 0:
                  myf=new Account();
            break;
            case 1:
                myf=new ViewerPrivacy();
                break;
            case 2:
                myf=new TermsofService();
                break;
            case 3:
        }


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.menu_action_frame, myf);
        transaction.commit();

    }
}
