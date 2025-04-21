package com.example.androidcleanarchitecture.data.network

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility class for testing different network error scenarios.
 * Provides methods to simulate various error conditions.
 */
object NetworkErrorTestUtil {

    /**
     * Simulate a network connectivity error
     */
    fun simulateNoConnectivity(): NetworkError {
        return NetworkError.NoConnectivity(IOException("No internet connection"))
    }

    /**
     * Simulate a timeout error
     */
    fun simulateTimeout(): NetworkError {
        return NetworkError.Timeout(SocketTimeoutException("Connection timed out"))
    }

    /**
     * Simulate an unknown host error
     */
    fun simulateUnknownHost(): NetworkError {
        return NetworkError.UnknownHostError(UnknownHostException("Unknown host: api.example.com"))
    }

    /**
     * Simulate a client error (4xx) with optional error body
     */
    fun simulateClientError(
        code: Int = 400,
        errorMessage: String = "Bad Request"
    ): NetworkError {
        val errorResponse = ErrorResponse(
            message = errorMessage,
            technicalMessage = "Client error occurred with status code: $code",
            errorCode = "E$code"
        )
        return NetworkError.ClientError(code, errorResponse)
    }

    /**
     * Simulate a server error (5xx) with optional error body
     */
    fun simulateServerError(
        code: Int = 500,
        errorMessage: String = "Internal Server Error"
    ): NetworkError {
        val errorResponse = ErrorResponse(
            message = errorMessage,
            technicalMessage = "Server error occurred with status code: $code",
            errorCode = "E$code"
        )
        return NetworkError.ServerError(code, errorResponse)
    }

    /**
     * Simulate a validation error (422) with field errors
     */
    fun simulateValidationError(fieldErrors: Map<String, String> = mapOf(
        "email" to "Invalid email format",
        "password" to "Password must be at least 8 characters"
    )): NetworkError {
        val errorResponse = ErrorResponse(
            message = "Validation failed",
            technicalMessage = "One or more fields failed validation",
            errorCode = "E422",
            fieldErrors = fieldErrors
        )
        return NetworkError.ClientError(422, errorResponse)
    }

    /**
     * Simulate a parse error
     */
    fun simulateParseError(): NetworkError {
        return NetworkError.ParseError(Exception("Failed to parse response"))
    }

    /**
     * Create a failed Retrofit response with the specified error code and message
     */
    fun <T> createErrorResponse(
        code: Int,
        errorMessage: String = "Error occurred"
    ): Response<T> {
        val errorJson = """{"message":"$errorMessage","errorCode":"E$code"}"""
        val errorResponseBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        return Response.error(code, errorResponseBody)
    }
}
