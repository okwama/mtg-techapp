package com.example.mytechnician.data.repository

import com.example.mytechnician.data.model.*
import com.example.mytechnician.data.remote.RetrofitClient

class ShiftRepository {
    private val api = RetrofitClient.shiftApi

    suspend fun getStations(): Result<List<Station>> {
        return try {
            val response = api.getStations()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShiftStatus(): Result<ShiftStatusResponse> {
        return try {
            val response = api.getShiftStatus()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkin(request: CheckinRequest): Result<Unit> {
        return try {
            val response = api.checkin(request)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to check in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkout(request: CheckoutRequest): Result<Unit> {
        return try {
            val response = api.checkout(request)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to check out"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
