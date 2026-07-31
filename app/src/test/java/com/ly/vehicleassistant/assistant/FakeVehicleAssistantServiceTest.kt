package com.ly.vehicleassistant.assistant

import com.ly.vehicleassistant.data.assistant.FakeVehicleAssistantService
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.MaintenanceType
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeVehicleAssistantServiceTest {
    private val service = FakeVehicleAssistantService(AssistantRepository())

    @Test fun answersVehicleInformation() = runTest { assertTrue(service.ask("我的车信息").contains("东风 风神6")) }
    @Test fun answersLatestMaintenance() = runTest { assertTrue(service.ask("最近保养").contains("换机油")) }
    @Test fun answersMaintenanceHistory() = runTest { assertTrue(service.ask("保养历史").contains("1 次")) }
    @Test fun answersRepairCount() = runTest { assertTrue(service.ask("维修次数").contains("1 次")) }
    @Test fun answersTotalCosts() = runTest { assertTrue(service.ask("总费用").contains("400.0")) }
    @Test fun rejectsUnknownQuestion() = runTest { assertTrue(service.ask("发动机异响怎么办").contains("仅支持")) }
}

private class AssistantRepository : VehicleRepository {
    override fun observeVehicle() = flowOf(Vehicle(1, "东风", "风神6", null, null, 200, FuelType.GASOLINE, Instant.EPOCH, Instant.EPOCH))
    override fun observeMaintenanceRecords() = flowOf(listOf(MaintenanceRecord(1, 1, LocalDate.of(2026, 7, 30), 200, MaintenanceType.ENGINE_OIL, "换机油", 20_000, null, null, Instant.EPOCH)))
    override fun observeRepairRecords() = flowOf(listOf(RepairRecord(1, 1, LocalDate.of(2026, 7, 31), 200, "异响", "维护", 20_000, null, null, Instant.EPOCH)))
    override suspend fun createVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(1L)
    override suspend fun updateVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(Unit)
    override suspend fun saveMaintenance(input: com.ly.vehicleassistant.data.model.MaintenanceInput) = Result.success(1L)
    override suspend fun saveRepair(input: com.ly.vehicleassistant.data.model.RepairInput) = Result.success(1L)
    override suspend fun deleteAllVehicleData() = Result.success(Unit)
}
