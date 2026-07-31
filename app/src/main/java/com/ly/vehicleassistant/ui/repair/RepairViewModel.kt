package com.ly.vehicleassistant.ui.repair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RepairUiState(
    val records: List<RepairRecord> = emptyList(),
    val date: String = LocalDate.now().toString(),
    val mileage: String = "",
    val symptom: String = "",
    val repairItems: String = "",
    val costYuan: String = "",
    val editingId: Long? = null,
    val errorMessage: String? = null,
    val saved: Boolean = false,
)

class RepairViewModel(private val repository: VehicleRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(RepairUiState())
    val uiState: StateFlow<RepairUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeRepairRecords().collect { records ->
                mutableUiState.update { it.copy(records = records) }
            }
        }
    }

    fun updateDate(value: String) = mutableUiState.update { it.copy(date = value, saved = false) }
    fun updateMileage(value: String) = mutableUiState.update { it.copy(mileage = value, saved = false) }
    fun updateSymptom(value: String) = mutableUiState.update { it.copy(symptom = value, saved = false) }
    fun updateRepairItems(value: String) = mutableUiState.update { it.copy(repairItems = value, saved = false) }
    fun updateCost(value: String) = mutableUiState.update { it.copy(costYuan = value, saved = false) }

    suspend fun save(input: RepairInput): Result<Long> = repository.saveRepair(input)

    suspend fun saveForm() {
        val state = mutableUiState.value
        val date = runCatching { LocalDate.parse(state.date) }.getOrNull()
        val mileage = state.mileage.toIntOrNull()
        if (date == null || date.isAfter(LocalDate.now()) || mileage == null || mileage < 0 || state.symptom.isBlank() || state.repairItems.isBlank()) {
            mutableUiState.update { it.copy(errorMessage = "请检查日期、里程、故障现象和维修项目") }
            return
        }
        val input = RepairInput(date.toEpochDay(), mileage, state.symptom, state.repairItems, ((state.costYuan.toDoubleOrNull() ?: 0.0) * 100).toLong())
        val result = state.editingId?.let { repository.updateRepair(it, input).map { 0L } } ?: save(input)
        mutableUiState.update { it.copy(saved = result.isSuccess, errorMessage = result.exceptionOrNull()?.message) }
    }

    fun edit(record: RepairRecord) = mutableUiState.update {
        it.copy(date = record.date.toString(), mileage = record.mileage.toString(), symptom = record.symptom, repairItems = record.repairItems, costYuan = (record.costCents / 100.0).toString(), editingId = record.id, saved = false)
    }

    suspend fun delete(id: Long) {
        val result = repository.deleteRepair(id)
        if (result.isFailure) mutableUiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
    }
}
