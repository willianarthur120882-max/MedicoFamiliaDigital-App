package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class ChatMessage(
    val id: String = "",
    val userId: String = "",
    val profileId: String = "",
    val sender: String = "", // "user" or "ai"
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
) {
    companion object {
        const val SENDER_USER = "user"
        const val SENDER_AI = "ai"
    }
}

