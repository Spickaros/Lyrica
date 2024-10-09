package com.example.lyrica

import android.graphics.Bitmap
import android.hardware.biometrics.BiometricPrompt
import com.example.lyrica.data.Chat

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String= "",
    val bitmap: Bitmap? = null
)