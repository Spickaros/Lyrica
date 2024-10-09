package com.example.lyrica

data class FileConversionOutput(
    val vocals: FileStem?,
    val instrumentals: FileStem?,
    val source: FileStem?
)

data class FileStem(
    val name: String?,
    val url: String?,
    val download_file_name: String?
)
