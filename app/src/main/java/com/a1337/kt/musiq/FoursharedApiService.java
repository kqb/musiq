package com.a1337.kt.musiq;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

public class FoursharedApiService extends IntentService {
    private static final int PICK_CONTACT = 0;
    final String TAG = getClass().getName();
    private SharedPreferences prefs;
    private Intent intent;
    private String requestedAction;


    public FoursharedApiService() {
        super("FoursharedApiService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String request = intent.getStringExtra(Constants.REQUEST_URL_ALIAS);
        requestedAction = intent.getStringExtra(Constants.REQUEST_ACTION_ALIAS);
        if(request == "" ||request == null){
            Log.e(TAG, "Request cannot be empty!");
        } else {
            if (Constants.ACTION_HANDLE_MEDIA.equals(requestedAction))
                handleMedia(request);
            else
                performApiCall(request);
        }

//        performApiCall(Constants.API_REQUEST);
    }

    public void handleMedia(String request) {

    }
    public void performApiCall(String request) {
//        TextView textView = (TextView) findViewById(R.id.response_code);
        String jsonOutput = "";
        //if token is empty initiate auth flow
        if(this.prefs.getString(OAuth.OAUTH_TOKEN,"") == ""){
            startActivity(new Intent(this, FoursharedOauthActivity.class));
        }

        //start process
        try{
            jsonOutput = doGet(request, getConsumer(this.prefs));
        } catch (FoursharedGetException e) {
            Toast.makeText(FoursharedApiService.this, "Get request to 4Shared resulted in error", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Get request to 4Shared resulted in error");
            e.printStackTrace();

        }
        // If token has timeout, need to try re-authorize and then delegate api call to the end of the auth flow
        // TODO: Counter for number of retries
        catch (FoursharedTokenTimeoutException e){
            Toast.makeText(FoursharedApiService.this, "4shared access token expired, trying to obtain new one.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "4shared access token expired, trying to obtain new one.");
            Intent reqTokenFlow = new Intent().setClass(this, FoursharedOauthActivity.class);

            // delegate request
            reqTokenFlow.putExtra("request", request);
            startActivity(reqTokenFlow);

        }
        try {
            broadcastHelper(requestedAction, jsonOutput);
        } catch (Exception e) {
            Log.e(TAG, "Error executing request",e);
//            textView.setText("Error retrieving contacts : " + jsonOutput);
        }
    }

    private void broadcastHelper(String requestedAction, String jsonOutput) {
        if (requestedAction != null) {
            Intent newIntent = new Intent();
            newIntent.putExtra(Constants.QUERY_RESULT, jsonOutput);
            newIntent.setAction(Constants.ACTION_HANDLE_RESULTS);
            LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
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



    private OAuthConsumer getConsumer(SharedPreferences prefs) {
        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, secret);
        return consumer;
    }

    private String doGet(String url,OAuthConsumer consumer) throws FoursharedGetException, FoursharedTokenTimeoutException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        Log.i(TAG,"Requesting URL : " + url);
        try {
            consumer.sign(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpResponse response = null;

        try {
            response = httpclient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }


        int statusCode = response.getStatusLine().getStatusCode();
        Log.i(TAG,"Statusline : " + response.getStatusLine());

        //If status is OK get reponse body and return as string
        if(statusCode==200){
            StringBuilder responseBuilder = new StringBuilder();
            InputStream data = null;
            try {
                data = response.getEntity().getContent();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
                String responeLine;

                while ((responeLine = bufferedReader.readLine()) != null) {
                    responseBuilder.append(responeLine);
                }
                Log.i(TAG, "Response : " + responseBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseBuilder.toString();
        }

        // MSG: Timestamp refused. Difference between server and client timestamp must be no more than [value].
        // timestamp is outdated, attept to do OAuth flow again
        if(statusCode==401.0319){
            throw new FoursharedTokenTimeoutException();
        }
        // All other failures
        else{
            throw new FoursharedGetException(response.getStatusLine().toString());
        }
    }


    // Custom exceptions
    public class FoursharedGetException extends Exception{
        FoursharedGetException(String msg){
            super(msg);
        }
    }

    public class FoursharedTokenTimeoutException extends Exception{
        FoursharedTokenTimeoutException(){
        }
    }

}


