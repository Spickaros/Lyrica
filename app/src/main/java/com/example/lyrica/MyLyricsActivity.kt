package com.example.lyrica

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyLyricsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var lyricsAdapter: LyricsAdapter
    private val lyricsList = mutableListOf<Lyrics>() // List to hold the saved lyrics
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_lyrics_activity)

        // Initialize Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            // Handle case when user is not logged in
            return
        }

        // Initialize Firebase Database reference to fetch lyrics
        databaseReference = FirebaseDatabase.getInstance("https://login-register-ded34-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("users/$userId/myLyrics")

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewLyrics)
        lyricsAdapter = LyricsAdapter(lyricsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = lyricsAdapter

        // Load saved lyrics from Firebase
        loadSavedLyrics()
    }

    private fun loadSavedLyrics() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lyricsList.clear() // Clear existing data to avoid duplication
                for (snapshot in dataSnapshot.children) {
                    val lyrics = snapshot.getValue(Lyrics::class.java)
                    lyrics?.let { lyricsList.add(it) }
                }
                lyricsAdapter.notifyDataSetChanged() // Notify the adapter to refresh the data
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
