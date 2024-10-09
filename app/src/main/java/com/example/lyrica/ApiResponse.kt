package com.example.lyrica

data class ApiResponse(
    val status: String,
    val message: String,
    val data: Data
)

data class Data(
    val vocals_url: String?,
    val instrumentals_url: String?
)
