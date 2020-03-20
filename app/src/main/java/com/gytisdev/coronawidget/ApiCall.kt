package com.gytisdev.coronawidget

import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url

class APIClient {
    companion object {
        fun getClient() : Retrofit {
            return Builder()
                .baseUrl("https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}

interface ApiCall {
    @GET
    suspend fun getData(@Url url : String = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest/") : List<ApiResponseData>
}