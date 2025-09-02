package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val lastLoginAt: Timestamp = Timestamp.now()
)

