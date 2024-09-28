package com.rohnsha.medbuddyai.api.verify

import com.rohnsha.medbuddyai.ContextUtill
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object verifyObj {

    val retrofit= Retrofit
        .Builder()
        .baseUrl("https://api-jjtysweprq-el.a.run.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(ContextUtill.okHttpClient)
        .build()

    val verifyService= retrofit.create(verifyInterface::class.java)

}