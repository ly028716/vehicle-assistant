package com.ly.vehicleassistant.domain

import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.data.model.ValidationError
import java.time.LocalDate

fun validateVehicleInput(
    input: VehicleInput,
    maxRecordedMileage: Int,
): List<ValidationError> = buildList {
    if (input.brand.isBlank()) add(ValidationError.BrandRequired)
    if (input.model.isBlank()) add(ValidationError.ModelRequired)
    if (input.currentMileage < 0) add(ValidationError.MileageNegative)
    if (input.currentMileage >= 0 && input.currentMileage < maxRecordedMileage) {
        add(ValidationError.MileageBelowRecordedMileage)
    }
}

fun validateRecordInput(
    mileage: Int,
    maxRecordedMileage: Int,
    costCents: Long,
    date: LocalDate,
    today: LocalDate,
): List<ValidationError> = buildList {
    if (mileage < 0) add(ValidationError.MileageNegative)
    if (mileage >= 0 && mileage < maxRecordedMileage) {
        add(ValidationError.MileageBelowRecordedMileage)
    }
    if (costCents < 0) add(ValidationError.CostNegative)
    if (date.isAfter(today)) add(ValidationError.DateInFuture)
}
