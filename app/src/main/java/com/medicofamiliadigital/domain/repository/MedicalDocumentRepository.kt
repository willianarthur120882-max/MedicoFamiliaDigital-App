package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.MedicalDocument
import kotlinx.coroutines.flow.Flow

interface MedicalDocumentRepository {
    fun getDocumentsForProfile(profileId: String): Flow<List<MedicalDocument>>
    suspend fun uploadDocument(profileId: String, documentName: String, documentType: String, fileBytes: ByteArray): Result<String>
    suspend fun updateDocument(document: MedicalDocument): Result<Unit>
    suspend fun deleteDocument(documentId: String): Result<Unit>
    suspend fun getDocument(documentId: String): Result<MedicalDocument>
}

