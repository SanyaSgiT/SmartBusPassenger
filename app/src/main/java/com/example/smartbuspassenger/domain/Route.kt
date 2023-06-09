package com.example.smartbuspassenger.domain

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: Int,
    val route: String,
    val name: String,
    val transportType: TransportType,
    val firstStop: FirstStop,
    val lastStop: LastStop
)
