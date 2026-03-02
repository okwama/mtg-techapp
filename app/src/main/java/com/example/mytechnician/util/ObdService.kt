package com.example.mytechnician.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.example.mytechnician.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class ObdService(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    
    private val _connectedDevice = MutableStateFlow<ObdDevice?>(null)
    val connectedDevice: StateFlow<ObdDevice?> = _connectedDevice
    
    // Track discovered devices during scan
    private val discoveredDevices = mutableSetOf<ObdDevice>()
    
    // BroadcastReceiver for device discovery
    private val discoveryReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: android.content.Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = 
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    
                    device?.let {
                        if (isObdDevice(it.name)) {
                            discoveredDevices.add(
                                ObdDevice(
                                    name = it.name ?: "Unknown Device",
                                    address = it.address,
                                    isConnected = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Standard SPP UUID for Bluetooth serial communication
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    
    @SuppressLint("MissingPermission")
    suspend fun scanForDevices(): List<ObdDevice> = withContext(Dispatchers.IO) {
        _connectionStatus.value = ConnectionStatus.SCANNING
        
        try {
            // Clear previous discoveries
            discoveredDevices.clear()
            
            // First, get all paired devices
            val pairedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()
            pairedDevices
                .filter { isObdDevice(it.name) }
                .forEach { device ->
                    discoveredDevices.add(
                        ObdDevice(
                            name = device.name ?: "Unknown Device",
                            address = device.address,
                            isConnected = false
                        )
                    )
                }
            
            // Then, start discovery for unpaired devices
            bluetoothAdapter?.let { adapter ->
                // Register the BroadcastReceiver
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                context.registerReceiver(discoveryReceiver, filter)
                
                try {
                    if (adapter.isDiscovering) {
                        adapter.cancelDiscovery()
                    }
                    
                    // Start discovery
                    val discoveryStarted = adapter.startDiscovery()
                    
                    if (discoveryStarted) {
                        // Wait for discovery to find devices (5 seconds should be enough)
                        delay(5000)
                        
                        adapter.cancelDiscovery()
                    }
                } finally {
                    // Unregister the receiver
                    try {
                        context.unregisterReceiver(discoveryReceiver)
                    } catch (e: Exception) {
                        // Receiver might not be registered
                    }
                }
            }
            
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            discoveredDevices.toList()
        } catch (e: Exception) {
            _connectionStatus.value = ConnectionStatus.ERROR
            emptyList()
        }
    }
    
    private fun isObdDevice(name: String?): Boolean {
        if (name == null) return false
        return name.contains("OBD", ignoreCase = true) ||
               name.contains("ELM", ignoreCase = true) ||
               name.contains("OBDII", ignoreCase = true) ||
               name.contains("CHX", ignoreCase = true) ||
               name.contains("Vgate", ignoreCase = true) ||
               name.contains("Veepeak", ignoreCase = true)
    }
    
    @SuppressLint("MissingPermission")
    suspend fun connect(device: ObdDevice): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _connectionStatus.value = ConnectionStatus.CONNECTING
            
            val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)
                ?: return@withContext Result.failure(Exception("Bluetooth device not found"))
            
            // Create socket and connect
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothSocket?.connect()
            
            inputStream = bluetoothSocket?.inputStream
            outputStream = bluetoothSocket?.outputStream
            
            // Initialize ELM327
            val initSuccess = initializeElm327()
            if (!initSuccess) {
                disconnect()
                return@withContext Result.failure(Exception("Failed to initialize OBD adapter"))
            }
            
            _connectionStatus.value = ConnectionStatus.CONNECTED
            _connectedDevice.value = device.copy(isConnected = true)
            
            Result.success(Unit)
        } catch (e: IOException) {
            _connectionStatus.value = ConnectionStatus.ERROR
            disconnect()
            Result.failure(Exception("Connection failed: ${e.message}"))
        }
    }
    
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            // Ignore
        } finally {
            inputStream = null
            outputStream = null
            bluetoothSocket = null
            _connectionStatus.value = ConnectionStatus.DISCONNECTED
            _connectedDevice.value = null
        }
    }
    
    private suspend fun initializeElm327(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Reset device
            sendCommand("ATZ")
            delay(1000)
            
            // Turn off echo
            sendCommand("ATE0")
            
            // Set protocol to automatic
            sendCommand("ATSP0")
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun readDiagnosticCodes(): Result<List<DiagnosticCode>> = withContext(Dispatchers.IO) {
        if (_connectionStatus.value != ConnectionStatus.CONNECTED) {
            return@withContext Result.failure(Exception("Not connected to OBD device"))
        }
        
        try {
            // Request stored DTCs (Mode 03)
            val response = sendCommand("03")
            val codes = parseDtcResponse(response)
            
            Result.success(codes)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to read codes: ${e.message}"))
        }
    }
    
    suspend fun clearDiagnosticCodes(): Result<Unit> = withContext(Dispatchers.IO) {
        if (_connectionStatus.value != ConnectionStatus.CONNECTED) {
            return@withContext Result.failure(Exception("Not connected to OBD device"))
        }
        
        try {
            // Clear DTCs (Mode 04)
            sendCommand("04")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to clear codes: ${e.message}"))
        }
    }
    
    suspend fun readLiveData(pid: String): Result<LiveDataParameter> = withContext(Dispatchers.IO) {
        if (_connectionStatus.value != ConnectionStatus.CONNECTED) {
            return@withContext Result.failure(Exception("Not connected to OBD device"))
        }
        
        try {
            val response = sendCommand(pid)
            val parameter = parseLiveDataResponse(pid, response)
            
            Result.success(parameter)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to read live data: ${e.message}"))
        }
    }
    
    private suspend fun sendCommand(command: String): String = withContext(Dispatchers.IO) {
        val output = outputStream ?: throw IOException("Output stream not available")
        val input = inputStream ?: throw IOException("Input stream not available")
        
        // Send command
        output.write("$command\r".toByteArray())
        output.flush()
        
        // Read response
        delay(100) // Wait for response
        val buffer = ByteArray(1024)
        val bytes = input.read(buffer)
        
        String(buffer, 0, bytes).trim()
    }
    
    private fun parseDtcResponse(response: String): List<DiagnosticCode> {
        val codes = mutableListOf<DiagnosticCode>()
        
        // Remove spaces and split by lines
        val cleanResponse = response.replace(" ", "").replace(">", "")
        
        // Parse hex codes (format: 43 01 33 -> P0133)
        val hexPattern = Regex("[0-9A-F]{4}")
        val matches = hexPattern.findAll(cleanResponse)
        
        matches.forEach { match ->
            val hexCode = match.value
            val dtcCode = hexToDtc(hexCode)
            
            codes.add(
                DiagnosticCode(
                    code = dtcCode,
                    description = DtcDescriptions.getDescription(dtcCode),
                    system = DtcDescriptions.getSystem(dtcCode)
                )
            )
        }
        
        return codes
    }
    
    private fun hexToDtc(hex: String): String {
        if (hex.length != 4) return "UNKNOWN"
        
        val firstChar = hex[0].digitToInt(16)
        val prefix = when (firstChar shr 2) {
            0 -> "P0"
            1 -> "P1"
            2 -> "P2"
            3 -> "P3"
            4 -> "C0"
            5 -> "C1"
            6 -> "C2"
            7 -> "C3"
            8 -> "B0"
            9 -> "B1"
            10 -> "B2"
            11 -> "B3"
            12 -> "U0"
            13 -> "U1"
            14 -> "U2"
            15 -> "U3"
            else -> "P0"
        }
        
        val suffix = hex.substring(1)
        return "$prefix$suffix"
    }
    
    private fun parseLiveDataResponse(pid: String, response: String): LiveDataParameter {
        val cleanResponse = response.replace(" ", "").replace(">", "")
        
        return when (pid) {
            ObdPids.ENGINE_RPM -> {
                // Format: 41 0C XX XX
                val bytes = cleanResponse.substring(4, 8)
                val a = bytes.substring(0, 2).toInt(16)
                val b = bytes.substring(2, 4).toInt(16)
                val rpm = ((a * 256) + b) / 4
                
                LiveDataParameter("Engine RPM", rpm.toString(), "rpm", pid)
            }
            ObdPids.VEHICLE_SPEED -> {
                // Format: 41 0D XX
                val speed = cleanResponse.substring(4, 6).toInt(16)
                LiveDataParameter("Vehicle Speed", speed.toString(), "km/h", pid)
            }
            ObdPids.COOLANT_TEMP -> {
                // Format: 41 05 XX
                val temp = cleanResponse.substring(4, 6).toInt(16) - 40
                LiveDataParameter("Coolant Temperature", temp.toString(), "°C", pid)
            }
            ObdPids.ENGINE_LOAD -> {
                // Format: 41 04 XX
                val load = (cleanResponse.substring(4, 6).toInt(16) * 100) / 255
                LiveDataParameter("Engine Load", load.toString(), "%", pid)
            }
            ObdPids.THROTTLE_POSITION -> {
                // Format: 41 11 XX
                val throttle = (cleanResponse.substring(4, 6).toInt(16) * 100) / 255
                LiveDataParameter("Throttle Position", throttle.toString(), "%", pid)
            }
            else -> LiveDataParameter("Unknown", "N/A", "", pid)
        }
    }
}
