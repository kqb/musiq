package com.a1337.kt.musiq;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.a1337.kt.musiq.models.File;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity implements SearchResultFragment.OnListFragmentInteractionListener {

//    private MyHandler mHandler;

    final String TAG = getClass().getName();
    //    ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar);
    private ResultReceiver resultReceiver;
    private LocalBroadcastManager bManager;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBarSearch);

//        txt = (TextView)findViewById(R.id.textView);
        bManager = LocalBroadcastManager.getInstance(this);

        resultReceiver = new ResultReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_HANDLE_RESULTS);
        bManager.registerReceiver(resultReceiver, intentFilter);
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            performSearch(query);
            progressBar.setVisibility(View.VISIBLE);
//            mHandler = new MyHandler(this);
//            mHandler.startQuery(0, null, intent.getData(), null, null, null, null);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bManager.unregisterReceiver(resultReceiver);
    }


    private void performSearch(String query) {

        // The connection URL
        URIBuilder builder = new URIBuilder();
        String url = Utils.generate4sharedQueryUrl(query);
        Intent fsApiCall = new Intent(this, FoursharedApiService.class);
        fsApiCall.putExtra(Constants.REQUEST_URL_ALIAS, url);
        fsApiCall.putExtra(Constants.REQUEST_ACTION_ALIAS, Constants.ACTION_HANDLE_RESULTS);
        startService(fsApiCall);
//        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListFragmentInteraction(File item) {
        String url = Utils.generate4sharedDlUrl(item.getId());
        Intent fsApiCall = new Intent(this, FoursharedApiService.class);
        fsApiCall.putExtra(Constants.REQUEST_URL_ALIAS, url);
        fsApiCall.putExtra(Constants.REQUEST_ACTION_ALIAS, Constants.ACTION_HANDLE_MEDIA);
        startService(fsApiCall);

    }

    private class ResultReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.INVISIBLE);

            String results = intent.getExtras().getString(Constants.QUERY_RESULT);
            SearchResultFragment frag = SearchResultFragment.newInstance(results);
            FragmentManager fm = getSupportFragmentManager();

            Utils.deleteAndAddFragment(fm, frag, TAG);

            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonResponse != null) {
                System.out.println(jsonResponse);
            }
//                spinner.setVisibility(View.GONE);
        }
    }

//    private class DownloadReceiver extends BroadcastReceiver {
//
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
////            progressBar.setVisibility(View.INVISIBLE);
//
//            String results = intent.getExtras().getString(Constants.QUERY_RESULT);
//            SearchResultFragment frag = SearchResultFragment.newInstance(results);
//            FragmentManager fm = getSupportFragmentManager();
//
//            Utils.deleteAndAddFragment(fm, frag, TAG);
//
//            JSONObject jsonResponse = null;
//            try {
//                jsonResponse = new JSONObject(results);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (jsonResponse != null) {
//                System.out.println(jsonResponse);
//            }
////                spinner.setVisibility(View.GONE);
//        }
//    }


//    static class MyHandler extends AsyncQueryHandler {
//        // avoid memory leak
//        WeakReference<SearchActivity> activity;
//
//        public MyHandler(SearchActivity searchableActivity) {
//            super(searchableActivity.getContentResolver());
//            activity = new WeakReference<>(searchableActivity);
//        }
//
//        @Override
//        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//            super.onQueryComplete(token, cookie, cursor);
//            if (cursor == null || cursor.getCount() == 0) return;
//
//            cursor.moveToFirst();
//
//            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
//            String text = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
//            long dataId =  cursor.getLong(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID));
//
//            cursor.close();
//        }
//    };
}
