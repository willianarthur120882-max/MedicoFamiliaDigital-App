package com.medicofamiliadigital.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.ShareableContent
import com.medicofamiliadigital.domain.repository.ShareableContentRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareableContentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ShareableContentRepository {

    private val shareableContentCollection = firestore.collection("shareable_content")

    override suspend fun createShareableLink(shareableContent: ShareableContent): Result<String> {
        return try {
            val shareId = UUID.randomUUID().toString()
            val contentToSave = shareableContent.copy(id = shareId, shareLink = "https://medicofamiliadigital.app/share/$shareId")
            shareableContentCollection.document(shareId).set(contentToSave).await()
            Result.success(contentToSave.shareLink)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShareableContent(shareId: String): Result<ShareableContent> {
        return try {
            val document = shareableContentCollection.document(shareId).get().await()
            val shareableContent = document.toObject(ShareableContent::class.java)?.copy(id = document.id)

            shareableContent?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Conteúdo compartilhável não encontrado."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteShareableLink(shareId: String): Result<Unit> {
        return try {
            shareableContentCollection.document(shareId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

