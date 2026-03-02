package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Conversion(
    val id: Int,
    val owner_full_name: String,
    val vehicle_registration: String,
    val make: String? = null,
    val model: String? = null,
    val scheduled_date: String? = null,
    val inspection_id: Int? = null,
    val inspection_status: String? = null
)

@Serializable
data class Inspection(
    val id: Int,
    val inspection_number: String,
    val conversion_id: Int? = null,
    val technician_id: Int,
    val vehicle_id: Int? = null,
    val station_id: Int,
    val status: String,
    val checklist_data: List<InspectionCategory>? = null,
    val summary: String? = null,
    val overall_condition: String? = null,
    val inspection_date: String,
    val registration_number: String? = null,
    val owner_full_name: String? = null,
    val photos: List<InspectionPhoto> = emptyList()
)

@Serializable
data class InspectionCategory(
    val name: String,
    val items: List<ChecklistItem>
)

@Serializable
data class ChecklistItem(
    val id: String,
    val name: String,
    val condition: String? = null, // Good, Fair, Poor
    val notes: String? = null,
    val hasPhoto: Boolean = false
)

@Serializable
data class InspectionPhoto(
    val id: Int,
    val inspection_id: Int,
    val photo_url: String,
    val photo_type: String,
    val caption: String? = null
)

@Serializable
data class Client(
    val id: Int,
    val name: String,
    val contact: String,
    val email: String? = null,
    val source: String? = "account"
)

@Serializable
data class InspectionVehicle(
    val id: Int,
    val registration_number: String,
    val make: String? = null,
    val model: String? = null,
    val vin_serial_number: String? = null,
    val source: String? = "account"
)

@Serializable
data class StartInspectionRequest(
    val vehicle_id: Int? = null,
    val conversion_id: Int? = null,
    val station_id: Int
)

@Serializable
data class UpdateInspectionRequest(
    val summary: String? = null,
    val overall_condition: String? = null,
    val checklist_data: List<InspectionCategory>? = null,
    val status: String? = "pending"
)
