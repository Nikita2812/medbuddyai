package com.rohnsha.medbuddyai.api.authUsername

import retrofit2.http.GET
import retrofit2.http.Url

interface usernameINTERFACE {

    @GET
    suspend fun getUsernameDetails(@Url url: String): usernameDC

}