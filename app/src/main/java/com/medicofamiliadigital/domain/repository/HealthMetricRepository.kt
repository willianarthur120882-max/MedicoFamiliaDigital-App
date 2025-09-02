package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.HealthMetric
import kotlinx.coroutines.flow.Flow

interface HealthMetricRepository {
    fun getMetricsForProfile(profileId: String, metricType: String): Flow<List<HealthMetric>>
    suspend fun addMetric(metric: HealthMetric): Result<Unit>
    suspend fun updateMetric(metric: HealthMetric): Result<Unit>
    suspend fun deleteMetric(metricId: String): Result<Unit>
}

