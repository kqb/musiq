package com.a1337.kt.musiq

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import oauth.signpost.OAuth
import oauth.signpost.OAuthConsumer
import oauth.signpost.OAuthProvider
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import org.jetbrains.anko.alert


/**
 * Prepares a OAuthConsumer and OAuthProvider

 * OAuthConsumer is configured with the consumer key & consumer secret.
 * OAuthProvider is configured with the 3 OAuth endpoints.

 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the request.

 * After the request is authorized, a callback is made here.

 */
class FoursharedOauthActivity : Activity() {

    internal val TAG = javaClass.name

    private var consumer: OAuthConsumer? = null
    private var provider: OAuthProvider? = null

    var broadcastReceiver: BroadcastReceiver? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                alert(intent.getStringExtra(Constants.OAUTH_ERROR)
                        + "\nPlease check if your system time is set up correctly.",
                        "Error during OAUth retrieve request token") {
                    positiveButton("Return") {
                        onBackPressed()
                    }
                }.show()
            }
        }
        var ifilter = IntentFilter()
        ifilter.addAction(Constants.OAUTH_ERROR)
        registerReceiver(
                broadcastReceiver,
                ifilter
        )
        try {
            System.setProperty("debug", "true")
            this.consumer = DefaultOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET)
            this.provider = DefaultOAuthProvider(
                    Constants.REQUEST_URL,
                    Constants.ACCESS_URL,
                    Constants.AUTHORIZE_URL)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating consumer / provider", e)
        }

        Log.i(TAG, "Starting task to retrieve request token.")
        OAuthRequestTokenTask(this, consumer, provider).execute()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }


    /**
     * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
     * The callback URL will be intercepted here.
     */
    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val uri = intent.data
        if (uri != null && uri.scheme == Constants.OAUTH_CALLBACK_SCHEME) {
            Log.i(TAG, "Callback received : " + uri)
            Log.i(TAG, "Retrieving Access Token")
            RetrieveAccessTokenTask(this, consumer, provider, prefs).execute(uri)
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    /**
     * An asynchronous task that communicates with Google to
     * retrieve a request token.
     * (OAuthGetRequestToken)

     * After receiving the request token from Google,
     * show a browser to the user to authorize the Request Token.
     * (OAuthAuthorizeToken)

     */
    inner class OAuthRequestTokenTask
    /**

     * We pass the OAuth consumer and provider.

     * @param    context
     * * 			Required to be able to start the intent to launch the browser.
     * *
     * @param    provider
     * * 			The OAuthProvider object
     * *
     * @param    consumer
     * * 			The OAuthConsumer object
     */
    (private val context: Context, private val consumer: OAuthConsumer?, private val provider: OAuthProvider?) : AsyncTask<Void, Void, Void>() {

        internal val TAG = javaClass.name

        /**

         * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.

         */


        override fun doInBackground(vararg params: Void): Void? {

            try {
                Log.i(TAG, "Retrieving request token from Google servers")
                val url = provider?.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL)
                Log.i(TAG, "Popping a browser with the authorize URL : " + url)
                val webView = Intent(applicationContext, WebviewActivity::class.java)
                //				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
                webView.putExtra("url", url)
                //                Toast toast = Toast.makeText(context,  "Login to 4shared successful", Toast.LENGTH_SHORT);
                //                toast.show();
                context.startActivity(webView)

            } catch (e: Exception) {
                Log.e(TAG, "Error during OAUth retrieve request token", e)
                var newIntent = Intent()
                newIntent.action = Constants.OAUTH_ERROR
                newIntent.putExtra(Constants.OAUTH_ERROR, e.toString())
                sendBroadcast(newIntent)
//                alert(e.toString(), "Error during OAUth retrieve request token") {
//                    positiveButton("Return"){
//                    }
//                }.show()
                //				Toast toast = Toast.makeText(context,  "Error during OAUth retrieve request token", Toast.LENGTH_SHORT);
                //				toast.show();
            }

            return null
        }

    }

    inner class RetrieveAccessTokenTask(private val context: Context, private val consumer: OAuthConsumer?, private val provider: OAuthProvider?, private val prefs: SharedPreferences) : AsyncTask<Uri, Void, Void>() {

        internal val TAG = javaClass.name


        /**
         * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret
         * for future API calls.
         */
        override fun doInBackground(vararg params: Uri): Void? {
            val uri = params[0]


            val oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER)

            try {
                provider?.retrieveAccessToken(consumer, oauth_verifier)

                val edit = prefs.edit()
                edit.putString(OAuth.OAUTH_TOKEN, consumer?.token)
                edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer?.tokenSecret)
                edit.commit()

                val token = prefs.getString(OAuth.OAUTH_TOKEN, "")
                val secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "")

                consumer?.setTokenWithSecret(token, secret)

                Log.i(TAG, "OAuth - Access Token Retrieved")
                val classname = intent.getStringExtra("callback")
                if (classname != null) {
                    Log.i(TAG, "Redirect to callback activity")
                    val clazz = Class.forName(classname) as Class<Activity>
                    val i = Intent(context, clazz)
                    context.startActivity(i)
                }


            } catch (e: Exception) {
                Log.e(TAG, "OAuth - Access Token Retrieval Error", e)
                Log.i(TAG, "Redirect to LoginActivity activity")
                val i = Intent(context, LoginActivity::class.java)
                context.startActivity(i)
            }

            return null
        }
    }


}
