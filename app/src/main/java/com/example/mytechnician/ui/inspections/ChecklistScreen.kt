package com.example.mytechnician.ui.inspections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.data.model.ChecklistItem
import com.example.mytechnician.data.model.InspectionCategory

private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val PrimaryGreen = Color(0xFF22C55E)
private val FairAmber = Color(0xFFFACC15)
private val PoorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel,
    inspectionId: Int,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val totalTabs = uiState.categories.size + 1

    LaunchedEffect(inspectionId) {
        viewModel.loadInspection(inspectionId)
    }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("Checklist", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveDraft() }) {
                        Text("Save Draft", color = PrimaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (uiState.errorMessage != null) {
            Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = PoorRed, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(uiState.errorMessage!!, color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.loadInspection(inspectionId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800)
                    ) {
                        Text("Retry", color = PrimaryGreen)
                    }
                }
            }
        } else if (uiState.categories.isNotEmpty()) {
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    containerColor = Slate900,
                    contentColor = PrimaryGreen,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    uiState.categories.forEachIndexed { index, category ->
                        Tab(
                            selected = selectedCategoryIndex == index,
                            onClick = { selectedCategoryIndex = index },
                            text = { Text(category.name, color = if (selectedCategoryIndex == index) Color.White else Color.White.copy(alpha = 0.5f)) }
                        )
                    }
                    // ADDED: Final Assessment Tab
                    Tab(
                        selected = selectedCategoryIndex == uiState.categories.size,
                        onClick = { selectedCategoryIndex = uiState.categories.size },
                        text = { Text("Final", color = if (selectedCategoryIndex == uiState.categories.size) Color.White else Color.White.copy(alpha = 0.5f)) }
                    )
                }

                // Checklist Items
                if (selectedCategoryIndex < uiState.categories.size) {
                    val currentCategory = uiState.categories[selectedCategoryIndex]
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(currentCategory.items) { item ->
                            ChecklistItemRow(
                                item = item,
                                onConditionChange = { condition ->
                                    viewModel.updateItemCondition(currentCategory.name, item.id, condition)
                                }
                            )
                        }
                    }
                } else {
                    // Final Assessment Tab UI
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text("Final Assessment", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        
                        Column {
                            Text("Overall Vehicle Condition", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val currentCond = uiState.inspection?.overall_condition ?: ""
                                ConditionButton("Good", PrimaryGreen, currentCond == "Good") { viewModel.updateOverallCondition("Good") }
                                ConditionButton("Fair", FairAmber, currentCond == "Fair") { viewModel.updateOverallCondition("Fair") }
                                ConditionButton("Poor", PoorRed, currentCond == "Poor") { viewModel.updateOverallCondition("Poor") }
                            }
                        }

                        Column {
                            Text("Technician Summary", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = uiState.inspection?.summary ?: "",
                                onValueChange = { viewModel.updateSummary(it) },
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                placeholder = { Text("Enter detailed findings...", color = Color.White.copy(alpha = 0.3f)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PrimaryGreen,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    cursorColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // Bottom Action
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Slate800,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { 
                                if (selectedCategoryIndex < uiState.categories.size) {
                                    selectedCategoryIndex++
                                } else {
                                    viewModel.saveDraft()
                                    onNextClick()
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (selectedCategoryIndex < uiState.categories.size) "Next" else "Damage Report",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onConditionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConditionButton("Good", PrimaryGreen, item.condition == "Good") { onConditionChange("Good") }
                ConditionButton("Fair", FairAmber, item.condition == "Fair") { onConditionChange("Fair") }
                ConditionButton("Poor", PoorRed, item.condition == "Poor") { onConditionChange("Poor") }
            }
        }
    }
}

@Composable
fun ConditionButton(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) color.copy(alpha = 0.2f) else Transparent,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.width(90.dp)
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(label, color = if (isSelected) color else Color.White.copy(alpha = 0.4f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

val Transparent = Color(0x00000000)
