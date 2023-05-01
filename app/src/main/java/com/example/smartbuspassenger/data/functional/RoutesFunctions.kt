package com.example.smartbuspassenger.data.functional

import com.example.smartbuspassenger.data.api.TransportApi
import com.example.smartbuspassenger.data.mappers.asRoute
import com.example.smartbuspassenger.domain.Route

class RoutesFunctions(private val api: TransportApi) {
    suspend fun getAllRoutes(): List<Route> = api.getAllRoutes().routes
//
//    suspend fun findRoutesById(id: Int) = api.findRouteById(id).books

    suspend fun findRouteById(id: Int): Route = api.findRouteById(id).asRoute()

//    suspend fun findRoutesByTypeId(id: Int): BookResponse = api.(id)
}