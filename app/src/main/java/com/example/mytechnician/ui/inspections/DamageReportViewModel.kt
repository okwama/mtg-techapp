package com.example.mytechnician.ui.inspections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.Inspection
import com.example.mytechnician.data.repository.InspectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class DamageReportUiState(
    val isLoading: Boolean = false,
    val inspection: Inspection? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false
)

class DamageReportViewModel(
    private val repository: InspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DamageReportUiState())
    val uiState: StateFlow<DamageReportUiState> = _uiState.asStateFlow()

    fun loadInspection(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isCompleted = false, errorMessage = null)
            repository.getInspectionDetail(id).fold(
                onSuccess = { inspection ->
                    _uiState.value = _uiState.value.copy(isLoading = false, inspection = inspection)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    fun uploadPhotos(id: Int, photos: List<File>, type: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true)
            var successCount = 0
            photos.forEachIndexed { index, file ->
                repository.uploadPhoto(id, file, type).onSuccess {
                    successCount++
                    _uiState.value = _uiState.value.copy(uploadProgress = (index + 1).toFloat() / photos.size)
                }
            }
            
            // Re-load inspection to get updated photo list
            loadInspection(id)
            _uiState.value = _uiState.value.copy(isUploading = false, uploadProgress = 0f)
        }
    }

    fun finishInspection() {
        val inspectionId = _uiState.value.inspection?.id ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true) // Use isUploading as a temporary loading state
            repository.submitInspection(inspectionId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isUploading = false, isCompleted = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isUploading = false, errorMessage = error.message)
                }
            )
        }
    }
}
