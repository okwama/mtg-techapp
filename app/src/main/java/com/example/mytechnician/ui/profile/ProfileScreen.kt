package com.example.mytechnician.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.ui.theme.PrimaryGreen
import com.example.mytechnician.ui.theme.Slate900
import com.example.mytechnician.ui.theme.Slate700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate900)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                // Header Card
                ProfileHeader(uiState.user?.name ?: "User", uiState.user?.role ?: "Technician")

                Spacer(modifier = Modifier.height(32.dp))

                // Info Section
                InfoSection(
                    title = "Personal Information",
                    items = listOf(
                        InfoItem("Phone", uiState.user?.phone_number ?: "N/A", Icons.Default.Phone),
                        InfoItem("Email", uiState.user?.business_email ?: "N/A", Icons.Default.Email),
                        InfoItem("Station", uiState.user?.station_name ?: "N/A", Icons.Default.LocationOn)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Security Section
                InfoSection(
                    title = "Security",
                    items = listOf(
                        InfoItem("Change Password", "****", Icons.Default.Lock, isAction = true),
                        InfoItem("Biometric Login", "Enabled", Icons.Default.Fingerprint, isAction = true)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (uiState.errorMessage != null) {
        // Handled via snackbar or simple text
    }
}

@Composable
fun ProfileHeader(name: String, role: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(role, fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

data class InfoItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val isAction: Boolean = false
)

@Composable
fun InfoSection(title: String, items: List<InfoItem>) {
    Column {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(item.icon, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                    Text(item.value, fontSize = 16.sp, color = Color.White)
                }
                if (item.isAction) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                }
            }
        }
    }
}
