package com.a1337.kt.musiq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

/**
 * Prepares a OAuthConsumer and OAuthProvider 
 * 
 * OAuthConsumer is configured with the consumer key & consumer secret.
 * OAuthProvider is configured with the 3 OAuth endpoints.
 * 
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the request.
 * 
 * After the request is authorized, a callback is made here.
 * 
 */
public class FoursharedOauthActivity extends Activity {

	final String TAG = getClass().getName();
	
    private OAuthConsumer consumer; 
    private OAuthProvider provider;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	try {
        	System.setProperty("debug", "true");
    	      this.consumer = new DefaultOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
    	        this.provider = new DefaultOAuthProvider(
    	        		Constants.REQUEST_URL,
    	        		Constants.ACCESS_URL,
    	        		Constants.AUTHORIZE_URL);
        	} catch (Exception e) {
        		Log.e(TAG, "Error creating consumer / provider",e);
    		}

        Log.i(TAG, "Starting task to retrieve request token.");
		new OAuthRequestTokenTask(this,consumer,provider).execute();
	}

	/**
	 * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
	 * The callback URL will be intercepted here.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
			Log.i(TAG, "Callback received : " + uri);
			Log.i(TAG, "Retrieving Access Token");
			new RetrieveAccessTokenTask(this,consumer,provider,prefs).execute(uri);
			finish();	
		}
	}

	/**
	 * An asynchronous task that communicates with Google to
	 * retrieve a request token.
	 * (OAuthGetRequestToken)
	 *
	 * After receiving the request token from Google,
	 * show a browser to the user to authorize the Request Token.
	 * (OAuthAuthorizeToken)
	 *
	 */
	public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {

		final String TAG = getClass().getName();
		private Context context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;

		/**
		 *
		 * We pass the OAuth consumer and provider.
		 *
		 * @param 	context
		 * 			Required to be able to start the intent to launch the browser.
		 * @param 	provider
		 * 			The OAuthProvider object
		 * @param 	consumer
		 * 			The OAuthConsumer object
		 */
		public OAuthRequestTokenTask(Context context,OAuthConsumer consumer,OAuthProvider provider) {
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
		}

		/**
		 *
		 * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
		 *
		 */
		@Override
		protected Void doInBackground(Void... params) {

			try {
				Log.i(TAG, "Retrieving request token from Google servers");
				final String url = provider.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL);
				Log.i(TAG, "Popping a browser with the authorize URL : " + url);
                Intent webView = new Intent(getApplicationContext(), WebviewActivity.class);
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
                webView.putExtra("url", url);
//                Toast toast = Toast.makeText(context,  "Login to 4shared successful", Toast.LENGTH_SHORT);
//                toast.show();
				context.startActivity(webView);

			} catch (Exception e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
//				Toast toast = Toast.makeText(context,  "Error during OAUth retrieve request token", Toast.LENGTH_SHORT);
//				toast.show();
			}

			return null;
		}

	}

	public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

		final String TAG = getClass().getName();

		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private SharedPreferences prefs;

		public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) {
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.prefs=prefs;
		}


		/**
		 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret
		 * for future API calls.
		 */
		@Override
		protected Void doInBackground(Uri...params) {
			final Uri uri = params[0];


			final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

			try {
				provider.retrieveAccessToken(consumer, oauth_verifier);

				final SharedPreferences.Editor edit = prefs.edit();
				edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
				edit.commit();

				String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
				String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

				consumer.setTokenWithSecret(token, secret);

                Log.i(TAG, "OAuth - Access Token Retrieved");
                String classname = getIntent().getStringExtra("callback");
                if(classname!=null) {
                    Log.i(TAG, "Redirect to callback activity");
                    Class<Activity> clazz = (Class<Activity>)Class.forName(classname);
                    Intent i = new Intent(context, clazz);
                    context.startActivity(i);
                }


			} catch (Exception e) {
				Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
                Log.i(TAG, "Redirect to LoginActivity activity");
                Intent i = new Intent(context, LoginActivity.class);
                context.startActivity(i);
			}

			return null;
		}
	}



}
