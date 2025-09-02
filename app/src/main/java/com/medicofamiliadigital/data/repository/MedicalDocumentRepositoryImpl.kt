package com.medicofamiliadigital.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medicofamiliadigital.data.model.MedicalDocument
import com.medicofamiliadigital.domain.repository.MedicalDocumentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicalDocumentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MedicalDocumentRepository {

    private val documentsCollection = firestore.collection("medical_documents")
    private val storageRef = storage.reference

    override fun getDocumentsForProfile(profileId: String): Flow<List<MedicalDocument>> = callbackFlow {
        val listener = documentsCollection
            .whereEqualTo("profileId", profileId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val documents = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(MedicalDocument::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(documents)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun uploadDocument(
        profileId: String,
        documentName: String,
        documentType: String,
        fileBytes: ByteArray
    ): Result<String> {
        return try {
            val fileName = "${UUID.randomUUID()}_${documentName}"
            val ref = storageRef.child("medical_documents/${profileId}/${fileName}")
            val uploadTask = ref.putBytes(fileBytes).await()
            val fileUrl = uploadTask.storage.downloadUrl.await().toString()

            val newDocument = MedicalDocument(
                profileId = profileId,
                documentType = documentType,
                documentName = documentName,
                fileUrl = fileUrl,
                uploadDate = Timestamp.now()
            )

            val documentRef = documentsCollection.add(newDocument).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDocument(document: MedicalDocument): Result<Unit> {
        return try {
            documentsCollection.document(document.id).set(document.copy(updatedAt = Timestamp.now())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDocument(documentId: String): Result<Unit> {
        return try {
            // Optionally, delete file from Firebase Storage as well
            // val document = documentsCollection.document(documentId).get().await().toObject(MedicalDocument::class.java)
            // document?.fileUrl?.let { Uri.parse(it) }?.lastPathSegment?.let { fileName ->
            //     storageRef.child("medical_documents/${document.profileId}/${fileName}").delete().await()
            // }
            documentsCollection.document(documentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDocument(documentId: String): Result<MedicalDocument> {
        return try {
            val document = documentsCollection.document(documentId).get().await()
            val medicalDocument = document.toObject(MedicalDocument::class.java)?.copy(id = document.id)

            medicalDocument?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Document not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

