package com.example.mytechnician.data.remote

import com.example.mytechnician.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ShiftApi {
    @GET("stations")
    suspend fun getStations(): List<Station>

    @GET("shift/status")
    suspend fun getShiftStatus(): ShiftStatusResponse

    @POST("checkin")
    suspend fun checkin(@Body request: CheckinRequest): ApiResponse<Unit>

    @POST("checkout")
    suspend fun checkout(@Body request: CheckoutRequest): ApiResponse<Unit>
}
