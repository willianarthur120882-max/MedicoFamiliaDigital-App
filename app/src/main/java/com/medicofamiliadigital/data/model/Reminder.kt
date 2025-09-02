package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class Reminder(
    val id: String = "",
    val profileId: String = "",
    val reminderType: String = "",
    val title: String = "",
    val description: String? = null,
    val dateTime: Timestamp = Timestamp.now(),
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    companion object {
        const val TYPE_APPOINTMENT = "Consulta"
        const val TYPE_EXAM = "Exame"
        const val TYPE_MEDICATION = "Medicação"
        const val TYPE_MEAL = "Refeição"
        const val TYPE_OTHER = "Outro"

        const val RECURRENCE_DAILY = "Diário"
        const val RECURRENCE_WEEKLY = "Semanal"
        const val RECURRENCE_MONTHLY = "Mensal"
        const val RECURRENCE_YEARLY = "Anual"
    }
}

