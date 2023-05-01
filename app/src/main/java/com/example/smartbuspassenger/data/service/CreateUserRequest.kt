package com.example.smartbuspassenger.data.service

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val login: String,
    val name: String,
    val password: String
)