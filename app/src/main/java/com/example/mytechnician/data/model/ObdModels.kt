package com.example.mytechnician.data.model

import kotlinx.serialization.Serializable

enum class ConnectionStatus {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    ERROR
}

enum class DtcSystem {
    POWERTRAIN,    // P codes
    CHASSIS,       // C codes
    BODY,          // B codes
    NETWORK        // U codes
}

@Serializable
data class ObdDevice(
    val name: String,
    val address: String,
    val isConnected: Boolean = false
)

@Serializable
data class DiagnosticCode(
    val code: String,              // e.g., "P0420"
    val description: String,       // e.g., "Catalyst System Efficiency Below Threshold"
    val system: DtcSystem,
    val freezeFrame: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class LiveDataParameter(
    val name: String,              // e.g., "Engine RPM"
    val value: String,             // e.g., "2500"
    val unit: String,              // e.g., "rpm"
    val pid: String                // e.g., "010C"
)

@Serializable
data class ObdScanResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val vehicleVin: String? = null,
    val diagnosticCodes: List<DiagnosticCode>,
    val liveData: List<LiveDataParameter>? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val inspectionId: Int? = null
)

// Common OBD-II PIDs (Parameter IDs)
object ObdPids {
    const val ENGINE_RPM = "010C"
    const val VEHICLE_SPEED = "010D"
    const val COOLANT_TEMP = "0105"
    const val ENGINE_LOAD = "0104"
    const val THROTTLE_POSITION = "0111"
    const val FUEL_PRESSURE = "010A"
    const val INTAKE_TEMP = "010F"
    const val MAF_FLOW = "0110"
}

// DTC code descriptions (common codes)
object DtcDescriptions {
    private val descriptions = mapOf(
        "P0420" to "Catalyst System Efficiency Below Threshold (Bank 1)",
        "P0171" to "System Too Lean (Bank 1)",
        "P0172" to "System Too Rich (Bank 1)",
        "P0300" to "Random/Multiple Cylinder Misfire Detected",
        "P0301" to "Cylinder 1 Misfire Detected",
        "P0302" to "Cylinder 2 Misfire Detected",
        "P0303" to "Cylinder 3 Misfire Detected",
        "P0304" to "Cylinder 4 Misfire Detected",
        "P0401" to "Exhaust Gas Recirculation Flow Insufficient",
        "P0440" to "Evaporative Emission Control System Malfunction",
        "P0442" to "Evaporative Emission Control System Leak Detected (Small Leak)",
        "P0455" to "Evaporative Emission Control System Leak Detected (Large Leak)",
        "P0500" to "Vehicle Speed Sensor Malfunction",
        "P0505" to "Idle Control System Malfunction",
        "P0700" to "Transmission Control System Malfunction",
        "P0715" to "Input/Turbine Speed Sensor Circuit Malfunction",
        "P0720" to "Output Speed Sensor Circuit Malfunction",
        "P0741" to "Torque Converter Clutch Circuit Performance or Stuck Off"
    )
    
    fun getDescription(code: String): String {
        return descriptions[code] ?: "Unknown diagnostic trouble code"
    }
    
    fun getSystem(code: String): DtcSystem {
        return when (code.firstOrNull()) {
            'P' -> DtcSystem.POWERTRAIN
            'C' -> DtcSystem.CHASSIS
            'B' -> DtcSystem.BODY
            'U' -> DtcSystem.NETWORK
            else -> DtcSystem.POWERTRAIN
        }
    }
}
