package com.a1337.kt.musiq

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient

class WebviewActivity : AppCompatActivity() {
    private val TAG = this.javaClass.name
    private var webView: WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url")
        if (url != null) {
            setContentView(R.layout.activity_webview)
            webView = findViewById(R.id.webView) as WebView
            val settings = webView!!.settings
            settings.javaScriptEnabled = true
            webView!!.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (Uri.parse(url).scheme != "x-oauthflow") {
                        return false
                    }
                    val intent = Intent(null, Uri.parse(url), applicationContext, FoursharedOauthActivity::class.java)
                    startActivity(intent)

                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    val cookieMgr = CookieManager.getInstance()
                    val cookies = cookieMgr.getCookie("http://4shared.com/")
                    Log.d(TAG, "URL: $url\ncookies:$cookies")
                }
            })
            webView?.loadUrl(url)
        } else {
            Log.e(TAG, "URL is not provided.")
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}
