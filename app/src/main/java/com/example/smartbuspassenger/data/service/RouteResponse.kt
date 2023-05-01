package com.example.smartbuspassenger.data.service

import com.example.smartbuspassenger.domain.FirstStop
import com.example.smartbuspassenger.domain.LastStop

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponse(
    val id: Int,
    val route: String,
    val name: String,
    val transportType: String,
    val firstStop: FirstStop,
    val lastStop: LastStop
)