package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getRemindersForProfile(profileId: String): Flow<List<Reminder>>
    suspend fun createReminder(reminder: Reminder): Result<String>
    suspend fun updateReminder(reminder: Reminder): Result<Unit>
    suspend fun deleteReminder(reminderId: String): Result<Unit>
    suspend fun getReminder(reminderId: String): Result<Reminder>
}

