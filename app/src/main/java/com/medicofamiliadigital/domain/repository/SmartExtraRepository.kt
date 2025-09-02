package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.SmartExtra

interface SmartExtraRepository {
    suspend fun getSmartExtrasByType(type: String): Result<List<SmartExtra>>
    suspend fun getSmartExtraById(id: String): Result<SmartExtra>
}

