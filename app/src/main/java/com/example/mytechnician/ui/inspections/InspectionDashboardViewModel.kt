package com.example.mytechnician.ui.inspections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.*
import com.example.mytechnician.data.repository.InspectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val isLoading: Boolean = false,
    val scheduledConversions: List<Conversion> = emptyList(),
    val inspections: List<Inspection> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedStatus: String = "All",
    val errorMessage: String? = null
)

class InspectionDashboardViewModel(
    private val repository: InspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Load both concurrently
            val conversionsResult = repository.getScheduledConversions()
            val inspectionsResult = repository.getInspections()
            
            _uiState.value = _uiState.value.copy(isLoading = false)
            
            conversionsResult.fold(
                onSuccess = { conversions ->
                    _uiState.value = _uiState.value.copy(scheduledConversions = conversions)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
            )
            
            inspectionsResult.fold(
                onSuccess = { inspections ->
                    _uiState.value = _uiState.value.copy(inspections = inspections)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
            )
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun selectStatus(status: String) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }
}
