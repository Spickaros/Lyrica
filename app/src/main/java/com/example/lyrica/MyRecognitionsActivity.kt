package com.example.lyrica

import SongRecognition
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyRecognitionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songRecognitionAdapter: SongRecognitionAdapter
    private val songList = mutableListOf<SongRecognition>() // This will hold the song names
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recognition_activity)

        // Initialize Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Get the current user ID
        if (userId == null) {
            // Handle case when user is not logged in
            // You might want to redirect them to the login screen or show a message
            return
        }

        // Initialize Firebase Database reference using the current user ID
        databaseReference =
            FirebaseDatabase.getInstance("https://login-register-ded34-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users/$userId/myRecognitions")

        recyclerView = findViewById(R.id.recyclerView)
        songRecognitionAdapter = SongRecognitionAdapter(songList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = songRecognitionAdapter

        // Load songs from Firebase
        loadSavedSongs()
    }

    private fun loadSavedSongs() {
        // Attach a listener to read the data at our posts reference
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songList.clear() // Clear any existing data
                for (snapshot in dataSnapshot.children) {
                    // Use the new structure to get the song data
                    val songMap = snapshot.getValue(SongRecognition::class.java)
                    songMap?.let { songList.add(it) }
                }
                songRecognitionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
                Log.e("MyRecognitionsActivity", "Database error: ${error.message}")
            }
        })
    }

}
