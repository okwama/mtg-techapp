package com.example.mytechnician.ui.inspections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.*
import com.example.mytechnician.data.repository.InspectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChecklistUiState(
    val isLoading: Boolean = false,
    val inspection: Inspection? = null,
    val categories: List<InspectionCategory> = emptyList(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSubmitted: Boolean = false
)

class ChecklistViewModel(
    private val repository: InspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChecklistUiState())
    val uiState: StateFlow<ChecklistUiState> = _uiState.asStateFlow()

    fun loadInspection(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getInspectionDetail(id).fold(
                onSuccess = { inspection ->
                    val categories = inspection.checklist_data ?: getDefaultTemplate()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        inspection = inspection,
                        categories = categories
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    fun updateItemCondition(categoryName: String, itemId: String, condition: String) {
        val currentCategories = _uiState.value.categories.toMutableList()
        val categoryIndex = currentCategories.indexOfFirst { it.name == categoryName }
        if (categoryIndex != -1) {
            val category = currentCategories[categoryIndex]
            val items = category.items.toMutableList()
            val itemIndex = items.indexOfFirst { it.id == itemId }
            if (itemIndex != -1) {
                items[itemIndex] = items[itemIndex].copy(condition = condition)
                currentCategories[categoryIndex] = category.copy(items = items)
                _uiState.value = _uiState.value.copy(categories = currentCategories)
            }
        }
    }

    fun saveDraft() {
        val inspection = _uiState.value.inspection ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            repository.updateInspection(
                inspection.id,
                _uiState.value.categories,
                inspection.summary ?: "",
                inspection.overall_condition ?: "good",
                "in-progress"
            ).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isSaving = false) },
                onFailure = { error -> _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = error.message) }
            )
        }
    }

    fun submitInspection() {
        val inspection = _uiState.value.inspection ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            // First save the final state
            val updateResult = repository.updateInspection(
                inspection.id,
                _uiState.value.categories,
                inspection.summary ?: "",
                inspection.overall_condition ?: "good",
                "completed"
            )
            
            if (updateResult.isSuccess) {
                // Then submit
                repository.submitInspection(inspection.id).fold(
                    onSuccess = { _uiState.value = _uiState.value.copy(isSaving = false, isSubmitted = true) },
                    onFailure = { error -> _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = error.message) }
                )
            } else {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = updateResult.exceptionOrNull()?.message)
            }
        }
    }

    fun updateSummary(summary: String) {
        _uiState.value = _uiState.value.copy(
            inspection = _uiState.value.inspection?.copy(summary = summary)
        )
    }

    fun updateOverallCondition(condition: String) {
        _uiState.value = _uiState.value.copy(
            inspection = _uiState.value.inspection?.copy(overall_condition = condition)
        )
    }

    private fun getDefaultTemplate(): List<InspectionCategory> {
        return listOf(
            InspectionCategory("Test Drive", listOf(
                ChecklistItem("td_1", "Engine Performance"),
                ChecklistItem("td_2", "Road Handling"),
                ChecklistItem("td_3", "Braking"),
                ChecklistItem("td_4", "Steering/Alignment"),
                ChecklistItem("td_5", "Transmission Shifting")
            )),
            InspectionCategory("Exterior Inspection", listOf(
                ChecklistItem("ext_1", "Paint Finish"),
                ChecklistItem("ext_2", "Body Damage"),
                ChecklistItem("ext_3", "Rust"),
                ChecklistItem("ext_4", "Windshield/Glass"),
                ChecklistItem("ext_5", "Headlights/Turn Signals")
            )),
            InspectionCategory("Electrical System", listOf(
                ChecklistItem("elec_1", "Battery"),
                ChecklistItem("elec_2", "Instrument Gauges"),
                ChecklistItem("elec_3", "Air Conditioning"),
                ChecklistItem("elec_4", "Heater Operation"),
                ChecklistItem("elec_5", "Wiper System")
            )),
            InspectionCategory("Under The Hood", listOf(
                ChecklistItem("uth_1", "Fluid Levels"),
                ChecklistItem("uth_2", "Hoses"),
                ChecklistItem("uth_3", "Belts"),
                ChecklistItem("uth_4", "Air Filter"),
                ChecklistItem("uth_5", "Radiator")
            ))
        )
    }
}
