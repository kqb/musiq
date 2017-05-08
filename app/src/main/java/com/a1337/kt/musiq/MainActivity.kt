package com.a1337.kt.musiq

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.a1337.kt.musiq.models.File
import oauth.signpost.OAuth
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchResultFragment.OnListFragmentInteractionListener {

    private val TAG = this.javaClass.name
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
        }
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Just displaying token for debug
        val token = prefs!!.getString(OAuth.OAUTH_TOKEN, "")
        val secret = prefs!!.getString(OAuth.OAUTH_TOKEN_SECRET, "")
        Log.i(TAG, "OAUTH_token: $token  OAUTH_token_secret: $secret")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Toast.makeText(this, "Searching by: " + query, Toast.LENGTH_SHORT).show()

        } else if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.dataString
            Toast.makeText(this, "Suggestion: " + uri, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search)

        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                var frag = SearchFragment.newInstance(query)
                fragmentManager
                Utils.deleteAndAddFragment(R.id.mainFrameContents, supportFragmentManager, frag)
                return true;
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                toast("onQueryTextChange")
                return true;
            }
        })


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        //        searchView.setSearchableInfo(searchManager.getSearchableInfo(
        //                new ComponentName(this, SearchFragment.class)));
        searchView.setIconifiedByDefault(false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            Utils.clearCookies(applicationContext)
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onListFragmentInteraction(item: File) {
        toast(item.id)
        val url = Utils.generate4sharedDlUrl(item.id)
        val fsApiCall = Intent(this, FoursharedApiService::class.java)
        fsApiCall.putExtra(Constants.REQUEST_URL_ALIAS, url)
        fsApiCall.putExtra(Constants.FILE_NAME, item.name)
        fsApiCall.putExtra(Constants.REQUEST_ACTION_ALIAS, Constants.ACTION_HANDLE_MEDIA)
        startService(fsApiCall)

    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.

     * @param query the query text that is to be submitted
     * *
     * @return true if the query has been handled by the listener, false to let the
     * * SearchView perform the default action.
     */
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    /**
     * Called when the query text is changed by the user.

     * @param newText the new content of the query text field.
     * *
     * @return false if the SearchView should perform the default action of showing any
     * * suggestions if available, true if the action was handled by the listener.
     */
    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

}
