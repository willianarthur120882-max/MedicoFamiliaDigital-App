package com.medicofamiliadigital.ui.screens.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.Reminder
import com.medicofamiliadigital.ui.components.ProfileSelector
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(text = "Calendário e Lembretes")
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.showCreateDialog() }) {
                    Icon(
                        imageVector = Icons.Default.AddAlarm,
                        contentDescription = "Adicionar Lembrete"
                    )
                }
            }
        )

        // Profile Selector
        if (uiState.profiles.isNotEmpty()) {
            ProfileSelector(
                profiles = uiState.profiles,
                selectedProfileId = uiState.selectedProfileId,
                onProfileSelected = { viewModel.selectProfile(it) }
            )
        } else if (!uiState.isLoading) {
            Text(
                text = "Nenhum perfil encontrado. Crie um perfil para adicionar lembretes.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Content
        if (uiState.isLoading && uiState.reminders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onEditClick = { viewModel.showEditDialog(reminder) },
                        onDeleteClick = { viewModel.deleteReminder(reminder.id) }
                    )
                }

                if (uiState.reminders.isEmpty() && !uiState.isLoading) {
                    item {
                        EmptyRemindersState(
                            onAddReminderClick = { viewModel.showCreateDialog() }
                        )
                    }
                }
            }
        }
    }

    // Create Reminder Dialog
    if (uiState.showCreateDialog) {
        ReminderDialog(
            reminder = null,
            onDismiss = { viewModel.hideCreateDialog() },
            onSave = { reminder -> viewModel.createReminder(reminder) }
        )
    }

    // Edit Reminder Dialog
    if (uiState.showEditDialog && uiState.editingReminder != null) {
        ReminderDialog(
            reminder = uiState.editingReminder,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { reminder -> viewModel.updateReminder(reminder) }
        )
    }

    // Error Snackbar
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar
            viewModel.clearError()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(
    reminder: Reminder,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (reminder.reminderType) {
                    Reminder.TYPE_APPOINTMENT -> Icons.Default.Event
                    Reminder.TYPE_EXAM -> Icons.Default.MedicalServices
                    Reminder.TYPE_MEDICATION -> Icons.Default.Medication
                    Reminder.TYPE_MEAL -> Icons.Default.Restaurant
                    else -> Icons.Default.Alarm
                },
                contentDescription = reminder.reminderType,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tipo: ${reminder.reminderType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                reminder.dateTime?.let { timestamp ->
                    Text(
                        text = "Data: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(timestamp.toDate())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (reminder.isRecurring) {
                    Text(
                        text = "Recorrência: ${reminder.recurrencePattern ?: "Não especificado"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyRemindersState(
    onAddReminderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EventNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nenhum lembrete agendado",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Adicione consultas, exames e medicações para não esquecer nada.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = Alignment.CenterHorizontally,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddReminderClick
        ) {
            Icon(
                imageVector = Icons.Default.AddAlarm,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Adicionar Lembrete")
        }
    }
}

