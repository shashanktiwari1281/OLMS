package com.employee_management_system.shashank.api

import com.employee_management_system.shashank.models.Employee
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("login-user")
    fun login(
        @Query("number") number: String,
        @Query("password") password: String
    ) : Call<Employee>
}