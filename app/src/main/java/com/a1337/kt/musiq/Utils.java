package com.a1337.kt.musiq;


import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.net.URLEncoder;

/**
 * Created by kt on 8/23/2016.
 */
public class Utils {
    Utils() {

    }

    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("clearCookies", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("clearCookies", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static String generate4sharedQueryUrl(String query, String... extras) {
        String limit = "30";
        String sort = "downloads,desc";
        if (extras.length > 0) {
            limit = extras[0];
            if (extras.length > 1) {
                sort = extras[1];
            }
        }
        return Constants.API_REQUEST_FILES
                + "?query="
                + URLEncoder.encode(query)
                + "&category=1"
//                + "&type=mp3"
                + "&sort=" + sort
                + "&limit=" + limit
                + "&addFields=id3";
    }

    public static String generate4sharedDlUrl(String id) {
        return Constants.API_REQUEST_FILES
                + "/"
                + id
                + "/download";
    }

    public static void deleteAndAddFragment(FragmentManager fm, Fragment frag, String tag) {
        // Remove old fragment
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null)
            fm.beginTransaction().remove(fragment).commit();

        // Add new fragment
        fm.beginTransaction()
                .add(R.id.searchLayout, frag, tag)
                .commit();


    }
}
