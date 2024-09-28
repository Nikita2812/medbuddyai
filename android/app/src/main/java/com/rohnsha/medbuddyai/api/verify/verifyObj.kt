package com.rohnsha.medbuddyai.api.verify

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object verifyObj {

    val retrofit= Retrofit
        .Builder()
        .baseUrl("https://api-jjtysweprq-el.a.run.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val verifyService= retrofit.create(verifyInterface::class.java)

}