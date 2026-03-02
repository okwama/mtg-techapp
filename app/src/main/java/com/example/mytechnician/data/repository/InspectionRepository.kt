package com.example.mytechnician.data.repository

import com.example.mytechnician.data.model.*
import com.example.mytechnician.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class InspectionRepository {
    private val api = RetrofitClient.inspectionApi

    suspend fun getInspections(): Result<List<Inspection>> {
        return try {
            Result.success(api.getInspections())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getScheduledConversions(): Result<List<Conversion>> {
        return try {
            Result.success(api.getScheduledConversions())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClients(): Result<List<Client>> {
        return try {
            Result.success(api.getClients())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVehicles(clientId: Int, source: String? = null): Result<List<InspectionVehicle>> {
        return try {
            Result.success(api.getVehicles(clientId, source))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startInspection(vehicleId: Int?, stationId: Int, conversionId: Int? = null): Result<Int> {
        return try {
            val request = StartInspectionRequest(
                vehicle_id = vehicleId,
                conversion_id = conversionId,
                station_id = stationId
            )
            val response = api.startInspection(request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to start inspection"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInspectionDetail(id: Int): Result<Inspection> {
        return try {
            Result.success(api.getInspectionDetail(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInspection(id: Int, checklistData: List<InspectionCategory>, summary: String, condition: String, status: String): Result<Unit> {
        return try {
            val request = UpdateInspectionRequest(
                checklist_data = checklistData,
                summary = summary,
                overall_condition = condition,
                status = status
            )
            val response = api.updateInspection(id, request)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitInspection(id: Int): Result<Unit> {
        return try {
            val response = api.submitInspection(id)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadPhoto(inspectionId: Int, photoFile: File, type: String, caption: String? = null): Result<String> {
        return try {
            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
            val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())
            val captionPart = caption?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.uploadPhoto(inspectionId, body, typePart, captionPart)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
