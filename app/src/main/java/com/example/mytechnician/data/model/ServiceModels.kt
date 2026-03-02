package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequest(
    val client_id: Int,
    val vehicle_id: Int,
    val description: String,
    val service_type: String,
    val technician_id: Int? = null,
    val station_id: Int? = null
)

@Serializable
data class ServiceRequestResponse(
    val id: Int,
    val message: String
)

@Serializable
data class ServiceApproval(
    val id: Int,
    val service_request_id: Int,
    val status: String,
    val message: String? = null,
    val created_at: String
)
