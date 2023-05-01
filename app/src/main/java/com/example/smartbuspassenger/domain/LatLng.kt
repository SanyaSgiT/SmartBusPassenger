package com.example.smartbuspassenger.domain

import kotlinx.serialization.Serializable

@Serializable
data class LatLng(
    val lat: Double,
    val lng: Double
)
