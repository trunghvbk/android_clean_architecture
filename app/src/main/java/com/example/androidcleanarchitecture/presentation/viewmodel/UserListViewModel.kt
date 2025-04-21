package com.example.androidcleanarchitecture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.usecase.GetAllUsersUseCase
import com.example.androidcleanarchitecture.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the user list screen.
 * Updated to handle detailed error states.
 */
class UserListViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    // UI state for the user list
    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val uiState: StateFlow<UserListUiState> = _uiState

    init {
        loadUsers()
    }

    /**
     * Load all users
     */
    fun loadUsers() {
        _uiState.value = UserListUiState.Loading
        viewModelScope.launch {
            when (val result = getAllUsersUseCase()) {
                is Resource.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = UserListUiState.Empty
                    } else {
                        _uiState.value = UserListUiState.Success(result.data)
                    }
                }
                is Resource.Error -> {
                    _uiState.value = UserListUiState.Error(
                        message = result.message,
                        errorType = result.errorType,
                        isRetryable = isErrorRetryable(result.errorType)
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = UserListUiState.Loading
                }
            }
        }
    }

    /**
     * Determine if an error is retryable based on its type
     */
    private fun isErrorRetryable(errorType: Resource.ErrorType): Boolean {
        return when (errorType) {
            Resource.ErrorType.NETWORK_ERROR -> true
            Resource.ErrorType.SERVER_ERROR -> true
            Resource.ErrorType.CLIENT_ERROR -> false
            Resource.ErrorType.VALIDATION_ERROR -> false
            Resource.ErrorType.NOT_FOUND -> false
            Resource.ErrorType.UNAUTHORIZED -> false
            Resource.ErrorType.FORBIDDEN -> false
            Resource.ErrorType.LOCAL_ERROR -> true
            Resource.ErrorType.UNKNOWN_ERROR -> true
        }
    }
}

/**
 * Sealed class representing different UI states for the user list screen
 */
sealed class UserListUiState {
    object Loading : UserListUiState()
    object Empty : UserListUiState()
    data class Success(val users: List<User>) : UserListUiState()
    data class Error(
        val message: String,
        val errorType: Resource.ErrorType,
        val isRetryable: Boolean
    ) : UserListUiState()
}

/**
 * Factory for creating UserListViewModel with dependencies
 */
class UserListViewModelFactory(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserListViewModel(getAllUsersUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
