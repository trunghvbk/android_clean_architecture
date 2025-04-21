package com.example.androidcleanarchitecture.data.datasource

import com.example.androidcleanarchitecture.data.model.UserModel

/**
 * Interface for User data sources.
 * This defines the contract for different data sources (remote, local, etc.)
 * Updated to use Result<T> for better error handling.
 */
interface UserDataSource {
    /**
     * Get a user by their ID
     * @param userId The ID of the user to retrieve
     * @return Result containing the user model if successful, or an exception if failed
     */
    suspend fun getUserById(userId: Int): Result<UserModel>
    
    /**
     * Get all users
     * @return Result containing list of all user models if successful, or an exception if failed
     */
    suspend fun getAllUsers(): Result<List<UserModel>>
    
    /**
     * Save a user to the data source
     * @param userModel The user model to save
     * @return Result containing true if successful, or an exception if failed
     */
    suspend fun saveUser(userModel: UserModel): Result<Boolean>
    
    /**
     * Delete a user from the data source
     * @param userId The ID of the user to delete
     * @return Result containing true if successful, or an exception if failed
     */
    suspend fun deleteUser(userId: Int): Result<Boolean>
}
