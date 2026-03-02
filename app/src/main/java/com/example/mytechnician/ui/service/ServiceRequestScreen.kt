package com.example.mytechnician.ui.service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.ui.theme.PrimaryGreen
import com.example.mytechnician.ui.theme.Slate900
import com.example.mytechnician.ui.theme.Slate800
import com.example.mytechnician.ui.inspections.SourceBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestScreen(
    viewModel: ServiceViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState
    val clients by viewModel.clients
    val vehicles by viewModel.vehicles
    
    var selectedClient by remember { mutableStateOf<com.example.mytechnician.data.model.Client?>(null) }
    var selectedVehicle by remember { mutableStateOf<com.example.mytechnician.data.model.InspectionVehicle?>(null) }
    var description by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("General Service") }
    
    var showClientPicker by remember { mutableStateOf(false) }
    var showVehiclePicker by remember { mutableStateOf(false) }
    var showServicePicker by remember { mutableStateOf(false) }
    
    val serviceTypes = listOf("General Service", "Brake Check", "Oil Change", "Engine Tune-up", "AC Repair", "Other")

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("New Service Request", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState is ServiceUiState.Success) {
                Surface(
                    color = PrimaryGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text((uiState as ServiceUiState.Success).message, color = Color.White)
                    }
                }
                
                Button(
                    onClick = { 
                        viewModel.resetState()
                        onBackClick()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Go to Dashboard")
                }
            } else {
                Text(
                    "Assign a service to a vehicle",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                // Client Selection
                SelectionCard(
                    title = "Client",
                    value = selectedClient?.name ?: "Select Client",
                    icon = Icons.Default.Person,
                    onClick = { showClientPicker = true }
                )

                // Vehicle Selection
                SelectionCard(
                    title = "Vehicle",
                    value = selectedVehicle?.registration_number ?: "Select Vehicle",
                    icon = Icons.Default.DirectionsCar,
                    enabled = selectedClient != null,
                    onClick = { 
                        viewModel.loadVehicles(selectedClient!!.id)
                        showVehiclePicker = true 
                    }
                )

                // Service Type
                SelectionCard(
                    title = "Service Type",
                    value = serviceType,
                    icon = Icons.Default.Build,
                    onClick = { showServicePicker = true }
                )

                // Description
                Text(
                    "Service Details",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Describe the issues or work required...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedIndicatorColor = PrimaryGreen,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (uiState is ServiceUiState.Error) {
                    Text((uiState as ServiceUiState.Error).message, color = Color.Red, fontSize = 14.sp)
                }

                Button(
                    onClick = { 
                        if (selectedClient != null && selectedVehicle != null) {
                            viewModel.submitServiceRequest(
                                selectedClient!!.id,
                                selectedVehicle!!.id,
                                description,
                                serviceType
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedClient != null && selectedVehicle != null && description.isNotBlank() && uiState !is ServiceUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState is ServiceUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Submit Service Request", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Client Picker
    if (showClientPicker) {
        ModalBottomSheet(
            onDismissRequest = { showClientPicker = false },
            containerColor = Slate800
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                item {
                    Text("Select Client", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                }
                items(clients) { client ->
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
                            selectedClient = client
                            selectedVehicle = null // Reset vehicle when client changes
                            showClientPicker = false
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    // Vehicle Picker
    if (showVehiclePicker) {
        ModalBottomSheet(
            onDismissRequest = { showVehiclePicker = false },
            containerColor = Slate800
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                item {
                    Text("Select Vehicle", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                }
                if (vehicles.isEmpty()) {
                    item {
                        Text("No vehicles found for this client", color = Color.Gray, modifier = Modifier.padding(vertical = 24.dp))
                    }
                } else {
                    items(vehicles) { vehicle ->
                        ListItem(
                            headlineContent = { Text(vehicle.registration_number, color = Color.White) },
                            supportingContent = { Text("${vehicle.make} ${vehicle.model}", color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier.clickable {
                                selectedVehicle = vehicle
                                showVehiclePicker = false
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }

    // Service Type Picker
    if (showServicePicker) {
        ModalBottomSheet(
            onDismissRequest = { showServicePicker = false },
            containerColor = Slate800
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                item {
                    Text("Select Service Type", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                }
                items(serviceTypes) { type ->
                    ListItem(
                        headlineContent = { Text(type, color = Color.White) },
                        modifier = Modifier.clickable {
                            serviceType = type
                            showServicePicker = false
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    value: String,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.02f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (enabled) PrimaryGreen.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = if (enabled) PrimaryGreen else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Text(
                    value, 
                    color = if (enabled) Color.White else Color.Gray, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Default.KeyboardArrowDown, 
                contentDescription = null, 
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}
