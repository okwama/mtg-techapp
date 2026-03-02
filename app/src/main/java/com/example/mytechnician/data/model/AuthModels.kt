package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phone_number: String,
    val password: String,
    val remember_me: Boolean = false
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val role: String,
    val phone_number: String = "",
    val station_id: Int? = null,
    val station_name: String? = null,
    val business_email: String? = null
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)
