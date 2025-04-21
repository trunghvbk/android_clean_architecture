package com.example.androidcleanarchitecture.domain.usecase

import com.example.androidcleanarchitecture.domain.repository.UserRepository
import com.example.androidcleanarchitecture.domain.util.Resource

/**
 * Use case for deleting a user.
 * This class contains the business logic for deleting a user from the data source.
 */
class DeleteUserUseCase(private val userRepository: UserRepository) {
    
    /**
     * Execute the use case to delete a user
     * @param userId The ID of the user to delete
     * @return true if successful, false otherwise
     */
    suspend operator fun invoke(userId: Int): Resource<Boolean> {
        return userRepository.deleteUser(userId)
    }
}
