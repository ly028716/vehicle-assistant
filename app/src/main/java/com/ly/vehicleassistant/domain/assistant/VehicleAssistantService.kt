package com.ly.vehicleassistant.domain.assistant

interface VehicleAssistantService { suspend fun ask(question: String): String }
