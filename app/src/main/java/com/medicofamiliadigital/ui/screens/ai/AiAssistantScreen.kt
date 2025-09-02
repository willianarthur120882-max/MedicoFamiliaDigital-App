package com.medicofamiliadigital.ui.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medicofamiliadigital.R
import com.medicofamiliadigital.data.model.ChatMessage
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.ui.components.ProfileSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var messageInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.ai_assistant))
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
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
                text = "Nenhum perfil encontrado. Crie um perfil para usar o assistente de IA.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Chat History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            reverseLayout = true // Display new messages at the bottom
        ) {
            items(uiState.chatHistory.reversed()) { chatMessage ->
                MessageBubble(chatMessage)
            }
        }

        // Message Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                label = { Text("Sua mensagem") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageInput.isNotBlank() && uiState.selectedProfileId != null) {
                        viewModel.sendMessage(
                            userId = "current_user_id", // TODO: Get actual user ID
                            profileId = uiState.selectedProfileId!!,
                            message = messageInput
                        )
                        messageInput = ""
                    }
                },
                enabled = messageInput.isNotBlank() && uiState.selectedProfileId != null
            ) {
                Text("Enviar")
            }
        }

        // Error Message
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(chatMessage: ChatMessage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (chatMessage.sender == ChatMessage.SENDER_USER) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = chatMessage.message,
                color = if (chatMessage.sender == ChatMessage.SENDER_USER) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${chatMessage.sender} - ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.timestamp.toDate())}",
                style = Material-Typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

