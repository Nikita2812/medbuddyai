package com.rohnsha.medbuddyai.api.community

import retrofit2.http.POST
import retrofit2.http.Query

interface communityClassificationInterface {

    @POST("/text_classify")
    suspend fun isAllowedToBePosted(
        @Query("text") text: String
    ) : communityVerifyDC

}