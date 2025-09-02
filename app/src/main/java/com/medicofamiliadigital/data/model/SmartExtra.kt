package com.medicofamiliadigital.data.model

data class SmartExtra(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "", // e.g., "first_aid", "offline_content", "language_pack"
    val contentUrl: String? = null,
    val languageCode: String? = null
) {
    companion object {
        const val TYPE_FIRST_AID = "first_aid"
        const val TYPE_OFFLINE_CONTENT = "offline_content"
        const val TYPE_LANGUAGE_PACK = "language_pack"
    }
}

