package com.ly.vehicleassistant.domain.repository

import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface VehicleRepository {
    fun observeVehicle(): Flow<Vehicle?>

    fun observeMaintenanceRecords(): Flow<List<MaintenanceRecord>> = flowOf(emptyList())

    suspend fun createVehicle(input: VehicleInput): Result<Long>

    suspend fun updateVehicle(input: VehicleInput): Result<Unit>

    suspend fun saveMaintenance(input: MaintenanceInput): Result<Long>

    suspend fun updateMaintenance(id: Long, input: MaintenanceInput): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    suspend fun saveRepair(input: RepairInput): Result<Long>

    suspend fun deleteMaintenance(id: Long): Result<Unit> = Result.failure(UnsupportedOperationException())

    suspend fun deleteAllVehicleData(): Result<Unit>
}
