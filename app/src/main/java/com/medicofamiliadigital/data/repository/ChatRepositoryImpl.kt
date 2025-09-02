package com.medicofamiliadigital.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.ChatMessage
import com.medicofamiliadigital.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    private val chatCollection = firestore.collection("chat_messages")

    override fun getChatMessages(userId: String, profileId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = chatCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("profileId", profileId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(ChatMessage::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {
            chatCollection.add(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAiResponse(message: String, chatHistory: List<ChatMessage>): Result<String> {
        // TODO: Implement actual AI integration here (e.g., call a backend API)
        // For now, return a dummy response
        return Result.success("Olá! Sou seu assistente de saúde. Como posso ajudar hoje?")
    }
}

