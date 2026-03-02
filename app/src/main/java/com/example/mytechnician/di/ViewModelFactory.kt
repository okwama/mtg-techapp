package com.example.mytechnician.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytechnician.data.repository.AuthRepository
import com.example.mytechnician.data.repository.InspectionRepository
import com.example.mytechnician.data.repository.ShiftRepository
import com.example.mytechnician.ui.auth.LoginViewModel
import com.example.mytechnician.ui.checkin.CheckinViewModel
import com.example.mytechnician.ui.inspections.ChecklistViewModel
import com.example.mytechnician.ui.inspections.DamageReportViewModel
import com.example.mytechnician.ui.inspections.InspectionDashboardViewModel
import com.example.mytechnician.ui.inspections.InspectionSetupViewModel
import com.example.mytechnician.ui.profile.ProfileViewModel
import com.example.mytechnician.ui.obd.ObdViewModel
import com.example.mytechnician.ui.parts.PartsViewModel
import com.example.mytechnician.ui.service.ServiceViewModel
import com.example.mytechnician.data.repository.PartRepository
import com.example.mytechnician.data.repository.ServiceRepository

import com.example.mytechnician.util.LocationService
import com.example.mytechnician.util.ObdService

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val inspectionRepository: InspectionRepository,
    private val shiftRepository: ShiftRepository,
    private val partRepository: PartRepository,
    private val serviceRepository: ServiceRepository,
    private val locationService: LocationService,
    private val obdService: ObdService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(InspectionDashboardViewModel::class.java) -> {
                InspectionDashboardViewModel(inspectionRepository) as T
            }
            modelClass.isAssignableFrom(InspectionSetupViewModel::class.java) -> {
                InspectionSetupViewModel(inspectionRepository) as T
            }
            modelClass.isAssignableFrom(ChecklistViewModel::class.java) -> {
                ChecklistViewModel(inspectionRepository) as T
            }
            modelClass.isAssignableFrom(DamageReportViewModel::class.java) -> {
                DamageReportViewModel(inspectionRepository) as T
            }
            modelClass.isAssignableFrom(CheckinViewModel::class.java) -> {
                CheckinViewModel(shiftRepository, locationService) as T
            }
            modelClass.isAssignableFrom(ObdViewModel::class.java) -> {
                ObdViewModel(obdService) as T
            }
            modelClass.isAssignableFrom(PartsViewModel::class.java) -> {
                PartsViewModel(partRepository) as T
            }
            modelClass.isAssignableFrom(ServiceViewModel::class.java) -> {
                ServiceViewModel(serviceRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
