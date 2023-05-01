package com.example.smartbuspassenger.data.api

import com.example.smartbuspassenger.data.service.CreateUserRequest
import com.example.smartbuspassenger.data.service.LoginRequest
import com.example.smartbuspassenger.domain.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int): User?

    @POST("account/login")
    suspend fun authentication(@Body loginRequest: LoginRequest): User?

    @POST("account/register")
    suspend fun createUser(@Body createUserRequest: CreateUserRequest): User?
}