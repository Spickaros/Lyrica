package com.example.lyrica

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import com.google.firebase.auth.FirebaseAuth

class MainPageActivity : AppCompatActivity() {

    // Animations
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_buttom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_buttom_anim) }

    private var clicked = false
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // Find views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Inflate the custom navigation header layout
        navigationView.getHeaderView(0).findViewById<ImageButton>(R.id.logout_button).setOnClickListener {
            // Handle logout
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Find other views
        val addBtn = findViewById<FloatingActionButton>(R.id.add_btn)
        val openVocalRemoverButton = findViewById<FloatingActionButton>(R.id.openVocalRemoverButton)
        val openSongRecognizerButton = findViewById<FloatingActionButton>(R.id.openSongRecognizer_btn)
        val openVoiceToTextButton = findViewById<FloatingActionButton>(R.id.openVoiceToText_btn)

        // Add button click listener to toggle visibility and play animations
        addBtn.setOnClickListener {
            onAddButtonClicked(addBtn, openVocalRemoverButton, openSongRecognizerButton, openVoiceToTextButton)
        }

        // Listeners for the remaining buttons
        openVocalRemoverButton.setOnClickListener {
            val intent = Intent(this, VocalRemoverMainActivity::class.java)
            startActivity(intent)
        }

        openSongRecognizerButton.setOnClickListener {
            val intent = Intent(this, SongRecognizerMainActivity::class.java)
            startActivity(intent)
        }

        openVoiceToTextButton.setOnClickListener {
            val intent = Intent(this, VoiceToTextMainActivity::class.java)
            startActivity(intent)
        }

        // Set up navigation drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItemSelected(menuItem)
            true
        }

        // Find and set up the navigation button
        val navButton = findViewById<ImageButton>(R.id.nav_button)
        navButton.setOnClickListener {
            // Open or close the navigation drawer
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    // Handle the navigation item selection
    private fun handleNavigationItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_my_recognitions -> {
                val intent = Intent(this, MyRecognitionsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_my_lyrics -> {
                // Navigate to MyLyricsActivity
                val intent = Intent(this, MyLyricsActivity::class.java)
                startActivity(intent)
            }
            // New: Handle navigation for Assistant, Artist, and Mapping
            R.id.nav_assistant -> {
                val intent = Intent(this, AssistantMainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_artist_info -> {
                val intent = Intent(this, ArtistMainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_mapping -> {
                val intent = Intent(this, MappingMainActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawers() // Close the drawer after selection
    }

    // Handle the add button click to toggle visibility of the other buttons
    private fun onAddButtonClicked(
        addBtn: FloatingActionButton,
        openVocalRemoverButton: FloatingActionButton,
        openSongRecognizerButton: FloatingActionButton,
        openVoiceToTextButton: FloatingActionButton
    ) {
        setVisibility(clicked, openVocalRemoverButton, openSongRecognizerButton, openVoiceToTextButton)
        setAnimation(clicked, addBtn, openVocalRemoverButton, openSongRecognizerButton, openVoiceToTextButton)
        setClickable(clicked, openVocalRemoverButton, openSongRecognizerButton, openVoiceToTextButton)
        clicked = !clicked
    }

    // Toggle visibility of buttons
    private fun setVisibility(
        clicked: Boolean,
        openVocalRemoverButton: FloatingActionButton,
        openSongRecognizerButton: FloatingActionButton,
        openVoiceToTextButton: FloatingActionButton
    ) {
        if (!clicked) {
            openVocalRemoverButton.visibility = View.VISIBLE
            openSongRecognizerButton.visibility = View.VISIBLE
            openVoiceToTextButton.visibility = View.VISIBLE
        } else {
            openVocalRemoverButton.visibility = View.INVISIBLE
            openSongRecognizerButton.visibility = View.INVISIBLE
            openVoiceToTextButton.visibility = View.INVISIBLE
        }
    }

    // Set the animations for the buttons
    private fun setAnimation(
        clicked: Boolean,
        addBtn: FloatingActionButton,
        openVocalRemoverButton: FloatingActionButton,
        openSongRecognizerButton: FloatingActionButton,
        openVoiceToTextButton: FloatingActionButton
    ) {
        if (!clicked) {
            openVocalRemoverButton.startAnimation(fromBottom)
            openSongRecognizerButton.startAnimation(fromBottom)
            openVoiceToTextButton.startAnimation(fromBottom)
            addBtn.startAnimation(rotateOpen)
        } else {
            openVocalRemoverButton.startAnimation(toBottom)
            openSongRecognizerButton.startAnimation(toBottom)
            openVoiceToTextButton.startAnimation(toBottom)
            addBtn.startAnimation(rotateClose)
        }
    }

    // Make buttons clickable or not based on the toggle state
    private fun setClickable(
        clicked: Boolean,
        openVocalRemoverButton: FloatingActionButton,
        openSongRecognizerButton: FloatingActionButton,
        openVoiceToTextButton: FloatingActionButton
    ) {
        openVocalRemoverButton.isClickable = !clicked
        openSongRecognizerButton.isClickable = !clicked
        openVoiceToTextButton.isClickable = !clicked
    }
}
