package com.example.smartbuspassenger.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val login: String,
    val name: String,
    val password: String
)