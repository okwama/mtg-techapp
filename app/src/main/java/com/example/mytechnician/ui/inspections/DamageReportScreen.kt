package com.example.mytechnician.ui.inspections

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.mytechnician.utils.FileUtils

private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val PrimaryGreen = Color(0xFF22C45E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageReportScreen(
    viewModel: DamageReportViewModel,
    inspectionId: Int,
    onBackClick: () -> Unit,
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val files = uris.mapNotNull { FileUtils.getFileFromUri(context, it) }
        if (files.isNotEmpty()) {
            viewModel.uploadPhotos(inspectionId, files, "damage")
        }
    }

    LaunchedEffect(inspectionId) {
        viewModel.loadInspection(inspectionId)
    }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) onFinish()
    }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("Damage Report", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
            )
        }
    ) { padding ->
        if (uiState.errorMessage != null) {
            Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
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
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Upload Pictures",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Add up to 50 images of vehicle condition and damage.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Upload Button / Progress
                if (uiState.isUploading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = { uiState.uploadProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = PrimaryGreen,
                            trackColor = Slate800
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Uploading... ${(uiState.uploadProgress * 100).toInt()}%", color = Color.White)
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        color = Slate800,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Drag and drop or click to upload", color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Image Grid
                val photoCount = uiState.inspection?.photos?.size ?: 0
                Text("Evidence Photos ($photoCount)", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.inspection?.photos?.let { photos ->
                        items(photos) { photo ->
                            AsyncImage(
                                model = photo.photo_url,
                                contentDescription = photo.caption,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.finishInspection() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isUploading
                ) {
                    Text("Finish Inspection", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
