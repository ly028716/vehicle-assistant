package com.ly.vehicleassistant.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ly.vehicleassistant.data.local.entity.MaintenanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceRecordDao {
    @Query("SELECT * FROM maintenance_records ORDER BY dateEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<MaintenanceRecordEntity>>

    @Query("SELECT MAX(mileage) FROM maintenance_records")
    suspend fun getMaxMileage(): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: MaintenanceRecordEntity): Long

    @Update
    suspend fun update(record: MaintenanceRecordEntity)

    @Delete
    suspend fun delete(record: MaintenanceRecordEntity)

    @Query("DELETE FROM maintenance_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM maintenance_records")
    suspend fun deleteAll()
}
