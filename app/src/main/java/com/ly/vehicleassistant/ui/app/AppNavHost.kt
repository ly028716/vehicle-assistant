package com.ly.vehicleassistant.ui.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.ly.vehicleassistant.ui.maintenance.MaintenanceScreen
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
                Surface(shadowElevation = 3.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            listOf(
                                Routes.Home to "首页",
                                Routes.Records to "记录",
                                Routes.Profile to "我的",
                            ).forEach { (route, label) ->
                                val selected = currentRoute == route
                                TextButton(
                                    onClick = {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    modifier = Modifier.height(56.dp),
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                    )
                                }
                            }
                        }
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
                )
            }
            composable(Routes.Profile) {
                ProfileEntryScreen(onVehicle = { navController.navigate(Routes.Vehicle) })
            }
            composable(Routes.Vehicle) { VehicleProfileScreen(repository) }
            composable(Routes.Maintenance) { MaintenanceScreen(repository) }
            composable(Routes.Repair) { RepairScreen(repository) }
            composable(Routes.Assistant) { Text("智能助手将在后续任务中实现") }
        }
    }
}

@Composable
private fun RecordsScreen(onMaintenance: () -> Unit, onRepair: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("用车记录")
        Button(onClick = onMaintenance, modifier = Modifier.fillMaxWidth()) { Text("保养记录") }
        Button(onClick = onRepair, modifier = Modifier.fillMaxWidth()) { Text("维修记录") }
    }
}

@Composable
private fun ProfileEntryScreen(onVehicle: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("我的")
        Button(onClick = onVehicle, modifier = Modifier.fillMaxWidth()) { Text("车辆档案") }
        Text("数据导出和删除将在下一任务中提供。")
    }
}

private object Routes {
    const val Home = "home"
    const val Records = "records"
    const val Profile = "profile"
    const val Vehicle = "vehicle"
    const val Maintenance = "maintenance"
    const val Repair = "repair"
    const val Assistant = "assistant"
}
