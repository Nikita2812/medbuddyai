package com.rohnsha.medbuddyai.api.chatbot

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface chatbot_interface {

    @GET
    suspend fun getChatReply(@Url url: String): chatbot_dc

    @POST("chat/")
    suspend fun sendMessage(
        @Query("serviceName") serviceName: String,
        @Query("secretCode") secretCode: String,
        @Query("message") message: String
    ): chatbot_dc

    @POST("task/chat/")
    suspend fun sendTaskMsg(
        @Query("serviceName") serviceName: String,
        @Query("secretCode") secretCode: String,
        @Query("message") message: String,
        @Query("task") task: Int
    ): chatbot_dc
}