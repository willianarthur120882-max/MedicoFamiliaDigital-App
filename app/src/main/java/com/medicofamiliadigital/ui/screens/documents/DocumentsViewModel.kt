package com.medicofamiliadigital.ui.screens.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicofamiliadigital.data.model.MedicalDocument
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.domain.repository.AuthRepository
import com.medicofamiliadigital.domain.repository.MedicalDocumentRepository
import com.medicofamiliadigital.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val medicalDocumentRepository: MedicalDocumentRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentsUiState())
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

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
                        medicalDocumentRepository.getDocumentsForProfile(profileId).collect { documents ->
                            _uiState.value = _uiState.value.copy(documents = documents)
                        }
                    }
                }
            }
        }
    }

    fun selectProfile(profileId: String) {
        _uiState.value = _uiState.value.copy(selectedProfileId = profileId)
    }

    fun uploadDocument(
        documentName: String,
        documentType: String,
        fileBytes: ByteArray
    ) {
        val currentProfileId = _uiState.value.selectedProfileId
        if (currentProfileId != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                medicalDocumentRepository.uploadDocument(
                    currentProfileId,
                    documentName,
                    documentType,
                    fileBytes
                ).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showUploadDialog = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao fazer upload do documento"
                        )
                    }
                )
            }
        }
    }

    fun deleteDocument(documentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            medicalDocumentRepository.deleteDocument(documentId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao excluir documento"
                    )
                }
            )
        }
    }

    fun showUploadDialog() {
        _uiState.value = _uiState.value.copy(showUploadDialog = true)
    }

    fun hideUploadDialog() {
        _uiState.value = _uiState.value.copy(showUploadDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class DocumentsUiState(
    val profiles: List<Profile> = emptyList(),
    val selectedProfileId: String? = null,
    val documents: List<MedicalDocument> = emptyList(),
    val isLoading: Boolean = true,
    val showUploadDialog: Boolean = false,
    val errorMessage: String? = null
)

