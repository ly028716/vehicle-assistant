package com.ly.vehicleassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.ly.vehicleassistant.ui.app.AppNavHost
import com.ly.vehicleassistant.ui.theme.VehicleassistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VehicleassistantTheme {
                AppNavHost()
            }
        }
    }
}
