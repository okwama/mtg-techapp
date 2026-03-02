package com.example.mytechnician.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytechnician.ui.theme.PrimaryGreen
import com.example.mytechnician.ui.theme.Slate900
import com.example.mytechnician.ui.theme.Slate700
import java.text.SimpleDateFormat
import java.util.*

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: Color = PrimaryGreen
)

@Composable
fun DashboardScreen(
    userName: String,
    shiftStatus: com.example.mytechnician.data.model.ShiftStatusResponse? = null,
    onMenuItemClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val menuItems = listOf(
        MenuItem(
            title = if (shiftStatus?.active == true) "Checked In" else "Station Check In",
            icon = if (shiftStatus?.active == true) Icons.Default.CheckCircle else Icons.Default.LocationOn,
            route = "checkin",
            color = if (shiftStatus?.active == true) PrimaryGreen else Color.White
        ),
        MenuItem("Vehicle Inspections", Icons.Default.List, "inspections"),
        MenuItem("Service Requests", Icons.Default.Build, "service_requests"),
        MenuItem("Parts Management", Icons.Default.ShoppingCart, "parts"),
        MenuItem("OBD Reader", Icons.Default.Settings, "obd"),
        MenuItem("Profile", Icons.Default.Person, "profile")
    )

    val currentDate = remember { 
        SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date()) 
    }

    Scaffold(
        containerColor = Slate900,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, $userName",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (shiftStatus?.active == true) PrimaryGreen else Color.Red)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (shiftStatus?.active == true) "Station: ${shiftStatus.shift?.station_name}" else "Not Checked In",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Dashboard",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGreen,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    MenuTile(item = item, onClick = { onMenuItemClick(item.route) })
                }
            }
        }
    }
}

@Composable
fun MenuTile(
    item: MenuItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
