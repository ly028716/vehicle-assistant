package com.ly.vehicleassistant.ui.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.MaintenanceType
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MaintenanceUiState(
    val records: List<MaintenanceRecord> = emptyList(),
    val mileage: String = "",
    val items: String = "",
    val costYuan: String = "",
    val type: MaintenanceType = MaintenanceType.ROUTINE,
    val mileageError: String? = null,
    val itemsError: String? = null,
    val errorMessage: String? = null,
    val saved: Boolean = false,
)

sealed interface MaintenanceEvent {
    data class MileageChanged(val value: String) : MaintenanceEvent
    data class ItemsChanged(val value: String) : MaintenanceEvent
    data class CostChanged(val value: String) : MaintenanceEvent
    data class TypeChanged(val value: MaintenanceType) : MaintenanceEvent
    data object SaveClicked : MaintenanceEvent
}

class MaintenanceViewModel(private val repository: VehicleRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(MaintenanceUiState())
    val uiState: StateFlow<MaintenanceUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeMaintenanceRecords().collect { records ->
                mutableUiState.update { it.copy(records = records) }
            }
        }
    }

    fun onEvent(event: MaintenanceEvent) {
        when (event) {
            is MaintenanceEvent.MileageChanged -> mutableUiState.update { it.copy(mileage = event.value, mileageError = null, saved = false) }
            is MaintenanceEvent.ItemsChanged -> mutableUiState.update { it.copy(items = event.value, itemsError = null, saved = false) }
            is MaintenanceEvent.CostChanged -> mutableUiState.update { it.copy(costYuan = event.value, saved = false) }
            is MaintenanceEvent.TypeChanged -> mutableUiState.update { it.copy(type = event.value, saved = false) }
            MaintenanceEvent.SaveClicked -> viewModelScope.launch { save() }
        }
    }

    suspend fun save() {
        val state = mutableUiState.value
        val mileage = state.mileage.toIntOrNull()
        val mileageError = if (mileage == null || mileage < 0) "请输入非负里程" else null
        val itemsError = if (state.items.isBlank()) "请输入保养项目" else null
        if (mileageError != null || itemsError != null) {
            mutableUiState.update { it.copy(mileageError = mileageError, itemsError = itemsError) }
            return
        }
        val costCents = ((state.costYuan.toDoubleOrNull() ?: 0.0) * 100).toLong()
        val result = repository.saveMaintenance(
            MaintenanceInput(
                dateEpochDay = LocalDate.now().toEpochDay(),
                mileage = requireNotNull(mileage),
                type = state.type,
                items = state.items,
                costCents = costCents,
            ),
        )
        mutableUiState.update {
            it.copy(
                errorMessage = result.exceptionOrNull()?.message,
                saved = result.isSuccess,
                mileage = if (result.isSuccess) "" else it.mileage,
                items = if (result.isSuccess) "" else it.items,
                costYuan = if (result.isSuccess) "" else it.costYuan,
            )
        }
    }
}
