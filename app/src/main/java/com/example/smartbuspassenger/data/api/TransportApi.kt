package com.example.smartbuspassenger.data.api

import com.example.smartbuspassenger.data.models.RouteResponse
import com.example.smartbuspassenger.data.models.RoutesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TransportApi {
    @GET("routes")
    suspend fun getAllRoutes(): RoutesResponse

    @GET("routes/{id}")
    suspend fun findRouteById(@Path("id") id: Int): RouteResponse

    @GET("routes")
    suspend fun findRouteByTypeId(@Query("type") transportType: Int, @Query("route") route: String): RouteResponse
}