package com.example.androidcleanarchitecture.data.network

/**
 * Custom exception class for network-related errors.
 * Wraps NetworkError with additional context for better error handling.
 */
class NetworkException(
    message: String,
    val technicalMessage: String,
    val networkError: NetworkError
) : Exception(message)
