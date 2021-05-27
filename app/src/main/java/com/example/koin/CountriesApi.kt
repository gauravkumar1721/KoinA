package com.example.koin

import com.example.koin.model.CountriesData
import retrofit2.Response
import retrofit2.http.GET

interface CountriesApi {
    @GET("/api/v1")
    suspend fun getAllCountries(): Response<List<CountriesData>>
}