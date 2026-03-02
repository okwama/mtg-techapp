package com.example.mytechnician.ui.service

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.ServiceRequest
import com.example.mytechnician.data.repository.ServiceRepository
import kotlinx.coroutines.launch

sealed class ServiceUiState {
    object Idle : ServiceUiState()
    object Loading : ServiceUiState()
    data class Success(val message: String) : ServiceUiState()
    data class Error(val message: String) : ServiceUiState()
}

class ServiceViewModel(private val repository: ServiceRepository) : ViewModel() {
    
    private val _uiState = mutableStateOf<ServiceUiState>(ServiceUiState.Idle)
    val uiState: State<ServiceUiState> = _uiState
    
    private val _clients = mutableStateOf<List<com.example.mytechnician.data.model.Client>>(emptyList())
    val clients: State<List<com.example.mytechnician.data.model.Client>> = _clients
    
    private val _vehicles = mutableStateOf<List<com.example.mytechnician.data.model.InspectionVehicle>>(emptyList())
    val vehicles: State<List<com.example.mytechnician.data.model.InspectionVehicle>> = _vehicles
    
    init {
        loadClients()
    }
    
    fun loadClients() {
        viewModelScope.launch {
            repository.getClients().onSuccess {
                _clients.value = it
            }
        }
    }
    
    fun loadVehicles(clientId: Int) {
        viewModelScope.launch {
            repository.getVehicles(clientId).onSuccess {
                _vehicles.value = it
            }
        }
    }
    
    fun submitServiceRequest(clientId: Int, vehicleId: Int, description: String, serviceType: String) {
        _uiState.value = ServiceUiState.Loading
        viewModelScope.launch {
            val request = ServiceRequest(
                client_id = clientId,
                vehicle_id = vehicleId,
                description = description,
                service_type = serviceType
            )
            repository.submitServiceRequest(request).onSuccess { response ->
                _uiState.value = ServiceUiState.Success(response.message)
            }.onFailure { error ->
                _uiState.value = ServiceUiState.Error(error.message ?: "Failed to submit request")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ServiceUiState.Idle
    }
}
