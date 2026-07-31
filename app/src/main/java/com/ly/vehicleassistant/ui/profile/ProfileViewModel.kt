package com.ly.vehicleassistant.ui.profile

import androidx.lifecycle.ViewModel
import com.ly.vehicleassistant.data.export.VehicleDataExporter
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val pendingExportJson: String? = null,
    val errorMessage: String? = null,
    val deleted: Boolean = false,
)

class ProfileViewModel(
    private val repository: VehicleRepository,
    private val exporter: VehicleDataExporter,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = mutableUiState.asStateFlow()

    suspend fun prepareExport() {
        val result = exporter.createJson()
        mutableUiState.update {
            it.copy(
                pendingExportJson = result.getOrNull(),
                errorMessage = result.exceptionOrNull()?.message,
            )
        }
    }

    fun exportFinished(error: Throwable? = null) = mutableUiState.update {
        it.copy(
            pendingExportJson = null,
            errorMessage = error?.message,
        )
    }

    suspend fun deleteAllData() {
        val result = repository.deleteAllVehicleData()
        mutableUiState.update {
            it.copy(
                deleted = result.isSuccess,
                errorMessage = result.exceptionOrNull()?.message,
            )
        }
    }
}
