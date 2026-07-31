package com.ly.vehicleassistant.ui.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import com.ly.vehicleassistant.ui.home.HomeScreen
import com.ly.vehicleassistant.ui.assistant.AssistantScreen
import com.ly.vehicleassistant.ui.maintenance.MaintenanceScreen
import com.ly.vehicleassistant.ui.profile.ProfileScreen
import com.ly.vehicleassistant.ui.repair.RepairScreen
import com.ly.vehicleassistant.ui.vehicle.VehicleProfileScreen

@Composable
fun AppNavHost(
    repository: VehicleRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabRoutes = setOf(Routes.Home, Routes.Records, Routes.Profile)

    Scaffold(
        bottomBar = {
            if (currentRoute in tabRoutes) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                    listOf(
                        NavigationItem(Routes.Home, "首页", Icons.Filled.Home),
                        NavigationItem(Routes.Records, "记录", Icons.AutoMirrored.Filled.Article),
                        NavigationItem(Routes.Profile, "我的", Icons.Filled.Person),
                    ).forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    repository = repository,
                    onEditVehicle = { navController.navigate(Routes.Vehicle) },
                    onAddMaintenance = { navController.navigate(Routes.Maintenance) },
                    onAddRepair = { navController.navigate(Routes.Repair) },
                )
            }
            composable(Routes.Records) {
                RecordsScreen(
                    onMaintenance = { navController.navigate(Routes.Maintenance) },
                    onRepair = { navController.navigate(Routes.Repair) },
                    onAssistant = { navController.navigate(Routes.Assistant) },
                )
            }
            composable(Routes.Profile) {
                ProfileScreen(
                    repository = repository,
                    onVehicle = { navController.navigate(Routes.Vehicle) },
                    onDeleted = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.Vehicle) { VehicleProfileScreen(repository) }
            composable(Routes.Maintenance) { MaintenanceScreen(repository) }
            composable(Routes.Repair) { RepairScreen(repository) }
            composable(Routes.Assistant) { AssistantScreen(repository) }
        }
    }
}

@Composable
private fun RecordsScreen(onMaintenance: () -> Unit, onRepair: () -> Unit, onAssistant: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("用车记录", style = MaterialTheme.typography.headlineMedium)
        Text("所有服务历史，都沉淀为下一次安心出行的依据。", color = MaterialTheme.colorScheme.onSurfaceVariant)
        RecordEntry("保养记录", "保养周期、项目与费用", onMaintenance)
        RecordEntry("维修记录", "维修事项与解决状态", onRepair)
        RecordEntry("本地用车助手", "基于本地记录快速查询", onAssistant)
    }
}

@Composable
private fun RecordEntry(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private object Routes {
    const val Home = "home"
    const val Records = "records"
    const val Profile = "profile"
    const val Vehicle = "vehicle"
    const val Maintenance = "maintenance"
    const val Repair = "repair"
    const val Assistant = "assistant"
}
