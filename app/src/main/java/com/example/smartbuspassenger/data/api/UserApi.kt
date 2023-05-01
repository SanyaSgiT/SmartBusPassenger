package com.example.smartbuspassenger.data.api

import com.example.smartbuspassenger.data.models.AuthResponse
import com.example.smartbuspassenger.data.models.CreateUserRequest
import com.example.smartbuspassenger.data.models.LoginRequest
import com.example.smartbuspassenger.domain.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int): User?

    @POST("account/login")
    suspend fun authentication(@Body loginRequest: LoginRequest): AuthResponse

    @POST("account/register")
    suspend fun createUser(@Body createUserRequest: CreateUserRequest): AuthResponse
}