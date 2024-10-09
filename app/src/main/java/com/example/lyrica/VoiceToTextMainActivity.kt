package com.example.lyrica

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class VoiceToTextMainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var editText: EditText? = null
    private var micBtn: ImageView? = null

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voice_to__text_activity_main)

        // Initialize Firebase Authentication and get current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Initialize Firebase Database reference for storing lyrics
            database = FirebaseDatabase.getInstance("https://login-register-ded34-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users/$userId/myLyrics")
        }

        // Check for audio recording permissions
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermissions()
        }

        editText = findViewById(R.id.texts)
        micBtn = findViewById(R.id.buttons)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {
                editText!!.setText("")
                editText!!.setHint("Listening...")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                Toast.makeText(this@VoiceToTextMainActivity, "Speech recognition error: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(bundle: Bundle?) {
                micBtn!!.setImageResource(R.drawable.mic_off_icon)
                val data = bundle!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val lyrics = data!![0]
                editText!!.setText(lyrics)

                // Save recognized lyrics to Firebase
                saveLyricsToDatabase(lyrics)
            }

            override fun onPartialResults(p0: Bundle?) {}

            override fun onEvent(p0: Int, p1: Bundle?) {}
        })

        micBtn!!.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer!!.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                micBtn!!.setImageResource(R.drawable.microphone_icon)
                speechRecognizer!!.startListening(speechRecognizerIntent)
            }
            false
        }
    }

    private fun saveLyricsToDatabase(lyrics: String) {
        // Create a map for the lyrics data
        val lyricsData = mapOf(
            "lyrics" to lyrics,
            "timestamp" to System.currentTimeMillis() // Save the time of the recognition
        )

        // Push the lyrics to Firebase under the current user's "myLyrics" node
        database.push().setValue(lyricsData)
            .addOnSuccessListener {
                Toast.makeText(this, "Lyrics saved to database!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save lyrics: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer!!.destroy()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val RecordAudioRequestCode = 1
    }
}
