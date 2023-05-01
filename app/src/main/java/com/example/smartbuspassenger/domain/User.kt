package com.example.smartbuspassenger.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val login: String,
    val name: String,
)