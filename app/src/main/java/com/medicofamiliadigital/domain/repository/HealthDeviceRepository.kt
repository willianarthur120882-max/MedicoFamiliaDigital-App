package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.HealthDevice
import kotlinx.coroutines.flow.Flow

interface HealthDeviceRepository {
    fun getDevicesForUser(userId: String): Flow<List<HealthDevice>>
    suspend fun addDevice(device: HealthDevice): Result<Unit>
    suspend fun updateDevice(device: HealthDevice): Result<Unit>
    suspend fun deleteDevice(deviceId: String): Result<Unit>
}

