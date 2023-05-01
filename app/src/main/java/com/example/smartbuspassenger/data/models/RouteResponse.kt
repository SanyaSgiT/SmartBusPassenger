package com.example.smartbuspassenger.data.models

import com.example.smartbuspassenger.domain.FirstStop
import com.example.smartbuspassenger.domain.LastStop
import com.example.smartbuspassenger.domain.TransportType

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponse(
    val id: Int,
    val route: String,
    val name: String,
    val transportType: TransportType,
    val firstStop: FirstStop,
    val lastStop: LastStop
)