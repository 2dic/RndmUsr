package com.example.rndmusr.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rndmusr.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailsUiState>(UserDetailsUiState.Loading)
    val uiState: StateFlow<UserDetailsUiState> = _uiState

    fun loadUser(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _uiState.value = if (user != null) {
                UserDetailsUiState.Success(user)
            } else {
                UserDetailsUiState.Error("User not found")
            }
        }
    }
}

sealed class UserDetailsUiState {
    object Loading : UserDetailsUiState()
    data class Success(val user: com.example.rndmusr.domain.model.User) : UserDetailsUiState()
    data class Error(val message: String) : UserDetailsUiState()
}