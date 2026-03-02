package com.example.mytechnician.data.remote

import com.example.mytechnician.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.Part as RetrofitPart

interface InspectionApi {
    @GET("inspections")
    suspend fun getInspections(): List<Inspection>

    @GET("inspections/scheduled")
    suspend fun getScheduledConversions(): List<Conversion>

    @GET("inspections/clients")
    suspend fun getClients(): List<Client>

    @GET("inspections/clients/{clientId}/vehicles")
    suspend fun getVehicles(@Path("clientId") clientId: Int, @Query("source") source: String?): List<InspectionVehicle>

    @POST("inspections")
    suspend fun startInspection(@Body request: StartInspectionRequest): ApiResponse<Int>

    @GET("inspections/{id}")
    suspend fun getInspectionDetail(@Path("id") id: Int): Inspection

    @PUT("inspections/{id}")
    suspend fun updateInspection(
        @Path("id") id: Int,
        @Body request: UpdateInspectionRequest
    ): ApiResponse<Unit>

    @POST("inspections/{id}/submit")
    suspend fun submitInspection(@Path("id") id: Int): ApiResponse<Unit>

    @Multipart
    @POST("inspections/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: Int,
        @RetrofitPart photo: MultipartBody.Part,
        @RetrofitPart("photo_type") type: RequestBody,
        @RetrofitPart("caption") caption: RequestBody? = null
    ): ApiResponse<String>
}
