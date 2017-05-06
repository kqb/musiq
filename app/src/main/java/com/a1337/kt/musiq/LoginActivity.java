package com.a1337.kt.musiq;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import oauth.signpost.OAuth;


/**
 * Entry point in the application.
 * Launches the OAuth flow by starting the FoursharedOauthActivity
 *
 */

public class LoginActivity extends Activity {
	private static final int PICK_CONTACT = 0;
	final String TAG = getClass().getName();
	private SharedPreferences prefs;
    private FoursharedApiService fsLogin;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		if (android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Button launchOauth = (Button) findViewById(R.id.btn_launch_oauth);
        Button clearCredentials = (Button) findViewById(R.id.btn_clear_credentials);
        final Intent fsAuth = new Intent().setClass(this, FoursharedOauthActivity.class);
        //add callback activity once auth is done
        String packageName = this.getClass().getPackage().getName();
        fsAuth.putExtra("callback", packageName+".MainActivity");

        launchOauth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(fsAuth);
                finish();
            }
        });

        clearCredentials.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearCredentials();

            }
        });
    }

    public void clearCredentials() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = prefs.edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.commit();

        Utils.clearCookies(getApplicationContext());
    }
}