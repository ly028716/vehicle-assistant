package com.ly.vehicleassistant.data

import com.ly.vehicleassistant.data.local.DatabaseConverters
import com.ly.vehicleassistant.data.local.entity.VehicleEntity
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.repository.toEntity
import com.ly.vehicleassistant.data.repository.toVehicle
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomEntityMappingTest {
    private val converters = DatabaseConverters()

    @Test
    fun convertersRoundTripDateAndInstantAsEpochValues() {
        val date = LocalDate.of(2026, 7, 30)
        val instant = Instant.ofEpochMilli(1_754_000_000_000)

        assertEquals(date, converters.toLocalDate(converters.fromLocalDate(date)))
        assertEquals(instant, converters.toInstant(converters.fromInstant(instant)))
    }

    @Test
    fun vehicleMappingPreservesAllPersistedValues() {
        val vehicle = Vehicle(
            id = 8,
            brand = "Toyota",
            model = "Corolla",
            licensePlate = "鄂A12345",
            purchaseDate = LocalDate.of(2024, 1, 1),
            currentMileage = 20_000,
            fuelType = FuelType.GASOLINE,
            createdAt = Instant.ofEpochMilli(1_700_000_000_000),
            updatedAt = Instant.ofEpochMilli(1_750_000_000_000),
        )

        assertEquals(vehicle, vehicle.toEntity().toVehicle())
    }

    @Test
    fun vehicleEntityStoresDateAndTimestampInEpochUnits() {
        val entity = VehicleEntity(
            id = 1,
            brand = "BYD",
            model = "Seal",
            licensePlate = null,
            purchaseDateEpochDay = 20_000,
            currentMileage = 500,
            fuelType = FuelType.ELECTRIC.name,
            createdAtEpochMillis = 1_700_000_000_000,
            updatedAtEpochMillis = 1_700_000_001_000,
        )

        assertEquals(20_000L, entity.purchaseDateEpochDay)
        assertEquals(1_700_000_000_000L, entity.createdAtEpochMillis)
    }
}
