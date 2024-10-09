package com.example.lyrica

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configure the OkHttpClient with increased timeouts
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)  // Increase connection timeout
        .readTimeout(30, TimeUnit.SECONDS)     // Increase read timeout
        .writeTimeout(30, TimeUnit.SECONDS)    // Increase write timeout
        .build()

    val api: VocalRemoverService = Retrofit.Builder()
        .baseUrl("https://vocalremover.com/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(VocalRemoverService::class.java)
}
