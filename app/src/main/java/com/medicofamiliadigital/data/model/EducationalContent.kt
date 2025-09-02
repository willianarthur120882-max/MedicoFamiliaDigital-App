package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class EducationalContent(
    val id: String = "",
    val title: String = "",
    val category: String = "", // e.g., "nutrition", "exercise", "diseases", "first_aid"
    val content: String = "", // Markdown or HTML content
    val imageUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val CATEGORY_NUTRITION = "Nutrição"
        const val CATEGORY_EXERCISE = "Exercício"
        const val CATEGORY_DISEASES = "Doenças"
        const val CATEGORY_FIRST_AID = "Primeiros Socorros"
    }
}

