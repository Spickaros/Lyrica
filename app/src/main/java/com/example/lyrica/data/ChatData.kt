package com.example.lyrica.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    // Your API key (ensure this is secure in production)
    private const val apiKey = "AIzaSyB3-4SJYvcPTiKFt1ublDBDVgh1x4CeMNw"

    suspend fun getResponse(prompt: String): Chat {
        // Update the model name to the recommended version
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", // Updated model name
            apiKey = apiKey
        )

        return try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            Chat(
                prompt = response.text ?: "Error: No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: ResponseStoppedException) {
            Chat(
                prompt = e.message ?: "Error: Response stopped",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            // Catch any other exceptions and return an error message
            Chat(
                prompt = e.message ?: "Error: Something went wrong",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        // Update the model name for image processing
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", // Use the appropriate model for image as well
            apiKey = apiKey
        )

        return try {
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            Chat(
                prompt = response.text ?: "Error: No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: ResponseStoppedException) {
            Chat(
                prompt = e.message ?: "Error: Response stopped",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            // Catch any other exceptions and return an error message
            Chat(
                prompt = e.message ?: "Error: Something went wrong",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}
