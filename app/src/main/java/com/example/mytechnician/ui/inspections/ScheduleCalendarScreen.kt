package com.example.mytechnician.ui.inspections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.data.model.Conversion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val PrimaryGreen = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCalendarScreen(
    viewModel: InspectionDashboardViewModel,
    onBackClick: () -> Unit,
    onConversionClick: (Conversion) -> Unit,
    onInspectionClick: (com.example.mytechnician.data.model.Inspection) -> Unit,
    onNewInspectionClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Filter conversions for the selected date
    val filteredConversions = uiState.scheduledConversions.filter {
        it.scheduled_date?.startsWith(uiState.selectedDate.toString()) == true
    }
    
    val activeInspections = uiState.inspections.filter { 
        val matchesStatus = when (uiState.selectedStatus) {
            "Pending" -> it.status.lowercase() == "pending"
            "In Progress" -> it.status.lowercase() == "in-progress"
            else -> it.status.lowercase() != "completed" && it.status.lowercase() != "approved"
        }
        matchesStatus && (uiState.selectedStatus != "All" || it.status.lowercase() != "completed")
    }

    // Completed work for the selected date
    val completedInspections = uiState.inspections.filter {
        val isReady = it.status.lowercase() == "completed" || it.status.lowercase() == "approved"
        val matchesDate = it.inspection_date.startsWith(uiState.selectedDate.toString())
        val matchesStatus = uiState.selectedStatus == "All" || uiState.selectedStatus == "Ready"
        isReady && matchesDate && matchesStatus
    }
    
    // Filtered conversions (only show if Ready or All)
    val shownConversions = if (uiState.selectedStatus == "All" || uiState.selectedStatus == "Pending") {
        filteredConversions
    } else {
        emptyList()
    }

    val pullRefreshState = rememberPullToRefreshState()
    
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.loadDashboardData()
            // PullToRefresh state manages its own isRefreshing property if used with the container
        }
    }

    // Effect to sync pull-to-refresh state with ViewModel's loading state
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            pullRefreshState.endRefresh()
        }
    }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("Inspection Schedule", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewInspectionClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Inspection")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Date Selector Grid
                CalendarGrid(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.selectDate(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                
                // Status Filters
                StatusFilterRow(
                    selectedStatus = uiState.selectedStatus,
                    onStatusSelected = { viewModel.selectStatus(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Agenda List
                if (uiState.isLoading && !pullRefreshState.isRefreshing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (shownConversions.isEmpty() && completedInspections.isEmpty() && activeInspections.isEmpty()) {
                            item {
                                Text(
                                    "No tasks matching filters",
                                    color = Color.White.copy(alpha = 0.3f),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            // Active Section
                            if (activeInspections.isNotEmpty()) {
                                item {
                                    Text(
                                        "Active (${activeInspections.size})",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(activeInspections) { inspection ->
                                    InspectionCard(inspection, onInspectionClick)
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }

                            // Scheduled Section
                            if (shownConversions.isNotEmpty()) {
                                item {
                                    Text(
                                        "Schedule for ${uiState.selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(shownConversions) { conversion ->
                                    ConversionCard(conversion, onConversionClick)
                                }
                            }
                            
                            // Completed Section
                            if (completedInspections.isNotEmpty()) {
                                item {
                                    Text(
                                        "Completed Work",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                    )
                                }
                                items(completedInspections) { inspection ->
                                    InspectionCard(inspection, onInspectionClick)
                                }
                            }
                        }
                    }
                }
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullRefreshState,
                containerColor = Slate800,
                contentColor = PrimaryGreen
            )
        }
    }
}

@Composable
fun StatusFilterRow(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val statuses = listOf("All", "Pending", "In Progress", "Ready")
    
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(statuses) { status ->
            val isSelected = selectedStatus == status
            Surface(
                modifier = Modifier.clickable { onStatusSelected(status) },
                color = if (isSelected) PrimaryGreen else Slate800,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.1f))
            ) {
                Text(
                    status,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Current week starting from Monday
    val startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
    val dates = remember {
        (0..13).map { startOfWeek.plusDays(it.toLong()) }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Schedule View",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dates.size) { index ->
                val date = dates[index]
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryGreen else if (isToday) Slate800.copy(alpha = 0.5f) else Slate800)
                        .border(
                            width = 1.dp,
                            color = if (isToday && !isSelected) PrimaryGreen.copy(alpha = 0.5f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onDateSelected(date) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).first().toString(),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        date.dayOfMonth.toString(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun InspectionCard(
    inspection: com.example.mytechnician.data.model.Inspection,
    onInspectionClick: (com.example.mytechnician.data.model.Inspection) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspectionClick(inspection) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    inspection.inspection_number,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    inspection.owner_full_name ?: inspection.registration_number ?: "Unknown Vehicle",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Station ${inspection.station_id}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            
            StatusBadge(inspection.status)
        }
    }
}

@Composable
fun ConversionCard(
    conversion: Conversion,
    onConversionClick: (Conversion) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onConversionClick(conversion) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    conversion.vehicle_registration,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${conversion.make ?: ""} ${conversion.model ?: ""}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(conversion.owner_full_name, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
            
            StatusBadge(conversion.inspection_status ?: "Pending")
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status.lowercase()) {
        "completed", "approved" -> PrimaryGreen to "Ready"
        "in-progress" -> Color(0xFFFACC15) to "In Progress"
        else -> Color.White.copy(alpha = 0.2f) to "Check-in"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text,
            color = color,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyScheduleView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.EventBusy,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No inspections found", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
    }
}
