package com.medicofamiliadigital.domain.repository

import android.graphics.Bitmap
import com.medicofamiliadigital.data.model.MedicalDocument

interface DocumentAnalysisRepository {
    suspend fun analyzeDocument(documentId: String, imageBitmap: Bitmap): Result<String>
    suspend fun getAnalysisResult(documentId: String): Result<String>
}

