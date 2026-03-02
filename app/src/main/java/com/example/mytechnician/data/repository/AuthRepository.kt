package com.example.mytechnician.data.repository

import com.example.mytechnician.data.local.UserPreferences
import com.example.mytechnician.data.model.LoginRequest
import com.example.mytechnician.data.model.LoginResponse
import com.example.mytechnician.data.model.ApiResponse
import com.example.mytechnician.data.model.User
import com.example.mytechnician.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class AuthRepository(private val userPreferences: UserPreferences) {
    
    val authToken: Flow<String?> = userPreferences.authToken
    val currentUser: Flow<User?> = userPreferences.userData
    
    suspend fun login(phoneNumber: String, password: String, rememberMe: Boolean = false): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.authApi.login(
                LoginRequest(
                    phone_number = phoneNumber, 
                    password = password,
                    remember_me = rememberMe
                )
            )
            
            // Save to DataStore
            userPreferences.saveAuthData(response.token, response.user)
            RetrofitClient.authToken = response.token
            
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = try {
                val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                json.decodeFromString<ApiResponse<Unit>>(errorBody ?: "").message
            } catch (ex: Exception) {
                null
            } ?: "Invalid credentials"
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<User> {
        return try {
            val response = RetrofitClient.authApi.getProfile()
            if (response.success && response.data != null) {
                // Update local user data
                userPreferences.saveAuthData(null, response.data)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        userPreferences.clearAuthData()
    }
}
