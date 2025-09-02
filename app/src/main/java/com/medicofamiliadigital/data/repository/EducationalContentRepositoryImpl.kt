package com.medicofamiliadigital.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.EducationalContent
import com.medicofamiliadigital.domain.repository.EducationalContentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EducationalContentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EducationalContentRepository {

    private val educationalContentCollection = firestore.collection("educational_content")

    override fun getAllEducationalContent(): Flow<List<EducationalContent>> = callbackFlow {
        val listener = educationalContentCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val content = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(EducationalContent::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(content)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getEducationalContentById(contentId: String): Result<EducationalContent> {
        return try {
            val document = educationalContentCollection.document(contentId).get().await()
            val content = document.toObject(EducationalContent::class.java)?.copy(id = document.id)
            content?.let { Result.success(it) } ?: Result.failure(Exception("Conteúdo educativo não encontrado."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getEducationalContentByCategory(category: String): Flow<List<EducationalContent>> = callbackFlow {
        val listener = educationalContentCollection
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val content = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(EducationalContent::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(content)
            }

        awaitClose { listener.remove() }
    }
}

