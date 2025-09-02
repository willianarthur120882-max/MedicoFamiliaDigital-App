package com.medicofamiliadigital.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.HealthDevice
import com.medicofamiliadigital.domain.repository.HealthDeviceRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthDeviceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HealthDeviceRepository {

    private val devicesCollection = firestore.collection("health_devices")

    override fun getDevicesForUser(userId: String): Flow<List<HealthDevice>> = callbackFlow {
        val listener = devicesCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val devices = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(HealthDevice::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(devices)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addDevice(device: HealthDevice): Result<Unit> {
        return try {
            devicesCollection.add(device).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDevice(device: HealthDevice): Result<Unit> {
        return try {
            devicesCollection.document(device.id).set(device).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> {
        return try {
            devicesCollection.document(deviceId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

