package com.example.androidcleanarchitecture.data.repository

import com.example.androidcleanarchitecture.data.datasource.UserDataSource
import com.example.androidcleanarchitecture.data.mapper.UserMapper
import com.example.androidcleanarchitecture.data.network.NetworkException
import com.example.androidcleanarchitecture.data.network.NetworkError
import com.example.androidcleanarchitecture.domain.entity.User
import com.example.androidcleanarchitecture.domain.repository.UserRepository
import com.example.androidcleanarchitecture.domain.util.Resource

/**
 * Implementation of the UserRepository interface from the domain layer.
 * This class acts as a bridge between the domain layer and the data sources.
 * It handles both local and network data sources with a network-first strategy.
 * Updated with enhanced error handling.
 */
class UserRepositoryImpl(
    private val userLocalDataSource: UserDataSource,
    private val userNetworkDataSource: UserDataSource,
    private val userMapper: UserMapper
) : UserRepository {
    
    /**
     * Get a user by their ID
     * Uses network-first strategy: tries network first, falls back to local if network fails
     * @param userId The ID of the user to retrieve
     * @return Resource containing the user if found, or error details if failed
     */
    override suspend fun getUserById(userId: Int): Resource<User?> {
        // Try to get from network first
        val networkResult = userNetworkDataSource.getUserById(userId)
        
        networkResult.fold(
            onSuccess = { userModel ->
                // Cache in local data source
                userLocalDataSource.saveUser(userModel)
                return Resource.Success(userMapper.mapToDomain(userModel))
            },
            onFailure = { networkError ->
                // If network fails, try to get from local
                val localResult = userLocalDataSource.getUserById(userId)
                
                return localResult.fold(
                    onSuccess = { userModel ->
                        Resource.Success(userMapper.mapToDomain(userModel))
                    },
                    onFailure = { localError ->
                        // If both network and local fail, return the network error
                        mapExceptionToResource(networkError)
                    }
                )
            }
        )
    }
    
    /**
     * Get all users in the system
     * Uses network-first strategy: tries network first, falls back to local if network fails
     * @return Resource containing list of all users, or error details if failed
     */
    override suspend fun getAllUsers(): Resource<List<User>> {
        // Try to get from network first
        val networkResult = userNetworkDataSource.getAllUsers()
        
        networkResult.fold(
            onSuccess = { userModels ->
                // Cache each user locally
                userModels.forEach { userModel ->
                    userLocalDataSource.saveUser(userModel)
                }
                return Resource.Success(userModels.map { userMapper.mapToDomain(it) })
            },
            onFailure = { networkError ->
                // If network fails, try to get from local
                val localResult = userLocalDataSource.getAllUsers()
                
                return localResult.fold(
                    onSuccess = { userModels ->
                        Resource.Success(userModels.map { userMapper.mapToDomain(it) })
                    },
                    onFailure = { localError ->
                        // If both network and local fail, return the network error
                        mapExceptionToResource(networkError)
                    }
                )
            }
        )
    }
    
    /**
     * Save a user to both data sources
     * @param user The user to save
     * @return Resource containing true if successful in at least one data source, or error details if both fail
     */
    override suspend fun saveUser(user: User): Resource<Boolean> {
        val userModel = userMapper.mapToData(user)
        
        // Try to save to network
        val networkResult = userNetworkDataSource.saveUser(userModel)
        
        // Always save to local regardless of network result
        val localResult = userLocalDataSource.saveUser(userModel)
        
        return when {
            networkResult.isSuccess -> Resource.Success(true)
            localResult.isSuccess -> Resource.Success(true)
            else -> {
                // If both fail, return the network error
                networkResult.exceptionOrNull()?.let { mapExceptionToResource(it) }
                    ?: localResult.exceptionOrNull()?.let { mapExceptionToResource(it) }
                    ?: Resource.Error("Unknown error", Resource.ErrorType.UNKNOWN_ERROR)
            }
        }
    }
    
    /**
     * Delete a user from both data sources
     * @param userId The ID of the user to delete
     * @return Resource containing true if successful in at least one data source, or error details if both fail
     */
    override suspend fun deleteUser(userId: Int): Resource<Boolean> {
        // Try to delete from network
        val networkResult = userNetworkDataSource.deleteUser(userId)
        
        // Always delete from local regardless of network result
        val localResult = userLocalDataSource.deleteUser(userId)
        
        return when {
            networkResult.isSuccess -> Resource.Success(true)
            localResult.isSuccess -> Resource.Success(true)
            else -> {
                // If both fail, return the network error
                networkResult.exceptionOrNull()?.let { mapExceptionToResource(it) }
                    ?: localResult.exceptionOrNull()?.let { mapExceptionToResource(it) }
                    ?: Resource.Error("Unknown error", Resource.ErrorType.UNKNOWN_ERROR)
            }
        }
    }
    
    /**
     * Map exceptions to appropriate Resource.Error with error type categorization
     */
    private fun mapExceptionToResource(exception: Throwable): Resource<Nothing> {
        return when (exception) {
            is NetworkException -> {
                when (exception.networkError) {
                    is NetworkError.NoConnectivity, 
                    is NetworkError.Timeout,
                    is NetworkError.UnknownHostError -> 
                        Resource.Error(exception.message ?: "Network error", Resource.ErrorType.NETWORK_ERROR, exception)
                    
                    is NetworkError.ServerError -> 
                        Resource.Error(exception.message ?: "Server error", Resource.ErrorType.SERVER_ERROR, exception)
                    
                    is NetworkError.ClientError -> {
                        val clientError = exception.networkError as NetworkError.ClientError
                        when (clientError.code) {
                            404 -> Resource.Error(exception.message ?: "Resource not found", Resource.ErrorType.NOT_FOUND, exception)
                            401 -> Resource.Error(exception.message ?: "Authentication required", Resource.ErrorType.UNAUTHORIZED, exception)
                            403 -> Resource.Error(exception.message ?: "Access forbidden", Resource.ErrorType.FORBIDDEN, exception)
                            422 -> Resource.Error(exception.message ?: "Validation error", Resource.ErrorType.VALIDATION_ERROR, exception)
                            else -> Resource.Error(exception.message ?: "Client error", Resource.ErrorType.CLIENT_ERROR, exception)
                        }
                    }
                    
                    else -> Resource.Error(exception.message ?: "Unknown error", Resource.ErrorType.UNKNOWN_ERROR, exception)
                }
            }
            else -> Resource.Error(exception.message ?: "Local error", Resource.ErrorType.LOCAL_ERROR, exception)
        }
    }
}
