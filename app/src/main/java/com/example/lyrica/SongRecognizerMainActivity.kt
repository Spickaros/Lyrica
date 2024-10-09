package com.example.lyrica

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.acrcloud.rec.ACRCloudClient
import com.acrcloud.rec.ACRCloudConfig
import com.acrcloud.rec.ACRCloudResult
import com.acrcloud.rec.IACRCloudListener
import com.acrcloud.rec.utils.ACRCloudLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.BuildConfig
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject

class SongRecognizerMainActivity : AppCompatActivity(), IACRCloudListener {

    private lateinit var permissionButton: Button
    private lateinit var recognizeButton: AppCompatImageButton
    private lateinit var resultTextView: TextView
    private var mClient: ACRCloudClient? = null

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_recognizer_activity_main)

        // Initialize Firebase database reference using current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance("https://login-register-ded34-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("users/$userId/myRecognitions") // Using the user ID dynamically

        permissionButton = findViewById(R.id.permission)
        recognizeButton = findViewById(R.id.recognize)
        resultTextView = findViewById(R.id.result)

        checkPermission()
        initAcrcloud()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionButton.visibility = View.VISIBLE
            permissionButton.setOnClickListener {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
            }
        } else {
            permissionButton.visibility = View.GONE
            hasPermission()
        }
    }

    private fun hasPermission() {
        recognizeButton.setOnClickListener {
            startRecognition()
        }
    }

    private fun startRecognition() {
        mClient?.let {
            if (it.startRecognize()) {
                resultTextView.text = "Recognizing..."
            } else {
                resultTextView.text = "Init error"
            }
        } ?: run {
            resultTextView.text = "Client not ready"
        }
    }

    private fun initAcrcloud() {
        val config = ACRCloudConfig().apply {
            acrcloudListener = this@SongRecognizerMainActivity
            context = this@SongRecognizerMainActivity
            host = "identify-eu-west-1.acrcloud.com" // Replace with your host
            accessKey = "d3c7b4b47259b8583fd3b05c66f56f7e" // Replace with your access key
            accessSecret = "i4eUnLt8nECnMwEr9sSPTlyUiZArW2ACLUm39Rw6" // Replace with your access secret
            recorderConfig.rate = 8000
            recorderConfig.channels = 1
        }

        mClient = ACRCloudClient().apply {
            ACRCloudLogger.setLog(BuildConfig.DEBUG) // Enable logging for debugging
            initWithConfig(config)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            checkPermission()
        }
    }

    override fun onResult(acrResult: ACRCloudResult?) {
        acrResult?.let {
            Log.d("ACR", "ACRCloud result received: ${it.result}")
            handleResult(it.result)
        }
    }

    override fun onVolumeChanged(vol: Double) {
        Log.d("ACR", "Volume changed: $vol")
    }

    private fun handleResult(acrResult: String) {
        var res = ""
        try {
            val json = JSONObject(acrResult)
            val status: JSONObject = json.getJSONObject("status")
            val code = status.getInt("code")
            if (code == 0) {
                val metadata: JSONObject = json.getJSONObject("metadata")
                if (metadata.has("music")) {
                    val musics = metadata.getJSONArray("music")
                    val tt = musics[0] as JSONObject
                    val title = tt.getString("title")
                    val artistt = tt.getJSONArray("artists")
                    val art = artistt[0] as JSONObject
                    val artist = art.getString("name")

                    res = "$title ($artist)"

                    // Save recognized song to Firebase
                    saveRecognitionToDatabase(title, artist)
                }
            } else {
                res = acrResult
            }
        } catch (e: JSONException) {
            res = "Error parsing metadata"
            Log.e("ACR", "JSONException", e)
        }

        resultTextView.text = res
    }

    // Method to save recognized song to Firebase
    private fun saveRecognitionToDatabase(title: String, artist: String) {
        // Create a new recognition entry
        val recognition = mapOf(
            "title" to title,
            "artist" to artist,
            "timestamp" to System.currentTimeMillis()
        )

        // Push the new recognition to Firebase Realtime Database
        database.push().setValue(recognition)
            .addOnSuccessListener {
                Toast.makeText(this, "Song saved to database!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save song: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error saving song", e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mClient?.release()
        mClient = null
    }
}
