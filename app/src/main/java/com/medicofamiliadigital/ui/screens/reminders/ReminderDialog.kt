package com.medicofamiliadigital.ui.screens.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.Timestamp
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(
    reminder: Reminder?,
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf(reminder?.title ?: "") }
    var description by remember { mutableStateOf(reminder?.description ?: "") }
    var reminderType by remember { mutableStateOf(reminder?.reminderType ?: Reminder.TYPE_APPOINTMENT) }
    var dateTime by remember { mutableStateOf(reminder?.dateTime?.toDate() ?: Calendar.getInstance().time) }
    var isRecurring by remember { mutableStateOf(reminder?.isRecurring ?: false) }
    var recurrencePattern by remember { mutableStateOf(reminder?.recurrencePattern ?: Reminder.RECURRENCE_DAILY) }

    val isEditing = reminder != null
    val dialogTitle = if (isEditing) "Editar Lembrete" else "Criar Lembrete"
    val saveButtonText = if (isEditing) "Salvar" else "Criar"

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Lembrete") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder Type Dropdown
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = reminderType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Tipo de Lembrete") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        listOf(
                            Reminder.TYPE_APPOINTMENT,
                            Reminder.TYPE_EXAM,
                            Reminder.TYPE_MEDICATION,
                            Reminder.TYPE_MEAL,
                            Reminder.TYPE_OTHER
                        ).forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    reminderType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date and Time Picker (Simplified for now)
                OutlinedTextField(
                    value = dateFormatter.format(dateTime),
                    onValueChange = { /* Read-only for now */ },
                    label = { Text("Data e Hora") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    // TODO: Implement actual date/time pickers
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Is Recurring Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it }
                    )
                    Text("Lembrete Recorrente")
                }

                if (isRecurring) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Recurrence Pattern Dropdown
                    var recurrenceExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = recurrenceExpanded,
                        onExpandedChange = { recurrenceExpanded = !recurrenceExpanded }
                    ) {
                        OutlinedTextField(
                            value = recurrencePattern,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Padrão de Recorrência") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = recurrenceExpanded,
                            onDismissRequest = { recurrenceExpanded = false }
                        ) {
                            listOf(
                                Reminder.RECURRENCE_DAILY,
                                Reminder.RECURRENCE_WEEKLY,
                                Reminder.RECURRENCE_MONTHLY,
                                Reminder.RECURRENCE_YEARLY
                            ).forEach { pattern ->
                                DropdownMenuItem(
                                    text = { Text(pattern) },
                                    onClick = {
                                        recurrencePattern = pattern
                                        recurrenceExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val newReminder = Reminder(
                                id = reminder?.id ?: "",
                                profileId = reminder?.profileId ?: "",
                                title = title.trim(),
                                description = description.trim().takeIf { it.isNotEmpty() },
                                reminderType = reminderType,
                                dateTime = Timestamp(dateTime),
                                isRecurring = isRecurring,
                                recurrencePattern = if (isRecurring) recurrencePattern else null,
                                isCompleted = reminder?.isCompleted ?: false,
                                createdAt = reminder?.createdAt ?: Timestamp.now(),
                                updatedAt = Timestamp.now()
                            )
                            onSave(newReminder)
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text(saveButtonText)
                    }
                }
            }
        }
    }
}

