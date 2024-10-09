package com.example.lyrica
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface VocalRemoverApi {

    @Multipart
    @POST("file-conversion/create")
    fun createFileConversion(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part,
        @Part("task") task: RequestBody,
        @Part("sample") sample: RequestBody
    ): Call<FileConversionResponse>

    @GET("file-conversion/{ulid}")
    fun getFileConversionStatus(
        @Header("Authorization") authHeader: String,
        @Path("ulid") ulid: String
    ): Call<FileConversionStatusResponse>
}
data class FileConversionResponse(
    val file_conversion: FileConversion
)

data class FileConversion(
    val ulid: String,
    val file_name: String,
    val status: String,
    val sample: Boolean,
    val created_at: String,
    val expires_at: String
)

data class FileConversionStatusResponse(
    val file_conversion: FileConversion
)
