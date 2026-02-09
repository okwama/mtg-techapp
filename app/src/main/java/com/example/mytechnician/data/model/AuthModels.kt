package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phone_number: String,
    val password: String
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
    val phone_number: String,
    val role: String,
    val station_id: Int?,
    val business_email: String?
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
