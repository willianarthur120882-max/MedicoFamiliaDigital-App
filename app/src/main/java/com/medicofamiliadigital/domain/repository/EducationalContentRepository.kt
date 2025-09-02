package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.EducationalContent
import kotlinx.coroutines.flow.Flow

interface EducationalContentRepository {
    fun getAllEducationalContent(): Flow<List<EducationalContent>>
    suspend fun getEducationalContentById(contentId: String): Result<EducationalContent>
    suspend fun getEducationalContentByCategory(category: String): Flow<List<EducationalContent>>
}

