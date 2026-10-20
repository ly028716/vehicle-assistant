package com.ly.vehicleassistant.ui.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import com.ly.vehicleassistant.ui.assistant.AssistantScreen
import com.ly.vehicleassistant.ui.home.HomeScreen
import com.ly.vehicleassistant.ui.home.formatYuan
import com.ly.vehicleassistant.ui.maintenance.MaintenanceScreen
import com.ly.vehicleassistant.ui.profile.ProfileScreen
import com.ly.vehicleassistant.ui.repair.RepairScreen
import com.ly.vehicleassistant.ui.vehicle.VehicleProfileScreen

@Composable
fun AppNavHost(repository: VehicleRepository) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabRoutes = setOf(Routes.Home, Routes.Records, Routes.Assistant, Routes.Profile)
    Scaffold(
        bottomBar = {
            if (currentRoute in tabRoutes) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    listOf(NavigationItem(Routes.Home, "首页", Icons.Filled.Home), NavigationItem(Routes.Records, "记录", Icons.AutoMirrored.Filled.Article), NavigationItem(Routes.Assistant, "助手", Icons.Filled.AutoStories), NavigationItem(Routes.Profile, "我的", Icons.Filled.Person)).forEach { item ->
                        NavigationBarItem(selected = currentRoute == item.route, onClick = {
                            navController.navigate(item.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true }
                        }, icon = { Icon(item.icon, contentDescription = item.label) }, label = { Text(item.label) })
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(navController, Routes.Home, Modifier.fillMaxSize().padding(innerPadding)) {
            composable(Routes.Home) { HomeScreen(repository, { navController.navigate(Routes.Vehicle) }, { navController.navigate(Routes.Maintenance) }, { navController.navigate(Routes.Repair) }, { navController.navigate(Routes.Records) }) }
            composable(Routes.Records) { RecordsScreen(repository, { navController.navigate(Routes.Maintenance) }, { navController.navigate(Routes.Repair) }) }
            composable(Routes.Assistant) { AssistantScreen(repository) }
            composable(Routes.Profile) { ProfileScreen(repository, { navController.navigate(Routes.Vehicle) }) { navController.navigate(Routes.Home) { popUpTo(Routes.Home) { inclusive = true } } } }
            composable(Routes.Vehicle) { VehicleProfileScreen(repository, onBack = { navController.popBackStack() }) }
            composable(Routes.Maintenance) { MaintenanceScreen(repository, onBack = { navController.popBackStack() }) }
            composable(Routes.Repair) { RepairScreen(repository, onBack = { navController.popBackStack() }) }
        }
    }
}

@Composable
private fun RecordsScreen(repository: VehicleRepository, onMaintenance: () -> Unit, onRepair: () -> Unit) {
    val maintenance by repository.observeMaintenanceRecords().collectAsState(emptyList())
    val repairs by repository.observeRepairRecords().collectAsState(emptyList())
    val records = (maintenance.map { TimelineRecord(it.date.toString(), "保养", it.items, it.mileage, it.costCents, onMaintenance) } + repairs.map { TimelineRecord(it.date.toString(), "维修", it.repairItems, it.mileage, it.costCents, onRepair) }).sortedByDescending { it.date }
    Scaffold(floatingActionButton = { FloatingActionButton(onClick = onMaintenance) { Icon(Icons.Filled.Add, "新增保养记录") } }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), contentPadding = padding, verticalArrangement = Arrangement.spacedBy(0.dp)) {
            item { Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 28.dp), verticalAlignment = Alignment.CenterVertically) { Text("用车记录", style = MaterialTheme.typography.headlineMedium); Spacer(Modifier.weight(1f)); Text("按日期", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) } }
            item { Text("所有服务历史，都是下一次安心出行的依据。", color = MaterialTheme.colorScheme.onSurfaceVariant); Text("共 ${records.size} 条记录", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 28.dp, bottom = 14.dp)) }
            if (records.isEmpty()) item { Text("还没有用车记录", style = MaterialTheme.typography.titleLarge); Text("从一条保养或维修记录开始。", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp)) }
            items(records) { record -> TimelineRow(record) }
        }
    }
}

@Composable
private fun TimelineRow(record: TimelineRecord) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = record.onClick).padding(vertical = 16.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 6.dp)) { Text(record.date.substringAfter('-').substringBefore('-'), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium); Text("月", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) }
        Column(modifier = Modifier.padding(start = 22.dp).weight(1f)) { Text(record.title, style = MaterialTheme.typography.titleMedium); Text(record.kind, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 5.dp)); Text("${record.date} · ${record.mileage.formatMileage()} km", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 5.dp)) }
        Text(formatYuan(record.costCents), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

private data class TimelineRecord(val date: String, val kind: String, val title: String, val mileage: Int, val costCents: Long, val onClick: () -> Unit)
private fun Int.formatMileage(): String = java.lang.String.format(java.util.Locale.CHINA, "%,d", this)
private data class NavigationItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private object Routes { const val Home = "home"; const val Records = "records"; const val Profile = "profile"; const val Vehicle = "vehicle"; const val Maintenance = "maintenance"; const val Repair = "repair"; const val Assistant = "assistant" }
