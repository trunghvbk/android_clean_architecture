package com.example.androidcleanarchitecture.data.network

import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Wrapper class for network responses to handle different states.
 * Enhanced with detailed error handling.
 * @param T The type of data contained in the response
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
    
    companion object {
        /**
         * Safely handle a Retrofit response and convert it to NetworkResult
         * with enhanced error handling
         */
        suspend fun <T> handleResponse(
            execute: suspend () -> Response<T>
        ): NetworkResult<T> {
            return try {
                val response = execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Success(body)
                    } else {
                        Error(NetworkError.ParseError(Exception("Response body is null")))
                    }
                } else {
                    // Parse error body
                    val errorBody = try {
                        response.errorBody()?.string()?.let {
                            parseErrorBody(it)
                        }
                    } catch (e: Exception) {
                        null
                    }
                    
                    // Create appropriate error based on status code
                    when (response.code()) {
                        in 400..499 -> Error(NetworkError.ClientError(response.code(), errorBody))
                        in 500..599 -> Error(NetworkError.ServerError(response.code(), errorBody))
                        else -> Error(NetworkError.UnknownError())
                    }
                }
            } catch (e: Exception) {
                Error(mapExceptionToNetworkError(e))
            }
        }
        
        /**
         * Map common exceptions to appropriate NetworkError types
         */
        private fun mapExceptionToNetworkError(exception: Exception): NetworkError {
            return when (exception) {
                is SocketTimeoutException -> NetworkError.Timeout(exception)
                is UnknownHostException -> NetworkError.UnknownHostError(exception)
                is IOException -> NetworkError.NoConnectivity(exception)
                else -> NetworkError.UnknownError(exception)
            }
        }
        
        /**
         * Parse error body from API response
         * This is a simplified implementation that should be enhanced with actual API error format
         */
        private fun parseErrorBody(errorBody: String): ErrorResponse {
            return try {
                // Use Gson or Moshi to parse the error body
                // This is a simplified example
                val message = if (errorBody.contains("message")) {
                    errorBody.substringAfter("message\":\"").substringBefore("\"")
                } else {
                    "Unknown error"
                }
                
                ErrorResponse(message = message)
            } catch (e: Exception) {
                ErrorResponse(message = "Could not parse error response")
            }
        }
    }
}
