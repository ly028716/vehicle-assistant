package com.ly.vehicleassistant.data

import com.ly.vehicleassistant.data.repository.InMemoryVehicleDataStore
import com.ly.vehicleassistant.data.repository.VehicleRepositoryImpl
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceType
import com.ly.vehicleassistant.data.model.VehicleInput
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleRepositoryTest {
    @Test
    fun createVehicleRejectsSecondVehicle() = runTest {
        val repository = VehicleRepositoryImpl(InMemoryVehicleDataStore())
        val input = VehicleInput("Toyota", "Corolla", currentMileage = 20_000, fuelType = FuelType.GASOLINE)

        assertTrue(repository.createVehicle(input).isSuccess)
        assertTrue(repository.createVehicle(input).isFailure)
    }

    @Test
    fun saveMaintenanceUpdatesVehicleMileageWhenItIncreases() = runTest {
        val repository = VehicleRepositoryImpl(InMemoryVehicleDataStore())
        repository.createVehicle(
            VehicleInput("Toyota", "Corolla", currentMileage = 20_000, fuelType = FuelType.GASOLINE),
        )

        val result = repository.saveMaintenance(
            MaintenanceInput(
                dateEpochDay = 20_000,
                mileage = 21_000,
                type = MaintenanceType.ROUTINE,
                items = "机油机滤",
                costCents = 500_00,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(21_000, repository.currentVehicle()?.currentMileage)
    }

    @Test
    fun saveMaintenanceRejectsMileageBelowHistory() = runTest {
        val repository = VehicleRepositoryImpl(InMemoryVehicleDataStore())
        repository.createVehicle(
            VehicleInput("Toyota", "Corolla", currentMileage = 20_000, fuelType = FuelType.GASOLINE),
        )

        val result = repository.saveMaintenance(
            MaintenanceInput(
                dateEpochDay = 20_000,
                mileage = 19_999,
                type = MaintenanceType.ROUTINE,
                items = "机油机滤",
                costCents = 500_00,
            ),
        )

        assertFalse(result.isSuccess)
    }

    @Test
    fun deleteAllVehicleDataRemovesVehicleAndRecords() = runTest {
        val repository = VehicleRepositoryImpl(InMemoryVehicleDataStore())
        repository.createVehicle(
            VehicleInput("Toyota", "Corolla", currentMileage = 20_000, fuelType = FuelType.GASOLINE),
        )
        repository.saveMaintenance(
            MaintenanceInput(20_000, 20_000, MaintenanceType.ROUTINE, "机油机滤", 500_00),
        )

        assertTrue(repository.deleteAllVehicleData().isSuccess)
        assertEquals(null, repository.currentVehicle())
        assertEquals(0, repository.maintenanceCount())
    }
}
