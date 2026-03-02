package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Part(
    val id: Int,
    val name: String,
    val description: String? = null,
    val sku: String? = null,
    val price: Double? = null,
    val stock_quantity: Int = 0,
    val station_id: Int? = null
)

@Serializable
data class PartRequest(
    val technician_id: Int,
    val part_id: Int,
    val quantity: Int,
    val station_id: Int,
    val inspection_id: Int? = null,
    val reason: String? = null
)

@Serializable
data class PartRequestResponse(
    val id: Int,
    val message: String
)
