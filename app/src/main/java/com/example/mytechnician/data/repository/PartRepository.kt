package com.example.mytechnician.data.repository

import com.example.mytechnician.data.model.Part
import com.example.mytechnician.data.model.PartRequest
import com.example.mytechnician.data.model.PartRequestResponse
import com.example.mytechnician.data.remote.RetrofitClient

class PartRepository {
    private val api = RetrofitClient.partApi

    suspend fun getParts(): Result<List<Part>> {
        return try {
            Result.success(api.getParts())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestParts(request: PartRequest): Result<PartRequestResponse> {
        return try {
            Result.success(api.requestParts(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
