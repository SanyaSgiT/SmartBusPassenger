package com.example.smartbuspassenger.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val login: String, val password: String)