package com.medicofamiliadigital.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    private val profilesCollection = firestore.collection("profiles")

    override fun getProfilesForUser(userId: String): Flow<List<Profile>> = callbackFlow {
        val listener = profilesCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val profiles = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Profile::class.java)?.copy(id = document.id)
                } ?: emptyList()
                
                trySend(profiles)
            }
        
        awaitClose { listener.remove() }
    }

    override suspend fun createProfile(profile: Profile): Result<String> {
        return try {
            val profileWithTimestamp = profile.copy(
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isChild = profile.age < 13
            )
            
            val documentRef = profilesCollection.add(profileWithTimestamp).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(profile: Profile): Result<Unit> {
        return try {
            val profileWithTimestamp = profile.copy(
                updatedAt = Timestamp.now(),
                isChild = profile.age < 13
            )
            
            profilesCollection.document(profile.id).set(profileWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(profileId: String): Result<Unit> {
        return try {
            profilesCollection.document(profileId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(profileId: String): Result<Profile> {
        return try {
            val document = profilesCollection.document(profileId).get().await()
            val profile = document.toObject(Profile::class.java)?.copy(id = document.id)
            
            profile?.let { 
                Result.success(it) 
            } ?: Result.failure(Exception("Profile not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

