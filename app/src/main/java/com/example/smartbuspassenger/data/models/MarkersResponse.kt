package com.example.smartbuspassenger.data.models

import com.example.smartbuspassenger.domain.Transport
import kotlinx.serialization.Serializable

@Serializable
data class MarkersResponse(val markers: List<Transport>)