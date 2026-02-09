package com.example.mytechnician.data.repository

import com.example.mytechnician.data.local.UserPreferences
import com.example.mytechnician.data.model.LoginRequest
import com.example.mytechnician.data.model.LoginResponse
import com.example.mytechnician.data.model.User
import com.example.mytechnician.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val userPreferences: UserPreferences) {
    
    val authToken: Flow<String?> = userPreferences.authToken
    val currentUser: Flow<User?> = userPreferences.userData
    
    suspend fun login(phoneNumber: String, password: String): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.authApi.login(
                LoginRequest(phone_number = phoneNumber, password = password)
            )
            
            // Save to DataStore
            userPreferences.saveAuthData(response.token, response.user)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        userPreferences.clearAuthData()
    }
}
