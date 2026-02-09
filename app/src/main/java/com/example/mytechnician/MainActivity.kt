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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dependencies
        val userPreferences = UserPreferences(applicationContext)
        val authRepository = AuthRepository(userPreferences)
        val loginViewModel = LoginViewModel(authRepository)
        
        setContent {
            MyTechnicianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("login") }
                    
                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { destination ->
                                    currentScreen = destination
                                }
                            )
                        }
                        "dashboard" -> {
                            // Placeholder for Dashboard
                            DashboardPlaceholder()
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
