package com.varsitycollege.locuslocatorsapp.MyFavourites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.varsitycollege.locuslocatorsapp.R;

/*
 * Code Attribution
 * Name: Dalvik Bytes
 * Published: 1 July 2020
 * URL: https://youtu.be/rZ-idvvsm_w
 */
//alieya started
public class GameActivity extends AppCompatActivity {

    private WebView webView;
    String url ="file:///android_asset/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        webView=findViewById(R.id.wv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
//alieya ended
/*
 * Code Attribution Ended*/