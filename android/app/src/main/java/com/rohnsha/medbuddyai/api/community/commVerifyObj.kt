package com.rohnsha.medbuddyai.api.community

import com.rohnsha.medbuddyai.ContextUtill
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object commVerifyObj {

    val retrofit= Retrofit
        .Builder()
        .baseUrl("https://api-jjtysweprq-el.a.run.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(ContextUtill.okHttpClient)
        .build()

    val commVerifyService= retrofit.create(communityClassificationInterface::class.java)

}