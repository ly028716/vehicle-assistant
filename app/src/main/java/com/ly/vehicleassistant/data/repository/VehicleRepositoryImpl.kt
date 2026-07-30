package com.ly.vehicleassistant.data.repository

import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 供 Repository 测试和后续 Room 适配共用的存储边界。
 * Room 版本将该边界映射到上一任务中的 DAO。
 */
class InMemoryVehicleDataStore {
    internal val vehicle = MutableStateFlow<Vehicle?>(null)
    internal val maintenanceRecords = mutableListOf<MaintenanceRecord>()
    internal val repairRecords = mutableListOf<RepairRecord>()
    internal var nextVehicleId = 1L
    internal var nextRecordId = 1L

    suspend fun <T> transaction(block: suspend () -> T): T = block()
}

class VehicleRepositoryImpl(
    private val store: InMemoryVehicleDataStore,
    private val now: () -> Instant = Instant::now,
) : VehicleRepository {
    override fun observeVehicle(): Flow<Vehicle?> = store.vehicle

    override suspend fun createVehicle(input: VehicleInput): Result<Long> = runCatching {
        check(store.vehicle.value == null) { "第一版仅支持一辆车" }
        val id = store.nextVehicleId++
        val timestamp = now()
        store.vehicle.value = Vehicle(
            id = id,
            brand = input.brand.trim(),
            model = input.model.trim(),
            licensePlate = input.licensePlate?.trim()?.ifBlank { null },
            purchaseDate = input.purchaseDate,
            currentMileage = input.currentMileage,
            fuelType = input.fuelType,
            createdAt = timestamp,
            updatedAt = timestamp,
        )
        id
    }

    override suspend fun updateVehicle(input: VehicleInput): Result<Unit> = runCatching {
        val vehicle = requireNotNull(store.vehicle.value) { "请先创建车辆" }
        check(input.currentMileage >= maxRecordedMileage()) { "当前里程不能小于历史记录" }
        store.vehicle.value = vehicle.copy(
            brand = input.brand.trim(),
            model = input.model.trim(),
            licensePlate = input.licensePlate?.trim()?.ifBlank { null },
            purchaseDate = input.purchaseDate,
            currentMileage = input.currentMileage,
            fuelType = input.fuelType,
            updatedAt = now(),
        )
    }

    override suspend fun saveMaintenance(input: MaintenanceInput): Result<Long> = runCatching {
        val vehicle = requireNotNull(store.vehicle.value) { "请先创建车辆" }
        check(input.mileage >= maxRecordedMileage()) { "记录里程不能小于历史最大里程" }
        val id = store.nextRecordId++
        store.maintenanceRecords += MaintenanceRecord(
            id = id,
            vehicleId = vehicle.id,
            date = LocalDate.ofEpochDay(input.dateEpochDay),
            mileage = input.mileage,
            type = input.type,
            items = input.items.trim(),
            costCents = input.costCents,
            shopName = input.shopName?.trim()?.ifBlank { null },
            note = input.note?.trim()?.ifBlank { null },
            createdAt = now(),
        )
        updateMileageIfNeeded(vehicle, input.mileage)
        id
    }

    override suspend fun saveRepair(input: RepairInput): Result<Long> = runCatching {
        val vehicle = requireNotNull(store.vehicle.value) { "请先创建车辆" }
        check(input.mileage >= maxRecordedMileage()) { "记录里程不能小于历史最大里程" }
        val id = store.nextRecordId++
        store.repairRecords += RepairRecord(
            id = id,
            vehicleId = vehicle.id,
            date = LocalDate.ofEpochDay(input.dateEpochDay),
            mileage = input.mileage,
            symptom = input.symptom.trim(),
            repairItems = input.repairItems.trim(),
            costCents = input.costCents,
            shopName = input.shopName?.trim()?.ifBlank { null },
            note = input.note?.trim()?.ifBlank { null },
            createdAt = now(),
        )
        updateMileageIfNeeded(vehicle, input.mileage)
        id
    }

    override suspend fun deleteAllVehicleData(): Result<Unit> = runCatching {
        store.transaction {
            store.maintenanceRecords.clear()
            store.repairRecords.clear()
            store.vehicle.value = null
        }
    }

    fun currentVehicle(): Vehicle? = store.vehicle.value

    fun maintenanceCount(): Int = store.maintenanceRecords.size

    private fun maxRecordedMileage(): Int = buildList {
        store.vehicle.value?.currentMileage?.let(::add)
        store.maintenanceRecords.maxOfOrNull(MaintenanceRecord::mileage)?.let(::add)
        store.repairRecords.maxOfOrNull(RepairRecord::mileage)?.let(::add)
    }.maxOrNull() ?: 0

    private fun updateMileageIfNeeded(vehicle: Vehicle, mileage: Int) {
        if (mileage > vehicle.currentMileage) {
            store.vehicle.value = vehicle.copy(currentMileage = mileage, updatedAt = now())
        }
    }
}
