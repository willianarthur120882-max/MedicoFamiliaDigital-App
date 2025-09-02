package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class FunctionalItem(
    val id: String = "",
    val name: String = "",
    val type: String = "", // "tea" or "food"
    val scientificJustification: String = "",
    val benefits: String = "",
    val usage: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val TYPE_TEA = "Chá"
        const val TYPE_FOOD = "Alimento"
    }
}

