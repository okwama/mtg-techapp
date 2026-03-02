package com.example.mytechnician.ui.inspections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.*
import com.example.mytechnician.data.repository.InspectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SetupUiState(
    val isLoading: Boolean = false,
    val clients: List<Client> = emptyList(),
    val filteredVehicles: List<InspectionVehicle> = emptyList(),
    val selectedClient: Client? = null,
    val selectedVehicle: InspectionVehicle? = null,
    val preSelectedConversion: Conversion? = null,
    val errorMessage: String? = null,
    val inspectionStartedId: Int? = null
)

class InspectionSetupViewModel(
    private val repository: InspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getClients().fold(
                onSuccess = { clients ->
                    _uiState.value = _uiState.value.copy(isLoading = false, clients = clients)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    fun setPreSelectedConversion(conversion: Conversion) {
        _uiState.value = _uiState.value.copy(
            preSelectedConversion = conversion,
            selectedClient = Client(conversion.id, conversion.owner_full_name, "", ""),
            selectedVehicle = InspectionVehicle(0, conversion.vehicle_registration, conversion.make, conversion.model)
        )
    }

    fun selectClient(client: Client) {
        _uiState.value = _uiState.value.copy(
            selectedClient = client, 
            selectedVehicle = null, 
            filteredVehicles = emptyList(),
            preSelectedConversion = null // Clear pre-selection if user manually picks
        )
        loadVehicles(client.id, client.source)
    }

    private fun loadVehicles(clientId: Int, source: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getVehicles(clientId, source).fold(
                onSuccess = { vehicles ->
                    _uiState.value = _uiState.value.copy(isLoading = false, filteredVehicles = vehicles)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    fun selectVehicle(vehicle: InspectionVehicle) {
        _uiState.value = _uiState.value.copy(selectedVehicle = vehicle, preSelectedConversion = null)
    }

    fun startInspection(stationId: Int) {
        val uiState = _uiState.value
        val vehicleId = uiState.selectedVehicle?.id.takeIf { it != 0 && uiState.selectedVehicle?.source != "conversion" }
        // If vehicle is from conversion, its ID is the conversion ID
        val conversionId = uiState.preSelectedConversion?.id ?: uiState.selectedVehicle?.id.takeIf { uiState.selectedVehicle?.source == "conversion" }
        
        if (vehicleId == null && conversionId == null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.startInspection(vehicleId, stationId, conversionId).fold(
                onSuccess = { inspectionId ->
                    _uiState.value = _uiState.value.copy(isLoading = false, inspectionStartedId = inspectionId)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }
}
