package com.example.androidcleanarchitecture.data.datasource

import com.example.androidcleanarchitecture.data.mapper.UserNetworkMapper
import com.example.androidcleanarchitecture.data.model.UserModel
import com.example.androidcleanarchitecture.data.network.NetworkError
import com.example.androidcleanarchitecture.data.network.NetworkException
import com.example.androidcleanarchitecture.data.network.NetworkResult
import com.example.androidcleanarchitecture.data.network.UserApiService

/**
 * Network implementation of UserDataSource.
 * This class handles communication with the remote API for user data.
 * Updated to use Result<T> for better error handling.
 */
class UserNetworkDataSource(
    private val userApiService: UserApiService,
    private val userNetworkMapper: UserNetworkMapper
) : UserDataSource {
    
    /**
     * Get a user by their ID from the remote API
     * @param userId The ID of the user to retrieve
     * @return Result containing the user model if found and request successful, or an exception if failed
     */
    override suspend fun getUserById(userId: Int): Result<UserModel> {
        return when (val result = NetworkResult.handleResponse { userApiService.getUserById(userId) }) {
            is NetworkResult.Success -> Result.success(userNetworkMapper.mapToData(result.data))
            is NetworkResult.Error -> Result.failure(mapNetworkErrorToException(result.error))
            is NetworkResult.Loading -> Result.failure(IllegalStateException("Operation in progress"))
        }
    }
    
    /**
     * Get all users from the remote API
     * @return Result containing list of all user models if request successful, or an exception if failed
     */
    override suspend fun getAllUsers(): Result<List<UserModel>> {
        return when (val result = NetworkResult.handleResponse { userApiService.getAllUsers() }) {
            is NetworkResult.Success -> Result.success(userNetworkMapper.mapToDataList(result.data))
            is NetworkResult.Error -> Result.failure(mapNetworkErrorToException(result.error))
            is NetworkResult.Loading -> Result.failure(IllegalStateException("Operation in progress"))
        }
    }
    
    /**
     * Save a user to the remote API
     * @param userModel The user model to save
     * @return Result containing true if successful, or an exception if failed
     */
    override suspend fun saveUser(userModel: UserModel): Result<Boolean> {
        val networkModel = userNetworkMapper.mapToNetwork(userModel)
        return when (val result = NetworkResult.handleResponse { userApiService.saveUser(networkModel) }) {
            is NetworkResult.Success -> Result.success(true)
            is NetworkResult.Error -> Result.failure(mapNetworkErrorToException(result.error))
            is NetworkResult.Loading -> Result.failure(IllegalStateException("Operation in progress"))
        }
    }
    
    /**
     * Delete a user from the remote API
     * @param userId The ID of the user to delete
     * @return Result containing true if successful, or an exception if failed
     */
    override suspend fun deleteUser(userId: Int): Result<Boolean> {
        return when (val result = NetworkResult.handleResponse { userApiService.deleteUser(userId) }) {
            is NetworkResult.Success -> Result.success(true)
            is NetworkResult.Error -> Result.failure(mapNetworkErrorToException(result.error))
            is NetworkResult.Loading -> Result.failure(IllegalStateException("Operation in progress"))
        }
    }
    
    /**
     * Map NetworkError to NetworkException for proper error propagation
     */
    private fun mapNetworkErrorToException(error: NetworkError): NetworkException {
        return NetworkException(
            message = error.getUserMessage(),
            technicalMessage = error.getTechnicalMessage(),
            networkError = error
        )
    }
}
