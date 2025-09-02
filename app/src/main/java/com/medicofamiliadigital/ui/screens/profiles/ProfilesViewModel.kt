package com.medicofamiliadigital.ui.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.domain.repository.AuthRepository
import com.medicofamiliadigital.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                profileRepository.getProfilesForUser(currentUser.uid).collect { profiles ->
                    _uiState.value = _uiState.value.copy(
                        profiles = profiles,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun createProfile(profile: Profile) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val profileWithUserId = profile.copy(userId = currentUser.uid)
                profileRepository.createProfile(profileWithUserId).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showCreateDialog = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao criar perfil"
                        )
                    }
                )
            }
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            profileRepository.updateProfile(profile).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showEditDialog = false,
                        editingProfile = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao atualizar perfil"
                    )
                }
            )
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            profileRepository.deleteProfile(profileId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao excluir perfil"
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

    fun showEditDialog(profile: Profile) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            editingProfile = profile
        )
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            editingProfile = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ProfilesUiState(
    val profiles: List<Profile> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingProfile: Profile? = null,
    val errorMessage: String? = null
)

