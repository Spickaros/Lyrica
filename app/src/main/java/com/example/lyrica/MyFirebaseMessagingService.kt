package com.example.lyrica

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
        }

        // Start PermissionActivity to check/request notifications permission
        val intent = Intent(this, PermissionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token) // Implement this method to send the token to your app's server
    }

    private fun sendRegistrationToServer(token: String) {
        // Add custom implementation to send the token to your server if needed
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}