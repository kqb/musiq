package com.a1337.kt.musiq

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import oauth.signpost.OAuth
import org.jetbrains.anko.alert


/**
 * Entry point in the application.
 * Launches the OAuth flow by starting the FoursharedOauthActivity

 */

class LoginActivity : AppCompatActivity() {

    internal val TAG = javaClass.name
    private var prefs: SharedPreferences? = null
    private val fsLogin: FoursharedApiService? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (Constants.ACTION_RELOGIN.equals(intent.action)) {
            clearCredentials()
            alert(intent.getStringExtra(Constants.ACTION_RELOGIN), "Please relogin") {
                positiveButton("Login") {
                }
            }.show()
        }
        if (isLoggedIn())
            startActivity(Intent(this, MainActivity::class.java))

        setContentView(R.layout.main)


        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        val launchOauth = findViewById(R.id.btn_launch_oauth) as Button
        val clearCredentials = findViewById(R.id.btn_clear_credentials) as Button
        val fsAuth = Intent().setClass(this, FoursharedOauthActivity::class.java)
        //add callback activity once auth is done
        val packageName = this.javaClass.`package`.name
        fsAuth.putExtra("callback", packageName + ".MainActivity")
        fsAuth.putExtra("callbackFalure", packageName + ".LoginActivity")



        launchOauth.setOnClickListener {
            startActivity(fsAuth)
            finish()
        }

        clearCredentials.setOnClickListener { clearCredentials() }
    }


    fun clearCredentials() {

        val edit = prefs!!.edit()
        edit.remove(OAuth.OAUTH_TOKEN)
        edit.remove(OAuth.OAUTH_TOKEN_SECRET)
        edit.commit()

        Utils.clearCookies(applicationContext)
    }

    fun isLoggedIn(): Boolean {
        return (!(prefs!!.getString(OAuth.OAUTH_TOKEN, null).isNullOrBlank()
                || prefs!!.getString(OAuth.OAUTH_TOKEN_SECRET, null).isNullOrBlank()))

    }


    companion object {
        private val PICK_CONTACT = 0
    }

}