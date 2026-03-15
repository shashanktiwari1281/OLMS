package com.employee_management_system.shashank.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit;

object RetrofitClient {

    private const val BASE_URL = "http://10.218.73.222:8080/"
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}