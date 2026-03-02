package com.example.mytechnician

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mytechnician.data.local.UserPreferences
import com.example.mytechnician.data.repository.AuthRepository
import com.example.mytechnician.ui.auth.LoginScreen
import com.example.mytechnician.ui.auth.LoginViewModel
import com.example.mytechnician.ui.theme.MyTechnicianTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytechnician.data.repository.InspectionRepository
import com.example.mytechnician.di.ViewModelFactory
import com.example.mytechnician.ui.inspections.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dependencies
        val userPreferences = UserPreferences(applicationContext)
        val authRepository = AuthRepository(userPreferences)
        val inspectionRepository = InspectionRepository()
        val shiftRepository = com.example.mytechnician.data.repository.ShiftRepository()
        val partRepository = com.example.mytechnician.data.repository.PartRepository()
        val serviceRepository = com.example.mytechnician.data.repository.ServiceRepository()
        val locationService = com.example.mytechnician.util.LocationService(applicationContext)
        val obdService = com.example.mytechnician.util.ObdService(applicationContext)
        val factory = ViewModelFactory(authRepository, inspectionRepository, shiftRepository, partRepository, serviceRepository, locationService, obdService)
        
        setContent {
            MyTechnicianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("login") }
                    var activeInspectionId by remember { mutableIntStateOf(-1) }
                    var activeConversion by remember { mutableStateOf<com.example.mytechnician.data.model.Conversion?>(null) }
                    
                    val loginViewModel: LoginViewModel = viewModel(factory = factory)
                    val profileViewModel: com.example.mytechnician.ui.profile.ProfileViewModel = viewModel(factory = factory)
                    val checkinViewModel: com.example.mytechnician.ui.checkin.CheckinViewModel = viewModel(factory = factory)
                    val partsViewModel: com.example.mytechnician.ui.parts.PartsViewModel = viewModel(factory = factory)
                    val serviceViewModel: com.example.mytechnician.ui.service.ServiceViewModel = viewModel(factory = factory)
                    
                    val dashboardUiState by loginViewModel.uiState.collectAsState()
                    val shiftStatus by checkinViewModel.shiftStatus
                    
                    LaunchedEffect(Unit) {
                        authRepository.authToken.collect { token ->
                            com.example.mytechnician.data.remote.RetrofitClient.authToken = token
                            if (token != null) {
                                checkinViewModel.loadStations()
                                checkinViewModel.checkShiftStatus()
                            }
                        }
                    }
                    
                    LaunchedEffect(dashboardUiState.isLoggedIn) {
                        if (!dashboardUiState.isLoggedIn) {
                            currentScreen = "login"
                        } else if (currentScreen == "login") {
                            currentScreen = "dashboard"
                        }
                    }
                    
                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { destination ->
                                    currentScreen = destination
                                    checkinViewModel.checkShiftStatus()
                                }
                            )
                        }
                        "dashboard" -> {
                            com.example.mytechnician.ui.dashboard.DashboardScreen(
                                userName = dashboardUiState.user?.name ?: "Technician",
                                shiftStatus = shiftStatus,
                                onMenuItemClick = { route ->
                                    when (route) {
                                        "profile" -> currentScreen = "profile"
                                        "inspections" -> currentScreen = "inspection_dashboard"
                                        "checkin" -> currentScreen = "checkin"
                                        "obd" -> currentScreen = "obd"
                                        "parts" -> currentScreen = "parts"
                                        "service_requests" -> currentScreen = "service_requests"
                                        else -> android.util.Log.d("MainActivity", "Navigate to $route")
                                    }
                                },
                                onLogoutClick = {
                                    loginViewModel.logout()
                                }
                            )
                        }
                        "checkin" -> {
                            com.example.mytechnician.ui.checkin.CheckinScreen(
                                viewModel = checkinViewModel,
                                onBackClick = { currentScreen = "dashboard" }
                            )
                        }
                        "profile" -> {
                            com.example.mytechnician.ui.profile.ProfileScreen(
                                viewModel = profileViewModel,
                                onBackClick = { currentScreen = "dashboard" },
                                onLogoutClick = { loginViewModel.logout() }
                            )
                        }
                        "inspection_dashboard" -> {
                            val inspectionDashboardViewModel: InspectionDashboardViewModel = viewModel(factory = factory)
                            ScheduleCalendarScreen(
                                viewModel = inspectionDashboardViewModel,
                                onBackClick = { currentScreen = "dashboard" },
                                onConversionClick = { conversion ->
                                    if (conversion.inspection_id != null && conversion.inspection_id != 0) {
                                        activeInspectionId = conversion.inspection_id
                                        currentScreen = "inspection_checklist"
                                    } else {
                                        activeConversion = conversion
                                        currentScreen = "inspection_setup"
                                    }
                                },
                                onInspectionClick = { inspection ->
                                    activeInspectionId = inspection.id
                                    currentScreen = "inspection_checklist"
                                },
                                onNewInspectionClick = {
                                    activeConversion = null
                                    currentScreen = "inspection_setup"
                                }
                            )
                        }
                        "inspection_setup" -> {
                            val setupViewModel: InspectionSetupViewModel = viewModel(factory = factory)
                            InspectionSetupScreen(
                                viewModel = setupViewModel,
                                stationId = 1, // Mock station ID for now
                                preSelectedConversion = activeConversion,
                                onBackClick = { currentScreen = "inspection_dashboard" },
                                onInspectionStarted = { id ->
                                    activeInspectionId = id
                                    currentScreen = "inspection_checklist"
                                }
                            )
                        }
                        "inspection_checklist" -> {
                            val checklistViewModel: ChecklistViewModel = viewModel(factory = factory)
                            ChecklistScreen(
                                viewModel = checklistViewModel,
                                inspectionId = activeInspectionId,
                                onBackClick = { currentScreen = "inspection_setup" },
                                onNextClick = { currentScreen = "inspection_damage_report" }
                            )
                        }
                        "inspection_damage_report" -> {
                            val damageReportViewModel: DamageReportViewModel = viewModel(factory = factory)
                            DamageReportScreen(
                                viewModel = damageReportViewModel,
                                inspectionId = activeInspectionId,
                                onBackClick = { currentScreen = "inspection_checklist" },
                                onFinish = { currentScreen = "inspection_dashboard" }
                            )
                        }
                        "obd" -> {
                            val obdViewModel: com.example.mytechnician.ui.obd.ObdViewModel = viewModel(factory = factory)
                            com.example.mytechnician.ui.obd.ObdReaderScreen(
                                viewModel = obdViewModel,
                                onBackClick = { currentScreen = "dashboard" }
                            )
                        }
                        "parts" -> {
                            com.example.mytechnician.ui.parts.PartsScreen(
                                viewModel = partsViewModel,
                                onBackClick = { currentScreen = "dashboard" }
                            )
                        }
                        "service_requests" -> {
                            com.example.mytechnician.ui.service.ServiceRequestScreen(
                                viewModel = serviceViewModel,
                                onBackClick = { currentScreen = "dashboard" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        androidx.compose.material3.Text(
            text = "Dashboard - Coming Soon",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxSize()
        )
    }
}
