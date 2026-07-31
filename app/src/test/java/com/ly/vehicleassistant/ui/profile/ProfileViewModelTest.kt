package com.ly.vehicleassistant.ui.profile

import com.ly.vehicleassistant.data.export.ExportSnapshot
import com.ly.vehicleassistant.data.export.VehicleDataExporter
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileViewModelTest {
    @Test
    fun exportMakesJsonAvailableForSystemFilePicker() = runTest {
        val viewModel = ProfileViewModel(ProfileRepository(), SuccessExporter)

        viewModel.prepareExport()

        assertEquals("{\"schemaVersion\":1}", viewModel.uiState.value.pendingExportJson)
    }

    @Test
    fun deleteDelegatesToRepository() = runTest {
        val repository = ProfileRepository()
        val viewModel = ProfileViewModel(repository, SuccessExporter)

        viewModel.deleteAllData()

        assertTrue(repository.deleted)
        assertTrue(viewModel.uiState.value.deleted)
    }

    @Test
    fun exportWriteFailureIsShownToUser() {
        val viewModel = ProfileViewModel(ProfileRepository(), SuccessExporter)

        viewModel.exportFinished(IllegalStateException("无法写入导出文件"))

        assertEquals("无法写入导出文件", viewModel.uiState.value.errorMessage)
    }
}

private object SuccessExporter : VehicleDataExporter {
    override suspend fun createJson() = Result.success("{\"schemaVersion\":1}")
}

private class ProfileRepository : VehicleRepository {
    var deleted = false
    override fun observeVehicle() = kotlinx.coroutines.flow.flowOf(null)
    override suspend fun createVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(1L)
    override suspend fun updateVehicle(input: com.ly.vehicleassistant.data.model.VehicleInput) = Result.success(Unit)
    override suspend fun saveMaintenance(input: com.ly.vehicleassistant.data.model.MaintenanceInput) = Result.success(1L)
    override suspend fun saveRepair(input: com.ly.vehicleassistant.data.model.RepairInput) = Result.success(1L)
    override suspend fun deleteAllVehicleData(): Result<Unit> { deleted = true; return Result.success(Unit) }
    override suspend fun exportSnapshot() = ExportSnapshot()
}
