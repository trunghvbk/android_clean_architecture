package com.example.androidcleanarchitecture.domain.usecase

import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.repository.UserRepository
import com.example.androidcleanarchitecture.domain.util.Resource

/**
 * Use case for getting a user by ID.
 * This class contains the business logic for retrieving a user.
 */
class GetUserByIdUseCase(private val userRepository: UserRepository) {
    
    /**
     * Execute the use case to get a user by ID
     * @param userId The ID of the user to retrieve
     * @return The user if found, null otherwise
     */
    suspend operator fun invoke(userId: Int): Resource<User?> {
        return userRepository.getUserById(userId)
    }
}
