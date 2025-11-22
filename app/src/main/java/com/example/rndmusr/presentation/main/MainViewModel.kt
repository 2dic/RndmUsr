package com.example.rndmusr.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rndmusr.domain.model.User
import com.example.rndmusr.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState

    fun generateUser(gender: String? = null, nationality: String? = null) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                val result = userRepository.getRandomUser(gender, nationality)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _uiState.value = MainUiState.Success(user)
                    } else {
                        _uiState.value = MainUiState.Error("No user data received")
                    }
                } else {
                    _uiState.value = MainUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            userRepository.saveUser(user)
        }
    }

    fun resetState() {
        _uiState.value = MainUiState.Idle
    }
}

sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    data class Success(val user: User) : MainUiState()
    data class Error(val message: String) : MainUiState()
}