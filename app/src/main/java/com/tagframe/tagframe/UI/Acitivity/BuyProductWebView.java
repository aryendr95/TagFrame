package com.tagframe.tagframe.UI.Acitivity;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.tagframe.tagframe.R;
import com.tagframe.tagframe.UI.Fragments.MarketPlaceFragment;

public class BuyProductWebView extends AppCompatActivity {
    private ImageView mimg_menu_back;
  WebView  webView;
    private Activity dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        String url = getIntent ().getStringExtra ( "url");
        setContentView (R.layout.activity_buy_product_web_view);
        webView = findViewById (R.id.wv);
        webView.loadUrl(url);

        mimg_menu_back = findViewById(R.id.menu_action_back1);
        mimg_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyProductWebView.this, MarketPlaceFragment.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
               //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //startActivity(intent);
                finish();
            }
        });


    }
}
