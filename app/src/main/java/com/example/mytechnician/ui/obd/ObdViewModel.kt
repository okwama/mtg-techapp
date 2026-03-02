package com.example.mytechnician.ui.obd

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytechnician.data.model.*
import com.example.mytechnician.util.ObdService
import kotlinx.coroutines.launch

sealed class ObdUiState {
    object Idle : ObdUiState()
    object Loading : ObdUiState()
    data class Success(val message: String) : ObdUiState()
    data class Error(val message: String) : ObdUiState()
}

class ObdViewModel(private val obdService: ObdService) : ViewModel() {
    
    private val _uiState = mutableStateOf<ObdUiState>(ObdUiState.Idle)
    val uiState: State<ObdUiState> = _uiState
    
    private val _devices = mutableStateOf<List<ObdDevice>>(emptyList())
    val devices: State<List<ObdDevice>> = _devices
    
    private val _diagnosticCodes = mutableStateOf<List<DiagnosticCode>>(emptyList())
    val diagnosticCodes: State<List<DiagnosticCode>> = _diagnosticCodes
    
    private val _liveData = mutableStateOf<Map<String, LiveDataParameter>>(emptyMap())
    val liveData: State<Map<String, LiveDataParameter>> = _liveData
    
    private val _scanHistory = mutableStateOf<List<ObdScanResult>>(emptyList())
    val scanHistory: State<List<ObdScanResult>> = _scanHistory
    
    val connectionStatus = obdService.connectionStatus
    val connectedDevice = obdService.connectedDevice
    
    fun scanForDevices() {
        _uiState.value = ObdUiState.Loading
        viewModelScope.launch {
            val devices = obdService.scanForDevices()
            _devices.value = devices
            _uiState.value = if (devices.isEmpty()) {
                ObdUiState.Error("No OBD devices found. Make sure your adapter is paired.")
            } else {
                ObdUiState.Success("Found ${devices.size} device(s)")
            }
        }
    }
    
    fun connectToDevice(device: ObdDevice) {
        _uiState.value = ObdUiState.Loading
        viewModelScope.launch {
            obdService.connect(device).onSuccess { _: Unit ->
                _uiState.value = ObdUiState.Success("Connected to ${device.name}")
            }.onFailure { error: Throwable ->
                _uiState.value = ObdUiState.Error(error.message ?: "Connection failed")
            }
        }
    }
    
    fun disconnect() {
        viewModelScope.launch {
            obdService.disconnect()
            _diagnosticCodes.value = emptyList()
            _liveData.value = emptyMap()
            _uiState.value = ObdUiState.Idle
        }
    }
    
    fun readDiagnosticCodes() {
        _uiState.value = ObdUiState.Loading
        viewModelScope.launch {
            obdService.readDiagnosticCodes().onSuccess { codes: List<DiagnosticCode> ->
                _diagnosticCodes.value = codes
                _uiState.value = if (codes.isEmpty()) {
                    ObdUiState.Success("No diagnostic codes found")
                } else {
                    ObdUiState.Success("Found ${codes.size} diagnostic code(s)")
                }
            }.onFailure { error: Throwable ->
                _uiState.value = ObdUiState.Error(error.message ?: "Failed to read codes")
            }
        }
    }
    
    fun clearDiagnosticCodes() {
        _uiState.value = ObdUiState.Loading
        viewModelScope.launch {
            obdService.clearDiagnosticCodes().onSuccess { _: Unit ->
                _diagnosticCodes.value = emptyList()
                _uiState.value = ObdUiState.Success("Diagnostic codes cleared successfully")
            }.onFailure { error: Throwable ->
                _uiState.value = ObdUiState.Error(error.message ?: "Failed to clear codes")
            }
        }
    }
    
    fun startLiveDataMonitoring() {
        viewModelScope.launch {
            // Read common PIDs periodically
            val pids = listOf(
                ObdPids.ENGINE_RPM,
                ObdPids.VEHICLE_SPEED,
                ObdPids.COOLANT_TEMP,
                ObdPids.ENGINE_LOAD,
                ObdPids.THROTTLE_POSITION
            )
            
            pids.forEach { pid ->
                launch {
                    obdService.readLiveData(pid).onSuccess { parameter: LiveDataParameter ->
                        val currentData = _liveData.value.toMutableMap()
                        currentData[pid] = parameter
                        _liveData.value = currentData
                    }
                }
            }
        }
    }
    
    fun stopLiveDataMonitoring() {
        _liveData.value = emptyMap()
    }
    
    fun saveScanResult() {
        val scanResult = ObdScanResult(
            diagnosticCodes = _diagnosticCodes.value,
            liveData = _liveData.value.values.toList()
        )
        
        val currentHistory = _scanHistory.value.toMutableList()
        currentHistory.add(0, scanResult)
        _scanHistory.value = currentHistory
        
        _uiState.value = ObdUiState.Success("Scan result saved")
    }
    
    fun resetState() {
        _uiState.value = ObdUiState.Idle
    }
}
