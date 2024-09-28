package com.rohnsha.medbuddyai.api

import com.rohnsha.medbuddyai.domain.dataclass.doctors
import retrofit2.http.GET
import retrofit2.http.Url

interface results_interface {

    @GET
    suspend fun getDiseaseData(@Url url: String): doctors

}