package com.example.smartbuspassenger.domain

import kotlinx.serialization.Serializable

@Serializable
data class FirstStop(
    val id: Int,
    val name: String,
    val latLng: LatLng,
    val len: Int
)