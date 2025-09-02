package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class MedicalDocument(
    val id: String = "",
    val profileId: String = "",
    val documentType: String = "",
    val documentName: String = "",
    val fileUrl: String = "",
    val uploadDate: Timestamp = Timestamp.now(),
    val examDate: Timestamp? = null,
    val expirationDate: Timestamp? = null,
    val aiAnalysisSummary: String? = null,
    val isAnalyzed: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val TYPE_EXAM = "Exame"
        const val TYPE_RECIPE = "Receita"
        const val TYPE_REPORT = "Laudo"
        const val TYPE_OTHER = "Outro"
    }
}

