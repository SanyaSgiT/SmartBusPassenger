package com.example.smartbuspassenger.data.models

import com.example.smartbuspassenger.domain.Route
import com.example.smartbuspassenger.domain.Trace
import kotlinx.serialization.Serializable

@Serializable
data class TracesResponse(
    val route: Route,
    val traces: List<Trace>
)