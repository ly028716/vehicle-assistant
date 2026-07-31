package com.ly.vehicleassistant.data.repository

import androidx.room.withTransaction
import com.ly.vehicleassistant.data.local.AppDatabase
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room 持久化实现；页面层仅依赖 [VehicleRepository]。 */
class RoomVehicleRepository(
    private val database: AppDatabase,
    private val now: () -> Instant = Instant::now,
) : VehicleRepository {
    private val vehicleDao = database.vehicleDao()
    private val maintenanceDao = database.maintenanceRecordDao()
    private val repairDao = database.repairRecordDao()

    override fun observeVehicle(): Flow<Vehicle?> = vehicleDao.observeVehicle().map { it?.toVehicle() }

    override fun observeMaintenanceRecords(): Flow<List<MaintenanceRecord>> =
        maintenanceDao.observeAll().map { records -> records.map { it.toMaintenanceRecord() } }

    override fun observeRepairRecords(): Flow<List<RepairRecord>> =
        repairDao.observeAll().map { records -> records.map { it.toRepairRecord() } }

    override suspend fun createVehicle(input: VehicleInput): Result<Long> = runCatching {
        check(vehicleDao.getVehicle() == null) { "第一版仅支持一辆车" }
        val timestamp = now()
        vehicleDao.insert(
            Vehicle(
                id = 0,
                brand = input.brand.trim(),
                model = input.model.trim(),
                licensePlate = input.licensePlate?.trim()?.ifBlank { null },
                purchaseDate = input.purchaseDate,
                currentMileage = input.currentMileage,
                fuelType = input.fuelType,
                createdAt = timestamp,
                updatedAt = timestamp,
            ).toEntity(),
        )
    }

    override suspend fun updateVehicle(input: VehicleInput): Result<Unit> = runCatching {
        val current = requireNotNull(vehicleDao.getVehicle()) { "请先创建车辆" }
        check(input.currentMileage >= maxMileage(current.currentMileage)) { "当前里程不能小于历史记录" }
        vehicleDao.update(
            current.copy(
                brand = input.brand.trim(),
                model = input.model.trim(),
                licensePlate = input.licensePlate?.trim()?.ifBlank { null },
                purchaseDateEpochDay = input.purchaseDate?.toEpochDay(),
                currentMileage = input.currentMileage,
                fuelType = input.fuelType.name,
                updatedAtEpochMillis = now().toEpochMilli(),
            ),
        )
    }

    override suspend fun saveMaintenance(input: MaintenanceInput): Result<Long> = runCatching {
        database.withTransaction {
            val vehicle = requireNotNull(vehicleDao.getVehicle()) { "请先创建车辆" }
            check(input.mileage >= maxMileage(vehicle.currentMileage)) { "记录里程不能小于历史最大里程" }
            val id = maintenanceDao.insert(input.toEntity(vehicle.id, now()))
            updateVehicleMileage(vehicle, input.mileage)
            id
        }
    }

    override suspend fun updateMaintenance(id: Long, input: MaintenanceInput): Result<Unit> = runCatching {
        database.withTransaction {
            val existing = requireNotNull(maintenanceDao.getById(id)) { "保养记录不存在" }
            maintenanceDao.update(input.toEntity(existing.vehicleId, Instant.ofEpochMilli(existing.createdAtEpochMillis)).copy(id = id))
        }
    }

    override suspend fun saveRepair(input: RepairInput): Result<Long> = runCatching {
        database.withTransaction {
            val vehicle = requireNotNull(vehicleDao.getVehicle()) { "请先创建车辆" }
            check(input.mileage >= maxMileage(vehicle.currentMileage)) { "记录里程不能小于历史最大里程" }
            val id = repairDao.insert(input.toEntity(vehicle.id, now()))
            updateVehicleMileage(vehicle, input.mileage)
            id
        }
    }

    override suspend fun updateRepair(id: Long, input: RepairInput): Result<Unit> = runCatching {
        val existing = requireNotNull(repairDao.getById(id)) { "维修记录不存在" }
        repairDao.update(input.toEntity(existing.vehicleId, Instant.ofEpochMilli(existing.createdAtEpochMillis)).copy(id = id))
    }

    override suspend fun deleteRepair(id: Long): Result<Unit> = runCatching { repairDao.deleteById(id) }

    override suspend fun deleteMaintenance(id: Long): Result<Unit> = runCatching {
        maintenanceDao.deleteById(id)
    }

    override suspend fun deleteAllVehicleData(): Result<Unit> = runCatching {
        database.withTransaction {
            maintenanceDao.deleteAll()
            repairDao.deleteAll()
            val vehicle = vehicleDao.getVehicle()
            if (vehicle != null) vehicleDao.delete(vehicle)
        }
    }

    private suspend fun maxMileage(currentMileage: Int): Int = listOfNotNull(
        currentMileage,
        maintenanceDao.getMaxMileage(),
        repairDao.getMaxMileage(),
    ).max()

    private suspend fun updateVehicleMileage(
        vehicle: com.ly.vehicleassistant.data.local.entity.VehicleEntity,
        mileage: Int,
    ) {
        if (mileage > vehicle.currentMileage) {
            vehicleDao.update(vehicle.copy(currentMileage = mileage, updatedAtEpochMillis = now().toEpochMilli()))
        }
    }
}
