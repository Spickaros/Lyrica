package com.example.lyrica

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if user is already authenticated
        if (firebaseAuth.currentUser != null) {
            // If the user is authenticated, navigate to MainPageActivity directly
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Reference to buttons
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        // Handle login button click
        loginButton.setOnClickListener {
            // Navigate to SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivityForResult(intent, LOGIN_REQUEST_CODE)
        }

        // Handle signup button click
        signupButton.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(intent, SIGNUP_REQUEST_CODE)
        }
    }

    // Handle result from login and signup activities
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            // Check if the result is from login or signup
            if (requestCode == LOGIN_REQUEST_CODE || requestCode == SIGNUP_REQUEST_CODE) {
                // User is authenticated, navigate to MainPageActivity
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val LOGIN_REQUEST_CODE = 1
        const val SIGNUP_REQUEST_CODE = 2
    }
}
