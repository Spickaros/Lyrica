package com.example.lyrica
data class Stem(
    var id: String? = null,   // Unique ID for each stem
    var filePath: String? = null,  // Path or URL of the stem file
    var type: String? = null  // "vocals" or "instrumentals"
)
