package com.rohnsha.medbuddyai

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object ContextUtill {

    object ContextUtils {
        private lateinit var appContext: Context

        fun initialize(context: Context) {
            appContext = context.applicationContext
        }

        fun getApplicationContext(): Context {
            return appContext
        }
    }

    private const val MAX_RETRIES = 3

    private val retryInterceptor = Interceptor { chain ->
        var request = chain.request()
        var response: Response? = null
        var responseCount = 0

        while (responseCount < MAX_RETRIES) {
            try {
                response = chain.proceed(request)
                if (response.isSuccessful) {
                    return@Interceptor response
                }
            } catch (e: IOException) {
                if (responseCount == MAX_RETRIES - 1) throw e
            } finally {
                if (response != null && !response.isSuccessful) {
                    response.close()
                }
            }

            responseCount++
            if (responseCount < MAX_RETRIES) {
                Thread.sleep(1000L) // Wait for 1 second before retrying
            }
        }

        response ?: throw IOException("Request failed after $MAX_RETRIES attempts")
    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(retryInterceptor)
        .build()

}