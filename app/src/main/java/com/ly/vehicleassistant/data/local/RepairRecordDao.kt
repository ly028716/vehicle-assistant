package com.ly.vehicleassistant.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ly.vehicleassistant.data.local.entity.RepairRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairRecordDao {
    @Query("SELECT * FROM repair_records ORDER BY dateEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<RepairRecordEntity>>

    @Query("SELECT MAX(mileage) FROM repair_records")
    suspend fun getMaxMileage(): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: RepairRecordEntity): Long

    @Update
    suspend fun update(record: RepairRecordEntity)

    @Delete
    suspend fun delete(record: RepairRecordEntity)

    @Query("DELETE FROM repair_records")
    suspend fun deleteAll()
}
