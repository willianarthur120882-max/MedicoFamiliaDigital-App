package com.medicofamiliadigital.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.HealthMetric
import com.medicofamiliadigital.domain.repository.HealthMetricRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthMetricRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HealthMetricRepository {

    private val metricsCollection = firestore.collection("health_metrics")

    override fun getMetricsForProfile(profileId: String, metricType: String): Flow<List<HealthMetric>> = callbackFlow {
        val listener = metricsCollection
            .whereEqualTo("profileId", profileId)
            .whereEqualTo("metricType", metricType)
            .orderBy("date")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val metrics = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(HealthMetric::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(metrics)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addMetric(metric: HealthMetric): Result<Unit> {
        return try {
            val metricWithTimestamp = metric.copy(
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            metricsCollection.add(metricWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMetric(metric: HealthMetric): Result<Unit> {
        return try {
            val metricWithTimestamp = metric.copy(
                updatedAt = Timestamp.now()
            )
            metricsCollection.document(metric.id).set(metricWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMetric(metricId: String): Result<Unit> {
        return try {
            metricsCollection.document(metricId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

