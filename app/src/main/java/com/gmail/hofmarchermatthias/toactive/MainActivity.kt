package com.gmail.hofmarchermatthias.toactive

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.gmail.hofmarchermatthias.toactive.about.AboutFragment
import com.gmail.hofmarchermatthias.toactive.edit.EditSampleFragment
import com.gmail.hofmarchermatthias.toactive.home.HomeFragment
import com.gmail.hofmarchermatthias.toactive.list.ListFragment
import com.gmail.hofmarchermatthias.toactive.map.MapFragment
import com.gmail.hofmarchermatthias.toactive.settings.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ListFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener,
    AboutFragment.OnFragmentInteractionListener, EditSampleFragment.OnFragmentInteractionListener,
    HomeFragment.OnFragmentInteractionListener,
    SettingsFragment.OnFragmentInteractionListener {

    companion object {
        const val RC_SIGN_IN = 501
        const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Use Timestamps instead of Util.date
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        //Firebase-Auth
        handleAuthentication()
    }

    private fun openSetting(): Boolean {
        Toast.makeText(this, "", Toast.LENGTH_LONG).show()
        Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_settingsFragment)
        return true
    }

    private fun handleAuthentication() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    Arrays.asList(
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                ).build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> onAuthResult(resultCode, data)
        }
    }

    private fun onAuthResult(resultCode: Int, data: Intent?) {
        //val response = IdpResponse.fromResultIntent(data)
        if (resultCode == Activity.RESULT_OK) {
            val firebaseAuth = FirebaseAuth.getInstance()
            Log.d(TAG, "Signed in")
            nav_view.getHeaderView(0).tv_nav_email.text = firebaseAuth.currentUser!!.email
            nav_view.getHeaderView(0).tv_nav_user.text = firebaseAuth.currentUser!!.displayName
            Glide.with(this).load(firebaseAuth.currentUser!!.photoUrl.toString())
                .into(nav_view.getHeaderView(0).imgv_nav_picture)
        } else {
            Log.e(TAG, "Could not log in User")
            finish()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                this.openSetting()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_homeFragment)
            }
            R.id.nav_list -> {
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_listFragment)
            }
            R.id.nav_map -> {
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_mapFragment)
            }

            R.id.nav_about -> {
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_aboutFragment)
            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.e(TAG, "Notified")
    }
}
