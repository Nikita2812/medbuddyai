package com.rohnsha.medbuddyai.api.decodeReport

import com.rohnsha.medbuddyai.api.chatbot.chatbot_dc
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface decodeReportInterface {

    @POST("chat/unrag")
    suspend fun decodeReport(
        @Query("serviceName") serviceName: String,
        @Query("secretCode") secretCode: String,
        @Body message: String
    ): chatbot_dc

}