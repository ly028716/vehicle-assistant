package com.ly.vehicleassistant.data.assistant

import com.ly.vehicleassistant.domain.assistant.VehicleAssistantService
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.first

class FakeVehicleAssistantService(private val repository: VehicleRepository) : VehicleAssistantService {
    override suspend fun ask(question: String): String {
        val vehicle = repository.observeVehicle().first()
        val maintenance = repository.observeMaintenanceRecords().first()
        val repairs = repository.observeRepairRecords().first()
        return when {
            question.containsAny("总费用", "费用", "花了多少钱") -> "保养累计 ¥${maintenance.sumOf { it.costCents } / 100.0}，维修累计 ¥${repairs.sumOf { it.costCents } / 100.0}，总计 ¥${(maintenance.sumOf { it.costCents } + repairs.sumOf { it.costCents }) / 100.0}。"
            question.containsAny("最近保养", "上次保养") -> maintenance.maxByOrNull { it.date }?.let { "最近保养：${it.date}，${it.items}，费用 ¥${it.costCents / 100.0}。" } ?: "暂无本地保养记录。"
            question.containsAny("保养历史", "保养次数") -> "本地共有 ${maintenance.size} 次保养记录。"
            question.containsAny("维修次数", "维修了几次") -> "本地共有 ${repairs.size} 次维修记录。"
            question.containsAny("车辆信息", "我的车", "车型", "里程") -> vehicle?.let { "当前车辆：${it.brand} ${it.model}，里程 ${it.currentMileage} km，${it.fuelType.name}。" } ?: "暂无本地车辆档案。"
            else -> "当前仅支持查询本地车辆、保养、维修和费用记录，不能提供故障诊断或维修建议。"
        }
    }
}

private fun String.containsAny(vararg keywords: String) = keywords.any { contains(it) }
