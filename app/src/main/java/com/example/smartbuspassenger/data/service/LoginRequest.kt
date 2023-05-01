package com.example.smartbuspassenger.data.service

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val login: String, val password: String)