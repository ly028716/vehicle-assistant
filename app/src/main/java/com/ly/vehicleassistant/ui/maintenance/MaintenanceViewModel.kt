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
    val editingId: Long? = null,
    val dateEpochDay: Long = LocalDate.now().toEpochDay(),
    val date: String = LocalDate.now().toString(),
    val dateError: String? = null,
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
    data class DateChanged(val value: String) : MaintenanceEvent
    data class DeleteClicked(val id: Long) : MaintenanceEvent
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
            is MaintenanceEvent.DateChanged -> mutableUiState.update { it.copy(date = event.value, dateError = null, saved = false) }
            is MaintenanceEvent.DeleteClicked -> viewModelScope.launch { delete(event.id) }
            MaintenanceEvent.SaveClicked -> viewModelScope.launch { save() }
        }
    }

    suspend fun save() {
        val state = mutableUiState.value
        val mileage = state.mileage.toIntOrNull()
        val date = runCatching { LocalDate.parse(state.date) }.getOrNull()
        val mileageError = if (mileage == null || mileage < 0) "请输入非负里程" else null
        val itemsError = if (state.items.isBlank()) "请输入保养项目" else null
        val dateError = if (date == null || date.isAfter(LocalDate.now())) "请输入有效且不晚于今天的日期" else null
        if (mileageError != null || itemsError != null || dateError != null) {
            mutableUiState.update { it.copy(mileageError = mileageError, itemsError = itemsError, dateError = dateError) }
            return
        }
        val costCents = ((state.costYuan.toDoubleOrNull() ?: 0.0) * 100).toLong()
        val input = MaintenanceInput(
                dateEpochDay = requireNotNull(date).toEpochDay(),
                mileage = requireNotNull(mileage),
                type = state.type,
                items = state.items,
                costCents = costCents,
            )
        val result = state.editingId?.let { repository.updateMaintenance(it, input).map { it } }
            ?: repository.saveMaintenance(input)
        mutableUiState.update {
            it.copy(
                errorMessage = result.exceptionOrNull()?.message,
                saved = result.isSuccess,
                mileage = if (result.isSuccess && it.editingId == null) "" else it.mileage,
                items = if (result.isSuccess && it.editingId == null) "" else it.items,
                costYuan = if (result.isSuccess && it.editingId == null) "" else it.costYuan,
            )
        }
    }

    fun edit(record: MaintenanceRecord) {
        mutableUiState.update {
            it.copy(
                editingId = record.id,
                dateEpochDay = record.date.toEpochDay(),
                date = record.date.toString(),
                mileage = record.mileage.toString(),
                items = record.items,
                costYuan = (record.costCents / 100.0).toString(),
                type = record.type,
                saved = false,
            )
        }
    }

    suspend fun delete(id: Long) {
        val result = repository.deleteMaintenance(id)
        if (result.isFailure) {
            mutableUiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
        }
    }
}
