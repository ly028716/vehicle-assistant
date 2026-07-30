package com.ly.vehicleassistant.domain

import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.ValidationError
import com.ly.vehicleassistant.data.model.VehicleInput
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleValidationTest {
    @Test
    fun validateVehicleInputRejectsBlankBrandAndModel() {
        val errors = validateVehicleInput(
            input = VehicleInput(
                brand = " ",
                model = "",
                currentMileage = 20_000,
                fuelType = FuelType.GASOLINE,
            ),
            maxRecordedMileage = 0,
        )

        assertTrue(errors.contains(ValidationError.BrandRequired))
        assertTrue(errors.contains(ValidationError.ModelRequired))
    }

    @Test
    fun validateVehicleInputRejectsMileageBelowRecordedMileage() {
        val errors = validateVehicleInput(
            input = VehicleInput(
                brand = "Toyota",
                model = "Corolla",
                currentMileage = 19_999,
                fuelType = FuelType.GASOLINE,
            ),
            maxRecordedMileage = 20_000,
        )

        assertEquals(
            listOf(ValidationError.MileageBelowRecordedMileage),
            errors,
        )
    }

    @Test
    fun validateRecordInputRejectsNegativeCostAndFutureDate() {
        val errors = validateRecordInput(
            mileage = 20_000,
            maxRecordedMileage = 20_000,
            costCents = -1,
            date = LocalDate.of(2026, 7, 31),
            today = LocalDate.of(2026, 7, 30),
        )

        assertTrue(errors.contains(ValidationError.CostNegative))
        assertTrue(errors.contains(ValidationError.DateInFuture))
    }
}
