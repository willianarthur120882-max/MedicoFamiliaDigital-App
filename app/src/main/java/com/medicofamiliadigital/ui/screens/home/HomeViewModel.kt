package com.medicofamiliadigital.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medicofamiliadigital.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val currentUser = authRepository.getCurrentUser()
        _uiState.value = _uiState.value.copy(
            userName = currentUser?.displayName ?: "Usuário",
            userEmail = currentUser?.email ?: ""
        )
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

data class HomeUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false
)

