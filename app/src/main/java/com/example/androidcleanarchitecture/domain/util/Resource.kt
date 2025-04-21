package com.example.androidcleanarchitecture.domain.util

/**
 * Resource wrapper class to handle different states of data operations.
 * This is used to communicate operation results between layers.
 * Enhanced with more detailed error types for better error handling.
 * @param T The type of data contained in the resource
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(
        val message: String,
        val errorType: ErrorType,
        val exception: Throwable? = null
    ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    
    /**
     * Enum representing different types of errors for better categorization
     */
    enum class ErrorType {
        NETWORK_ERROR,    // Connectivity issues, timeouts, etc.
        SERVER_ERROR,     // 5xx errors
        CLIENT_ERROR,     // 4xx errors (except validation errors)
        VALIDATION_ERROR, // 422 errors with field validation failures
        NOT_FOUND,        // 404 errors
        UNAUTHORIZED,     // 401 errors
        FORBIDDEN,        // 403 errors
        LOCAL_ERROR,      // Local storage/database errors
        UNKNOWN_ERROR     // Any other errors
    }
    
    /**
     * Check if this resource represents a success state
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Check if this resource represents an error state
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Check if this resource represents a loading state
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Get the data if this is a success resource, or null otherwise
     */
    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}
