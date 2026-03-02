package com.example.mytechnician.ui.inspections

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.data.model.Client
import com.example.mytechnician.data.model.InspectionVehicle

private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val PrimaryGreen = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionSetupScreen(
    viewModel: InspectionSetupViewModel,
    stationId: Int,
    preSelectedConversion: com.example.mytechnician.data.model.Conversion? = null,
    onBackClick: () -> Unit,
    onInspectionStarted: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showClientPicker by remember { mutableStateOf(false) }
    var showVehiclePicker by remember { mutableStateOf(false) }

    LaunchedEffect(preSelectedConversion) {
        preSelectedConversion?.let { viewModel.setPreSelectedConversion(it) }
    }

    LaunchedEffect(uiState.inspectionStartedId) {
        uiState.inspectionStartedId?.let { onInspectionStarted(it) }
    }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("New Inspection", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Complete Setup Tasks",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            uiState.errorMessage?.let { error ->
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }

            // Setup Cards
            SetupItemCard(
                title = "Select Client",
                subtitle = uiState.selectedClient?.name ?: "Tap to choose a client",
                icon = Icons.Default.Person,
                color = Color(0xFF10B981), // Emerald
                isCompleted = uiState.selectedClient != null,
                onClick = { showClientPicker = true }
            )

            SetupItemCard(
                title = "Select Vehicle",
                subtitle = uiState.selectedVehicle?.registration_number ?: "Tap to choose a vehicle",
                icon = Icons.Default.DirectionsCar,
                color = Color(0xFF06B6D4), // Cyan
                isCompleted = uiState.selectedVehicle != null,
                isEnabled = uiState.selectedClient != null,
                onClick = { showVehiclePicker = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.startInspection(stationId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = (uiState.selectedVehicle != null || uiState.preSelectedConversion != null) && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Proceed to Checklist", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Client Picker Bottom Sheet
        if (showClientPicker) {
            ModalBottomSheet(
                onDismissRequest = { showClientPicker = false },
                containerColor = Slate800
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text("Choose Client", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    items(uiState.clients) { client ->
                        ListItem(
                            headlineContent = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(client.name, color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    SourceBadge(client.source ?: "account")
                                }
                            },
                            supportingContent = { Text(client.contact, color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier.clickable {
                                viewModel.selectClient(client)
                                showClientPicker = false
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }

        // Vehicle Picker Bottom Sheet
        if (showVehiclePicker) {
            ModalBottomSheet(
                onDismissRequest = { showVehiclePicker = false },
                containerColor = Slate800
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text("Choose Vehicle", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    items(uiState.filteredVehicles) { vehicle ->
                        ListItem(
                            headlineContent = { Text(vehicle.registration_number, color = Color.White) },
                            supportingContent = { Text("${vehicle.make} ${vehicle.model}", color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier.clickable {
                                viewModel.selectVehicle(vehicle)
                                showVehiclePicker = false
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SetupItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isCompleted: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) color.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)
        ),
        border = if (isEnabled) androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = if (isEnabled) 0.6f else 0.3f), fontSize = 14.sp)
            }
            if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color)
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun SourceBadge(source: String) {
    val (color, label) = if (source == "conversion") {
        PrimaryGreen to "CONVERSION"
    } else {
        Color(0xFF3B82F6) to "ACCOUNT" // Blue
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            label,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
