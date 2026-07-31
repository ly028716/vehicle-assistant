package com.ly.vehicleassistant.data

import com.ly.vehicleassistant.data.export.ExportSnapshot
import com.ly.vehicleassistant.data.export.JsonVehicleDataExporter
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleDataExporterTest {
    @Test
    fun createsJsonWithSchemaAndVehicleButExcludesLicensePlate() = runTest {
        val exporter = JsonVehicleDataExporter(ExportRepository(vehicle = sampleVehicle()))

        val json = exporter.createJson().getOrThrow()

        assertTrue(json.contains("\"schemaVersion\":1"))
        assertTrue(json.contains("\"brand\":\"东风\""))
        assertFalse(json.contains("鄂A12345"))
    }

    @Test
    fun createsJsonForEmptyData() = runTest {
        val json = JsonVehicleDataExporter(ExportRepository(vehicle = null)).createJson().getOrThrow()

        assertTrue(json.contains("\"vehicle\":null"))
        assertTrue(json.contains("\"maintenanceRecords\":[]"))
        assertTrue(json.contains("\"repairRecords\":[]"))
    }
}

private class ExportRepository(private val vehicle: Vehicle?) : VehicleRepository {
    override fun observeVehicle() = kotlinx.coroutines.flow.flowOf(vehicle)
    override suspend fun createVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(1L)
    override suspend fun updateVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(Unit)
    override suspend fun saveMaintenance(input: com.ly.vehicleassistant.data.model.MaintenanceInput) = Result.success(1L)
    override suspend fun saveRepair(input: com.ly.vehicleassistant.data.model.RepairInput) = Result.success(1L)
    override suspend fun deleteAllVehicleData() = Result.success(Unit)
    override suspend fun exportSnapshot() = ExportSnapshot(vehicle = vehicle)
}

private fun sampleVehicle() = Vehicle(
    id = 1,
    brand = "东风",
    model = "风神6",
    licensePlate = "鄂A12345",
    purchaseDate = null,
    currentMileage = 200,
    fuelType = FuelType.GASOLINE,
    createdAt = Instant.EPOCH,
    updatedAt = Instant.EPOCH,
)
