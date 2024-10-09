package com.example.lyrica

import com.google.gson.annotations.SerializedName

data class FileConversionListResponse(
    @SerializedName("data")
    val data: List<FileConversionResponseItem> = emptyList(),

    @SerializedName("links")
    val links: Links? = null,

    @SerializedName("meta")
    val meta: Meta? = null
)

data class FileConversionResponseItem(
    @SerializedName("ulid")
    val ulid: String,

    @SerializedName("file_name")
    val fileName: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("sample")
    val sample: Boolean,

    @SerializedName("task_name")
    val taskName: String? = null,

    @SerializedName("error_message")
    val errorMessage: String? = null,

    @SerializedName("file_duration")
    val fileDuration: FileDuration? = null,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("expires_at")
    val expiresAt: String,

    @SerializedName("output")
    val output: Output? = null
)

data class Links(
    @SerializedName("first")
    val first: String? = null,

    @SerializedName("last")
    val last: String? = null,

    @SerializedName("prev")
    val prev: String? = null,

    @SerializedName("next")
    val next: String? = null
)

data class Meta(
    @SerializedName("path")
    val path: String? = null,

    @SerializedName("per_page")
    val perPage: Int? = null,

    @SerializedName("next_cursor")
    val nextCursor: String? = null,

    @SerializedName("prev_cursor")
    val prevCursor: String? = null
)

/*data class FileDuration(
    @SerializedName("duration") // Example field, adjust as needed
    val duration: String? = null
)*/

data class Output(
    @SerializedName("vocals")
    val vocals: FileOutput? = null,

    @SerializedName("instrumentals")
    val instrumentals: FileOutput? = null,

    @SerializedName("source")
    val source: FileOutput? = null
)

data class FileOutput(
    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("download_file_name")
    val downloadFileName: String
)
