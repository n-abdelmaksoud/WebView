package com.example.littleprincess.karimstore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private ImageView imageView;
    private static final String KARIM_URL="https://developer.android.com/index.html";
    private static final String TAG=MainActivity.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView=findViewById(R.id.web_view);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);
        imageView=findViewById(R.id.image);
        imageView.setVisibility(View.INVISIBLE);


        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                if(isNetworkConnected()){
                    progressBar.setVisibility(View.VISIBLE);
                    webView.loadUrl( "javascript:window.location.reload( true )" );
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }, 10000);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,getString(R.string.internet_disconnected),Toast.LENGTH_LONG).show();
                }

            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        MyWebViewClient myWebViewClient=new MyWebViewClient();
        webView.setWebViewClient(myWebViewClient);



        if(savedInstanceState!=null){
            webView.restoreState(savedInstanceState);
        }else {
            if(isNetworkConnected()){
                webView.loadUrl(KARIM_URL);
            } else {
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this,getString(R.string.internet_disconnected),Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean isNetworkConnected(){
        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.i(TAG,"Network is connected"+isConnected);
        return isConnected;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG,"shouldOverrideUrlLoading is accessed");
          progressBar.setVisibility(View.VISIBLE);

            view.loadUrl(url);

            return false;
        }
        @Override
        public void onPageFinished(WebView view, final String url) {
            Log.i(TAG,"onPageFinished is accessed");
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
                  imageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (webView != null) {
           webView.destroy();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }


}
