package com.ly.vehicleassistant.ui.home

import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.MaintenanceType
import com.ly.vehicleassistant.data.model.ReminderStatus
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test
    fun noVehicleShowsEmptySummary() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val viewModel = HomeViewModel(FakeHomeRepository(), today = { LocalDate.of(2026, 7, 31) })

            advanceUntilIdle()

            assertEquals(null, viewModel.uiState.value.vehicle)
            assertEquals(ReminderStatus.NO_RECORD, viewModel.uiState.value.reminderStatus)
            assertEquals(0L, viewModel.uiState.value.maintenanceCostCents)
            assertEquals(0L, viewModel.uiState.value.repairCostCents)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun recordsAreSummarizedWithCostsAndMaintenanceReminder() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val repository = FakeHomeRepository().apply {
                vehicle.value = vehicle(currentMileage = 15_000)
                maintenance.value = listOf(
                    maintenanceRecord(id = 1, date = LocalDate.of(2026, 1, 1), mileage = 10_000, costCents = 20_000),
                    maintenanceRecord(id = 2, date = LocalDate.of(2026, 7, 20), mileage = 11_000, costCents = 35_000),
                )
                repair.value = listOf(
                    repairRecord(id = 1, date = LocalDate.of(2026, 7, 1), costCents = 50_000),
                    repairRecord(id = 2, date = LocalDate.of(2026, 7, 25), costCents = 12_000),
                )
            }
            val viewModel = HomeViewModel(repository, today = { LocalDate.of(2026, 7, 31) })

            advanceUntilIdle()

            assertEquals(2L, viewModel.uiState.value.latestMaintenance?.id)
            assertEquals(2L, viewModel.uiState.value.latestRepair?.id)
            assertEquals(55_000L, viewModel.uiState.value.maintenanceCostCents)
            assertEquals(62_000L, viewModel.uiState.value.repairCostCents)
            assertEquals(4, viewModel.uiState.value.recordCount)
            assertEquals(117_000L, viewModel.uiState.value.totalCostCents)
            assertEquals(ReminderStatus.DUE_SOON, viewModel.uiState.value.reminderStatus)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class FakeHomeRepository : VehicleRepository {
    val vehicle = MutableStateFlow<Vehicle?>(null)
    val maintenance = MutableStateFlow<List<MaintenanceRecord>>(emptyList())
    val repair = MutableStateFlow<List<RepairRecord>>(emptyList())

    override fun observeVehicle() = vehicle
    override fun observeMaintenanceRecords() = maintenance
    override fun observeRepairRecords() = repair
    override suspend fun createVehicle(input: VehicleInput) = Result.success(1L)
    override suspend fun updateVehicle(input: VehicleInput) = Result.success(Unit)
    override suspend fun saveMaintenance(input: com.ly.vehicleassistant.data.model.MaintenanceInput) = Result.success(1L)
    override suspend fun saveRepair(input: com.ly.vehicleassistant.data.model.RepairInput) = Result.success(1L)
    override suspend fun deleteAllVehicleData() = Result.success(Unit)
}

private fun vehicle(currentMileage: Int) = Vehicle(
    id = 1,
    brand = "东风",
    model = "风神",
    licensePlate = null,
    purchaseDate = null,
    currentMileage = currentMileage,
    fuelType = FuelType.GASOLINE,
    createdAt = Instant.EPOCH,
    updatedAt = Instant.EPOCH,
)

private fun maintenanceRecord(id: Long, date: LocalDate, mileage: Int, costCents: Long) = MaintenanceRecord(
    id = id,
    vehicleId = 1,
    date = date,
    mileage = mileage,
    type = MaintenanceType.ROUTINE,
    items = "更换机油",
    costCents = costCents,
    shopName = null,
    note = null,
    createdAt = Instant.EPOCH,
)

private fun repairRecord(id: Long, date: LocalDate, costCents: Long) = RepairRecord(
    id = id,
    vehicleId = 1,
    date = date,
    mileage = 12_000,
    symptom = "异响",
    repairItems = "检查底盘",
    costCents = costCents,
    shopName = null,
    note = null,
    createdAt = Instant.EPOCH,
)
