package com.ly.vehicleassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ly.vehicleassistant.data.local.entity.MaintenanceRecordEntity
import com.ly.vehicleassistant.data.local.entity.RepairRecordEntity
import com.ly.vehicleassistant.data.local.entity.VehicleEntity

@Database(
    entities = [VehicleEntity::class, MaintenanceRecordEntity::class, RepairRecordEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao

    abstract fun maintenanceRecordDao(): MaintenanceRecordDao

    abstract fun repairRecordDao(): RepairRecordDao
}
