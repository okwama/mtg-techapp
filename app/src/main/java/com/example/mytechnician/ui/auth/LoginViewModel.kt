package com.example.mytechnician.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            authRepository.authToken.collect { token ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = !token.isNullOrEmpty()
                )
            }
        }
    }
    
    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phoneNumber,
            errorMessage = null
        )
    }
    
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }
    
    fun login() {
        val currentState = _uiState.value
        
        if (currentState.phoneNumber.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "Please enter both phone number and password"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
            
            val result = authRepository.login(
                phoneNumber = currentState.phoneNumber,
                password = currentState.password
            )
            
            result.fold(
                onSuccess = { response ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed. Please try again."
                    )
                }
            )
        }
    }
}
