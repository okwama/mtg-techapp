package com.example.mytechnician.ui.checkin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.*
import com.example.mytechnician.data.repository.ShiftRepository
import com.example.mytechnician.util.LocationService
import kotlinx.coroutines.launch

sealed class CheckinState {
    object Idle : CheckinState()
    object Loading : CheckinState()
    data class Success(val message: String) : CheckinState()
    data class Error(val message: String) : CheckinState()
}

class CheckinViewModel(
    private val repository: ShiftRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _state = mutableStateOf<CheckinState>(CheckinState.Idle)
    val state: State<CheckinState> = _state

    private val _stations = mutableStateOf<List<Station>>(emptyList())
    val stations: State<List<Station>> = _stations

    private val _shiftStatus = mutableStateOf<ShiftStatusResponse?>(null)
    val shiftStatus: State<ShiftStatusResponse?> = _shiftStatus

    private val _locationAddress = mutableStateOf<String?>(null)
    val locationAddress: State<String?> = _locationAddress

    fun loadStations() {
        viewModelScope.launch {
            repository.getStations().onSuccess {
                _stations.value = it
            }.onFailure {
                // Silent failure or handle error
            }
        }
    }

    fun checkShiftStatus() {
        viewModelScope.launch {
            repository.getShiftStatus().onSuccess {
                _shiftStatus.value = it
            }
        }
    }

    fun checkin(stationId: Int) {
        _state.value = CheckinState.Loading
        viewModelScope.launch {
            val location = locationService.getCurrentLocation()
            if (location != null) {
                // Get address from coordinates
                val address = locationService.getAddressFromLocation(
                    location.latitude,
                    location.longitude
                )
                _locationAddress.value = address ?: "${location.latitude}, ${location.longitude}"
            }
            
            val request = CheckinRequest(stationId, location?.latitude, location?.longitude)
            repository.checkin(request).onSuccess {
                _state.value = CheckinState.Success("Checked in successfully")
                checkShiftStatus()
            }.onFailure {
                _state.value = CheckinState.Error(it.message ?: "Check-in failed")
            }
        }
    }

    fun checkout() {
        _state.value = CheckinState.Loading
        viewModelScope.launch {
            val location = locationService.getCurrentLocation()
            if (location != null) {
                // Get address from coordinates
                val address = locationService.getAddressFromLocation(
                    location.latitude,
                    location.longitude
                )
                _locationAddress.value = address ?: "${location.latitude}, ${location.longitude}"
            }
            
            val request = CheckoutRequest(location?.latitude, location?.longitude)
            repository.checkout(request).onSuccess {
                _state.value = CheckinState.Success("Checked out successfully")
                _shiftStatus.value = ShiftStatusResponse(false)
            }.onFailure {
                _state.value = CheckinState.Error(it.message ?: "Check-out failed")
            }
        }
    }

    fun resetState() {
        _state.value = CheckinState.Idle
        _locationAddress.value = null
    }
}
