package com.example.androidcleanarchitecture.data.network

/**
 * Model class for API error responses.
 * Contains structured error information from the API.
 */
data class ErrorResponse(
    val message: String,
    val technicalMessage: String? = null,
    val errorCode: String? = null,
    val fieldErrors: Map<String, String>? = null
)
