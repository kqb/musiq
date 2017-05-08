package com.a1337.kt.musiq

import android.app.DownloadManager
import android.app.IntentService
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import oauth.signpost.OAuth
import oauth.signpost.OAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.downloadManager
import org.jetbrains.anko.info
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class FoursharedApiService : IntentService("FoursharedApiService"), AnkoLogger {
    internal val TAG = javaClass.name
    private var prefs: SharedPreferences? = null
    private var intent: Intent? = null
    private var requestedAction: String? = null


    override fun onHandleIntent(intent: Intent?) {
        this.intent = intent
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val request = intent?.getStringExtra(Constants.REQUEST_URL_ALIAS)
        requestedAction = intent?.getStringExtra(Constants.REQUEST_ACTION_ALIAS)

        if (request === "" || request == null) {
            Log.e(TAG, "Request cannot be empty!")
        } else {
            if (Constants.ACTION_HANDLE_MEDIA == requestedAction) {
                var fileName = intent?.getStringExtra(Constants.FILE_NAME)

                handleMedia(request, fileName)
            } else {
                performApiCall(request)
            }
        }

        //        performApiCall(Constants.API_REQUEST);
    }

    fun handleMedia(request: String, fileName: String) {
//        performApiCall(request)
        info { "handleMedia" }
        var signedReq = getConsumer(this.prefs).sign(HttpGet(request))
        var allHeaders: MutableMap<String, String> = signedReq.getAllHeaders()
        var rawStr = allHeaders.get("Authorization")
        var queryStr = rawStr?.replace("OAuth ", "")?.replace("\"", "")?.replace(", ", "&")

//        info{signedReq.getHeader("oauth_consumer_key")}
//        info{signedReq.getHeader("oauth_nonce")}
//        info{signedReq.getHeader("oauth_signature")}
//        info{signedReq.getHeader("oauth_signature_method")}


        var uri = Uri.parse(request + "?" + queryStr)

        info { uri.toString() }

        var dmReq = DownloadManager.Request(uri)
        dmReq.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${fileName}.mp3");

        dmReq.addRequestHeader("Authorization", "OAuth")
        dmReq.addRequestHeader("oauth_version", "1.0")
        dmReq.addRequestHeader("Content-Type", "audio/mpeg")
        dmReq.setMimeType("audio/mpeg")


        downloadManager.enqueue(dmReq)
    }

    fun performApiCall(request: String) {
        //        TextView textView = (TextView) findViewById(R.id.response_code);
        var jsonOutput = ""
        //if token is empty initiate auth flow
        if (this.prefs!!.getString(OAuth.OAUTH_TOKEN, "") === "") {
            startActivity(Intent(this, FoursharedOauthActivity::class.java))
        }

        //start process
        try {
            jsonOutput = doGet(request, getConsumer(this.prefs))
        } catch (e: FoursharedGetException) {
            Toast.makeText(this@FoursharedApiService, "Get request to 4Shared resulted in error", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Get request to 4Shared resulted in error")
            e.printStackTrace()

        } catch (e: FoursharedTokenTimeoutException) {
            Toast.makeText(this@FoursharedApiService, "4shared access token expired, trying to obtain new one.", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "4shared access token expired, trying to obtain new one.")
            val reqTokenFlow = Intent().setClass(this, FoursharedOauthActivity::class.java)

            // delegate request
            reqTokenFlow.putExtra("request", request)
            startActivity(reqTokenFlow)

        }
        // If token has timeout, need to try re-authorize and then delegate api call to the end of the auth flow
        // TODO: Counter for number of retries
        try {
            broadcastHelper(requestedAction, jsonOutput)
        } catch (e: Exception) {
            Log.e(TAG, "Error executing request", e)
            //            textView.setText("Error retrieving contacts : " + jsonOutput);
        }

    }

    private fun broadcastHelper(requestedAction: String?, jsonOutput: String) {
        if (requestedAction != null) {
            val newIntent = Intent()
            newIntent.putExtra(Constants.QUERY_RESULT, jsonOutput)
            newIntent.action = Constants.ACTION_HANDLE_RESULTS
            info("Sending broadcast after search response..")
            sendBroadcast(newIntent)
        }
    }

    //    public void onActivityResult(int reqCode, int resultCode, Intent data) {
    //        super.onActivityResult(reqCode, resultCode, data);
    //        Log.e(TAG,"onActivityResult");
    //
    //        switch (reqCode) {
    //            case (PICK_CONTACT) :
    //                if (resultCode == Activity.RESULT_OK) {
    //                    Uri contactData = data.getData();
    //                    Cursor c =  managedQuery(contactData, null, null, null, null);
    //                    if (c.moveToFirst()) {
    //                        String name = c.getString(c.getColumnIndexOrThrow(Contacts.People.NAME));
    //                        Log.i(TAG,"Response : " + "Selected contact : " + name);
    //                    }
    //                }
    //                break;
    //        }
    //    }


    private fun getConsumer(prefs: SharedPreferences?): OAuthConsumer {
        val token = prefs?.getString(OAuth.OAUTH_TOKEN, "")
        val secret = prefs?.getString(OAuth.OAUTH_TOKEN_SECRET, "")
        val consumer = CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET)
        consumer.setTokenWithSecret(token, secret)
        return consumer
    }

    @Throws(FoursharedGetException::class, FoursharedTokenTimeoutException::class)
    private fun doGet(url: String, consumer: OAuthConsumer): String {
        val httpclient = DefaultHttpClient()
        val request = HttpGet(url)

        Log.i(TAG, "Requesting URL : " + url)
        try {
            consumer.sign(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var response: HttpResponse? = null

        try {
            httpclient
            response = httpclient.execute(request)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        val statusCode = response!!.statusLine.statusCode
        Log.i(TAG, "Statusline : " + response.statusLine)

        //If status is OK get reponse body and return as string
        if (statusCode == 200) {
            val responseBuilder = StringBuilder()
            var data: InputStream? = null
            try {
                data = response.entity.content

                val bufferedReader = BufferedReader(InputStreamReader(data!!))

                bufferedReader.lineSequence().forEach {
                    x ->
                    responseBuilder.append(x)
                }
                Log.i(TAG, "Response : " + responseBuilder.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return responseBuilder.toString()
        } else {
//            toast(statusCode)
//
            val loginIntent = Intent(baseContext, LoginActivity::class.java)
            loginIntent.setAction(Constants.ACTION_RELOGIN)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.putExtra(Constants.ACTION_RELOGIN, response.statusLine.toString())
            application.startActivity(loginIntent)

            // MSG: Timestamp refused. Difference between server and client timestamp must be no more than [value].
            // timestamp is outdated, attept to do OAuth flow again
//            if (statusCode.toDouble() == 401.0319) {
//                throw FoursharedTokenTimeoutException()
//            } else {
//
//                throw FoursharedGetException(response.statusLine.toString())
//            }// All other failures
            return ""
        }
    }


    // Custom exceptions
    inner class FoursharedGetException internal constructor(msg: String) : Exception(msg)

    inner class FoursharedTokenTimeoutException internal constructor() : Exception()

    companion object {
        private val PICK_CONTACT = 0
    }

}


