package com.medicofamiliadigital.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.SmartExtra
import com.medicofamiliadigital.domain.repository.SmartExtraRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartExtraRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SmartExtraRepository {

    private val smartExtrasCollection = firestore.collection("smart_extras")

    override suspend fun getSmartExtrasByType(type: String): Result<List<SmartExtra>> {
        return try {
            val snapshot = smartExtrasCollection.whereEqualTo("type", type).get().await()
            val extras = snapshot.documents.mapNotNull { it.toObject(SmartExtra::class.java)?.copy(id = it.id) }
            Result.success(extras)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSmartExtraById(id: String): Result<SmartExtra> {
        return try {
            val document = smartExtrasCollection.document(id).get().await()
            val extra = document.toObject(SmartExtra::class.java)?.copy(id = document.id)
            extra?.let { Result.success(it) } ?: Result.failure(Exception("Smart Extra not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

