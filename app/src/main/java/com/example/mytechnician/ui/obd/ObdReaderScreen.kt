package com.example.mytechnician.ui.obd

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mytechnician.data.model.*
import com.example.mytechnician.ui.theme.PrimaryGreen
import com.example.mytechnician.ui.theme.Slate700
import com.example.mytechnician.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObdReaderScreen(
    viewModel: ObdViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState
    val devices by viewModel.devices
    val diagnosticCodes by viewModel.diagnosticCodes
    val liveData by viewModel.liveData
    val scanHistory by viewModel.scanHistory
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showClearDialog by remember { mutableStateOf(false) }
    
    // Bluetooth permissions
    val bluetoothPermissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.scanForDevices()
        }
    }
    
    fun checkAndRequestPermissions(onGranted: () -> Unit) {
        val allGranted = bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        
        if (allGranted) {
            onGranted()
        } else {
            permissionLauncher.launch(bluetoothPermissions)
        }
    }
    
    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("OBD Reader", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.disconnect()
                        onBackClick()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Connection status indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (connectionStatus) {
                                        ConnectionStatus.CONNECTED -> PrimaryGreen
                                        ConnectionStatus.CONNECTING, ConnectionStatus.SCANNING -> Color.Yellow
                                        ConnectionStatus.ERROR -> Color.Red
                                        else -> Color.Gray
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (connectionStatus) {
                                ConnectionStatus.CONNECTED -> "Connected"
                                ConnectionStatus.CONNECTING -> "Connecting..."
                                ConnectionStatus.SCANNING -> "Scanning..."
                                ConnectionStatus.ERROR -> "Error"
                                else -> "Disconnected"
                            },
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate900,
                contentColor = PrimaryGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Connection") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Diagnostics") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Live Data") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("History") }
                )
            }
            
            // Tab content
            when (selectedTab) {
                0 -> ConnectionTab(
                    devices = devices,
                    connectedDevice = connectedDevice,
                    connectionStatus = connectionStatus,
                    onScanClick = { checkAndRequestPermissions { viewModel.scanForDevices() } },
                    onConnectClick = { viewModel.connectToDevice(it) },
                    onDisconnectClick = { viewModel.disconnect() }
                )
                1 -> DiagnosticsTab(
                    diagnosticCodes = diagnosticCodes,
                    connectionStatus = connectionStatus,
                    onScanClick = { viewModel.readDiagnosticCodes() },
                    onClearClick = { showClearDialog = true },
                    onSaveClick = { viewModel.saveScanResult() }
                )
                2 -> LiveDataTab(
                    liveData = liveData,
                    connectionStatus = connectionStatus,
                    onStartMonitoring = { viewModel.startLiveDataMonitoring() },
                    onStopMonitoring = { viewModel.stopLiveDataMonitoring() }
                )
                3 -> HistoryTab(scanHistory = scanHistory)
            }
            
            // Status messages
            when (uiState) {
                is ObdUiState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryGreen
                    )
                }
                is ObdUiState.Success -> {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = PrimaryGreen
                    ) {
                        Text((uiState as ObdUiState.Success).message)
                    }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.resetState()
                    }
                }
                is ObdUiState.Error -> {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = Color.Red
                    ) {
                        Text((uiState as ObdUiState.Error).message)
                    }
                }
                else -> {}
            }
        }
    }
    
    // Clear codes confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Diagnostic Codes?") },
            text = { Text("This will clear all diagnostic trouble codes from the vehicle's computer. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearDiagnosticCodes()
                    showClearDialog = false
                }) {
                    Text("Clear", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ConnectionTab(
    devices: List<ObdDevice>,
    connectedDevice: ObdDevice?,
    connectionStatus: ConnectionStatus,
    onScanClick: () -> Unit,
    onConnectClick: (ObdDevice) -> Unit,
    onDisconnectClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (connectedDevice != null) {
            // Connected device card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate700)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Connected Device", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(connectedDevice.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(connectedDevice.address, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDisconnectClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Disconnect")
                    }
                }
            }
        } else {
            // Scan button
            Button(
                onClick = onScanClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = connectionStatus != ConnectionStatus.SCANNING
            ) {
                Icon(Icons.Default.Search, "Scan")
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (connectionStatus == ConnectionStatus.SCANNING) "Scanning..." else "Scan for Devices")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Device list
            if (devices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Bluetooth,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No devices found",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "Tap 'Scan for Devices' to search",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(devices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onConnectClick(device) },
                            colors = CardDefaults.cardColors(containerColor = Slate700)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(device.name, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(device.address, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                                }
                                Icon(Icons.Default.ChevronRight, "Connect", tint = PrimaryGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticsTab(
    diagnosticCodes: List<DiagnosticCode>,
    connectionStatus: ConnectionStatus,
    onScanClick: () -> Unit,
    onClearClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (connectionStatus != ConnectionStatus.CONNECTED) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Connect to a device to scan for codes",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onScanClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Scan for Codes")
                }
                if (diagnosticCodes.isNotEmpty()) {
                    Button(
                        onClick = onClearClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Clear Codes")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (diagnosticCodes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No diagnostic codes found",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Vehicle systems are operating normally",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(diagnosticCodes) { code ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Slate700)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        code.code,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                    Text(
                                        code.system.name,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    code.description,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    item {
                        Button(
                            onClick = onSaveClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                        ) {
                            Icon(Icons.Default.Save, "Save")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Scan Result")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveDataTab(
    liveData: Map<String, LiveDataParameter>,
    connectionStatus: ConnectionStatus,
    onStartMonitoring: () -> Unit,
    onStopMonitoring: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (connectionStatus != ConnectionStatus.CONNECTED) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Connect to a device to view live data",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        } else {
            Button(
                onClick = if (liveData.isEmpty()) onStartMonitoring else onStopMonitoring,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveData.isEmpty()) PrimaryGreen else Color.Red
                )
            ) {
                Text(if (liveData.isEmpty()) "Start Monitoring" else "Stop Monitoring")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (liveData.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tap 'Start Monitoring' to view live data",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(liveData.values.toList()) { parameter ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Slate700)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(parameter.name, color = Color.White)
                                Text(
                                    "${parameter.value} ${parameter.unit}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(scanHistory: List<ObdScanResult>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (scanHistory.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No scan history",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scanHistory) { scan ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate700)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                    .format(Date(scan.timestamp)),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "${scan.diagnosticCodes.size} diagnostic code(s)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (scan.diagnosticCodes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    scan.diagnosticCodes.joinToString(", ") { it.code },
                                    fontSize = 12.sp,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
