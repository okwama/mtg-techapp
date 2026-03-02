package com.example.mytechnician.data.remote

import com.example.mytechnician.data.model.ServiceRequest
import com.example.mytechnician.data.model.ServiceRequestResponse
import com.example.mytechnician.data.model.ServiceApproval
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceApi {
    @GET("service-approvals")
    suspend fun getServiceApprovals(): List<ServiceApproval>

    @POST("service-approvals")
    suspend fun submitServiceRequest(@Body request: ServiceRequest): ServiceRequestResponse
}
