package com.medicofamiliadigital.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.Reminder
import com.medicofamiliadigital.domain.repository.ReminderRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReminderRepository {

    private val remindersCollection = firestore.collection("reminders")

    override fun getRemindersForProfile(profileId: String): Flow<List<Reminder>> = callbackFlow {
        val listener = remindersCollection
            .whereEqualTo("profileId", profileId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val reminders = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Reminder::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(reminders)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createReminder(reminder: Reminder): Result<String> {
        return try {
            val reminderWithTimestamp = reminder.copy(
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            val documentRef = remindersCollection.add(reminderWithTimestamp).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            val reminderWithTimestamp = reminder.copy(
                updatedAt = Timestamp.now()
            )
            remindersCollection.document(reminder.id).set(reminderWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            remindersCollection.document(reminderId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReminder(reminderId: String): Result<Reminder> {
        return try {
            val document = remindersCollection.document(reminderId).get().await()
            val reminder = document.toObject(Reminder::class.java)?.copy(id = document.id)

            reminder?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Reminder not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

