package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessages(userId: String, profileId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(message: ChatMessage): Result<Unit>
    suspend fun getAiResponse(message: String, chatHistory: List<ChatMessage>): Result<String>
}

