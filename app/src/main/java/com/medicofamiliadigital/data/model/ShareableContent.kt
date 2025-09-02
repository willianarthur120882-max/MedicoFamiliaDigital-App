package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class ShareableContent(
    val id: String = "",
    val userId: String = "",
    val profileId: String = "",
    val contentType: String = "", // e.g., "medical_document", "health_report", "diet_plan"
    val contentId: String = "",
    val shareLink: String = "",
    val expirationDate: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val TYPE_MEDICAL_DOCUMENT = "medical_document"
        const val TYPE_HEALTH_REPORT = "health_report"
        const val TYPE_DIET_PLAN = "diet_plan"
    }
}

