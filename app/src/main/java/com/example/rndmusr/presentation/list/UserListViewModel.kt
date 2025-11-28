package com.example.rndmusr.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rndmusr.domain.model.User
import com.example.rndmusr.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UserListUiState.Loading
            try {
                userRepository.getUsers().collect { users ->
                    _uiState.value = if (users.isEmpty()) {
                        UserListUiState.Empty
                    } else {
                        UserListUiState.Success(users)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UserListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(user)
                // Flow автоматически обновится
            } catch (e: Exception) {
                // Можно показать ошибку или проигнорировать
            }
        }
    }
}

sealed class UserListUiState {
    object Loading : UserListUiState()
    object Empty : UserListUiState()
    data class Success(val users: List<User>) : UserListUiState()
    data class Error(val message: String) : UserListUiState()
}