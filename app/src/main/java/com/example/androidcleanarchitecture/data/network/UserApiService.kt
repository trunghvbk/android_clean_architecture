package com.example.androidcleanarchitecture.data.network

import com.example.androidcleanarchitecture.data.model.UserNetworkModel
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for user-related network operations.
 * Defines the endpoints and methods for interacting with the user API.
 */
interface UserApiService {
    
    /**
     * Get a user by their ID
     * @param userId The ID of the user to retrieve
     * @return Response containing the user data
     */
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): Response<UserNetworkModel>
    
    /**
     * Get all users
     * @return Response containing a list of users
     */
    @GET("users")
    suspend fun getAllUsers(): Response<List<UserNetworkModel>>
    
    /**
     * Create a new user or update an existing one
     * @param user The user data to save
     * @return Response indicating success or failure
     */
    @POST("users")
    suspend fun saveUser(@Body user: UserNetworkModel): Response<UserNetworkModel>
    
    /**
     * Delete a user
     * @param userId The ID of the user to delete
     * @return Response indicating success or failure
     */
    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): Response<Unit>
}
