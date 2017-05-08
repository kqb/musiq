package com.a1337.kt.musiq

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import org.apache.http.client.utils.URIBuilder
import org.jetbrains.anko.support.v4.toast
import org.json.JSONException
import org.json.JSONObject

class SearchFragment : Fragment() {

    //    private MyHandler mHandler;

    private var queryResult: String? = null
    private var resultReceiver: ResultReceiver? = null
    private var progressBar: ProgressBar? = null

    //Fragment
    private var parentActivity: FragmentActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            queryResult = arguments.getString(Constants.QUERY_RESULT)
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        parentActivity = super.getActivity()
        val view = inflater!!.inflate(R.layout.activity_search, container, false)
        progressBar = view.findViewById(R.id.progressBarSearch) as ProgressBar

        //        txt = (TextView)findViewById(R.id.textView);

        resultReceiver = ResultReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ACTION_HANDLE_RESULTS)
        activity.registerReceiver(resultReceiver, intentFilter)
        //        Intent intent = getIntent();
        //
        //        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        //            String query = intent.getStringExtra(SearchManager.QUERY);

        performSearch(queryResult)
        progressBar?.visibility = View.VISIBLE
        //            mHandler = new MyHandler(this);
        //            mHandler.startQuery(0, null, intent.getData(), null, null, null, null);

        //        }
        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        activity.unregisterReceiver(resultReceiver)
    }


    private fun performSearch(query: String?) {

        // The connection URL
        val builder = URIBuilder()
        val url = Utils.generate4sharedQueryUrl(query)
        val fsApiCall = Intent(activity, FoursharedApiService::class.java)
        fsApiCall.putExtra(Constants.REQUEST_URL_ALIAS, url)
        fsApiCall.putExtra(Constants.REQUEST_ACTION_ALIAS, Constants.ACTION_HANDLE_RESULTS)
        activity.startService(fsApiCall)
        //        spinner.setVisibility(View.VISIBLE);
    }


    private inner class ResultReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            toast("Received intent broadcast: ${Constants.QUERY_RESULT}")
            progressBar?.visibility = View.INVISIBLE
            val results = intent.extras.getString(Constants.QUERY_RESULT)
            val frag = SearchResultFragment.newInstance(results)
            val fm = activity.supportFragmentManager

            Utils.deleteAndAddFragment(R.id.searchLayout, fm, frag)

            var jsonResponse: JSONObject? = null
            try {
                jsonResponse = JSONObject(results)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (jsonResponse != null) {
                println(jsonResponse)
            }
            //                spinner.setVisibility(View.GONE);
        }
    }

    companion object {

        fun newInstance(queryResult: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString(Constants.QUERY_RESULT, queryResult)
            fragment.arguments = args
            return fragment
        }
    }

    //    static class MyHandler extends AsyncQueryHandler {
    //        // avoid memory leak
    //        WeakReference<SearchFragment> activity;
    //
    //        public MyHandler(SearchFragment searchableActivity) {
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
