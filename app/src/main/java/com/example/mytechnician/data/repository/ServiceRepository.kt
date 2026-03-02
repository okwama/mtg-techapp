package com.example.mytechnician.data.repository

import com.example.mytechnician.data.model.ServiceApproval
import com.example.mytechnician.data.model.ServiceRequest
import com.example.mytechnician.data.model.ServiceRequestResponse
import com.example.mytechnician.data.remote.RetrofitClient

class ServiceRepository {
    private val serviceApi = RetrofitClient.serviceApi
    private val inspectionApi = RetrofitClient.inspectionApi

    suspend fun getClients(): Result<List<com.example.mytechnician.data.model.Client>> {
        return try {
            Result.success(inspectionApi.getClients())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVehicles(clientId: Int): Result<List<com.example.mytechnician.data.model.InspectionVehicle>> {
        return try {
            Result.success(inspectionApi.getVehicles(clientId, null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServiceApprovals(): Result<List<ServiceApproval>> {
        return try {
            Result.success(serviceApi.getServiceApprovals())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitServiceRequest(request: ServiceRequest): Result<ServiceRequestResponse> {
        return try {
            Result.success(serviceApi.submitServiceRequest(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
