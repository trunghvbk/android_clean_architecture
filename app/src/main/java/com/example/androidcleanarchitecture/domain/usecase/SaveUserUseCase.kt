package com.example.androidcleanarchitecture.domain.usecase

import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.repository.UserRepository
import com.example.androidcleanarchitecture.domain.util.Resource

/**
 * Use case for saving a user.
 * This class contains the business logic for saving a user to the data source.
 */
class SaveUserUseCase(private val userRepository: UserRepository) {
    
    /**
     * Execute the use case to save a user
     * @param user The user to save
     * @return true if successful, false otherwise
     */
    suspend operator fun invoke(user: User): Resource<Boolean> {
        return userRepository.saveUser(user)
    }
}
