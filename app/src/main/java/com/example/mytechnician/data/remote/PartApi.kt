package com.example.mytechnician.data.remote

import com.example.mytechnician.data.model.Part
import com.example.mytechnician.data.model.PartRequest
import com.example.mytechnician.data.model.PartRequestResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PartApi {
    @GET("parts")
    suspend fun getParts(): List<Part>

    @POST("parts-requests")
    suspend fun requestParts(@Body request: PartRequest): PartRequestResponse
}
