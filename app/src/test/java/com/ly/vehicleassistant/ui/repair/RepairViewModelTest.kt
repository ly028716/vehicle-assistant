package com.ly.vehicleassistant.ui.repair

import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RepairViewModelTest {
    @Test
    fun saveValidRepairDelegatesToRepository() = runTest {
        val repository = FakeRepairRepository()
        val viewModel = RepairViewModel(repository)

        viewModel.save(RepairInput(20_000, 21_000, "异响", "更换皮带", 300_00))

        assertTrue(repository.saved)
    }
}

private class FakeRepairRepository : VehicleRepository {
    var saved = false
    override fun observeVehicle() = kotlinx.coroutines.flow.flowOf(null)
    override suspend fun createVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(1L)
    override suspend fun updateVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(Unit)
    override suspend fun saveMaintenance(input: com.ly.vehicleassistant.data.model.MaintenanceInput) = Result.success(1L)
    override suspend fun saveRepair(input: RepairInput): Result<Long> { saved = true; return Result.success(1L) }
    override suspend fun deleteAllVehicleData() = Result.success(Unit)
}
