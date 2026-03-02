package com.example.mytechnician.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val rememberMe: Boolean = false,
    val user: com.example.mytechnician.data.model.User? = null
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
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            combine(
                authRepository.authToken,
                authRepository.currentUser
            ) { token, user ->
                token to user
            }.collect { (token, user) ->
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = !token.isNullOrEmpty(),
                    user = user,
                    isLoading = false
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

    fun onRememberMeChange(checked: Boolean) {
        _uiState.value = _uiState.value.copy(
            rememberMe = checked
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
                password = currentState.password,
                rememberMe = currentState.rememberMe
            )
            
            result.fold(
                onSuccess = { response ->
                    android.util.Log.d("LoginViewModel", "Login success: ${response.user.name}")
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = response.user,
                        phoneNumber = "",
                        password = ""
                    )
                },
                onFailure = { error ->
                    android.util.Log.e("LoginViewModel", "Login error", error)
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed. Please try again."
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = LoginUiState()
        }
    }
}
