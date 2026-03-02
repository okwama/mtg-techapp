package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val id: Int,
    val name: String,
    val address: String,
    val longitude: String? = null,
    val latitude: String? = null,
    val regionId: Int? = null,
    val region_name: String? = null,
    val contact: String? = null,
    val price: Double? = null,
    val lpgQuantity: Double? = null
)

@Serializable
data class Shift(
    val id: Int,
    val userId: Int,
    val userName: String,
    val station_id: Int,
    val station_name: String,
    val status: Int, // 1: Active, 2: Completed
    val checkInTime: String? = null,
    val checkoutTime: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val checkoutLatitude: Double? = null,
    val checkoutLongitude: Double? = null,
    val outlet_address: String? = null
)

@Serializable
data class ShiftStatusResponse(
    val active: Boolean,
    val shift: Shift? = null
)

@Serializable
data class CheckinRequest(
    val station_id: Int,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class CheckoutRequest(
    val latitude: Double? = null,
    val longitude: Double? = null
)
