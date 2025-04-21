package com.example.androidcleanarchitecture.domain.repository

import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.util.Resource

/**
 * Repository interface for User operations in the domain layer.
 * This defines the contract for data operations on users.
 * Updated to use Resource<T> for better error handling.
 */
interface UserRepository {
    /**
     * Get a user by their ID
     * @param userId The ID of the user to retrieve
     * @return Resource containing the user if found, or error details if failed
     */
    suspend fun getUserById(userId: Int): Resource<User?>
    
    /**
     * Get all users in the system
     * @return Resource containing list of all users, or error details if failed
     */
    suspend fun getAllUsers(): Resource<List<User>>
    
    /**
     * Save a user to the data source
     * @param user The user to save
     * @return Resource containing true if successful, or error details if failed
     */
    suspend fun saveUser(user: User): Resource<Boolean>
    
    /**
     * Delete a user from the data source
     * @param userId The ID of the user to delete
     * @return Resource containing true if successful, or error details if failed
     */
    suspend fun deleteUser(userId: Int): Resource<Boolean>
}
