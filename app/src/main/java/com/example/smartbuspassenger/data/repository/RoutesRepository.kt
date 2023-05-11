package com.example.smartbuspassenger.data.repository

import com.example.smartbuspassenger.data.api.TransportApi
import com.example.smartbuspassenger.data.mappers.asRoute
import com.example.smartbuspassenger.domain.Route

class RoutesRepository(private val api: TransportApi) {
    suspend fun getAllRoutes(): List<Route> = api.getAllRoutes().routes
    suspend fun findRouteByName(id: Int) = api.findRouteByName(id).routes
    suspend fun getTrace(id: Int) = api.getTrace(id).traces

//    suspend fun findRoutesById(id: Int) = api.findRouteById(id).books

//    suspend fun findRouteByName(name: String): Route = api.findRouteByName(name).asRoute()
}