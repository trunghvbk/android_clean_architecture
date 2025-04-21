package com.example.androidcleanarchitecture.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.usecase.DeleteUserUseCase
import com.example.androidcleanarchitecture.domain.usecase.GetUserByIdUseCase
import com.example.androidcleanarchitecture.domain.usecase.SaveUserUseCase
import com.example.androidcleanarchitecture.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the user detail screen.
 * Updated to handle detailed error states.
 */
class UserDetailViewModel(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    // UI state for the user detail
    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState

    // Operation state for save/delete operations
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    /**
     * Load a user by ID
     */
    fun loadUser(userId: Int?) {
        if (userId == null) {
            return
        }
        _uiState.value = UserDetailUiState.Loading
        viewModelScope.launch {
            when (val result = getUserByIdUseCase(userId)) {
                is Resource.Success -> {
                    if (result.data != null) {
                        _uiState.value = UserDetailUiState.Success(result.data)
                    } else {
                        _uiState.value = UserDetailUiState.Error(
                            message = "User not found",
                            errorType = Resource.ErrorType.NOT_FOUND,
                            isRetryable = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.value = UserDetailUiState.Error(
                        message = result.message,
                        errorType = result.errorType,
                        isRetryable = isErrorRetryable(result.errorType)
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = UserDetailUiState.Loading
                }
            }
        }
    }

    /**
     * Save a user
     */
    fun saveUser(user: User) {
        _operationState.value = OperationState.Loading
        viewModelScope.launch {
            when (val result = saveUserUseCase(user)) {
                is Resource.Success -> {
                    _operationState.value = OperationState.Success("User saved successfully")
                }
                is Resource.Error -> {
                    _operationState.value = OperationState.Error(
                        message = result.message,
                        errorType = result.errorType,
                        isRetryable = isErrorRetryable(result.errorType)
                    )
                }
                is Resource.Loading -> {
                    _operationState.value = OperationState.Loading
                }
            }
        }
    }

    /**
     * Delete a user
     */
    fun deleteUser(userId: Int?) {
        if (userId == null) {
            return
        }
        _operationState.value = OperationState.Loading
        viewModelScope.launch {
            when (val result = deleteUserUseCase(userId)) {
                is Resource.Success -> {
                    _operationState.value = OperationState.Success("User deleted successfully")
                }
                is Resource.Error -> {
                    _operationState.value = OperationState.Error(
                        message = result.message,
                        errorType = result.errorType,
                        isRetryable = isErrorRetryable(result.errorType)
                    )
                }
                is Resource.Loading -> {
                    _operationState.value = OperationState.Loading
                }
            }
        }
    }

    /**
     * Reset operation state to idle
     */
    fun resetOperationState() {
        _operationState.value = OperationState.Idle
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
 * Sealed class representing different UI states for the user detail screen
 */
sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: User) : UserDetailUiState()
    data class Error(
        val message: String,
        val errorType: Resource.ErrorType,
        val isRetryable: Boolean
    ) : UserDetailUiState()
}

/**
 * Sealed class representing different operation states (save/delete)
 */
sealed class OperationState {
    object Idle : OperationState()
    object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(
        val message: String,
        val errorType: Resource.ErrorType,
        val isRetryable: Boolean
    ) : OperationState()
}

/**
 * Factory for creating UserDetailViewModel with dependencies
 */
class UserDetailViewModelFactory(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailViewModel(
                getUserByIdUseCase,
                saveUserUseCase,
                deleteUserUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
