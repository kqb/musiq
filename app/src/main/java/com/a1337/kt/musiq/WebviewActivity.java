package com.a1337.kt.musiq;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends AppCompatActivity {
    private String TAG = this.getClass().getName();
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            setContentView(R.layout.activity_webview);
            webView = (WebView) findViewById(R.id.webView);
            WebSettings settings =webView.getSettings();
            settings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!Uri.parse(url).getScheme().equals("x-oauthflow")) {
                        return false;
                    }
                    Intent intent = new Intent(null, Uri.parse(url),getApplicationContext(),FoursharedOauthActivity.class);
                    startActivity(intent);

                    return true;
                }

                public void onPageFinished(WebView view, String url){
                    CookieManager cookieMgr= CookieManager.getInstance();
                    String cookies = cookieMgr.getCookie("http://4shared.com/");
                    Log.d(TAG, "URL: "+url+"\ncookies:" + cookies);
                }
            });
            webView.loadUrl(url);
        }
        else{
            Log.e(TAG, "URL is not provided.");
        }

    }

}
