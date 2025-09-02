package com.medicofamiliadigital.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.data.model.Reminder
import com.medicofamiliadigital.domain.repository.AuthRepository
import com.medicofamiliadigital.domain.repository.ProfileRepository
import com.medicofamiliadigital.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                combine(
                    profileRepository.getProfilesForUser(currentUser.uid),
                    _uiState // Observe changes in selectedProfileId
                ) { profiles, uiState ->
                    val selectedProfile = profiles.find { it.id == uiState.selectedProfileId } ?: profiles.firstOrNull()
                    Pair(profiles, selectedProfile)
                }.collect { (profiles, selectedProfile) ->
                    _uiState.value = _uiState.value.copy(
                        profiles = profiles,
                        selectedProfileId = selectedProfile?.id,
                        isLoading = false
                    )
                    selectedProfile?.id?.let { profileId ->
                        reminderRepository.getRemindersForProfile(profileId).collect { reminders ->
                            _uiState.value = _uiState.value.copy(reminders = reminders)
                        }
                    }
                }
            }
        }
    }

    fun selectProfile(profileId: String) {
        _uiState.value = _uiState.value.copy(selectedProfileId = profileId)
    }

    fun createReminder(reminder: Reminder) {
        val currentProfileId = _uiState.value.selectedProfileId
        if (currentProfileId != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val reminderWithProfileId = reminder.copy(profileId = currentProfileId)
                reminderRepository.createReminder(reminderWithProfileId).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showCreateDialog = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao criar lembrete"
                        )
                    }
                )
            }
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            reminderRepository.updateReminder(reminder).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showEditDialog = false,
                        editingReminder = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao atualizar lembrete"
                    )
                }
            )
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            reminderRepository.deleteReminder(reminderId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao excluir lembrete"
                    )
                }
            )
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun showEditDialog(reminder: Reminder) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            editingReminder = reminder
        )
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            editingReminder = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class RemindersUiState(
    val profiles: List<Profile> = emptyList(),
    val selectedProfileId: String? = null,
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingReminder: Reminder? = null,
    val errorMessage: String? = null
)

