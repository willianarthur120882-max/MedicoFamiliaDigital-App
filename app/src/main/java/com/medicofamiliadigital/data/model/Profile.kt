package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class Profile(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val age: Int = 0,
    val gender: String = "",
    val medicalHistory: String? = null,
    val allergies: String? = null,
    val isChild: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val GENDER_MALE = "Masculino"
        const val GENDER_FEMALE = "Feminino"
        const val GENDER_OTHER = "Outro"
    }
}

