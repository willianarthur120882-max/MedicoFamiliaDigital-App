package com.medicofamiliadigital.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicofamiliadigital.data.model.ChatMessage
import com.medicofamiliadigital.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun sendMessage(userId: String, profileId: String, message: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val chatMessage = ChatMessage(userId = userId, profileId = profileId, sender = ChatMessage.SENDER_USER, message = message)
            chatRepository.sendMessage(chatMessage).fold(
                onSuccess = { 
                    // After sending, get AI response
                    getAiResponse(message, _uiState.value.chatHistory + chatMessage)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao enviar mensagem"
                    )
                }
            )
        }
    }

    private fun getAiResponse(userMessage: String, chatHistory: List<ChatMessage>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            chatRepository.getAiResponse(userMessage, chatHistory).fold(
                onSuccess = { aiResponse ->
                    val aiChatMessage = ChatMessage(userId = chatHistory.first().userId, profileId = chatHistory.first().profileId, sender = ChatMessage.SENDER_AI, message = aiResponse)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        chatHistory = _uiState.value.chatHistory + aiChatMessage
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao obter resposta da IA"
                    )
                }
            )
        }
    }

    fun loadChatHistory(userId: String, profileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            chatRepository.getChatMessages(userId, profileId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    chatHistory = messages
                )
            }
        }
    }
}

data class AiAssistantUiState(
    val chatHistory: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

