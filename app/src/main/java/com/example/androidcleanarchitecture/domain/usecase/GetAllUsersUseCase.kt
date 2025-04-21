package com.example.androidcleanarchitecture.domain.usecase

import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.repository.UserRepository
import com.example.androidcleanarchitecture.domain.util.Resource

/**
 * Use case for getting all users.
 * This class contains the business logic for retrieving all users.
 */
class GetAllUsersUseCase(private val userRepository: UserRepository) {
    
    /**
     * Execute the use case to get all users
     * @return List of all users
     */
    suspend operator fun invoke(): Resource<List<User>> {
        return userRepository.getAllUsers()
    }
}
