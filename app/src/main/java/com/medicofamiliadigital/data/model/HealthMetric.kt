package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class HealthMetric(
    val id: String = "",
    val profileId: String = "",
    val metricType: String = "", // e.g., "weight", "blood_pressure", "glucose"
    val value: Double = 0.0,
    val unit: String = "",
    val date: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val TYPE_WEIGHT = "Peso"
        const val TYPE_BLOOD_PRESSURE = "Pressão Arterial"
        const val TYPE_GLUCOSE = "Glicose"
        const val TYPE_HEART_RATE = "Frequência Cardíaca"
        const val TYPE_TEMPERATURE = "Temperatura"
        const val TYPE_SLEEP = "Sono"
        const val TYPE_STEPS = "Passos"
    }
}

