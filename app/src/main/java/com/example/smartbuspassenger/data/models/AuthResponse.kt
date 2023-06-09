package com.example.smartbuspassenger.data.models

import com.example.smartbuspassenger.domain.Token
import com.example.smartbuspassenger.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val user: User,
    val token: Token
)
