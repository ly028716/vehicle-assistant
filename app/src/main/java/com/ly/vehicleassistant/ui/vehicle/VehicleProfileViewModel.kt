package com.ly.vehicleassistant.ui.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehicleProfileUiState(
    val brand: String = "",
    val model: String = "",
    val licensePlate: String = "",
    val mileage: String = "",
    val fuelType: FuelType = FuelType.GASOLINE,
    val isSaving: Boolean = false,
    val brandError: String? = null,
    val modelError: String? = null,
    val mileageError: String? = null,
    val errorMessage: String? = null,
    val saved: Boolean = false,
)

sealed interface VehicleProfileEvent {
    data class BrandChanged(val value: String) : VehicleProfileEvent

    data class ModelChanged(val value: String) : VehicleProfileEvent

    data class LicensePlateChanged(val value: String) : VehicleProfileEvent

    data class MileageChanged(val value: String) : VehicleProfileEvent

    data class FuelTypeChanged(val value: FuelType) : VehicleProfileEvent

    data object SaveClicked : VehicleProfileEvent
}

class VehicleProfileViewModel(
    private val repository: VehicleRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(VehicleProfileUiState())
    val uiState: StateFlow<VehicleProfileUiState> = mutableUiState.asStateFlow()

    fun onEvent(event: VehicleProfileEvent) {
        when (event) {
            is VehicleProfileEvent.BrandChanged -> mutableUiState.update {
                it.copy(brand = event.value, brandError = null, saved = false)
            }
            is VehicleProfileEvent.ModelChanged -> mutableUiState.update {
                it.copy(model = event.value, modelError = null, saved = false)
            }
            is VehicleProfileEvent.LicensePlateChanged -> mutableUiState.update {
                it.copy(licensePlate = event.value, saved = false)
            }
            is VehicleProfileEvent.MileageChanged -> mutableUiState.update {
                it.copy(mileage = event.value, mileageError = null, saved = false)
            }
            is VehicleProfileEvent.FuelTypeChanged -> mutableUiState.update {
                it.copy(fuelType = event.value, saved = false)
            }
            VehicleProfileEvent.SaveClicked -> viewModelScope.launch { save() }
        }
    }

    suspend fun save() {
        val state = mutableUiState.value
        val mileage = state.mileage.toIntOrNull()
        val brandError = if (state.brand.isBlank()) "请输入品牌" else null
        val modelError = if (state.model.isBlank()) "请输入车型" else null
        val mileageError = if (mileage == null || mileage < 0) "请输入非负里程" else null
        if (brandError != null || modelError != null || mileageError != null) {
            mutableUiState.update {
                it.copy(brandError = brandError, modelError = modelError, mileageError = mileageError)
            }
            return
        }

        mutableUiState.update { it.copy(isSaving = true, errorMessage = null, saved = false) }
        val input = VehicleInput(
            brand = state.brand,
            model = state.model,
            licensePlate = state.licensePlate,
            currentMileage = requireNotNull(mileage),
            fuelType = state.fuelType,
        )
        val result = repository.createVehicle(input)
        mutableUiState.update {
            it.copy(
                isSaving = false,
                errorMessage = result.exceptionOrNull()?.message,
                saved = result.isSuccess,
            )
        }
    }
}
