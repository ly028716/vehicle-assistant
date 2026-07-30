package com.ly.vehicleassistant.ui.vehicle

import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleProfileViewModelTest {
    @Test
    fun brandChangedUpdatesFormState() {
        val viewModel = VehicleProfileViewModel(FakeVehicleRepository())

        viewModel.onEvent(VehicleProfileEvent.BrandChanged("Toyota"))

        assertEquals("Toyota", viewModel.uiState.value.brand)
    }

    @Test
    fun saveClickedShowsRequiredFieldErrorsForBlankBrandAndModel() = runTest {
        val viewModel = VehicleProfileViewModel(FakeVehicleRepository())

        viewModel.save()

        assertTrue(viewModel.uiState.value.brandError != null)
        assertTrue(viewModel.uiState.value.modelError != null)
        assertFalse(viewModel.uiState.value.saved)
    }

    @Test
    fun saveCreatesVehicleWithCurrentFormValues() = runTest {
        val repository = FakeVehicleRepository()
        val viewModel = VehicleProfileViewModel(repository)
        viewModel.onEvent(VehicleProfileEvent.BrandChanged("Toyota"))
        viewModel.onEvent(VehicleProfileEvent.ModelChanged("Corolla"))
        viewModel.onEvent(VehicleProfileEvent.MileageChanged("20000"))

        viewModel.save()

        assertEquals("Toyota", repository.lastCreatedInput?.brand)
        assertEquals(20_000, repository.lastCreatedInput?.currentMileage)
        assertTrue(viewModel.uiState.value.saved)
    }
}

private class FakeVehicleRepository : VehicleRepository {
    private val vehicle = MutableStateFlow<Vehicle?>(null)
    var lastCreatedInput: VehicleInput? = null

    override fun observeVehicle(): Flow<Vehicle?> = vehicle

    override suspend fun createVehicle(input: VehicleInput): Result<Long> {
        lastCreatedInput = input
        return Result.success(1)
    }

    override suspend fun updateVehicle(input: VehicleInput): Result<Unit> = Result.success(Unit)

    override suspend fun saveMaintenance(input: MaintenanceInput): Result<Long> = Result.failure(NotImplementedError())

    override suspend fun saveRepair(input: RepairInput): Result<Long> = Result.failure(NotImplementedError())

    override suspend fun deleteAllVehicleData(): Result<Unit> = Result.success(Unit)
}
