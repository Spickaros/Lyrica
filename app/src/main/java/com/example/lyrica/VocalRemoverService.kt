package com.example.lyrica

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface VocalRemoverService {

    @Multipart
    @POST("file-conversion/create")
    fun createFileConversion(
        @Header("Authorization") apiKey: String,
        @Part file: MultipartBody.Part,
        @Part("task") task: RequestBody,
        @Part("sample") sample: RequestBody
    ): Call<FileConversionResponse>

    @GET("file-conversion/{ulid}")
    fun getFileConversionStatus(
        @Header("Authorization") apiKey: String,
        @Path("ulid") ulid: String
    ): Call<FileConversionStatusResponse>

    @GET("file-conversion/list")
    fun listFileConversions(
        @Header("Authorization") apiKey: String,
        @Query("cursor") cursor: String? = null
    ): Call<FileConversionListResponse>
}
