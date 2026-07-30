package com.ly.vehicleassistant.ui.vehicle

import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
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

    @Test
    fun existingVehicleIsLoadedAndSavedAsAnUpdate() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val repository = FakeVehicleRepository(existingVehicle = Vehicle(
                id = 1,
                brand = "东风",
                model = "风神5",
                licensePlate = "鄂A56565",
                purchaseDate = null,
                currentMileage = 100,
                fuelType = FuelType.GASOLINE,
                createdAt = java.time.Instant.EPOCH,
                updatedAt = java.time.Instant.EPOCH,
            ))
            val viewModel = VehicleProfileViewModel(repository)
            advanceUntilIdle()

            assertEquals("东风", viewModel.uiState.value.brand)
            assertEquals("100", viewModel.uiState.value.mileage)

            viewModel.save()

            assertEquals(1, repository.updateCalls)
            assertEquals(0, repository.createCalls)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class FakeVehicleRepository(existingVehicle: Vehicle? = null) : VehicleRepository {
    private val vehicle = MutableStateFlow(existingVehicle)
    var lastCreatedInput: VehicleInput? = null
    var createCalls = 0
    var updateCalls = 0

    override fun observeVehicle(): Flow<Vehicle?> = vehicle

    override suspend fun createVehicle(input: VehicleInput): Result<Long> {
        createCalls++
        lastCreatedInput = input
        return Result.success(1)
    }

    override suspend fun updateVehicle(input: VehicleInput): Result<Unit> {
        updateCalls++
        return Result.success(Unit)
    }

    override suspend fun saveMaintenance(input: MaintenanceInput): Result<Long> = Result.failure(NotImplementedError())

    override suspend fun saveRepair(input: RepairInput): Result<Long> = Result.failure(NotImplementedError())

    override suspend fun deleteAllVehicleData(): Result<Unit> = Result.success(Unit)
}
