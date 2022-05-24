package com.example.valentinesgaragetaskmanagementapp.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token: $token")
    }
}