package com.example.smartbuspassenger.data.service

import com.example.smartbuspassenger.domain.Route
import kotlinx.serialization.Serializable

@Serializable
data class RoutesResponse(
    val routes: List<Route>
)