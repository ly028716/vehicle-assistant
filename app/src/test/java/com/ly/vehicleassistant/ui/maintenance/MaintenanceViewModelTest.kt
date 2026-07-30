package com.ly.vehicleassistant.ui.maintenance

import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.MaintenanceType
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MaintenanceViewModelTest {
    @Test
    fun saveValidMaintenanceAddsRecord() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val repository = FakeMaintenanceRepository()
            val viewModel = MaintenanceViewModel(repository)
            viewModel.onEvent(MaintenanceEvent.MileageChanged("21000"))
            viewModel.onEvent(MaintenanceEvent.ItemsChanged("机油机滤"))

            viewModel.save()

            assertEquals(1, repository.savedInputs.size)
            assertTrue(viewModel.uiState.value.saved)
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun saveRejectsInvalidMileage() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val viewModel = MaintenanceViewModel(FakeMaintenanceRepository())
            viewModel.onEvent(MaintenanceEvent.MileageChanged("-1"))
            viewModel.onEvent(MaintenanceEvent.ItemsChanged("机油"))

            viewModel.save()

            assertFalse(viewModel.uiState.value.saved)
            assertTrue(viewModel.uiState.value.mileageError != null)
        } finally { Dispatchers.resetMain() }
    }
}

private class FakeMaintenanceRepository : VehicleRepository {
    private val vehicle = MutableStateFlow<Vehicle?>(null)
    val savedInputs = mutableListOf<MaintenanceInput>()
    override fun observeVehicle(): Flow<Vehicle?> = vehicle
    override fun observeMaintenanceRecords(): Flow<List<MaintenanceRecord>> = MutableStateFlow(emptyList())
    override suspend fun createVehicle(input: VehicleInput): Result<Long> = Result.success(1)
    override suspend fun updateVehicle(input: VehicleInput): Result<Unit> = Result.success(Unit)
    override suspend fun saveMaintenance(input: MaintenanceInput): Result<Long> { savedInputs += input; return Result.success(1) }
    override suspend fun saveRepair(input: RepairInput): Result<Long> = Result.failure(NotImplementedError())
    override suspend fun deleteMaintenance(id: Long): Result<Unit> = Result.success(Unit)
    override suspend fun deleteAllVehicleData(): Result<Unit> = Result.success(Unit)
}
