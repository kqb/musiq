package com.a1337.kt.musiq


import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.webkit.CookieManager
import android.webkit.CookieSyncManager

import java.net.URLEncoder

/**
 * Created by kt on 8/23/2016.
 */
class Utils internal constructor() {
    companion object {

        fun clearCookies(context: Context) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Log.d("clearCookies", "Using clearCookies code for API >=" + Build.VERSION_CODES.LOLLIPOP_MR1.toString())
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
            } else {
                Log.d("clearCookies", "Using clearCookies code for API <" + Build.VERSION_CODES.LOLLIPOP_MR1.toString())
                val cookieSyncMngr = CookieSyncManager.createInstance(context)
                cookieSyncMngr.startSync()
                val cookieManager = CookieManager.getInstance()
                cookieManager.removeAllCookie()
                cookieManager.removeSessionCookie()
                cookieSyncMngr.stopSync()
                cookieSyncMngr.sync()
            }
        }

        fun generate4sharedQueryUrl(query: String?, vararg extras: String): String {
            var limit = "30"
            var sort = "downloads,desc"
            if (extras.size > 0) {
                limit = extras[0]
                if (extras.size > 1) {
                    sort = extras[1]
                }
            }
            return (Constants.API_REQUEST_FILES
                    + "?query="
                    + URLEncoder.encode(query)
                    + "&category=1"
                    //                + "&type=mp3"
                    + "&sort=" + sort
                    + "&limit=" + limit
                    + "&addFields=id3")
        }

        fun generate4sharedDlUrl(id: String): String {
            return (Constants.API_DOWNLOAD_FILES
                    + "/"
                    + id
                    + "/preview")
        }

        fun deleteAndAddFragment(layoutToAddReference: Int, fm: FragmentManager, frag: Fragment) {
            // Remove old fragment
            val tag = frag::class.java.name
            val fragment = fm.findFragmentByTag(tag)
            if (fragment != null)
                fm.beginTransaction().remove(fragment).commit()

            // Add new fragment
            fm.beginTransaction()
                    .add(layoutToAddReference, frag, tag)
                    .commit()
        }
    }
}
