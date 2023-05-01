package com.example.smartbuspassenger.data.functional

import com.example.smartbuspassenger.data.api.TransportApi
import com.example.smartbuspassenger.domain.Route

class RoutesFunctions(private val api: TransportApi) {
    suspend fun getAllRoutes(): List<Route> = api.getAllRoutes().routes
//
//    suspend fun findRoutesById(id: Int) = api.findRouteById(id).books

    suspend fun findRouteById(id: Int): List<Route> = api.findRouteById(id).books

//    suspend fun findRoutesByTypeId(id: Int): BookResponse = api.(id)
}