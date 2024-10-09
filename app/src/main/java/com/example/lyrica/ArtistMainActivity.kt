package com.example.lyrica

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ArtistMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var textNoData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.artist_activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView_artists)
        textNoData = findViewById(R.id.text_no_data)

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        artistAdapter = ArtistAdapter(listOf())
        recyclerView.adapter = artistAdapter

        // Initialize the database reference
        databaseReference = FirebaseDatabase.getInstance("https://login-register-ded34-default-rtdb.europe-west1.firebasedatabase.app/").reference

        // Fetch artist data
        fetchArtistData()
    }

    private fun fetchArtistData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid // Get the current user ID

        if (userId != null) {
            // Reference the user's data in the database
            val userRef = databaseReference.child("user").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ArtistMainActivity", "Data snapshot: ${snapshot.value}") // Log the snapshot
                    if (snapshot.exists()) {
                        val artistList = mutableListOf<Artist>()

                        // Fetch artists, descriptions, locations, and images
                        val artistSnapshot = snapshot.child("artist")
                        val descSnapshot = snapshot.child("desc")
                        val locationSnapshot = snapshot.child("location")
                        val imageSnapshot = snapshot.child("image")

                        // Combine artist, description, location, and image based on keys
                        for (artist in artistSnapshot.children) {
                            val artistName = artist.getValue(String::class.java)
                            val artistKey = artist.key?.toIntOrNull() // Convert key to Integer

                            // Find the corresponding description, location, and image
                            val artistDesc = descSnapshot.child(artistKey.toString()).getValue(String::class.java)
                            val artistLocation = locationSnapshot.child(artistKey.toString()).getValue(String::class.java)
                            val artistImage = imageSnapshot.child(artistKey.toString()).getValue(String::class.java)

                            if (artistName != null && artistDesc != null && artistLocation != null && artistImage != null) {
                                artistList.add(Artist(artistName, artistDesc, artistLocation, artistImage))
                            }
                        }

                        // Update RecyclerView with artist info list
                        if (artistList.isNotEmpty()) {
                            textNoData.visibility = View.GONE
                            artistAdapter.updateData(artistList)
                        } else {
                            textNoData.visibility = View.VISIBLE
                            Log.d("ArtistMainActivity", "No artists found")
                        }
                    } else {
                        textNoData.visibility = View.VISIBLE
                        Log.d("ArtistMainActivity", "No data exists for user")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ArtistMainActivity", "Error fetching data", error.toException())
                }
            })
        } else {
            textNoData.visibility = View.VISIBLE
            Log.d("ArtistMainActivity", "No user ID found")
        }
    }

}
