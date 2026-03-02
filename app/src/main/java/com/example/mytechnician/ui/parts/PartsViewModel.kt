package com.example.mytechnician.ui.parts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.Part
import com.example.mytechnician.data.model.PartRequest
import com.example.mytechnician.data.repository.PartRepository
import kotlinx.coroutines.launch

sealed class PartsUiState {
    object Loading : PartsUiState()
    data class Success(val parts: List<Part>) : PartsUiState()
    data class Error(val message: String) : PartsUiState()
    object Idle : PartsUiState()
}

class PartsViewModel(private val repository: PartRepository) : ViewModel() {
    
    private val _uiState = mutableStateOf<PartsUiState>(PartsUiState.Idle)
    val uiState: State<PartsUiState> = _uiState
    
    private val _allParts = mutableStateOf<List<Part>>(emptyList())
    
    init {
        loadParts()
    }
    
    fun loadParts() {
        _uiState.value = PartsUiState.Loading
        viewModelScope.launch {
            repository.getParts().onSuccess { parts ->
                _allParts.value = parts
                _uiState.value = PartsUiState.Success(parts)
            }.onFailure { error ->
                _uiState.value = PartsUiState.Error(error.message ?: "Failed to load parts")
            }
        }
    }
    
    fun searchParts(query: String) {
        if (query.isBlank()) {
            _uiState.value = PartsUiState.Success(_allParts.value)
            return
        }
        
        val filtered = _allParts.value.filter {
            it.name.contains(query, ignoreCase = true) || 
            it.sku?.contains(query, ignoreCase = true) == true ||
            it.description?.contains(query, ignoreCase = true) == true
        }
        _uiState.value = PartsUiState.Success(filtered)
    }
    
    fun requestPart(technicianId: Int, partId: Int, quantity: Int, stationId: Int, reason: String) {
        viewModelScope.launch {
            val request = PartRequest(
                technician_id = technicianId,
                part_id = partId,
                quantity = quantity,
                station_id = stationId,
                reason = reason
            )
            repository.requestParts(request).onSuccess {
                // We might want to show a toast or message
            }.onFailure {
                // Show error
            }
        }
    }
}
