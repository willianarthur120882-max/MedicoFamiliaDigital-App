package com.medicofamiliadigital.data.model

data class HealthDevice(
    val id: String = "",
    val userId: String = "",
    val deviceType: String = "", // e.g., "smartwatch", "blood_pressure_monitor"
    val deviceName: String = "",
    val connectionStatus: String = "", // e.g., "connected", "disconnected"
    val lastSync: com.google.firebase.Timestamp? = null
) {
    companion object {
        const val TYPE_SMARTWATCH = "smartwatch"
        const val TYPE_BLOOD_PRESSURE_MONITOR = "blood_pressure_monitor"
        const val STATUS_CONNECTED = "connected"
        const val STATUS_DISCONNECTED = "disconnected"
    }
}

