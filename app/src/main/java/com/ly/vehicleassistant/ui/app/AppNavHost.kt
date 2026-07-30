package com.ly.vehicleassistant.ui.app

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import com.ly.vehicleassistant.ui.vehicle.VehicleProfileScreen

@Composable
fun AppNavHost(
    repository: VehicleRepository,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        composable(Routes.Home) {
            Button(onClick = { navController.navigate(Routes.Vehicle) }) {
                Text("车辆档案")
            }
        }
        composable(Routes.Records) {
            Text("Records")
        }
        composable(Routes.Profile) {
            Text("Profile")
        }
        composable(Routes.Vehicle) {
            VehicleProfileScreen(repository)
        }
        composable(Routes.Maintenance) {
            Text("Maintenance")
        }
        composable(Routes.Repair) {
            Text("Repair")
        }
        composable(Routes.Assistant) {
            Text("Assistant")
        }
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
