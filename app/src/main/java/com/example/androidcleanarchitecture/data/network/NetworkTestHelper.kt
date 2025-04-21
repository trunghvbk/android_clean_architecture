package com.example.androidcleanarchitecture.data.network

import com.example.androidcleanarchitecture.data.model.UserNetworkModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.mockwebserver.MockWebServer
import java.util.UUID
import kotlin.random.Random

/**
 * Helper class for testing network components.
 * Provides mock data and utilities for testing network functionality.
 */
object NetworkTestHelper {
    
    /**
     * Generate a mock user with random ID
     * @param name Optional name for the user
     * @param email Optional email for the user
     * @return A mock UserNetworkModel
     */
    fun createMockUser(
        name: String = "Test User",
        email: String = "test@example.com"
    ): UserNetworkModel {
        return UserNetworkModel(
            id = Random.nextInt(),
            name = name,
            email = email,
            username = ""
        )
    }
    
    /**
     * Generate a list of mock users
     * @param count Number of users to generate
     * @return List of mock UserNetworkModel objects
     */
    fun createMockUserList(count: Int): List<UserNetworkModel> {
        return (1..count).map {
            UserNetworkModel(
                id = Random.nextInt(),
                name = "User $it",
                email = "user$it@example.com",
                username = ""
            )
        }
    }
    
    /**
     * Convert a UserNetworkModel to JSON string
     * @param user The user to convert
     * @return JSON string representation
     */
    fun userToJson(user: UserNetworkModel): String {
        return Gson().toJson(user)
    }
    
    /**
     * Convert a list of UserNetworkModel to JSON string
     * @param users The list of users to convert
     * @return JSON string representation
     */
    fun userListToJson(users: List<UserNetworkModel>): String {
        return Gson().toJson(users)
    }
    
    /**
     * Setup mock server with a successful response for getting all users
     * @param mockWebServer The mock web server instance
     * @param userCount Number of mock users to include in response
     */
    fun setupMockGetAllUsersSuccess(mockWebServer: MockWebServer, userCount: Int = 5) {
        val mockUsers = createMockUserList(userCount)
        val jsonResponse = userListToJson(mockUsers)
        
        ApiServiceFactory.enqueueMockResponse(jsonResponse)
    }
    
    /**
     * Setup mock server with a successful response for getting a user by ID
     * @param mockWebServer The mock web server instance
     * @param user Optional specific user to return, creates a random one if null
     */
    fun setupMockGetUserByIdSuccess(mockWebServer: MockWebServer, user: UserNetworkModel? = null) {
        val mockUser = user ?: createMockUser()
        val jsonResponse = userToJson(mockUser)
        
        ApiServiceFactory.enqueueMockResponse(jsonResponse)
    }
    
    /**
     * Setup mock server with a successful response for saving a user
     * @param mockWebServer The mock web server instance
     * @param user The user that was saved
     */
    fun setupMockSaveUserSuccess(mockWebServer: MockWebServer, user: UserNetworkModel) {
        val jsonResponse = userToJson(user)
        
        ApiServiceFactory.enqueueMockResponse(jsonResponse)
    }
    
    /**
     * Setup mock server with a successful response for deleting a user
     * @param mockWebServer The mock web server instance
     */
    fun setupMockDeleteUserSuccess(mockWebServer: MockWebServer) {
        ApiServiceFactory.enqueueMockResponse("{}")
    }
    
    /**
     * Setup mock server with an error response
     * @param mockWebServer The mock web server instance
     * @param errorCode HTTP error code
     * @param errorMessage Error message
     */
    fun setupMockError(mockWebServer: MockWebServer, errorCode: Int = 404, errorMessage: String = "Not found") {
        val jsonResponse = """{"error": "$errorMessage"}"""
        
        ApiServiceFactory.enqueueMockResponse(jsonResponse, errorCode)
    }
}
