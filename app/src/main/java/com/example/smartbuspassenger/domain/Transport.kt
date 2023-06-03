@file:UseSerializers(DateTimeTzSerializer::class)

package com.example.smartbuspassenger.domain

import kotlinx.serialization.Serializable
import com.soywiz.klock.DateTimeTz
import kotlinx.serialization.UseSerializers
import com.example.smartbuspassenger.serializers.DateTimeTzSerializer
import java.util.*

@Serializable
data class Transport(
    val name: String,
    val route: String,
    val routeId: Int,
    val transportType: TransportType,
    val graph: Int,
    val direction: Direction,
    val latLng: LatLng,
    val azimuth: Int,
    val timeNav: DateTimeTz,
    val idTypetr: Int, //WTF
    val rasp: String,
    val speed: Double,
    val segmentOrder: Int,
    val ramp: Int
)