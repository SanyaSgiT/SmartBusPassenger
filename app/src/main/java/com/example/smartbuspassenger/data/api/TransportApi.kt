package com.example.smartbuspassenger.data.api

import com.example.smartbuspassenger.data.service.RouteResponse
import com.example.smartbuspassenger.data.service.RoutesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TransportApi {
    @GET("routes")
    suspend fun getAllRoutes(): RoutesResponse

    @GET("routes/{id}")
    suspend fun findRouteById(@Path("id") id: Int): RouteResponse

    @GET("routes?type={transportType}&route={route}")
    suspend fun findRouteByTypeId(@Path("id") id: Int, type: String): RouteResponse
}