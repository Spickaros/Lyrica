package com.example.lyrica

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*

class MappingMainActivity : Activity() {
    private lateinit var map: MapView
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnCurrentLocation: ImageButton // Button for current location
    private val liveSpotsMap = mutableMapOf<GeoPoint, MutableList<String>>() // Stores live spots by location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapping_activity_main)

        // Set the user agent for OsmDroid
        Configuration.getInstance().userAgentValue = "com.example.lyrica/1.0" // Use your app's package name and version

        map = findViewById(R.id.mapview)
        val searchView = findViewById<SearchView>(R.id.searchView)
        btnCurrentLocation = findViewById(R.id.btnCurrentLocation) // Initialize the button

        // Enable zoom controls and multi-touch
        map.setMultiTouchControls(true)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://login-register-ded34-default-rtdb.europe-west1.firebasedatabase.app/").getReference("lives")

        // Initialize location client to get the device's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Load live spots from Firebase
        loadLiveSpots()

        // Set up the search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val geocoder = Geocoder(this@MappingMainActivity, Locale.getDefault())
                    try {
                        val addressList: List<Address>? = geocoder.getFromLocationName(query, 1)

                        addressList?.let { list ->
                            if (list.isNotEmpty()) {
                                val address: Address = list[0]
                                val latitude = address.latitude
                                val longitude = address.longitude

                                // Move the map to the specified location
                                val geoPoint = GeoPoint(latitude, longitude)
                                val mapController = map.controller
                                mapController.setZoom(15.0)
                                mapController.setCenter(geoPoint)

                                // Add a marker to the location
                                addMarkerWithMultipleEvents(geoPoint, query, listOf("$query (User Search)"))
                            } else {
                                Toast.makeText(this@MappingMainActivity, "Location not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@MappingMainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        // Set up click listener for the button to return to current location
        btnCurrentLocation.setOnClickListener {
            getCurrentLocation() // Move map to the current location when the button is clicked
        }

        // Get the user's current location and move the map to that position
        getCurrentLocation()
    }

    private fun loadLiveSpots() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear previous markers and live spots map
                map.overlays.clear()
                liveSpotsMap.clear()

                for (liveSnapshot in snapshot.children) {
                    val artist = liveSnapshot.child("artist").value as? String
                    val spot = liveSnapshot.child("spot").value as? String
                    val date = liveSnapshot.child("date").value as? String // Add date field

                    if (artist != null && spot != null) {
                        geocodeSpot(spot, artist, date ?: "Unknown Date")
                    }
                }

                // Refresh the map
                map.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MappingMainActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun geocodeSpot(spot: String?, artist: String?, date: String) {
        spot?.let {
            val geocoder = Geocoder(this, Locale.getDefault())
            Thread {
                try {
                    val addresses: List<Address>? = geocoder.getFromLocationName(spot, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val geoPoint = GeoPoint(address.latitude, address.longitude)

                        // Add the event to the list of events for this location
                        val eventDetails = "$artist on $date"
                        liveSpotsMap.getOrPut(geoPoint) { mutableListOf() }.add(eventDetails)

                        // Update the UI on the main thread
                        runOnUiThread {
                            addMarkerWithMultipleEvents(geoPoint, spot, liveSpotsMap[geoPoint]!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MappingMainActivity", "Geocoding error for $spot: ${e.message}")
                }
            }.start()
        }
    }

    private fun addMarkerWithMultipleEvents(geoPoint: GeoPoint, spotName: String, events: List<String>) {
        val marker = Marker(map)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Set the title to the spot's name
        marker.title = spotName

        // Combine event details into a single snippet with "|" separator
        marker.snippet = events.joinToString(separator = " | ") { it }

        map.overlays.add(marker)
        map.invalidate()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        // Get the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                val mapController = map.controller
                mapController.setZoom(15.0)
                mapController.setCenter(currentGeoPoint)

                // Optionally, add a marker for the current location
                addMarkerWithMultipleEvents(currentGeoPoint, "Current Location", listOf("You are here"))
            } else {
                Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
