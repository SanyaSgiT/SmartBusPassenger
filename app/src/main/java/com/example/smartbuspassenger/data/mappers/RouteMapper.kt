package com.example.smartbuspassenger.data.mappers

import com.example.smartbuspassenger.data.models.RouteResponse
import com.example.smartbuspassenger.domain.Route

fun RouteResponse.asRoute() = Route(id, route, name, transportType, firstStop, lastStop)