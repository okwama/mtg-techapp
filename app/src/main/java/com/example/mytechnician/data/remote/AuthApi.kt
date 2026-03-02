package com.example.mytechnician.data.remote

import com.example.mytechnician.data.model.ApiResponse
import com.example.mytechnician.data.model.LoginRequest
import com.example.mytechnician.data.model.LoginResponse
import com.example.mytechnician.data.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @retrofit2.http.GET("auth/profile")
    suspend fun getProfile(): ApiResponse<User>
}
