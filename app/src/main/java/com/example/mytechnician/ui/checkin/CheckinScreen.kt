package com.example.mytechnician.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.ui.theme.PrimaryGreen
import com.example.mytechnician.ui.theme.Slate700
import com.example.mytechnician.ui.theme.Slate800
import com.example.mytechnician.ui.theme.Slate900

import androidx.compose.ui.draw.alpha
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinScreen(
    viewModel: CheckinViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state
    val stations by viewModel.stations
    val shiftStatus by viewModel.shiftStatus
    val locationAddress by viewModel.locationAddress
    val context = LocalContext.current

    // Permission handling logic
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    // Reload data when screen is opened
    LaunchedEffect(Unit) {
        viewModel.loadStations()
        viewModel.checkShiftStatus()
    }

    fun checkAndRequestPermissions(onGranted: () -> Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        if (hasFine || hasCoarse) {
            onGranted()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    CheckinScreenContent(
        state = state,
        stations = stations,
        shiftStatus = shiftStatus,
        locationAddress = locationAddress,
        onBackClick = onBackClick,
        onCheckin = { stationId ->
            checkAndRequestPermissions { viewModel.checkin(stationId) }
        },
        onCheckout = {
            checkAndRequestPermissions { viewModel.checkout() }
        },
        onResetState = { viewModel.resetState() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinScreenContent(
    state: CheckinState,
    stations: List<com.example.mytechnician.data.model.Station>,
    shiftStatus: com.example.mytechnician.data.model.ShiftStatusResponse?,
    locationAddress: String?,
    onBackClick: () -> Unit,
    onCheckin: (Int) -> Unit,
    onCheckout: () -> Unit,
    onResetState: () -> Unit
) {
    var selectedStationId by remember { mutableIntStateOf(-1) }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("Station Check-in", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (shiftStatus?.active == true) {
                ShiftActiveContent(
                    stationName = shiftStatus.shift?.station_name ?: "Unknown Station",
                    checkinTime = shiftStatus.shift?.checkInTime ?: "",
                    onCheckout = onCheckout
                )
            } else {
                CheckinForm(
                    stations = stations,
                    selectedStationId = selectedStationId,
                    onStationSelect = { selectedStationId = it },
                    onCheckin = {
                        if (selectedStationId != -1) {
                            onCheckin(selectedStationId)
                        }
                    }
                )
            }

            // Success/Error Feedback
            when (state) {
                is CheckinState.Loading -> CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.padding(16.dp))
                is CheckinState.Success -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                        locationAddress?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Location: $it",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(3000)
                        onResetState()
                    }
                }
                is CheckinState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                else -> {}
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewCheckinForm() {
    val mockStations = listOf(
        com.example.mytechnician.data.model.Station(1, "Bay 01", "Workshop A", "0.0", "0.0"),
        com.example.mytechnician.data.model.Station(2, "Bay 02", "Workshop A", "0.0", "0.0")
    )
    MaterialTheme {
        Box(modifier = Modifier
            .background(Slate900)
            .padding(20.dp)) {
            CheckinScreenContent(
                state = CheckinState.Idle,
                stations = mockStations,
                shiftStatus = com.example.mytechnician.data.model.ShiftStatusResponse(false),
                locationAddress = null,
                onBackClick = {},
                onCheckin = {},
                onCheckout = {},
                onResetState = {}
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewShiftActive() {
    MaterialTheme {
        Box(modifier = Modifier
            .background(Slate900)
            .padding(20.dp)) {
            CheckinScreenContent(
                state = CheckinState.Idle,
                stations = emptyList(),
                shiftStatus = com.example.mytechnician.data.model.ShiftStatusResponse(
                    active = true,
                    shift = com.example.mytechnician.data.model.Shift(
                        id = 1,
                        userId = 1,
                        userName = "John Doe",
                        station_id = 1,
                        station_name = "Bay 01",
                        status = 1,
                        checkInTime = "2024-02-12 08:00:00"
                    )
                ),
                locationAddress = null,
                onBackClick = {},
                onCheckin = {},
                onCheckout = {},
                onResetState = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinForm(
    stations: List<com.example.mytechnician.data.model.Station>,
    selectedStationId: Int,
    onStationSelect: (Int) -> Unit,
    onCheckin: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedStation = stations.find { it.id == selectedStationId }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Manual Check-in",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Select your current workshop station from the list below to start your shift.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Material 3 Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedStation?.name ?: "Select Station",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = Slate700.copy(alpha = 0.3f),
                        unfocusedContainerColor = Slate700.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Slate800)
                ) {
                    stations.forEach { station ->
                        DropdownMenuItem(
                            text = { Text(station.name, color = Color.White) },
                            onClick = {
                                onStationSelect(station.id)
                                expanded = false
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onCheckin,
                enabled = selectedStationId != -1,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    disabledContainerColor = Slate700
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm Check-In", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(0.6f)
            ) {
                Icon(
                    Icons.Default.Info, 
                    contentDescription = null, 
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "GPS coordinates will be captured for audit.",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ShiftActiveContent(
    stationName: String,
    checkinTime: String,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(PrimaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Currently Checked In",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                stationName,
                fontSize = 16.sp,
                color = PrimaryGreen,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Check-in Time", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                    Text(checkinTime.split(" ").getOrNull(1) ?: checkinTime, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Status", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                    Text("ACTIVE", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finish Shift (Check-out)")
            }
        }
    }
}
