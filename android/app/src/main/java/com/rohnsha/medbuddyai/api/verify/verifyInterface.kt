package com.rohnsha.medbuddyai.api.verify

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface verifyInterface {

    @Multipart
    @POST("/verifyResults")
    suspend fun verifyRemoteModels(
        @Query("serviceName") serviceName: String,
        @Query("secretCode") secretCode: String,
        @Query("diseaseName") diseaseName: String,
        @Part bitmp: MultipartBody.Part
    ): verifyDC

}