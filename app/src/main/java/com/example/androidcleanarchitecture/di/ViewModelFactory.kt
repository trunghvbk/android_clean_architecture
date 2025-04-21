package com.example.androidcleanarchitecture.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidcleanarchitecture.domain.usecase.DeleteUserUseCase
import com.example.androidcleanarchitecture.domain.usecase.GetAllUsersUseCase
import com.example.androidcleanarchitecture.domain.usecase.GetUserByIdUseCase
import com.example.androidcleanarchitecture.domain.usecase.SaveUserUseCase
import com.example.androidcleanarchitecture.presentation.viewmodel.UserDetailViewModel
import com.example.androidcleanarchitecture.presentation.viewmodel.UserListViewModel

/**
 * Factory class for creating ViewModels with dependencies.
 * This class works with Android's ViewModel architecture component.
 */
class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    
    private val serviceLocator = ServiceLocator
    
    // Use cases
    private val getAllUsersUseCase by lazy { GetAllUsersUseCase(serviceLocator.userRepository) }
    private val getUserByIdUseCase by lazy { GetUserByIdUseCase(serviceLocator.userRepository) }
    private val saveUserUseCase by lazy { SaveUserUseCase(serviceLocator.userRepository) }
    private val deleteUserUseCase by lazy { DeleteUserUseCase(serviceLocator.userRepository) }
    
    /**
     * Create a new instance of the requested ViewModel
     * @param modelClass The class of the ViewModel to create
     * @return The created ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserListViewModel::class.java) ->
                UserListViewModel(getAllUsersUseCase) as T
                
            modelClass.isAssignableFrom(UserDetailViewModel::class.java) ->
                UserDetailViewModel(getUserByIdUseCase, saveUserUseCase, deleteUserUseCase) as T
                
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
    
    companion object {
        private var instance: ViewModelFactory? = null
        
        fun getInstance(application: Application): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(application).also { instance = it }
            }
        }
    }
}
