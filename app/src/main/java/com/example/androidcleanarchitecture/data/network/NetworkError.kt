package com.example.androidcleanarchitecture.data.network

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Sealed class representing different types of network errors.
 * Provides categorization and context for network-related errors.
 */
sealed class NetworkError {
    // Connection errors
    data class NoConnectivity(val exception: IOException? = null) : NetworkError()
    data class Timeout(val exception: SocketTimeoutException? = null) : NetworkError()
    
    // HTTP errors by category
    data class ClientError(val code: Int, val errorBody: ErrorResponse? = null) : NetworkError()
    data class ServerError(val code: Int, val errorBody: ErrorResponse? = null) : NetworkError()
    
    // Other errors
    data class UnknownHostError(val exception: UnknownHostException) : NetworkError()
    data class ParseError(val exception: Exception) : NetworkError()
    data class UnknownError(val exception: Throwable? = null) : NetworkError()
    
    /**
     * Get a user-friendly error message based on the error type.
     * @return A message suitable for displaying to users
     */
    fun getUserMessage(): String {
        return when (this) {
            is NoConnectivity -> "No internet connection. Please check your network settings."
            is Timeout -> "Request timed out. Please try again."
            is ClientError -> when (code) {
                401 -> "Authentication required. Please log in again."
                403 -> "You don't have permission to access this resource."
                404 -> "The requested resource was not found."
                else -> errorBody?.message ?: "Client error: $code"
            }
            is ServerError -> "Server error. Please try again later."
            is UnknownHostError -> "Could not reach the server. Please check your connection."
            is ParseError -> "Error processing the response. Please try again."
            is UnknownError -> "An unexpected error occurred. Please try again."
        }
    }
    
    /**
     * Get technical error details for logging purposes.
     * @return A detailed message with technical information
     */
    fun getTechnicalMessage(): String {
        return when (this) {
            is NoConnectivity -> "No connectivity: ${exception?.message}"
            is Timeout -> "Request timeout: ${exception?.message}"
            is ClientError -> "HTTP ${code}: ${errorBody?.technicalMessage ?: errorBody?.message}"
            is ServerError -> "HTTP ${code}: ${errorBody?.technicalMessage ?: errorBody?.message}"
            is UnknownHostError -> "Unknown host: ${exception.message}"
            is ParseError -> "Parse error: ${exception.message}"
            is UnknownError -> "Unknown error: ${exception?.message}"
        }
    }
}
