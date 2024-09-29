package com.rohnsha.medbuddyai.api.decodeReport

import com.rohnsha.medbuddyai.ContextUtill
import com.rohnsha.medbuddyai.api.chatbot.chatbot_obj.okHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object decodeObj {

    val retrofit = Retrofit
        .Builder()
        .baseUrl("https://api-252611030553.asia-south1.run.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val decodeService = retrofit.create(decodeReportInterface::class.java)


}