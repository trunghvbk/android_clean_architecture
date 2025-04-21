package com.example.androidcleanarchitecture.data.datasource

import com.example.androidcleanarchitecture.data.model.UserModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Local implementation of UserDataSource.
 * This class simulates a local database or cache for user data.
 * Updated to use Result<T> for better error handling.
 */
class UserLocalDataSource : UserDataSource {
    
    // In-memory storage for users with thread-safe access
    private val usersMap = mutableMapOf<Int, UserModel>()
    private val mutex = Mutex()
    
    /**
     * Get a user by their ID from local storage
     * @param userId The ID of the user to retrieve
     * @return Result containing the user model if found, or an exception if not found
     */
    override suspend fun getUserById(userId: Int): Result<UserModel> = mutex.withLock {
        val user = usersMap[userId]
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("User not found with ID: $userId"))
        }
    }
    
    /**
     * Get all users from local storage
     * @return Result containing list of all user models
     */
    override suspend fun getAllUsers(): Result<List<UserModel>> = mutex.withLock {
        return Result.success(usersMap.values.toList())
    }
    
    /**
     * Save a user to local storage
     * @param userModel The user model to save
     * @return Result containing true if successful, or an exception if failed
     */
    override suspend fun saveUser(userModel: UserModel): Result<Boolean> = mutex.withLock {
        return if (userModel.id != -1) {
            usersMap[userModel.id] = userModel
            Result.success(true)
        } else {
            Result.failure(Exception("Invalid user ID: ID cannot be empty"))
        }
    }
    
    /**
     * Delete a user from local storage
     * @param userId The ID of the user to delete
     * @return Result containing true if successful, or an exception if user not found
     */
    override suspend fun deleteUser(userId: Int): Result<Boolean> = mutex.withLock {
        return if (usersMap.containsKey(userId)) {
            usersMap.remove(userId)
            Result.success(true)
        } else {
            Result.failure(Exception("User not found with ID: $userId"))
        }
    }
}
